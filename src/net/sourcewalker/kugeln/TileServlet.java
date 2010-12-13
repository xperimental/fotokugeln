package net.sourcewalker.kugeln;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class TileServlet extends HttpServlet {

    @SuppressWarnings("unchecked")
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        PersistenceManager pm = PersistenceManagerFactory.get();
        try {
            String panoString = req.getParameter("panoKey");
            if (panoString == null) {
                resp.sendError(400, "Panorama key required!");
            } else {
                Key panoKey = KeyFactory.stringToKey(panoString);
                int level = Integer.parseInt(req.getParameter("l"));
                int column = Integer.parseInt(req.getParameter("x"));
                int row = Integer.parseInt(req.getParameter("y"));
                String position = TileGenerator.getPosition(level, column, row);
                Query tileQuery = pm.newQuery(PanoramaTile.class);
                tileQuery
                        .setFilter("panoramaKey == panoParam && position == posParam");
                tileQuery
                        .declareParameters("com.google.appengine.api.datastore.Key panoParam, String posParam");
                List<PanoramaTile> result = (List<PanoramaTile>) tileQuery
                        .execute(panoKey, position);
                if (result == null || result.size() != 1) {
                    resp.sendError(404, "Tile not found!");
                } else {
                    PanoramaTile tile = result.get(0);
                    byte[] data = tile.getData().getBytes();
                    resp.setContentType("image/jpeg");
                    resp.setContentLength(data.length);
                    resp.getOutputStream().write(data);
                }
            }
        } catch (NumberFormatException e) {
            resp.sendError(400, "Invalid parameters!");
        } catch (IllegalArgumentException e) {
            resp.sendError(400, "Invalid panorama key!");
        } catch (Exception e) {
            resp.sendError(500, "Uncaught error: " + e.getMessage());
        } finally {
            pm.close();
        }
    }
}
