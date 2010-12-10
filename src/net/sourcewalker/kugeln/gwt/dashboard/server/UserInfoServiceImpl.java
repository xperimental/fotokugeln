package net.sourcewalker.kugeln.gwt.dashboard.server;

import net.sourcewalker.kugeln.gwt.dashboard.shared.UserInfo;
import net.sourcewalker.kugeln.gwt.dashboard.shared.UserInfoService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class UserInfoServiceImpl extends RemoteServiceServlet implements
        UserInfoService {

    private UserService userService = UserServiceFactory.getUserService();

    @Override
    public UserInfo getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return null;
        } else {
            return createInfoObject(currentUser);
        }
    }

    private UserInfo createInfoObject(User user) {
        UserInfo result = new UserInfo();
        result.setAuthDomain(user.getAuthDomain());
        result.setEmail(user.getEmail());
        result.setFederatedIdentity(user.getFederatedIdentity());
        result.setNickname(user.getNickname());
        result.setUserId(user.getUserId());
        return result;
    }

    @Override
    public String getLoginUrl(String redirectPage) {
        return userService.createLoginURL(redirectPage);
    }

    @Override
    public String getLogoutUrl(String redirectPage) {
        return userService.createLogoutURL(redirectPage);
    }

}
