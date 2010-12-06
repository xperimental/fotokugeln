package net.sourcewalker.kugeln;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import net.sourcewalker.kugeln.data.Panorama;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class PanoramaTableBean {

    private static final Log LOG = LogFactory.getLog(PanoramaTableBean.class);

    private List<Panorama> panoramas;

    public List<Panorama> getPanoramas() {
        return panoramas;
    }

    public void setPanoramas(List<Panorama> panoramas) {
        this.panoramas = panoramas;
    }

    public PanoramaTableBean() {
        panoramas = new ArrayList<Panorama>();
        UserService userService = UserServiceFactory.getUserService();
        User currentUser = userService.getCurrentUser();
        LOG.error("Current user = " + currentUser);
        if (currentUser != null) {
            PersistenceManager pm = PersistenceManagerFactory.get();
            try {
                Query query = pm.newQuery(Panorama.class);
                query.setFilter("owner == ownerParam");
                query.declareParameters("String ownerParam");
                List<Panorama> panoramaList = (List<Panorama>) query
                        .execute(currentUser.getUserId());
                panoramas.addAll(panoramaList);
            } finally {
                pm.close();
            }
        }
    }
}
