package net.sourcewalker.kugeln.gwt.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("userInfo")
public interface UserInfoService extends RemoteService {

    public UserInfo getCurrentUser();

    public String getLoginUrl(String redirectPage);

    public String getLogoutUrl(String redirectPage);

}
