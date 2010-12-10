package net.sourcewalker.kugeln.gwt.dashboard.shared;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface PanoramaServiceAsync {

    public void getUserPanoramas(AsyncCallback<PanoramaEntry[]> callback);

    public void removePanorama(String key, AsyncCallback<Boolean> callback);

}
