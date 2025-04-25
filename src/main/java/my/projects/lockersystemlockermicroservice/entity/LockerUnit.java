package my.projects.lockersystemlockermicroservice.entity;

import jakarta.persistence.*;
import my.projects.lockersystemlockermicroservice.enums.LockerUnitSizeEnum;

@Entity
@Table(name = "locker_units")
public class LockerUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "locker_unit_size")
    @Enumerated(EnumType.STRING)
    private LockerUnitSizeEnum lockerUnitSize;

    @Column
    private boolean occupied;

    @Column(unique = true, name = "access_code")
    private String accessCode;

    @ManyToOne(fetch = FetchType.LAZY)
    private Locker locker;

    public LockerUnit(LockerUnitSizeEnum lockerUnitSize) {
        this.lockerUnitSize = lockerUnitSize;
    }

    public LockerUnit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LockerUnitSizeEnum getLockerUnitSize() {
        return lockerUnitSize;
    }

    public void setLockerUnitSize(LockerUnitSizeEnum lockerUnitSize) {
        this.lockerUnitSize = lockerUnitSize;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public Locker getLocker() {
        return locker;
    }

    public void setLocker(Locker locker) {
        this.locker = locker;
    }
}
