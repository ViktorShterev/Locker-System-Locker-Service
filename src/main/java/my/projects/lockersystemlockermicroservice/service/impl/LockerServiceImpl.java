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
import my.projects.lockersystemlockermicroservice.service.LockerService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LockerServiceImpl implements LockerService {

    private final LockerRepository lockerRepository;
    private final LockerUnitRepository lockerUnitRepository;

    public LockerServiceImpl(LockerRepository lockerRepository, LockerUnitRepository lockerUnitRepository) {
        this.lockerRepository = lockerRepository;
        this.lockerUnitRepository = lockerUnitRepository;
    }

    @Override
    public boolean createLocker(CreateLockerDTO lockerDTO) {

        if (this.lockerRepository.findByLocation(lockerDTO.getLocation()).isEmpty()) {

            Locker locker = new Locker();

            locker.setLocation(lockerDTO.getLocation());
            locker.setLockerStatus(LockerStatusEnum.AVAILABLE);

            Locker saved = this.lockerRepository.save(locker);

            saved.setLockerUnits(createLockerUnits(lockerDTO, saved));

            this.lockerRepository.save(saved);

            return true;
        }

        return false;
    }

    @Override
    public List<LockerLocationDTO> getLockerLocations() {

        return this.lockerRepository.findAll().stream()
                .map(locker -> new LockerLocationDTO(locker.getLocation()))
                .toList();
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


    private Set<LockerUnit> createLockerUnits(CreateLockerDTO lockerDTO, Locker locker) {

        Set<LockerUnit> lockerUnits = new HashSet<>();

        lockerUnits.addAll(generateUnits(LockerUnitSizeEnum.SMALL, lockerDTO.getSmallUnits(), locker));
        lockerUnits.addAll(generateUnits(LockerUnitSizeEnum.MEDIUM, lockerDTO.getMediumUnits(), locker));
        lockerUnits.addAll(generateUnits(LockerUnitSizeEnum.LARGE, lockerDTO.getLargeUnits(), locker));

        return lockerUnits;
    }

    private Set<LockerUnit> generateUnits(LockerUnitSizeEnum size, int count, Locker locker) {
        Set<LockerUnit> units = new HashSet<>();

        for (int i = 1; i <= count; i++) {
            LockerUnit lockerUnit = new LockerUnit(size);
            lockerUnit.setOccupied(false);
            lockerUnit.setLocker(locker);

            lockerUnit = this.lockerUnitRepository.save(lockerUnit);

            units.add(lockerUnit);
        }

        return units;
    }
}
