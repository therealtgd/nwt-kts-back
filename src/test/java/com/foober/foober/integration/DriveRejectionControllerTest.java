package com.foober.foober.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foober.foober.dto.LatLng;
import com.foober.foober.dto.RideCancellationDto;
import com.foober.foober.dto.ride.AddressDto;
import com.foober.foober.dto.ride.RideInfoDto;
import com.foober.foober.dto.ride.SimpleDriverDto;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.model.enumeration.VehicleType;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DriveRejectionControllerTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    private WebApplicationContext context;
    private MockMvc mvc;

    private String driverToken;
    private String clientToken;

    @Before
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    @SneakyThrows
    @BeforeEach
    public void login() {
        MvcResult result = mvc.perform(get("/auth/signin2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        driverToken = result.getResponse().getContentAsString();
        MvcResult result2 = mvc.perform(get("/auth/signin2").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        clientToken = result2.getResponse().getContentAsString();

    }
    @SneakyThrows
    @Test()
    @WithMockUser(authorities = "ROLE_CLIENT", username = "client@gmail.com", password = "client")
    public void orderRide() {

        RideInfoDto rideInfoDto = new RideInfoDto(
                0,
                0,
                new AddressDto("", new LatLng(0.0,0.0)),
                new AddressDto("", new LatLng(0.0,0.0)),
                VehicleType.SEDAN,
                new SimpleDriverDto(2, "driver", "Vozac", DriverStatus.AVAILABLE, false),
                List.of(new AddressDto("", new LatLng(0.0,0.0))),
                0,
                List.of("client"));

        mvc.perform(post("/ride/order").contentType(MediaType.APPLICATION_JSON).content(asJsonString(rideInfoDto)))
                .andExpect(status().isOk());
    }
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @SneakyThrows
    @Test
    @WithMockUser(authorities = "ROLE_DRIVER", username = "driver@gmail.com", password = "driver")
    public void testFinishRideOK() {
        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Some reason");

        mvc.perform(put("/ride/1/end").contentType(MediaType.APPLICATION_JSON).content(asJsonString(rideCancellationDto)))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    @WithMockUser(authorities = "ROLE_DRIVER", username = "driver@gmail.com", password = "driver")
    public void testFinishRideWrongId() {
        RideCancellationDto rideCancellationDto = new RideCancellationDto();
        rideCancellationDto.setReason("Some reason");

        mvc.perform(put("/ride/1/end").contentType(MediaType.APPLICATION_JSON).content(asJsonString(rideCancellationDto)))
                .andExpect(status().isOk());
    }
}
