package com.foober.foober.service;

import com.foober.foober.dto.*;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.exception.EmailAlreadyExistsException;
import com.foober.foober.exception.UsernameAlreadyExistsException;
import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.model.enumeration.VehicleType;
import com.foober.foober.repos.*;
import com.foober.foober.util.DtoConverter;
import com.foober.foober.util.GeneralUtils;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.foober.foober.util.SortUtils.sort;

@Service
@AllArgsConstructor
@EnableScheduling
public class DriverService {
    private final DriverRepository driverRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RideService rideService;
    private final RoleRepository roleRepository;
    private final ImageRepository imageRepository;
    private final VehicleRepository vehicleRepository;
    private final PendingDriverChangesRepository driverChangesRepository;
    private final RideRepository rideRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VehicleService vehicleService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public List<DriverDto> getActiveDriverDtos() {
        return this.driverRepository.findAllActive().stream().map(DriverDto::new).collect(Collectors.toList());
    }

    public List<DriverDto> getAllByStatus(DriverStatus status, User user) {
        List<Driver> drivers = this.driverRepository.findAllByStatus(status);
        if (user != null && user.getClass() == Driver.class) {
            drivers = drivers.stream().filter(d -> !Objects.equals(d.getId(), user.getId())).toList();
        }
        return drivers.stream().map(DriverDto::new).collect(Collectors.toList());
    }

    public DriverDto getCompatibleDriverDto(Long id) {
        return new DriverDto(this.driverRepository.getById(id));
    }

    public List<RideBriefDisplay> getRides(User user, String criteria) {
        Driver driver = (Driver) user;
        var ref = new Object() {
            List<RideBriefDisplay> rides = new ArrayList<>();
        };
        driver.getRides().stream().filter(ride -> ride.getStatus() == RideStatus.COMPLETED).forEach(ride -> ref.rides.add(DtoConverter.rideToBriefDisplay(ride, rideService.getRideDriverRating(ride))));
        ref.rides = sort(ref.rides, criteria);
        return ref.rides;
    }
    
    @Transactional
    public void updateStatus(Long driver_id, DriverStatus status) {
        Driver driver = this.driverRepository.getById(driver_id);
        driver.setStatus(status);
        this.driverRepository.save(driver);
    }

    public SimpleDriverDto getNearestFreeDriver(
        String vehicleType,
        boolean petsAllowed,
        boolean babiesAllowed,
        double lat,
        double lng
    ) {
        Optional<List<Driver>> drivers = driverRepository.findNearestFreeDriver(
                Enum.valueOf(VehicleType.class, vehicleType), petsAllowed, babiesAllowed, lat, lng
        );
        SimpleDriverDto simpleDriverDto = null;
        if (drivers.isPresent() && !drivers.get().isEmpty()) {
            Driver driver = drivers.get().get(0);
            driver.setStatus(DriverStatus.PENDING);
            driver = driverRepository.save(driver);
            simpleDriverDto = new SimpleDriverDto(driver);
        } else {
            List<Ride> rides = rideService.getRidesNearestToEnd();
            if (!rides.isEmpty()) {
                simpleDriverDto = getCompatibleDriverDto(vehicleType, petsAllowed, babiesAllowed, rides);
            }
        }
        return simpleDriverDto;
    }

    private SimpleDriverDto getCompatibleDriverDto(String vehicleType, boolean petsAllowed, boolean babiesAllowed, List<Ride> rides) {
        for (Ride r : rides) {
            Vehicle vehicle = r.getDriver().getVehicle();
            if (vehicle.getType() == Enum.valueOf(VehicleType.class, vehicleType)
                && (!petsAllowed || vehicle.isPetsAllowed())
                && (!babiesAllowed || vehicle.isBabiesAllowed())
            ) {
                Driver driver = r.getDriver();
                driver.setReserved(true);
                driver = driverRepository.save(driver);
                return new SimpleDriverDto(driver);
            }
        }
        return null;
    }

    @Scheduled(fixedRate = 1000)
    public void sendDriversStatusAndPosition() {
        List<DriverDto> driverDtos = this.driverRepository.findAllActive().stream().map(DriverDto::new).toList();
        if (driverDtos.size() > 0) {
            this.simpMessagingTemplate.convertAndSend(
                "/map-updates/update-drivers-status-and-position",
                driverDtos
            );
        }
    }

    private final Map<Long, List<Long>> workTimestamps = new HashMap<>();
    @Scheduled(fixedRate = 6*60*1000)
    public void trackWorkHours() {
        long now = Instant.now().getEpochSecond();
        Instant a24hours = Instant.now().minus(24, ChronoUnit.HOURS);

        List<Driver> drivers = this.driverRepository.findAllActive();
        for (Driver driver: drivers) {
            if (!workTimestamps.containsKey(driver.getId()))
                workTimestamps.put(driver.getId(), new ArrayList<>());
            workTimestamps.get(driver.getId()).add(now);
        }

        for (List<Long> entry: workTimestamps.values()) {
            ListIterator<Long> iter = entry.listIterator();
            while(iter.hasNext()) {
                if (!Instant.ofEpochSecond(now).isBefore(a24hours)) {
                    break;
                }
                iter.remove();
            }
        }

    }

