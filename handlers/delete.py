from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext import db
from google.appengine.api import users
from data.panorama import PanoramaTile

class DeleteHandler(RequestHandler):
    def get(self, panoId):
        user = users.get_current_user()
        panorama = db.get(panoId)
        if panorama:
            if user == panorama.owner:
                for tile in PanoramaTile.all().filter("panoramaKey ==", panorama.key()):
                    tile.delete()
                panorama.rawBlob.delete()
                panorama.delete()
                self.redirect('/')
            else:
                self.error(401)
        else:
            self.error(404)
