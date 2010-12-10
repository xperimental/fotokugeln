package net.sourcewalker.kugeln;

import java.io.IOException;
import java.util.Map;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class RawPanoramaServlet extends HttpServlet {

    private BlobstoreService blobStore;
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();

        blobStore = BlobstoreServiceFactory.getBlobstoreService();
        userService = UserServiceFactory.getUserService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, BlobKey> uploadedBlobs = blobStore.getUploadedBlobs(req);
        BlobKey blobKey = uploadedBlobs.get("rawImage");
        if (blobKey == null) {
            resp.sendRedirect("/upload.jsp");
        } else {
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                // If no user is logged in delete all uploaded blobs immediately
                blobStore
                        .delete(uploadedBlobs.values().toArray(new BlobKey[0]));
            } else {
                String title = req.getParameter("title");
                if (title != null && title.length() > 0) {
                    Panorama newPano = new Panorama(currentUser.getUserId(),
                            title, blobKey.getKeyString());
                    PersistenceManager pm = PersistenceManagerFactory.get();
                    try {
                        pm.makePersistent(newPano);
                    } catch (Exception e) {
                        throw new RuntimeException("Error saving panorama: "
                                + e.getMessage(), e);
                    } finally {
                        pm.close();
                    }
                } else {
                    throw new IllegalArgumentException("No title!");
                }
            }
            resp.sendRedirect("/dashboard.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String key = req.getParameter("key");
        if (key == null) {
            throw new IllegalArgumentException("No key!");
        } else {
            BlobKey blobKey = new BlobKey(key);
            blobStore.serve(blobKey, resp);
        }
    }
}
