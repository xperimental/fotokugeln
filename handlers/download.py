from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext.webapp.blobstore_handlers import BlobstoreDownloadHandler
from google.appengine.ext import db
from google.appengine.api import users
from data.panorama import PanoramaTile

class RawPanoHandler(BlobstoreDownloadHandler):
    def get(self, panoKey):
        user = users.get_current_user()
        panorama = db.get(panoKey)
        if not panorama:
            self.error(404)
        elif not user == panorama.owner:
            self.error(401)
        else:
            self.response.headers['Content-disposition'] = 'attachment; filename=%s' % panorama.rawBlob.filename
            self.response.headers['Content-length'] = panorama.rawBlob.size
            self.send_blob(panorama.rawBlob)

class ThumbnailHandler(RequestHandler):
    def get(self, panoKey):
        panorama = db.get(panoKey)
        if not panorama:
            self.error(404)
        else:
            self.response.headers['Content-type'] = 'image/jpeg'
            self.response.out.write(panorama.thumbnail)

class TileHandler(RequestHandler):
    def get(self, panoKey, level, column, row):
        panorama = db.get(panoKey)
        if not panorama:
            self.error(404)
        else:
            position = "%s:%s:%s" % (level, column, row)
            tileList = PanoramaTile.all().filter("panoramaKey ==", panorama).filter("position ==", position).fetch(1)
            if not tileList:
                self.error(404)
            else:
                for tile in tileList:
                    self.response.headers['Content-type'] = 'image/jpeg'
                    self.response.out.write(tile.data)
