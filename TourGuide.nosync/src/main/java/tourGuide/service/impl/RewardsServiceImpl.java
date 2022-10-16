package tourGuide.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	// private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	ExecutorService service = Executors.newWorkStealingPool();
	List<Attraction> attractions;

	public RewardsServiceImpl(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		// this.gpsUtil = gpsUtil;
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

	public void calculateRewardsAllUsers(List<User> users) {

		// List<Attraction> attractions = gpsUtil.getAttractions();

		final List<CompletableFuture<Void>> futureUserRewards = new ArrayList<>();
		// final List<CompletableFuture<RewardCalculation>> futureUserRewards = new
		// ArrayList<>();

		for (User user : users) {
			List<VisitedLocation> userLocations = user.getVisitedLocations();
			for (VisitedLocation visitedLocation : userLocations) {
				for (Attraction attraction : attractions) {
					if (user.getUserRewards().stream()
							.filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
						if (nearAttraction(visitedLocation, attraction)) {
							final CompletableFuture<Void> futureUserReward = CompletableFuture.supplyAsync(
									() -> getRewardPoints(attraction, user), service)
									.thenAcceptAsync(rewardsPoint -> user
											.addUserReward(new UserReward(visitedLocation, attraction, rewardsPoint))
									// new RewardCalculation(user,
									// new UserReward(visitedLocation, attraction, rewardsPoint))
									);

							// final CompletableFuture<RewardCalculation> futureUserReward =
							// CompletableFuture.supplyAsync(
							// () -> getRewardPoints(attraction, user))
							// .thenApplyAsync(rewardsPoint -> new RewardCalculation(user,
							// new UserReward(visitedLocation, attraction, rewardsPoint)));

							// final CompletableFuture<RewardCalculation> futureUserReward =
							// CompletableFuture.supplyAsync(
							// () -> {
							// logger.debug("coucou");
							// return new RewardCalculation(user, new UserReward(visitedLocation,
							// attraction,
							// getRewardPoints(attraction, user)));
							// });
							// getRewardPoints(attraction,
							// user))));
							// final Future<UserReward> futureUserReward = service.submit(
							// () -> new UserReward(visitedLocation, attraction,
							// getRewardPoints(attraction,
							// user)));
							futureUserRewards.add(futureUserReward);
						}
					}
				}
			}
		}

		CompletableFuture
				.allOf(futureUserRewards.toArray(new CompletableFuture[futureUserRewards.size()])).join();
		// CompletableFuture<Void> combinedFuture = CompletableFuture
		// .allOf(futureUserRewards.toArray(new
		// CompletableFuture[futureUserRewards.size()]));

		// try {
		// combinedFuture.get();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// e.printStackTrace();
		// }

		// for (CompletableFuture<Void> futureUserReward : futureUserRewards) {
		// try {
		// futureUserReward.get();
		// // User user = rewardCalculation.getUser();
		// // UserReward userReward = rewardCalculation.getUserReward();
		// // user.addUserReward(userReward);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// } catch (ExecutionException e) {
		// e.printStackTrace();
		// }
		// }

	}

	// public void calculateRewardsAllUsers(List<User> users) {
	// // for (User user : users) {
	// // service.execute(() -> calculateRewards(user));
	// // }
	// List<CompletableFuture<Void>> futures = new ArrayList<>();
	// for (User user : users) {
	// CompletableFuture<Void> future = CompletableFuture.runAsync(() ->
	// calculateRewards(user));
	// futures.add(future);
	// }
	// // CompletableFuture
	// // .allOf(futures.toArray(new CompletableFuture[futures.size()]));
	// CompletableFuture<Void> combinedFuture = CompletableFuture
	// .allOf(futures.toArray(new CompletableFuture[futures.size()]));
	// try {
	// combinedFuture.get();
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// } catch (ExecutionException e) {
	// e.printStackTrace();
	// }
	// }

	// @Override
	// public void calculateRewards(User user) {
	// List<VisitedLocation> userLocations = user.getVisitedLocations();
	// // List<Attraction> attractions = gpsUtil.getAttractions();

	// for (VisitedLocation visitedLocation : userLocations) {
	// for (Attraction attraction : attractions) {
	// if (user.getUserRewards().stream()
	// .filter(r ->
	// r.attraction.attractionName.equals(attraction.attractionName)).count() == 0)
	// {
	// if (nearAttraction(visitedLocation, attraction)) {
	// user.addUserReward(
	// new UserReward(visitedLocation, attraction, getRewardPoints(attraction,
	// user)));
	// }
	// }
	// }
	// }
	// }

	@Override
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		// List<Attraction> attractions = gpsUtil.getAttractions();

		final List<CompletableFuture<UserReward>> futureUserRewards = new ArrayList<>();

		for (VisitedLocation visitedLocation : userLocations) {
			for (Attraction attraction : attractions) {
				if (user.getUserRewards().stream()
						.filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if (nearAttraction(visitedLocation, attraction)) {
						final CompletableFuture<UserReward> futureUserReward = CompletableFuture.supplyAsync(
								() -> {
									logger.debug("coucou");
									return new UserReward(visitedLocation, attraction,
											getRewardPoints(attraction,
													user));
								});
						// final Future<UserReward> futureUserReward = service.submit(
						// () -> new UserReward(visitedLocation, attraction,
						// getRewardPoints(attraction,
						// user)));
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

	// public void shutdownService() {
	// service.shutdown();
	// }

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
