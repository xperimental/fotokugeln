from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from handlers.delete import DeleteHandler
from handlers.raw import RawPanoHandler
from handlers.index import IndexHandler
from handlers.upload import UploadHandler

fotokugeln_app = webapp.WSGIApplication(
        [
                (r'/pano/raw/(.+)', RawPanoHandler),
                ('/pano/upload', UploadHandler),
                (r'/pano/delete/(.*)', DeleteHandler),
                ('/', IndexHandler)
        ], debug=True)

def main():
    run_wsgi_app(fotokugeln_app)

if __name__ == '__main__':
    main()
