package com.foober.foober.service;

import com.foober.foober.dto.LatLng;
import com.foober.foober.dto.ActiveRideDto;
import com.foober.foober.dto.RideBriefDisplay;
import com.foober.foober.dto.RouteDto;
import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.exception.InvalidTokenException;
import com.foober.foober.exception.UserAlreadyActivatedException;
import com.foober.foober.model.*;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.repos.RideRepository;
import com.foober.foober.security.jwt.TokenProvider;
import com.foober.foober.util.AddressIndexComparator;
import com.foober.foober.util.DtoConverter;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import static com.foober.foober.util.SortUtils.sort;

@Service
@AllArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final RideRepository rideRepository;
    private final TokenProvider tokenUtils;

    public void confirmRegistration(String token) {

        try {
            tokenUtils.validateToken(token);
            Client client = clientRepository.findById(tokenUtils.getUserIdFromToken(token)).orElseThrow();
            if (client.isActivated()) {
                throw new UserAlreadyActivatedException("Client is already activated.");
            }
            client.setActivated(true);
            client.setEnabled(true);
            clientRepository.save(client);

        } catch (NoSuchElementException e) {
            throw new InvalidTokenException("Token is invalid");
        }

    }

    public int getCreditsBalance(User user) {
        return user.getCredits();
    }

    public List<RideBriefDisplay> getRides(User user, String criteria) {
        Client client = (Client) user;
        var ref = new Object() {
            List<RideBriefDisplay> rides = new ArrayList<>();
        };
        client.getRides().stream().filter(ride -> ride.getStatus() == RideStatus.COMPLETED)
                .forEach(ride -> ref.rides.add(DtoConverter.rideToBriefDisplay(ride, client)));
        ref.rides = sort(ref.rides, criteria);
        return ref.rides;
    }

    public ArrayList<RouteDto> getFavoriteRoutes(User user) {
        ArrayList<RouteDto> routes = new ArrayList<>();
        Set<Ride> favoriteRides = ((Client) user).getFavorites();
        favoriteRides.forEach(ride -> routes.add(getRouteDtoFromRoute(ride.getId(), ride.getRoute())));
        return routes;
    }

    private RouteDto getRouteDtoFromRoute(long id, Set<Address> route) {
        ArrayList<AddressDto> stops = new ArrayList<>();
        route.stream().sorted(new AddressIndexComparator()).forEach(address -> stops.add(new AddressDto(address.getStreetAddress(), new LatLng(address.getLatitude(), address.getLongitude()))));
        return new RouteDto(id, stops);
    }

    public void addFavoriteRoute(User user, long rideId) {
        try {
            Client client = (Client) user;
            client.getFavorites().add(rideRepository.getById(rideId));
            clientRepository.save(client);
        } catch (Exception e) {
            throw new NoSuchElementException("Ride doesn't exist.");
        }
    }

    public void removeFavoriteRoute(User user, long rideId) {
        try {
            Client client = (Client) user;
            Ride found = null;
            for (Ride r : client.getFavorites()) {
                if (r.getId().equals(rideId)) {
                    found = r;
                    break;
                }
            }
            if (found == null) {
                throw new Exception();
            }
            else {
                client.getFavorites().remove(found);
                clientRepository.save(client);
            }
        } catch (Exception e) {
            throw new NoSuchElementException("Ride doesn't exist.");
        }
    }

     public boolean hasActiveRide(User user) {
        return rideRepository.activeRideByUserIsPresent((Client) user);
    }

    public ActiveRideDto getActiveRide(User user) {
        return rideRepository.getActiveRideByClient((Client) user)
                .map(ActiveRideDto::new)
                .orElse(null);
    }
}
