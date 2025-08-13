package rustam.fadeev.sky_movies_api.security;

import rustam.fadeev.sky_movies_api.models.UserPrivateModel;

public class UserContext {
    private static final ThreadLocal<UserPrivateModel> currentUser = new ThreadLocal<>();

    public static void set(UserPrivateModel user) {
        currentUser.set(user);
    }

    public static UserPrivateModel get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}