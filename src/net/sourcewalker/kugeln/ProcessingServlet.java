package net.sourcewalker.kugeln;

import java.io.IOException;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

@SuppressWarnings("serial")
public class ProcessingServlet extends HttpServlet {

    private static final int BUF_SIZE = 1000000;
    private BlobstoreService blobStore;
    private Queue taskQueue;
    private BlobInfoFactory infoFactory;
    private ImagesService imagesService;

    @Override
    public void init() throws ServletException {
        super.init();

        blobStore = BlobstoreServiceFactory.getBlobstoreService();
        infoFactory = new BlobInfoFactory();
        taskQueue = QueueFactory.getDefaultQueue();
        imagesService = ImagesServiceFactory.getImagesService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String panoKey = req.getParameter("panoKey");
        if (panoKey != null) {
            PersistenceManager pm = PersistenceManagerFactory.get();
            Panorama panorama = null;
            try {
                panorama = pm.getObjectById(Panorama.class, KeyFactory
                        .stringToKey(panoKey));
                BlobKey rawKey = new BlobKey(panorama.getRawBlob());
                Image rawImage = getImageFromBlobstore(rawKey);
                switch (panorama.getStatus()) {
                case NEW:
                    checkRawSize(rawImage, panorama);
                    break;
                case THUMBNAIL:
                    if (generateThumbnail(rawImage, panorama)) {
                        panorama.setStatus(PanoramaStatus.OK);
                    }
                    break;
                }
                if (panorama.getStatus() != PanoramaStatus.ERROR
                        && panorama.getStatus() != PanoramaStatus.OK) {
                    TaskOptions task = TaskOptions.Builder.withUrl(
                            "/pano/process").param("panoKey", panoKey)
                            .countdownMillis(5000);
                    taskQueue.add(task);
                }
            } catch (Exception e) {
                if (panorama != null) {
                    panorama.setStatus(PanoramaStatus.ERROR);
                    panorama.setStatusText("Invalid image data!");
                }
            } finally {
                pm.close();
            }
        }
        resp.setContentType("text/plain");
        resp.getWriter().println("ok");
    }

    private boolean generateThumbnail(Image rawImage, Panorama panorama) {
        Transform resize = ImagesServiceFactory.makeResize(64, 32);
        Image thumbnail = imagesService.applyTransform(resize, rawImage,
                OutputEncoding.JPEG);
        panorama.setThumbnail(new Blob(thumbnail.getImageData()));
        return true;
    }

    private Image getImageFromBlobstore(BlobKey rawKey) {
        BlobInfo info = infoFactory.loadBlobInfo(rawKey);
        if (info.getContentType().startsWith("image/")) {
            byte[] data = new byte[(int) info.getSize()];
            int index = 0;
            while (index < info.getSize()) {
                byte[] buffer = blobStore.fetchData(rawKey, index, index
                        + BUF_SIZE);
                copyBytes(buffer, data, index);
                index += buffer.length;
            }
            return ImagesServiceFactory.makeImage(data);
        } else {
            throw new RuntimeException("No image in blob!");
        }
    }

    private void copyBytes(byte[] source, byte[] destination, int startIndex) {
        for (int i = 0; i < source.length; i++) {
            destination[startIndex + i] = source[i];
        }
    }

    private void checkRawSize(Image rawImage, Panorama panorama) {
        int width = rawImage.getWidth();
        int height = rawImage.getHeight();
        if (width >= 512) {
            if ((width & (width - 1)) == 0) {
                if (2 * height == width) {
                    panorama.setStatus(PanoramaStatus.THUMBNAIL);
                    return;
                }
            }
        }
        panorama.setStatus(PanoramaStatus.ERROR);
        panorama.setStatusText("Invalid image size (" + width + " x " + height
                + ")!");
    }

}
