package my.projects.lockersystemlockermicroservice.service.impl;

import my.projects.lockersystemlockermicroservice.dto.kafka.AccessCodeEventDTO;
import my.projects.lockersystemlockermicroservice.dto.kafka.PackageEventDTO;
import my.projects.lockersystemlockermicroservice.entity.Locker;
import my.projects.lockersystemlockermicroservice.entity.LockerUnit;
import my.projects.lockersystemlockermicroservice.repository.LockerRepository;
import my.projects.lockersystemlockermicroservice.repository.LockerUnitRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LockerEventListener {

    private final LockerRepository lockerRepository;
    private final LockerUnitRepository lockerUnitRepository;
    private final KafkaTemplate<String, AccessCodeEventDTO> kafkaTemplate;

    public LockerEventListener(LockerRepository lockerRepository, LockerUnitRepository lockerUnitRepository, KafkaTemplate<String, AccessCodeEventDTO> kafkaTemplate) {
        this.lockerRepository = lockerRepository;
        this.lockerUnitRepository = lockerUnitRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "package-placed", groupId = "locker-service-group")
    public void occupyingLockerUnit(PackageEventDTO event) {
        System.out.println("Received event: " + event);

        Optional<Locker> optionalLocker = this.lockerRepository.findByLocation(event.getLockerLocation());
        if (optionalLocker.isPresent()) {
            Locker locker = optionalLocker.get();

            LockerUnit unit = findLockerUnit(event, locker);

            if (unit != null) {
                unit.setOccupied(true);
                unit.setPackageId(event.getPackageId());
                unit.setAccessCode(event.getAccessCode());

                this.lockerUnitRepository.save(unit);

                AccessCodeEventDTO accessCodeEventDTO = new AccessCodeEventDTO(
                        unit.getAccessCode(), unit.getPackageId(), event.getRecipientEmail(), event.getLockerLocation());

                this.kafkaTemplate.send("package-placed-access-code", accessCodeEventDTO);
            }
        }
    }

    @KafkaListener(topics = "package-received", groupId = "locker-service-group")
    public void freeingLockerUnit(PackageEventDTO event) {
        System.out.println("Received event: " + event);

        Optional<Locker> optionalLocker = this.lockerRepository.findByLocation(event.getLockerLocation());
        if (optionalLocker.isPresent()) {
            Locker locker = optionalLocker.get();

            LockerUnit lockerUnit = findLockerUnit(event, locker);

            if (lockerUnit != null && lockerUnit.getAccessCode().equals(event.getAccessCode())
            && lockerUnit.getPackageId().equals(event.getPackageId())) {

                lockerUnit.setOccupied(false);
                this.lockerUnitRepository.save(lockerUnit);

                AccessCodeEventDTO accessCodeEventDTO = new AccessCodeEventDTO(
                        lockerUnit.getAccessCode(), lockerUnit.getPackageId(), event.getRecipientEmail(), event.getLockerLocation());

                this.kafkaTemplate.send("package-received-message", accessCodeEventDTO);
            }
        }
    }

    private static LockerUnit findLockerUnit(PackageEventDTO event, Locker locker) {
        return locker.getLockerUnits().stream()
                .filter(lockerUnit -> lockerUnit.getId().equals(event.getLockerUnitId()))
                .findFirst()
                .orElse(null);
    }
}

