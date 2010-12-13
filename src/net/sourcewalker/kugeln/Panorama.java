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
    private int rawWidth;

    @Persistent
    private int rawHeight;

    @Persistent
    private Blob thumbnail;

    @Persistent
    private PanoramaStatus status;

    @Persistent
    private String statusText;

    @Persistent
    private double latitude;

    @Persistent
    private double longitude;

    @Persistent
    private float heading;

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

    public int getRawWidth() {
        return rawWidth;
    }

    public void setRawWidth(int rawWidth) {
        this.rawWidth = rawWidth;
    }

    public int getRawHeight() {
        return rawHeight;
    }

    public void setRawHeight(int rawHeight) {
        this.rawHeight = rawHeight;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getHeading() {
        return heading;
    }

    public void setHeading(float heading) {
        this.heading = heading;
    }

}
