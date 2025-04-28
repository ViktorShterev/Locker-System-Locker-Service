package my.projects.lockersystemlockermicroservice.dto;

public class CreateLockerDTO {

    private String location;

    private int smallUnits;

    private int mediumUnits;

    private int largeUnits;

    public CreateLockerDTO(String location, int smallUnits, int mediumUnits, int largeUnits) {
        this.location = location;
        this.smallUnits = smallUnits;
        this.mediumUnits = mediumUnits;
        this.largeUnits = largeUnits;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSmallUnits() {
        return smallUnits;
    }

    public void setSmallUnits(int smallUnits) {
        this.smallUnits = smallUnits;
    }

    public int getMediumUnits() {
        return mediumUnits;
    }

    public void setMediumUnits(int mediumUnits) {
        this.mediumUnits = mediumUnits;
    }

    public int getLargeUnits() {
        return largeUnits;
    }

    public void setLargeUnits(int largeUnits) {
        this.largeUnits = largeUnits;
    }
}
