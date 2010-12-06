package net.sourcewalker.kugeln.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;

@PersistenceCapable
public class Panorama {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String owner;

    @Persistent
    private String name;

    @Persistent
    private String rawKey;

    @Persistent
    private byte[] thumbnail;

    @Persistent
    private double latitude;

    @Persistent
    private double longitude;

    @Persistent
    private double heading;

    public Panorama(User owner, String rawKey, String name) {
        this.owner = owner.getUserId();
        this.rawKey = rawKey;
        this.name = name;
        this.latitude = 0;
        this.longitude = 0;
        this.heading = 0;
    }

    public Key getKey() {
        return key;
    }

    public String getKeyString() {
        if (key != null) {
            return KeyFactory.keyToString(key);
        } else {
            return "zarro";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getHeading() {
        return heading;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public String getOwner() {
        return owner;
    }

    public String getRawKey() {
        return rawKey;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailUrl() {
        if (key != null) {
            return "/pano/thumbnail?key=" + KeyFactory.keyToString(key);
        } else {
            return "/images/nothumb.png";
        }
    }

}
