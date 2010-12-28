from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from handlers.delete import DeleteHandler
from handlers.process import ProcessHandler
from handlers.download import RawPanoHandler, ThumbnailHandler, TileHandler
from handlers.index import IndexHandler
from handlers.upload import UploadHandler
from handlers.kml import KmlDownloadHandler

fotokugeln_app = webapp.WSGIApplication(
        [
                ('/pano/raw/(.+)', RawPanoHandler),
                ('/pano/upload', UploadHandler),
                ('/pano/delete/(.+)', DeleteHandler),
                ('/pano/process/(.+)', ProcessHandler),
                ('/pano/thumbnail/(.+)', ThumbnailHandler),
                ('/pano/tile/(.+)/(\d+)/(\d+)/(\d+)', TileHandler),
                ('/pano/view/(.+)', KmlDownloadHandler),
                ('/', IndexHandler)
        ], debug=True)

def main():
    run_wsgi_app(fotokugeln_app)

if __name__ == '__main__':
    main()
