package tourGuide.model;

import tourGuide.model.user.User;
import tourGuide.model.user.UserReward;

public class RewardCalculation {
    private User user;
    private UserReward userReward;

    public RewardCalculation(User user, UserReward userReward) {
        this.user = user;
        this.userReward = userReward;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserReward getUserReward() {
        return userReward;
    }

    public void setUserReward(UserReward userReward) {
        this.userReward = userReward;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        result = prime * result + ((userReward == null) ? 0 : userReward.hashCode());
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
        RewardCalculation other = (RewardCalculation) obj;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (userReward == null) {
            if (other.userReward != null)
                return false;
        } else if (!userReward.equals(other.userReward))
            return false;
        return true;
    }

}
