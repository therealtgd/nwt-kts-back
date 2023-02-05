package com.foober.foober.integration;

import com.foober.foober.dto.*;
import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.VehicleType;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DriveRejectionIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;

    private String driverToken;
    private String clientToken;

    @SneakyThrows
    @BeforeEach
    public void login() {
        ResponseEntity<String> responseEntityDriver =
                restTemplate.postForEntity("/auth/signin2",
                        new HttpEntity<>(new LoginRequest("driver@gmail.com", "sifra123")),
                        String.class);
        driverToken = responseEntityDriver.getBody();

        ResponseEntity<String> responseEntityClient =
                restTemplate.postForEntity("/auth/signin2",
                        new HttpEntity<>(new LoginRequest("client@gmail.com", "sifra123")),
                        String.class);
        clientToken = responseEntityClient.getBody();
    }

    @Test
    @Disabled
    //@WithUserDetails("client@gmail.com")
    public void orderRide() {

        RideInfoDto rideInfoDto = new RideInfoDto(
                0,
                0,
                new AddressDto("", new LatLng(0.0,0.0)),
                new AddressDto("", new LatLng(0.0,0.0)),
                VehicleType.SEDAN,
                new SimpleDriverDto(2, "client", "", DriverStatus.AVAILABLE, false),
                List.of(new AddressDto("", new LatLng(0.0,0.0))),
                0,
                List.of("client")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(clientToken);
        ResponseEntity<ActiveRideDto>  response = restTemplate.postForEntity(
                "/ride/order",
                rideInfoDto,
                ActiveRideDto.class,
                headers);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    //@WithUserDetails("driver@gmail.com")
    public void testFinishRideOK() {
        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Some reason");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(driverToken);
        headers.setBasicAuth("driver@gmail.com", "driver");
        HttpEntity<RideCancellationDto> entity = new HttpEntity<>(rideCancellationDto, headers);

        ResponseEntity<SimpleApiResponse> response = restTemplate.exchange(
                "/ride/1/end",
                HttpMethod.PUT,
                entity,
                SimpleApiResponse.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    //@WithUserDetails("driver@gmail.com")
    public void testFinishRideWrongId() {
        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Some reason");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(driverToken);
        headers.setBasicAuth("driver@gmail.com", "driver");
        HttpEntity<RideCancellationDto> entity = new HttpEntity<>(rideCancellationDto, headers);

        ResponseEntity<SimpleApiResponse> response = restTemplate.exchange(
                "/ride/500/end",
                HttpMethod.PUT,
                entity,
                SimpleApiResponse.class
        );
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}
