package tourGuide.model;

import gpsUtil.location.VisitedLocation;
import tourGuide.model.user.User;

public class UserLocation {
    private VisitedLocation visitedLocation;
    private User user;

    public UserLocation(VisitedLocation visitedLocation, User user) {
        this.visitedLocation = visitedLocation;
        this.user = user;
    }

    public VisitedLocation getVisitedLocation() {
        return visitedLocation;
    }

    public void setVisitedLocation(VisitedLocation visitedLocation) {
        this.visitedLocation = visitedLocation;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((visitedLocation == null) ? 0 : visitedLocation.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        UserLocation other = (UserLocation) obj;
        if (visitedLocation == null) {
            if (other.visitedLocation != null)
                return false;
        } else if (!visitedLocation.equals(other.visitedLocation))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}
