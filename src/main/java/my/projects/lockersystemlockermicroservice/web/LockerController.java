package my.projects.lockersystemlockermicroservice.web;

import my.projects.lockersystemlockermicroservice.dto.CreateLockerDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerAvailabilityDTO;
import my.projects.lockersystemlockermicroservice.service.LockerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class LockerController {

    private final LockerService lockerService;

    public LockerController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @PostMapping("/create-locker")
    public ResponseEntity<Void> createLocker(@RequestBody CreateLockerDTO createLockerDTO) {
        this.lockerService.createLocker(createLockerDTO);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/lockers/availability")
    public ResponseEntity<Map<String, LockerAvailabilityDTO>> getAvailableLockers(@RequestParam String location) {

        Map<String, LockerAvailabilityDTO> availability = this.lockerService.getAvailableLockers(location);
        return ResponseEntity.ok(availability);
    }


}
