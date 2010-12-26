from google.appengine.ext import webapp
from google.appengine.ext.webapp.util import run_wsgi_app

from handlers.raw import RawPanoHandler
from handlers.index import IndexHandler

fotokugeln_app = webapp.WSGIApplication(
    [('/pano/raw/*', RawPanoHandler), ('/', IndexHandler)],
    debug=True
)

def main():
    run_wsgi_app(fotokugeln_app)

if __name__ == '__main__':
    main()
