from google.appengine.ext.webapp.blobstore_handlers import BlobstoreUploadHandler
from data.panorama import Panorama
from google.appengine.api import users

class UploadHandler(BlobstoreUploadHandler):
    def post(self):
        try:
            user = users.get_current_user()
            title = self.request.get('title')
            for upload in self.get_uploads('rawImage'):
                panorama = Panorama(title=title, owner=user, rawBlob=upload, status='NEW')
                panorama.save()
            self.redirect('/')
        except:
            self.redirect('/pano/upload/failed')
