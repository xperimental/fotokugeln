from google.appengine.ext import db

class Panorama(db.Model):
    owner = db.IntegerProperty(required=True)
    title = db.StringProperty(required=True)
    status = db.StringProperty(required=True)
    statusText = db.StringProperty(required=True)
    thumbnail = db.BlobProperty(required=True)
    heading = db.FloatProperty(required=True)
    latitude = db.FloatProperty(required=True)
    longitude = db.FloatProperty(required=True)
    rawBlob = db.StringProperty(required=True)
    rawHeight = db.IntegerProperty(required=True)
    rawWidth = db.IntegerProperty(required=True)

class PanoramaTile(db.Model):
    data = db.BlobProperty(required=True)
    panoramaKey = db.ReferenceProperty(Panorama, required=True)
    position = db.StringProperty(required=True)
