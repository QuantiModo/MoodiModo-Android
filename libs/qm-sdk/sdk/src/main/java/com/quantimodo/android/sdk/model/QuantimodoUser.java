package com.quantimodo.android.sdk.model;

public class QuantimodoUser {

    @Deprecated
    public String displayName;
    @Deprecated
    public String loginName;
    @Deprecated
    public String email;
    @Deprecated
    public String token;
    @Deprecated
    public boolean isAdministrator;
    public int id;

    // Do not use this constructor
    public QuantimodoUser() {
    }

    public QuantimodoUser(String displayName, String loginName, String email, String token, boolean isAdministrator) {
        this.displayName = displayName;
        this.loginName = loginName;
        this.email = email;
        this.token = token;
        this.isAdministrator = isAdministrator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isAdministrator() {
        return isAdministrator;
    }

    public void setIsAdministrator(boolean isAdministrator) {
        this.isAdministrator = isAdministrator;
    }

    public int getId() {
        return id;
    }
}
