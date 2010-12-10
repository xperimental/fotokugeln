package net.sourcewalker.kugeln.gwt.dashboard.shared;

import java.io.Serializable;

public class UserInfo implements Serializable {

    private static final long serialVersionUID = -4895477436191811696L;

    private String authDomain;
    private String email;
    private String federatedIdentity;
    private String nickname;
    private String userId;

    public String getAuthDomain() {
        return authDomain;
    }

    public void setAuthDomain(String authDomain) {
        this.authDomain = authDomain;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFederatedIdentity() {
        return federatedIdentity;
    }

    public void setFederatedIdentity(String federatedIdentity) {
        this.federatedIdentity = federatedIdentity;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
