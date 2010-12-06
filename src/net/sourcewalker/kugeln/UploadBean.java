package net.sourcewalker.kugeln;

import java.io.IOException;
import java.net.URL;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class UploadBean {

    public void handleUpload(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        if (file.getContentType().equals("image/jpeg")) {
            BlobstoreService blobService = BlobstoreServiceFactory
                    .getBlobstoreService();
            String uploadUrl = blobService.createUploadUrl("/pano/raw");
            URLFetchService fetchService = URLFetchServiceFactory
                    .getURLFetchService();
            ExternalContext externalContext = FacesContext.getCurrentInstance()
                    .getExternalContext();
            String scheme = externalContext.getRequestScheme();
            String serverName = externalContext.getRequestServerName();
            int serverPort = externalContext.getRequestServerPort();
            try {
                HTTPRequest request = new HTTPRequest(new URL(scheme,
                        serverName, serverPort, uploadUrl), HTTPMethod.POST);
                HTTPResponse response = fetchService.fetch(request);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            FacesContext.getCurrentInstance().addMessage("growl",
                    new FacesMessage("Only JPEG files allowed!"));
        }
    }

}
