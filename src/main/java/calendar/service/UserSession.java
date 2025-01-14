package calendar.service;

import calendar.model.User;

public class UserSession {
    private static UserSession instance;

    private static User user;

    private UserSession(User user) {
        this.user = user;
    }

    public static UserSession getInstance(User user) {
        if(instance == null) {
            instance = new UserSession(user);
        }
        else{
            instance = new UserSession(user);
        }
        return instance;
    }

    public static User getLoggedInUser() {
        return user;
    }

    public static void cleanUserSession() {
        user = null;// or nullify all attributes as you please
    }
}
