package net.sourcewalker.kugeln;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ThumbnailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String panoKey = req.getParameter("panoKey");
        if (panoKey != null) {
            PersistenceManager pm = PersistenceManagerFactory.get();
            try {
                Panorama panorama = pm.getObjectById(Panorama.class, KeyFactory
                        .stringToKey(panoKey));
                if (panorama.hasThumbnail()) {
                    Blob thumbnail = panorama.getThumbnail();
                    resp.setContentType("image/jpeg");
                    resp.getOutputStream().write(thumbnail.getBytes());
                } else {
                    resp.sendError(404, "Panorama has no thumbnail!");
                }
            } catch (IllegalArgumentException e) {
                resp.sendError(400, "Panorama key invalid!");
            } finally {
                pm.close();
            }
        } else {
            resp.sendError(400, "Panorama key required!");
        }
    }

}
