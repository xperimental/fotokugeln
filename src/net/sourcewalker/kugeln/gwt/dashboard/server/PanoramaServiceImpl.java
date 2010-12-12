package net.sourcewalker.kugeln.gwt.dashboard.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;

import net.sourcewalker.kugeln.Panorama;
import net.sourcewalker.kugeln.PersistenceManagerFactory;
import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaEntry;
import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaService;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class PanoramaServiceImpl extends RemoteServiceServlet implements
        PanoramaService {

    private UserService userService;
    private BlobstoreService blobStore;

    @Override
    public void init() throws ServletException {
        super.init();

        userService = UserServiceFactory.getUserService();
        blobStore = BlobstoreServiceFactory.getBlobstoreService();
    }

    @SuppressWarnings("unchecked")
    @Override
    public PanoramaEntry[] getUserPanoramas() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            return new PanoramaEntry[0];
        } else {
            PersistenceManager pm = PersistenceManagerFactory.get();
            try {
                Query query = pm.newQuery(Panorama.class);
                query.setFilter("owner == ownerParam");
                query.declareParameters("String ownerParam");
                List<Panorama> resultList = (List<Panorama>) query
                        .execute(currentUser.getUserId());
                PanoramaEntry[] result = new PanoramaEntry[resultList.size()];
                for (int i = 0; i < result.length; i++) {
                    Panorama source = resultList.get(i);
                    result[i] = convertEntry(source);
                }
                return result;
            } finally {
                pm.close();
            }
        }
    }

    private PanoramaEntry convertEntry(Panorama source) {
        PanoramaEntry result = new PanoramaEntry();
        result.setKey(KeyFactory.keyToString(source.getKey()));
        result.setName(source.getTitle());
        if (source.getStatusText() != null
                && source.getStatusText().length() > 0) {
            result.setStatus(source.getStatus().toString() + " ("
                    + source.getStatusText() + ")");
        } else {
            result.setStatus(source.getStatus().toString());
        }
        result.setThumbnail(source.hasThumbnail());
        return result;
    }

    @Override
    public boolean removePanorama(String key) {
        PersistenceManager pm = PersistenceManagerFactory.get();
        try {
            Panorama panorama = pm.getObjectById(Panorama.class, KeyFactory
                    .stringToKey(key));
            blobStore.delete(new BlobKey(panorama.getRawBlob()));
            pm.deletePersistent(panorama);
            return true;
        } catch (Exception e) {
        } finally {
            pm.close();
        }
        return false;
    }

}
