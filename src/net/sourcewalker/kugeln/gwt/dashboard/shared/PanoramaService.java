package net.sourcewalker.kugeln.gwt.dashboard.shared;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("panoramas")
public interface PanoramaService extends RemoteService {

    PanoramaEntry[] getUserPanoramas();

    boolean removePanorama(String key);

}
