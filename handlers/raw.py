from google.appengine.ext import webapp

class RawPanoHandler(webapp.RequestHandler):
    def get(self):
        self.response.headers['Content-type'] = 'text/plain'
        self.response.out.write("Test!")

