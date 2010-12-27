from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext.webapp.blobstore_handlers import BlobstoreDownloadHandler
from google.appengine.ext import db
from google.appengine.api import users

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
            self.send_blob(panorama.rawBlob)

class ThumbnailHandler(RequestHandler):
    def get(self, panoKey):
        panorama = db.get(panoKey)
        if not panorama:
            self.error(404)
        else:
            self.response.headers['Content-type'] = 'image/jpeg'
            self.response.out.write(panorama.thumbnail)
