package my.projects.lockersystemlockermicroservice.service.impl;

import my.projects.lockersystemlockermicroservice.dto.CreateLockerDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerAvailabilityDTO;
import my.projects.lockersystemlockermicroservice.entity.Locker;
import my.projects.lockersystemlockermicroservice.entity.LockerUnit;
import my.projects.lockersystemlockermicroservice.enums.LockerStatusEnum;
import my.projects.lockersystemlockermicroservice.enums.LockerUnitSizeEnum;
import my.projects.lockersystemlockermicroservice.repository.LockerRepository;
import my.projects.lockersystemlockermicroservice.service.LockerService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LockerServiceImpl implements LockerService {

    private final LockerRepository lockerRepository;

    public LockerServiceImpl(LockerRepository lockerRepository) {
        this.lockerRepository = lockerRepository;
    }

    @Override
    public void createLocker(CreateLockerDTO lockerDTO) {

        Locker locker = new Locker();

        locker.setLocation(lockerDTO.getLocation());
        locker.setLockerUnits(createLockerUnits(lockerDTO));
        locker.setLockerStatus(LockerStatusEnum.AVAILABLE);

        this.lockerRepository.save(locker);
    }

    @Override
    public Map<String, LockerAvailabilityDTO> getAvailableLockers(String location) {
        Optional<Locker> optionalLocker = lockerRepository.findByLocation(location);

        Map<String, LockerAvailabilityDTO> result = new HashMap<>();

        for (LockerUnitSizeEnum size : LockerUnitSizeEnum.values()) {
            Set<Long> ids = optionalLocker
                    .map(locker -> locker.getLockerUnits().stream()
                            .filter(unit -> !unit.isOccupied() && unit.getLockerUnitSize().equals(size))
                            .map(LockerUnit::getId)
                            .collect(Collectors.toSet()))
                    .orElse(Collections.emptySet());

            result.put(size.name(), new LockerAvailabilityDTO(ids.size(), ids));
        }

        return result;
    }


    private Set<LockerUnit> createLockerUnits(CreateLockerDTO lockerDTO) {

        Set<LockerUnit> lockerUnits = new HashSet<>();

        lockerUnits.addAll(generateUnits(LockerUnitSizeEnum.SMALL, lockerDTO.getSmallUnits()));
        lockerUnits.addAll(generateUnits(LockerUnitSizeEnum.MEDIUM, lockerDTO.getMediumUnits()));
        lockerUnits.addAll(generateUnits(LockerUnitSizeEnum.LARGE, lockerDTO.getLargeUnits()));

        return lockerUnits;
    }

    private Set<LockerUnit> generateUnits(LockerUnitSizeEnum size, int count) {
        Set<LockerUnit> units = new HashSet<>();

        for (int i = 1; i <= count; i++) {
            LockerUnit lockerUnit = new LockerUnit(size);
            lockerUnit.setOccupied(false);
            units.add(lockerUnit);
        }
        return units;
    }
}
