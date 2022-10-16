package tourGuide.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;
import tourGuide.service.RewardsService;

@Service
public class RewardsServiceImpl implements RewardsService {
	private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	private Logger logger = LoggerFactory.getLogger(TourGuideServiceImpl.class);

	// proximity in miles
	private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final RewardCentral rewardsCentral;
	List<Attraction> attractions;

	public RewardsServiceImpl(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.rewardsCentral = rewardCentral;
		attractions = gpsUtil.getAttractions();
	}

	@Override
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	@Override
	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}

	@Override
	public void calculateRewardsAllUsers(List<User> users) {
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		for (User user : users) {
			CompletableFuture<Void> future = CompletableFuture.runAsync(() -> calculateRewards(user));
			futures.add(future);
		}
		CompletableFuture
				.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
	}

	@Override
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();

		final List<CompletableFuture<UserReward>> futureUserRewards = new ArrayList<>();

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream()
						.filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if (nearAttraction(visitedLocation, attraction)) {
						final CompletableFuture<UserReward> futureUserReward = CompletableFuture.supplyAsync(
								() -> {
									logger.debug("Calculate reward for user {}", user.getUserName());
									return new UserReward(visitedLocation, attraction,
											getRewardPoints(attraction,
													user));
								});
						futureUserRewards.add(futureUserReward);
					}
				}
			}
		}

		for (CompletableFuture<UserReward> futureUserReward : futureUserRewards) {
			try {
				user.addUserReward(futureUserReward.get());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}

	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	@Override
	public double getDistance(Location loc1, Location loc2) {
		double lat1 = Math.toRadians(loc1.latitude);
		double lon1 = Math.toRadians(loc1.longitude);
		double lat2 = Math.toRadians(loc2.latitude);
		double lon2 = Math.toRadians(loc2.longitude);

		double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
				+ Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

		double nauticalMiles = 60 * Math.toDegrees(angle);
		double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
		return statuteMiles;
	}

}
