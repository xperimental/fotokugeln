package net.sourcewalker.kugeln;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

@SuppressWarnings("serial")
public class RawPanoramaServlet extends HttpServlet {

    private BlobstoreService blobStore;

    @Override
    public void init() throws ServletException {
        super.init();

        blobStore = BlobstoreServiceFactory.getBlobstoreService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Map<String, BlobKey> uploadedBlobs = blobStore.getUploadedBlobs(req);
        BlobKey blobKey = uploadedBlobs.get("rawImage");
        if (blobKey == null) {
            resp.sendRedirect("/upload.jsp");
        } else {
            resp.sendRedirect("/pano/raw?key=" + blobKey.getKeyString());
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
