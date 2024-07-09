package com.dony.common.passport;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserContext {
    private static final ThreadLocal<UserContext> userContext = new ThreadLocal<>();

    private Integer userId;
    private String username;
    private String email;
    private String address;
    private String phone;

    public static UserContext getCurrentUser() {
        return userContext.get();
    }

    public static void setCurrentUser(UserContext context) {
        userContext.set(context);
    }

    public static void clear() {
        userContext.remove();
    }
}
