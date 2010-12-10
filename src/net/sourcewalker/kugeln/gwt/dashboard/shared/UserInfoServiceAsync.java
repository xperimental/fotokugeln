package net.sourcewalker.kugeln.gwt.dashboard.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UserInfoServiceAsync {

    public void getCurrentUser(AsyncCallback<UserInfo> callback);

    public void getLoginUrl(String redirectPage, AsyncCallback<String> callback);

    public void getLogoutUrl(String redirectPage, AsyncCallback<String> callback);

}
