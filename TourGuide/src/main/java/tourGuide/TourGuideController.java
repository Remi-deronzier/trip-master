package tourGuide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jsoniter.output.JsonStream;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tripPricer.Provider;

@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    @Autowired
    RewardsService rewardsService;

    @Autowired
    RewardCentral rewardCentral;

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return JsonStream.serialize(visitedLocation.location);
    }

    // Instead: Get the closest five tourist attractions to the user - no matter how
    // far away they are.
    // Return a new JSON object that contains:
    // Name of Tourist attraction,
    // Tourist attractions lat/long,
    // The user's location lat/long,
    // The distance in miles between the user's location and each of the
    // attractions.
    // The reward points for visiting each Attraction.
    // Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        User user = tourGuideService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        List<Attraction> closestFiveAttractions = tourGuideService.getNearByAttractions(visitedLocation);
        List<NearAttraction> res = new ArrayList<>();
        for (Attraction attraction : closestFiveAttractions) {
            final NearAttraction nearAttraction = new NearAttraction(attraction.attractionName, attraction,
                    visitedLocation.location, rewardsService.getDistance(attraction, visitedLocation.location),
                    rewardCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId()));
            res.add(nearAttraction);
        }
        return JsonStream.serialize(res);
    }

    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // - Note: does not use gpsUtil to query for their current location,
        // but rather gathers the user's current location from their stored location
        // history.
        //
        // Return object should be the just a JSON mapping of userId to Locations
        // similar to:
        // {
        // "019b04a9-067a-4c76-8817-ee75088c3822":
        // {"longitude":-48.188821,"latitude":74.84371}
        // ...
        // }
        List<User> users = tourGuideService.getAllUsers();
        Map<UUID, Location> locationsByUserId = new HashMap<>();
        for (User user : users) {
            locationsByUserId.put(user.getUserId(), tourGuideService.getUserLocation(user).location);
        }

        return JsonStream.serialize(locationsByUserId);
    }

    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(getUser(userName));
        return JsonStream.serialize(providers);
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }

}