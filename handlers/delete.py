from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext import db
from google.appengine.api import users

class DeleteHandler(RequestHandler):
    def get(self, panoId):
        user = users.get_current_user()
        panorama = db.get(panoId)
        if panorama:
            if user == panorama.owner:
                panorama.rawBlob.delete()
                panorama.delete()
                self.redirect('/')
            else:
                self.error(401)
        else:
            self.error(404)
