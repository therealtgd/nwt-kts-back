package com.foober.foober.service;

import com.foober.foober.dto.RideCancellationDto;
import com.foober.foober.model.CancellationReason;
import com.foober.foober.model.Client;
import com.foober.foober.model.Driver;
import com.foober.foober.model.Ride;
import com.foober.foober.model.enumeration.ClientStatus;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.RideStatus;
import com.foober.foober.repos.CancellationReasonRepository;
import com.foober.foober.repos.ClientRepository;
import com.foober.foober.repos.DriverRepository;
import com.foober.foober.repos.RideRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RideServiceTest {
    @Mock
    RideRepository rideRepository;
    @Mock
    CancellationReasonRepository cancellationRepository;
    @Mock
    ClientRepository clientRepository;
    @InjectMocks
    private RideService rideService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    public void testEndRideOk() {
        long id = 1L;

        Client client1 = new Client();
        client1.setStatus(ClientStatus.IN_RIDE);
        client1.setCredits(500);

        Client client2 = new Client();
        client2.setStatus(ClientStatus.IN_RIDE);
        client2.setCredits(500);

        Ride ride = new Ride();
        ride.setId(id);
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setPrice(200);
        ride.setClients(Set.of(client1, client2));

        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Test Reason");

        when(rideRepository.getById(id)).thenReturn(ride);

        rideService.endRide(id, rideCancellationDto);

        verify(rideRepository).getById(id);
        verify(rideRepository).save(ride);
        verify(cancellationRepository).save(any(CancellationReason.class));
        verify(clientRepository).saveAll(ride.getClients());

        assertEquals(RideStatus.CANCELLED, ride.getStatus());
        assertTrue(ride.getEndTime() > 0);
        ride.getClients().forEach(c -> {
            assertEquals(ClientStatus.ONLINE, c.getStatus());
            assertEquals(600, c.getCredits());
        });
    }

    @Test(expected = EntityNotFoundException.class)
    public void testEndRideWithNonExistentRide() {
        long id = 1L;
        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Test Reason");

        when(rideRepository.getById(id)).thenThrow(new EntityNotFoundException("Ride not found"));

        rideService.endRide(id, rideCancellationDto);
    }
    @Test
    public void testGetRideClientsOk() {
        long id = 1;
        Set<Client> expectedClients = new HashSet<>();

        Client client1 = new Client();
        client1.setId(1L);
        Client client2 = new Client();
        client2.setId(2L);

        expectedClients.add(new Client());
        expectedClients.add(new Client());

        Ride ride = new Ride();
        ride.setId(id);
        ride.setClients(expectedClients);

        when(rideRepository.getById(id)).thenReturn(ride);

        Set<Client> actualClients = rideService.getRideClients(id);

        assertEquals(expectedClients, actualClients);
        verify(rideRepository).getById(id);
    }

    @Test(expected = EntityNotFoundException.class)
    public void testGetRideClientsWithNonExistantRide() {
        long id = 1;
        when(rideRepository.getById(id)).thenThrow(EntityNotFoundException.class);

        rideService.getRideClients(id);

        verify(rideRepository).getById(id);
    }
}
