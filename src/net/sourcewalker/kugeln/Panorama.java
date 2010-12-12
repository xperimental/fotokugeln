package net.sourcewalker.kugeln;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Panorama {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String owner;

    @Persistent
    private String title;

    @Persistent
    private String rawBlob;

    @Persistent
    private Blob thumbnail;

    @Persistent
    private PanoramaStatus status;

    @Persistent
    private String statusText;

    public Panorama(String owner, String title, String rawBlob) {
        this.owner = owner;
        this.title = title;
        this.rawBlob = rawBlob;
        this.status = PanoramaStatus.NEW;
        this.statusText = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Blob getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Blob thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean hasThumbnail() {
        return thumbnail != null && thumbnail.getBytes() != null
                && thumbnail.getBytes().length > 0;
    }

    public Key getKey() {
        return key;
    }

    public String getOwner() {
        return owner;
    }

    public String getRawBlob() {
        return rawBlob;
    }

    public PanoramaStatus getStatus() {
        return status;
    }

    public void setStatus(PanoramaStatus status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

}
