package net.sourcewalker.kugeln;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourcewalker.kugeln.data.Panorama;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class ThumbnailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String key = req.getParameter("key");
        if (key == null) {
            throw new IllegalArgumentException("No panorama key!");
        }
        PersistenceManager pm = PersistenceManagerFactory.get();
        try {
            Key panoramaKey = KeyFactory.stringToKey(key);
            Panorama panorama = pm.getObjectById(Panorama.class, panoramaKey);
            byte[] thumbnail = panorama.getThumbnail();
            resp.setContentType("image/png");
            resp.setContentLength(thumbnail.length);
            resp.getOutputStream().write(thumbnail);
        } finally {
            pm.close();
        }
    }

}
