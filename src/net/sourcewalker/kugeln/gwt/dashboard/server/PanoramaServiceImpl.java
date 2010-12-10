package net.sourcewalker.kugeln.gwt.dashboard.server;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;

import net.sourcewalker.kugeln.Panorama;
import net.sourcewalker.kugeln.PersistenceManagerFactory;
import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaEntry;
import net.sourcewalker.kugeln.gwt.dashboard.shared.PanoramaService;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class PanoramaServiceImpl extends RemoteServiceServlet implements
        PanoramaService {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();

        userService = UserServiceFactory.getUserService();
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
        result.setStatus(source.getStatus().toString());
        return result;
    }

    @Override
    public boolean removePanorama(String key) {
        // TODO Auto-generated method stub
        return false;
    }

}
