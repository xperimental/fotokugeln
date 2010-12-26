from google.appengine.ext import db
from google.appengine.ext import blobstore

class Panorama(db.Model):
    owner = db.UserProperty(required=True)
    title = db.StringProperty(required=True)
    status = db.StringProperty(required=True)
    statusText = db.StringProperty()
    thumbnail = db.BlobProperty()
    heading = db.FloatProperty()
    latitude = db.FloatProperty()
    longitude = db.FloatProperty()
    rawBlob = blobstore.BlobReferenceProperty()
    rawHeight = db.IntegerProperty()
    rawWidth = db.IntegerProperty()

class PanoramaTile(db.Model):
    data = db.BlobProperty(required=True)
    panoramaKey = db.ReferenceProperty(Panorama, required=True)
    position = db.StringProperty(required=True)
