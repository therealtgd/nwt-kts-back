package com.foober.foober.controller;

import com.foober.foober.dto.LocalUser;
import com.foober.foober.dto.LoginRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
class RideControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;
    private String driverToken;
    private LocalUser driverLocalUser;
    private String clientToken;
    private LocalUser clientLocalUser;

    @BeforeEach
    void setUp() {
        ResponseEntity<String> responseEntityDriver =
        restTemplate.exchange("/auth/signin",
                HttpMethod.POST,
                new HttpEntity<>(new LoginRequest("driver@gmail.com", "driver")),
                new ParameterizedTypeReference<>() {
                });
        driverToken = responseEntityDriver.getBody();
//        ResponseEntity<LocalUser> responseEntityDriverLocalUser =
//        restTemplate.exchange("/auth/get-local-user",
//                HttpMethod.POST,
//                new HttpEntity<>(new LoginRequest("driver@gmail.com", "driver")),
//                new ParameterizedTypeReference<>() {
//                });
//        driverLocalUser = responseEntityDriverLocalUser.getBody();


        ResponseEntity<String> responseEntityClient =
                restTemplate.exchange("/auth/signin",
                        HttpMethod.POST,
                        new HttpEntity<>(new LoginRequest("client@gmail.com", "client")),
                        new ParameterizedTypeReference<>() {
                        });
        clientToken = responseEntityClient.getBody();
//        ResponseEntity<LocalUser> responseEntityClientLocalUser =
//                restTemplate.exchange("/auth/get-local-user",
//                        HttpMethod.POST,
//                        new HttpEntity<>(new LoginRequest("client@gmail.com", "client")),
//                        new ParameterizedTypeReference<>() {
//                        });
//        driverLocalUser = responseEntityClientLocalUser.getBody();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Shold return ride startTime and send /driver/ride-started notification when making PUT request to endpoint - /ride/{id}/start")
    void startRide() {
        // Create a mock authentication object
        Authentication auth = new TestingAuthenticationToken("driver", "driver");
        auth.setAuthenticated(true);

        // Set up the security context with the mock authentication object
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + driverToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange("/ride/1/start",
                HttpMethod.PUT,
                new HttpEntity<>(null, headers),
                new ParameterizedTypeReference<>() {
                });

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertTrue(responseBody.containsKey("startTime"));
        assertTrue(responseBody.get("startTime") instanceof Long);

        Long startTime = (Long) responseBody.get("startTime");
        assertTrue(startTime > System.currentTimeMillis() - 3 * 60 * 1000);
    }

    @Test
    void finishRide() {
    }

    @Test
    void endRide() {
    }

    @Test
    void getDriverEta() {
    }

    @Test
    void testGetDriverEta() {
    }
}