package tourGuide;

import gpsUtil.location.Location;

public class NearAttraction {
    private String name;
    private Location attractionLocation;
    private Location userLocation;
    private double distanceUserAttraction;
    private int rewardPoints;

    public NearAttraction(String name, Location attractionLocation, Location userLocation,
            double distanceUserAttraction, int rewardPoints) {
        this.name = name;
        this.attractionLocation = attractionLocation;
        this.userLocation = userLocation;
        this.distanceUserAttraction = distanceUserAttraction;
        this.rewardPoints = rewardPoints;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((attractionLocation == null) ? 0 : attractionLocation.hashCode());
        result = prime * result + ((userLocation == null) ? 0 : userLocation.hashCode());
        long temp;
        temp = Double.doubleToLongBits(distanceUserAttraction);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + rewardPoints;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NearAttraction other = (NearAttraction) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (attractionLocation == null) {
            if (other.attractionLocation != null)
                return false;
        } else if (!attractionLocation.equals(other.attractionLocation))
            return false;
        if (userLocation == null) {
            if (other.userLocation != null)
                return false;
        } else if (!userLocation.equals(other.userLocation))
            return false;
        if (Double.doubleToLongBits(distanceUserAttraction) != Double.doubleToLongBits(other.distanceUserAttraction))
            return false;
        if (rewardPoints != other.rewardPoints)
            return false;
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getAttractionLocation() {
        return attractionLocation;
    }

    public void setAttractionLocation(Location attractionLocation) {
        this.attractionLocation = attractionLocation;
    }

    public Location getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(Location userLocation) {
        this.userLocation = userLocation;
    }

    public double getDistanceUserAttraction() {
        return distanceUserAttraction;
    }

    public void setDistanceUserAttraction(double distanceUserAttraction) {
        this.distanceUserAttraction = distanceUserAttraction;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
