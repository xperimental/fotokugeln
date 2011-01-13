from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext import db
from data.panorama import PanoramaStatus
from google.appengine.ext import blobstore
from google.appengine.api import images
from google.appengine.api import taskqueue
from data.tiles import TileGenerator

def queue(panorama):
    taskqueue.add(url='/pano/process/%s' % panorama.key(), name='process-%s-%s' % (panorama.key(), panorama.status))

class ProcessHandler(RequestHandler):
    MAXIMUM_SIZE = 30 * 1024 * 1024
    MINIMUM_WIDTH = 1024
    MAXIMUM_WIDTH = 8192

    def post(self, panoKey):
        panorama = db.get(panoKey)
        if panorama:
            try:
                if panorama.status == PanoramaStatus.NEW:
                    self.checkUpload(panorama)
                    self.checkImage(panorama)
                    panorama.status = PanoramaStatus.THUMBNAIL
                    queue(panorama)
                elif panorama.status == PanoramaStatus.THUMBNAIL:
                    panorama.thumbnail = self.generateThumbnail(panorama)
                    panorama.status = PanoramaStatus.TILES
                    queue(panorama)
                elif panorama.status == PanoramaStatus.TILES:
                    tiles = self.generateTiles(panorama)
                    if tiles:
                        self.saveTiles(tiles)
                        panorama.status = PanoramaStatus.LIVE
                    else:
                        queue(panorama)
                else:
                    pass
            except ValueError, detail:
                panorama.status = PanoramaStatus.ERROR
                panorama.statusText = detail.message\

            panorama.save()

    def generateThumbnail(self, panorama):
        rawImage = self.readBlobImage(panorama.rawBlob)
        rawImage.resize(64, 32)
        return rawImage.execute_transforms(output_encoding=images.JPEG)

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
        if rawWidth >= self.MINIMUM_WIDTH:
            if rawWidth <= self.MAXIMUM_WIDTH:
                if not (rawWidth & (rawWidth - 1)):
                    if rawHeight * 2 == rawWidth:
                        panorama.rawWidth = rawWidth
                        panorama.rawHeight = rawHeight
                    else:
                        raise ValueError("Wrong aspect ratio!")
                else:
                    raise ValueError("Width no power of 2!")
            else:
                raise ValueError("Maximum width is %dpx!" % self.MAXIMUM_WIDTH)
        else:
            raise ValueError("Minimum width is %dpx!" % self.MINIMUM_WIDTH)

    def readBlobImage(self, blob):
        reader = blobstore.BlobReader(blob)
        data = reader.read()
        return images.Image(data)

    def generateTiles(self, panorama):
        reader = blobstore.BlobReader(panorama.rawBlob)
        generator = TileGenerator(panorama.key(), panorama.rawWidth, panorama.rawHeight, reader.read())
        return generator.tiles

    def saveTiles(self, tiles):
        for tile in tiles:
            tile.save()
