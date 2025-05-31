package my.projects.lockersystemlockermicroservice.service.impl;

import my.projects.lockersystemlockermicroservice.dto.CreateLockerDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerAvailabilityDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerLocationDTO;
import my.projects.lockersystemlockermicroservice.entity.Locker;
import my.projects.lockersystemlockermicroservice.entity.LockerUnit;
import my.projects.lockersystemlockermicroservice.enums.LockerStatusEnum;
import my.projects.lockersystemlockermicroservice.enums.LockerUnitSizeEnum;
import my.projects.lockersystemlockermicroservice.repository.LockerRepository;
import my.projects.lockersystemlockermicroservice.repository.LockerUnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LockerServiceImplTest {

    @Mock
    private LockerRepository lockerRepository;

    @Mock
    private LockerUnitRepository lockerUnitRepository;

    @InjectMocks
    private LockerServiceImpl lockerService;

    @Test
    void createLocker_shouldCreateNewLocker_whenLocationNotExists() {
        CreateLockerDTO dto = new CreateLockerDTO("Central", 2, 1, 1);

        when(lockerRepository.findByLocation(dto.getLocation())).thenReturn(Optional.empty());

        Locker savedLocker = new Locker();
        savedLocker.setLocation(dto.getLocation());
        savedLocker.setLockerStatus(LockerStatusEnum.AVAILABLE);
        savedLocker.setLockerUnits(new HashSet<>());

        when(lockerRepository.save(any(Locker.class))).thenReturn(savedLocker);
        when(lockerUnitRepository.save(any(LockerUnit.class))).thenAnswer(inv -> inv.getArgument(0));

        boolean result = lockerService.createLocker(dto);

        assertTrue(result);
        verify(lockerRepository, times(2)).save(any(Locker.class));
        verify(lockerUnitRepository, times(4)).save(any(LockerUnit.class)); // 2 small, 1 medium, 1 large
    }

    @Test
    void createLocker_shouldReturnFalse_whenLockerAlreadyExists() {
        CreateLockerDTO dto = new CreateLockerDTO("Existing", 1, 1, 1);
        when(lockerRepository.findByLocation("Existing")).thenReturn(Optional.of(new Locker()));

        boolean result = lockerService.createLocker(dto);

        assertFalse(result);
        verify(lockerRepository, never()).save(any());
        verify(lockerUnitRepository, never()).save(any());
    }

    @Test
    void getLockerLocations_shouldReturnAllLocations() {
        List<Locker> lockers = List.of(
                new Locker("Central", new HashSet<>()),
                new Locker("Not Central", new HashSet<>())
        );

        when(lockerRepository.findAll()).thenReturn(lockers);

        List<LockerLocationDTO> result = lockerService.getLockerLocations();

        assertEquals(2, result.size());
        assertEquals("Central", result.get(0).getLocation());
    }

    @Test
    void getAvailableLockers_shouldReturnCorrectAvailability() {
        Locker locker = getLocker();

        when(lockerRepository.findByLocation("Central")).thenReturn(Optional.of(locker));

        Map<String, LockerAvailabilityDTO> result = lockerService.getAvailableLockers("Central");

        assertEquals(3, result.size());
        assertEquals(1, result.get("SMALL").getCount());
        assertTrue(result.get("SMALL").getUnitIds().contains(1L));
        assertEquals(1, result.get("MEDIUM").getCount());
        assertTrue(result.get("MEDIUM").getUnitIds().contains(3L));
        assertEquals(0, result.get("LARGE").getCount());
    }

    private static Locker getLocker() {
        Locker locker = new Locker();
        locker.setLocation("Central");

        LockerUnit unit1 = new LockerUnit(LockerUnitSizeEnum.SMALL);
        unit1.setId(1L);
        unit1.setOccupied(false);

        LockerUnit unit2 = new LockerUnit(LockerUnitSizeEnum.SMALL);
        unit2.setId(2L);
        unit2.setOccupied(true); // should be filtered

        LockerUnit unit3 = new LockerUnit(LockerUnitSizeEnum.MEDIUM);
        unit3.setId(3L);
        unit3.setOccupied(false);

        locker.setLockerUnits(Set.of(unit1, unit2, unit3));
        return locker;
    }
}