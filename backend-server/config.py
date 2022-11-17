import os

basedir = os.path.abspath(os.path.dirname(__file__))


class Config(object):
    SQLALCHEMY_DATABASE_URI = os.environ.get('DATABASE_URL') or \
                              'sqlite:///' + os.path.join(basedir, 'app.db')
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SECRET_KEY = os.environ.get('DATABASE_SECRET_KEY') or 'balajimt12345'
    # EMAIL SETTINGS
    MAIL_SERVER = os.environ.get('ELLE_MAIL_SERVER') or 'smtp.gmail.com'
    MAIL_PORT = os.environ.get('ELLE_MAIL_PORT') or 465
    MAIL_USE_SSL = True
    MAIL_USERNAME = os.environ.get('ELLE_MAIL_USERNAME') or 'ellebackend@gmail.com'
    MAIL_PASSWORD = os.environ.get('ELLE_MAIL_PASSWORD') or 'zrfojvbrxyrjwihs'
