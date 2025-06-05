package my.projects.lockersystemlockermicroservice.service.impl;

import my.projects.lockersystemlockermicroservice.dto.kafka.AccessCodeEventDTO;
import my.projects.lockersystemlockermicroservice.dto.kafka.PackageEventDTO;
import my.projects.lockersystemlockermicroservice.entity.Locker;
import my.projects.lockersystemlockermicroservice.entity.LockerUnit;
import my.projects.lockersystemlockermicroservice.repository.LockerRepository;
import my.projects.lockersystemlockermicroservice.repository.LockerUnitRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockerEventListenerTest {

    @Mock
    private LockerRepository lockerRepository;

    @Mock
    private LockerUnitRepository lockerUnitRepository;

    @Mock
    private KafkaTemplate<String, AccessCodeEventDTO> kafkaTemplate;

    @InjectMocks
    private LockerEventListener lockerEventListener;

    private Locker locker;
    private LockerUnit lockerUnit;
    private PackageEventDTO event;

    @BeforeEach
    void setUp() {
        lockerUnit = new LockerUnit();
        lockerUnit.setId(1L);
        lockerUnit.setOccupied(false);
        lockerUnit.setPackageId(null);
        lockerUnit.setAccessCode(null);

        HashSet<LockerUnit> lockerUnits = new HashSet<>();
        lockerUnits.add(lockerUnit);

        locker = new Locker();
        locker.setLockerUnits(lockerUnits);
        locker.setLocation("TestLocation");

        event = new PackageEventDTO();
        event.setLockerLocation("TestLocation");
        event.setLockerUnitId(1L);
        event.setPackageId(123L);
        event.setAccessCode("ABC123");
        event.setRecipientEmail("recipient@test.com");
    }

    @Test
    void testOccupyingLockerUnit_success() {
        when(lockerRepository.findByLocation(event.getLockerLocation())).thenReturn(Optional.of(locker));
        when(lockerUnitRepository.save(any(LockerUnit.class))).thenAnswer(inv -> inv.getArgument(0));

        lockerEventListener.occupyingLockerUnit(event);

        assertTrue(lockerUnit.isOccupied());
        assertEquals(event.getPackageId(), lockerUnit.getPackageId());
        assertEquals(event.getAccessCode(), lockerUnit.getAccessCode());

        verify(lockerUnitRepository).save(lockerUnit);
        verify(kafkaTemplate).send(eq("package-placed-access-code"), any(AccessCodeEventDTO.class));
    }

    @Test
    void testOccupyingLockerUnit_lockerNotFound() {
        when(lockerRepository.findByLocation(event.getLockerLocation())).thenReturn(Optional.empty());

        lockerEventListener.occupyingLockerUnit(event);

        verify(lockerUnitRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void testOccupyingLockerUnit_unitNotFound() {
        locker.getLockerUnits().clear(); // Simulate unit not present in locker

        when(lockerRepository.findByLocation(event.getLockerLocation())).thenReturn(Optional.of(locker));

        lockerEventListener.occupyingLockerUnit(event);

        verify(lockerUnitRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void testFreeingLockerUnit_success() {
        lockerUnit.setOccupied(true);
        lockerUnit.setPackageId(123L);
        lockerUnit.setAccessCode("ABC123");

        when(lockerRepository.findByLocation(event.getLockerLocation())).thenReturn(Optional.of(locker));
        when(lockerUnitRepository.save(any(LockerUnit.class))).thenAnswer(inv -> inv.getArgument(0));

        lockerEventListener.freeingLockerUnit(event);

        assertFalse(lockerUnit.isOccupied());
        verify(lockerUnitRepository).save(lockerUnit);
        verify(kafkaTemplate).send(eq("package-received-message"), any(AccessCodeEventDTO.class));
    }

    @Test
    void testFreeingLockerUnit_lockerNotFound() {
        when(lockerRepository.findByLocation("TestLocation")).thenReturn(Optional.empty());

        lockerEventListener.freeingLockerUnit(event);

        verify(lockerUnitRepository, never()).save(any());
        verify(kafkaTemplate, never()).send(any(), any());
    }

    @Test
    void testFreeingLockerUnit_lockerUnitNotFound() {
        locker.getLockerUnits().clear(); // No matching unit

        when(lockerRepository.findByLocation("TestLocation")).thenReturn(Optional.of(locker));

        lockerEventListener.freeingLockerUnit(event);

        verify(lockerUnitRepository, never()).save(any());
        verify(kafkaTemplate, never()).send((ProducerRecord<String, AccessCodeEventDTO>) any());
    }
}

