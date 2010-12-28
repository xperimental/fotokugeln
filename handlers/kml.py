from wsgiref.util import request_uri
from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext.webapp import template
from google.appengine.ext import db
from data.tiles import TILE_SIZE
from urlparse import urlparse

class KmlDownloadHandler(RequestHandler):
    def get(self, panoKey):
        panorama = db.get(panoKey)
        if not panorama:
            self.error(404)
        else:
            request_url = urlparse(request_uri(self.request.environ))
            server_url = "%s://%s" % (request_url.scheme, request_url.netloc)
            template_values = {
                'pano': panorama,
                'server_url': server_url,
                'tilesize': TILE_SIZE
            }

            self.response.headers['Content-type'] = 'application/vnd.google-earth.kml+xml; charset=utf-8'
            self.response.headers['Content-disposition'] = 'attachment; filename="%s.kml"' % panorama.title
            self.response.out.write(template.render('templates/panorama.kml', template_values))
