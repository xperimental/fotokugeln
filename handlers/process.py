from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext import db
from data.panorama import PanoramaStatus
from google.appengine.ext import blobstore
from google.appengine.api import images
from google.appengine.api import taskqueue

def queue(panorama):
    taskqueue.add(url='/pano/process/%s' % panorama.key(), name='process-%s-%s' % (panorama.key(), panorama.status))

class ProcessHandler(RequestHandler):
    MAXIMUM_SIZE = 30 * 1024 * 1024

    def post(self, panoKey):
        panorama = db.get(panoKey)
        if panorama:
            if panorama.status == PanoramaStatus.NEW:
                try:
                    self.checkUpload(panorama)
                    self.checkImage(panorama)
                    panorama.status = PanoramaStatus.THUMBNAIL
                    queue(panorama)
                except ValueError, detail:
                    panorama.status = PanoramaStatus.ERROR
                    panorama.statusText = detail.message
            elif panorama.status == PanoramaStatus.THUMBNAIL:
                pass
            elif panorama.status == PanoramaStatus.TILES:
                pass
            else:
                pass
            panorama.save()

    def checkUpload(self, panorama):
        if not panorama.rawBlob.content_type == 'image/jpeg':
            raise ValueError('Invalid filetype!')
        if panorama.rawBlob.size > self.MAXIMUM_SIZE:
            raise ValueError('File too large!')
        pass

    def checkImage(self, panorama):
        data = self.readBlobImage(panorama.rawBlob)
        rawWidth = data.width
        rawHeight = data.height
        if not (rawWidth & (rawWidth - 1)):
            if rawHeight * 2 == rawWidth:
                panorama.rawWidth = rawWidth
                panorama.rawHeight = rawHeight
            else:
                raise ValueError("Wrong aspect ratio!")
        else:
            raise ValueError("Width no power of 2!")

    def readBlobImage(self, blob):
        reader = blobstore.BlobReader(blob)
        data = reader.read()
        return images.Image(data)
