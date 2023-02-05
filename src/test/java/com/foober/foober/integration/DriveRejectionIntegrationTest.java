package com.foober.foober.integration;

import com.foober.foober.dto.*;
import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.VehicleType;
import lombok.SneakyThrows;
import org.junit.Before;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DriveRejectionIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;

    private String driverToken;
    private String clientToken;

    @SneakyThrows
    @Before
    public void login() {
        ResponseEntity<String> responseEntityDriver =
                restTemplate.postForEntity("/auth/signin2",
                        new HttpEntity<>(new LoginRequest("driver@gmail.com", "driver")),
                        String.class);
        driverToken = responseEntityDriver.getBody();

        ResponseEntity<String> responseEntityClient =
                restTemplate.postForEntity("/auth/signin2",
                        new HttpEntity<>(new LoginRequest("client@gmail.com", "client")),
                        String.class);
        clientToken = responseEntityClient.getBody();
    }

    @Test
    //@WithUserDetails("driver@gmail.com")
    public void testFinishRideOK() {
        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Some reason");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(driverToken);
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
