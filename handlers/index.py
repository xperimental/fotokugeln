from google.appengine.ext.webapp import RequestHandler
from google.appengine.ext.webapp import template
from google.appengine.api import users
from google.appengine.ext import blobstore
from data.panorama import Panorama

class IndexHandler(RequestHandler):
    def get(self):
        template_values = {}
        current_user = users.get_current_user()
        if (current_user):
            template_values['user'] = current_user
            template_values['logout_url'] = users.create_logout_url('/')

            template_values['panorama_list'] = Panorama.all().filter("owner = ", current_user).fetch(100)
            template_values['upload_url'] = blobstore.create_upload_url('/pano/upload')
        else:
            template_values['login_url'] = users.create_login_url('/')

        self.response.out.write(template.render('templates/index.html', template_values))
