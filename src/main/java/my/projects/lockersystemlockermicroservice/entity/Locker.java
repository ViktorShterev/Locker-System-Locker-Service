package my.projects.lockersystemlockermicroservice.entity;

import jakarta.persistence.*;
import my.projects.lockersystemlockermicroservice.enums.LockerStatusEnum;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lockers")
public class Locker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String location;

    @Column(nullable = false, name = "locker_status")
    @Enumerated(EnumType.STRING)
    private LockerStatusEnum lockerStatus;

    @OneToMany(mappedBy = "locker")
    private Set<LockerUnit> lockerUnits;

    public Locker(String location, Set<LockerUnit> lockerUnits) {
        this.location = location;
        this.lockerUnits = lockerUnits;
    }

    public Locker() {
        this.lockerUnits = new HashSet<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LockerStatusEnum getLockerStatus() {
        return lockerStatus;
    }

    public void setLockerStatus(LockerStatusEnum lockerStatus) {
        this.lockerStatus = lockerStatus;
    }

    public Set<LockerUnit> getLockerUnits() {
        return lockerUnits;
    }

    public void setLockerUnits(Set<LockerUnit> lockerUnits) {
        this.lockerUnits = lockerUnits;
    }
}
