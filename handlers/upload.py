from google.appengine.ext.webapp.blobstore_handlers import BlobstoreUploadHandler
from data.panorama import Panorama
from google.appengine.api import users
import process

class UploadHandler(BlobstoreUploadHandler):
    def post(self):
        user = users.get_current_user()
        title = self.request.get('title')
        for upload in self.get_uploads('rawImage'):
            panorama = Panorama(title=title, owner=user, rawBlob=upload)
            panorama.save()

            process.queue(panorama)
        self.redirect('/')
