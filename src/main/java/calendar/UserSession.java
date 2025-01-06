package calendar;

public class UserSession {
    private static UserSession instance;

    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getInstance(User user) {
        if(instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }

    public User getLoggedInUser() {
        return user;
    }

    public void cleanUserSession() {
        user = null;// or nullify all attributes as you please
    }
}
