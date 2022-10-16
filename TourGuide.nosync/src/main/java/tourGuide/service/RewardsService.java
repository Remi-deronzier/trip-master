package tourGuide.service;

import java.util.List;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import tourGuide.model.user.User;

public interface RewardsService {
    public void calculateRewards(User user);

    public boolean isWithinAttractionProximity(Attraction attraction, Location location);

    public double getDistance(Location loc1, Location loc2);

    public void setDefaultProximityBuffer();

    public void setProximityBuffer(int proximityBuffer);

    public void calculateRewardsAllUsers(List<User> users);
}
