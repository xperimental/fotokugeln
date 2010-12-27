from google.appengine.ext import db
from google.appengine.ext import blobstore

class PanoramaStatus():
    NEW = 'NEW'
    THUMBNAIL = 'THUMBNAIL'
    TILES = 'TILES'
    LIVE = 'LIVE'
    ERROR = 'ERROR'

class Panorama(db.Model):
    owner = db.UserProperty(required=True)
    title = db.StringProperty(required=True)
    status = db.StringProperty(required=True, default=PanoramaStatus.NEW)
    statusText = db.StringProperty()
    thumbnail = db.BlobProperty()
    heading = db.FloatProperty()
    latitude = db.FloatProperty()
    longitude = db.FloatProperty()
    rawBlob = blobstore.BlobReferenceProperty()
    rawHeight = db.IntegerProperty()
    rawWidth = db.IntegerProperty()

    def statusDescription(self):
        if self.status == PanoramaStatus.NEW:
            return "Waiting for processing..."
        elif self.status == PanoramaStatus.THUMBNAIL:
            return "Generating thumbnail..."
        elif self.status == PanoramaStatus.TILES:
            return "Tiling image..."
        elif self.status == PanoramaStatus.LIVE:
            return "Live"
        elif self.status == PanoramaStatus.ERROR:
            return "Error processing panorama"
        else:
            return "Unknown status"

class PanoramaTile(db.Model):
    data = db.BlobProperty(required=True)
    panoramaKey = db.ReferenceProperty(Panorama, required=True)
    position = db.StringProperty(required=True)
