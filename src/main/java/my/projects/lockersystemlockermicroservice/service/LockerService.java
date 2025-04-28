package my.projects.lockersystemlockermicroservice.service;

import my.projects.lockersystemlockermicroservice.dto.CreateLockerDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerAvailabilityDTO;

import java.util.Map;

public interface LockerService {

    void createLocker(CreateLockerDTO lockerDTO);

    Map<String, LockerAvailabilityDTO> getAvailableLockers(String location);
}
