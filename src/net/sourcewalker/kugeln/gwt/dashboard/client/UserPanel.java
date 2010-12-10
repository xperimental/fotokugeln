package net.sourcewalker.kugeln.gwt.dashboard.client;

import net.sourcewalker.kugeln.gwt.dashboard.shared.UserInfo;
import net.sourcewalker.kugeln.gwt.dashboard.shared.UserInfoService;
import net.sourcewalker.kugeln.gwt.dashboard.shared.UserInfoServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class UserPanel implements EntryPoint {

    private Label currentUserDisplay = new Label();
    private Button loginButton = new Button("Login");
    private Button logoutButton = new Button("Logout");
    private UserInfoServiceAsync userService = GWT
            .create(UserInfoService.class);

    @Override
    public void onModuleLoad() {
        loginButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                login();
            }
        });
        logoutButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                logout();
            }
        });
        userService.getCurrentUser(new AsyncCallback<UserInfo>() {

            @Override
            public void onSuccess(UserInfo result) {
                boolean loggedIn = result != null;
                currentUserDisplay
                        .setText(loggedIn ? result.getNickname() : "");
                loginButton.setVisible(!loggedIn);
                logoutButton.setVisible(loggedIn);
            }

            @Override
            public void onFailure(Throwable caught) {
                currentUserDisplay.setText("ERROR GETTING USER!");
                loginButton.setVisible(true);
                logoutButton.setVisible(false);
            }
        });

        HorizontalPanel userPanel = new HorizontalPanel();
        userPanel.addStyleName("userPanel");
        userPanel.add(loginButton);
        userPanel.add(currentUserDisplay);
        userPanel.add(logoutButton);
        RootPanel.get("userPanel").add(userPanel);
    }

    protected void login() {
        userService.getLoginUrl(Location.getHref(),
                new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Location.replace(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    protected void logout() {
        userService.getLogoutUrl(Location.getHref(),
                new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        Location.replace(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        // TODO Auto-generated method stub

                    }
                });
    }

}
