package tourGuide.service;

import java.util.List;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;
import tripPricer.Provider;

public interface TourGuideService {
    public List<UserReward> getUserRewards(User user);

    public VisitedLocation getUserLocation(User user);

    public User getUser(String userName);

    public List<User> getAllUsers();

    public void addUser(User user);

    public List<Provider> getTripDeals(User user);

    public VisitedLocation trackUserLocation(User user);

    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation);

    public List<VisitedLocation> trackAllUsersLocations(List<User> users);
}
