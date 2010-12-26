from urllib import unquote_plus
from google.appengine.ext import blobstore
from google.appengine.ext.webapp.blobstore_handlers import BlobstoreDownloadHandler

class RawPanoHandler(BlobstoreDownloadHandler):
    def get(self, blobKeyString):
        blobKey = blobstore.BlobKey(unquote_plus(blobKeyString))
        blobInfo = blobstore.get(blobKey)
        if not blobInfo:
            self.error(404)
        else:
            self.response.headers['Content-disposition'] = 'attachment; filename=%s' % blobInfo.filename
            self.send_blob(blobKey)
