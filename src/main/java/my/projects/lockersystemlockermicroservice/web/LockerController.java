package my.projects.lockersystemlockermicroservice.web;

import my.projects.lockersystemlockermicroservice.dto.CreateLockerDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerAvailabilityDTO;
import my.projects.lockersystemlockermicroservice.dto.LockerLocationDTO;
import my.projects.lockersystemlockermicroservice.service.LockerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lockers")
public class LockerController {

    private final LockerService lockerService;

    public LockerController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @PostMapping("/create-locker")
    public ResponseEntity<Boolean> createLocker(@RequestBody CreateLockerDTO createLockerDTO,
                                                @AuthenticationPrincipal User user) {

        boolean isAdmin = user != null && user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean success = isAdmin && lockerService.createLocker(createLockerDTO);

        return ResponseEntity.ok(success);
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LockerLocationDTO>> getLocations() {
        return ResponseEntity.ok(this.lockerService.getLockerLocations());
    }

    @GetMapping("/availability")
    public ResponseEntity<Map<String, LockerAvailabilityDTO>> getAvailableLockers(@RequestParam String location) {

        Map<String, LockerAvailabilityDTO> availability = this.lockerService.getAvailableLockers(location);
        return ResponseEntity.ok(availability);
    }
}
