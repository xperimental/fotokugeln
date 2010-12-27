from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from handlers.delete import DeleteHandler
from handlers.process import ProcessHandler
from handlers.raw import RawPanoHandler, ThumbnailHandler
from handlers.index import IndexHandler
from handlers.upload import UploadHandler

fotokugeln_app = webapp.WSGIApplication(
        [
                ('/pano/raw/(.+)', RawPanoHandler),
                ('/pano/upload', UploadHandler),
                ('/pano/delete/(.+)', DeleteHandler),
                ('/pano/process/(.+)', ProcessHandler),
                ('/pano/thumbnail/(.+)', ThumbnailHandler),
                ('/', IndexHandler)
        ], debug=True)

def main():
    run_wsgi_app(fotokugeln_app)

if __name__ == '__main__':
    main()