    public void unassignDriver(long id) {
        Driver driver = driverRepository.getById(id);
        if (driver.isReserved()) {
            driver.setReserved(false);
        } else if (driver.getStatus().equals(DriverStatus.PENDING)){
            driver.setStatus(DriverStatus.AVAILABLE);
        }
    }

    public void update(User user, DriverUpdateRequest updateRequest, MultipartFile file) throws IOException {
        Driver driver = (Driver) user;

        Vehicle vehicle = new Vehicle(
                updateRequest.getLicencePlate(),
                updateRequest.getCapacity(),
                updateRequest.isPetsAllowed(),
                updateRequest.isBabiesAllowed(),
                VehicleType.valueOf(updateRequest.getVehicleType())
        );
        vehicleRepository.save(vehicle);

        if (updateRequest.isImageUploaded()) {
            Image image = new Image(
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())),
                    file.getContentType(),
                    file.getSize(),
                    file.getBytes()
            );
            imageRepository.save(image);

            PendingDriverChanges changes = new PendingDriverChanges(
                    driver,
                    updateRequest.getDisplayName(),
                    updateRequest.getUsername(),
                    updateRequest.getPhoneNumber(),
                    updateRequest.getCity(),
                    image,
                    vehicle,
                    System.currentTimeMillis()
            );
            driverChangesRepository.save(changes);
        }
        else {
            PendingDriverChanges changes = new PendingDriverChanges(
                    driver,
                    updateRequest.getDisplayName(),
                    updateRequest.getUsername(),
                    updateRequest.getPhoneNumber(),
                    updateRequest.getCity(),
                    vehicle,
                    System.currentTimeMillis()
            );
            driverChangesRepository.save(changes);
        }
    }

    public DriverDto getMe(User user) {
        Driver driver = driverRepository.getById(user.getId());
        return new DriverDto(driver);
    }
    
    public ActiveRideDto getActiveRide(User user) {
        Optional<Ride> ride = rideRepository.getInProgressRideByClientId(user.getId());
        return ride.map(ActiveRideDto::new).orElse(null);
    }

    public void registerNewDriver(DriverSignUpRequest signUpRequest, MultipartFile file) throws IOException {
        final HashSet<Role> roles = new HashSet<Role>();
        roles.add(roleRepository.findByName("ROLE_DRIVER"));
        roles.add(roleRepository.findByName("ROLE_USER"));

        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("There is already someone registered with that username.");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new EmailAlreadyExistsException("There is already someone registered with that email.");
        }

        Vehicle vehicle = new Vehicle(
            signUpRequest.getLicencePlate(),
            signUpRequest.getCapacity(),
            signUpRequest.isPetsAllowed(),
            signUpRequest.isBabiesAllowed(),
            VehicleType.valueOf(signUpRequest.getVehicleType())
        );
        vehicle = vehicleRepository.save(vehicle);

        Driver driver = new Driver(
            signUpRequest.getUsername(),
            signUpRequest.getEmail(),
            passwordEncoder.encode(signUpRequest.getPassword()),
            signUpRequest.getDisplayName(),
            signUpRequest.getPhoneNumber(),
            signUpRequest.getCity(),
            roles,
            vehicle
        );
        driver = driverRepository.save(driver);

        if (signUpRequest.isImageUploaded()) {
            Image image = new Image(
                StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())),
                file.getContentType(),
                file.getSize(),
                file.getBytes()
            );
            driver.setImage(image);
            driverRepository.save(driver);
        }
    }

    public void simulateDrive(Vehicle vehicle, ArrayList<LatLng> waypoints) {
        Random generator = new Random();
        for (LatLng waypoint : waypoints) {
            GeneralUtils.wait((int) ((generator.nextDouble() * (2 - 0.5) + 0.5) * 1000));
            this.vehicleService.updateVehicleLocation(vehicle, waypoint);
        }
    }

    public ActiveRideDto getNextRide(Driver driver) {
        Ride ride = rideRepository.getRideByStatusAndDriverId(RideStatus.WAITING, driver.getId()).orElse(null);
        if (ride != null) {
            ride.setStatus(RideStatus.ON_ROUTE);
            driver.setStatus(DriverStatus.BUSY);
            this.rideRepository.save(ride);
            this.driverRepository.save(driver);
            return new ActiveRideDto(ride);
        }
        return null;
    }

    public ActiveRideDto getCurrentRideByDriver(Driver driver) {
        Ride ride = rideRepository.getRideByStatusAndDriverId(RideStatus.IN_PROGRESS, driver.getId()).orElseThrow();
        return new ActiveRideDto(ride);
    }
}

