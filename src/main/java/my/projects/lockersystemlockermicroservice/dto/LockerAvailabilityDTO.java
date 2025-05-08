package my.projects.lockersystemlockermicroservice.dto;

import java.util.Set;

public class LockerAvailabilityDTO {

    private int count;
    private Set<Long> unitIds;

    public LockerAvailabilityDTO(int count, Set<Long> unitIds) {
        this.count = count;
        this.unitIds = unitIds;
    }

    public int getCount() {
        return count;
    }

    public Set<Long> getUnitIds() {
        return unitIds;
    }
}
