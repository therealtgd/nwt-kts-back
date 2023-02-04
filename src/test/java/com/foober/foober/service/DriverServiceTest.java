package com.foober.foober.service;

import com.foober.foober.model.Driver;
import com.foober.foober.model.enumeration.DriverStatus;
import com.foober.foober.repos.DriverRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DriverServiceTest {

    @Mock
    DriverRepository driverRepository;
    @InjectMocks
    private DriverService driverService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test(expected = EntityNotFoundException.class)
    public void testUpdateStatusWithNonExistentDriver() {
        Long driverId = 1L;
        doThrow(EntityNotFoundException.class).when(driverRepository).getById(driverId);

        driverService.updateStatus(driverId, DriverStatus.AVAILABLE);
    }
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateStatusWithNonExistentDriverId() {
        doThrow(IllegalArgumentException.class).when(driverRepository).getById(null);

        driverService.updateStatus(null, DriverStatus.AVAILABLE);
    }
    @Test
    public void testUpdateStatusOk() {
        Long driverId = 1L;
        DriverStatus status = DriverStatus.AVAILABLE;
        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setStatus(DriverStatus.AWAY);

        when(driverRepository.getById(driverId)).thenReturn(driver);

        driverService.updateStatus(driverId, status);

        // getById was called with the correct driverId
        verify(driverRepository).getById(driverId);
        // save was called with the correct Driver instance
        verify(driverRepository).save(driver);
        assertEquals(status, driver.getStatus());
    }


}
