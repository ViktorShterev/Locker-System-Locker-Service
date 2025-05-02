package my.projects.lockersystemlockermicroservice.service;

import my.projects.lockersystemlockermicroservice.dto.CreateLockerDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerAvailabilityDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerLocationDTO;

import java.util.List;
import java.util.Map;

public interface LockerService {

    boolean createLocker(CreateLockerDTO lockerDTO);

    List<LockerLocationDTO> getLockerLocations();

    Map<String, LockerAvailabilityDTO> getAvailableLockers(String location);
}
