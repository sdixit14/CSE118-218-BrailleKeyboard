from pathlib import Path
import string
import random

# FLASK IMPORTS
import flask
from flask import render_template, flash, redirect, url_for, request, send_from_directory, current_app, Flask
from flask_sqlalchemy import SQLAlchemy
from flask_migrate import Migrate
from flask_login import UserMixin, LoginManager, login_user, logout_user, current_user, login_required
from flask_security import Security, SQLAlchemyUserDatastore, RoleMixin, roles_accepted
from werkzeug.security import generate_password_hash, check_password_hash
from flask_mail import Mail, Message

# CUSTOM FILE IMPORTS
from constants import *
from config import Config

import json

# CREATE CUSTOM DIRECTORIES
Path(DATABASE_DIRECTORY).mkdir(parents=True, exist_ok=True)

application = Flask(__name__)
application.config.from_object(Config)

# DB settings and migration
db = SQLAlchemy(application)
migrate = Migrate(application, db)

login = LoginManager(application)
login.login_view = '/'

mail = Mail(application)
application.debug = os.environ.get('FLASK_DEBUG') in ['true', 'True']


# DB classes

# Role defines - user or admin
class Role(db.Model):
    id = db.Column(db.Integer(), primary_key=True)
    name = db.Column(db.String(50), unique=True)


class UserRoles(db.Model):
    id = db.Column(db.Integer(), primary_key=True)
    user_id = db.Column(db.Integer(), db.ForeignKey('user.id', ondelete='CASCADE'))
    role_id = db.Column(db.Integer(), db.ForeignKey('role.id', ondelete='CASCADE'))


class User(UserMixin, db.Model):
    __tablename__ = 'user'
    id = db.Column(db.Integer, primary_key=True)
    username = db.Column(db.String(64), index=True, unique=True)
    email = db.Column(db.String(64))
    password_hash = db.Column(db.String(128))
    license_key = db.Column(db.String(128))
    roles = db.relationship('Role', secondary='user_roles',
                            backref=db.backref('users', lazy='dynamic'))

    def __repr__(self):
        return '<User {}>'.format(self.username)

    def set_password(self, password):
        self.password_hash = generate_password_hash(password)

    def check_password(self, password):
        return check_password_hash(self.password_hash, password)

    # Check for admin role or not
    def check_admin(self):
        for i in self.roles:
            if (i.name in ADMINS):
                return True
        return False


class Notes(db.Model):
    __tablename__ = 'notes'
    id = db.Column(db.Integer(), primary_key=True)
    user_id = db.Column(db.Integer(), db.ForeignKey('user.id', ondelete='CASCADE'))
    username = db.Column(db.String(64))
    notes = db.Column(db.String(128))


from db_queries import *

user_datastore = SQLAlchemyUserDatastore(db, User, Role)
security = Security(application, user_datastore)


@login.user_loader
def load_user(id):
    return User.query.get(int(id))


# Executes before the first request is processed.
@application.before_first_request
def before_first_request():
    db.create_all()
    find_or_create_role(name='elle_admin')
    find_or_create_role(name='user')
    db.session.commit()

    safe_user_entry("elle_admin", ELLE_ADMIN_PASSWORD, "ellebackend@gmail.com", "elle_admin", ELLE_ADMIN_LICENSE_KEY)
    db.session.commit()


@application.route('/', methods=['GET', 'POST'])
def login():
    message = "Hello, world!"
    if current_user.is_authenticated:
        return redirect(url_for('hello_world'))

    return flask.render_template('auth_login_template.html')


@application.route('/logincheck', methods=['POST'])
def login_check():
    if current_user.is_authenticated:
        return redirect(url_for('hello_world'))

    username = request.form['username']
    if username is None or username == "":
        return redirect(url_for('login'))

    password = request.form['password']
    if password is None or password == "":
        return redirect(url_for('login'))

    try:
        user = User.query.filter_by(username=username).first()
        if user is None or not user.check_password(password):
            return redirect(url_for('login'))

        login_user(user)
        status_message = "Welcome back user " + current_user.username
        return flask.render_template('status_template.html', status_message=status_message,
                                     admin_status=current_user.check_admin())
    except:
        return redirect(url_for('login'))


def get_license_key():
    S = 15  # number of characters in the license key
    return ''.join(random.choices(string.ascii_uppercase, k=S))


def send_mail(email, username, license_key):
    try:
        msg = Message("Elle | New user credentials", sender=Config.MAIL_USERNAME, recipients=[email])

        msg.html = flask.render_template('mail_template.html', username=username
                                         , license_key=license_key)
        mail.send(msg)
        return True
    except Exception as e:
        print(e)
        return False


@application.route('/createuser', methods=['GET', 'POST'])
@login_required
@roles_accepted("elle_admin")
def create_user():
    return flask.render_template('create_user.html',
                                 admin_status=current_user.check_admin())


@application.route('/createuserprocess', methods=['POST'])
@login_required
@roles_accepted('elle_admin')
def create_user_process():
    try:
        data = request.form
        username = data.get('username')
        email = data.get('email')
        password = data.get('password')
        role = data.get('role')
        license_key = get_license_key()
        user = safe_user_entry(username, password, email, role, license_key=license_key)
        if not user:
            status_message = "User with username " + username + " has been added successfully."
            if send_mail(email, username, get_license_key()):
                status_message = status_message + " The user credentials have been mailed to " + email + "."
            else:
                status_message = status_message + " The mail with credentials has not been sent out contact the elle admin team."
        else:
            status_message = "User with username " + username + " already exists"

    except Exception as e:
        print(e)
        status_message = "The user record was not created " + str(e)

    return flask.render_template('status_template.html',
                                 status_message=status_message,
                                 admin_status=current_user.check_admin())


# Publicly exposed API will contain only JSON params
@application.route('/addnotepublic', methods=['POST'])
def add_note_to_database():
    data = request.get_json()
    username = data['username']
    licensekey = data['licensekey']
    note = data['note']
    response = safe_note_entry(username, note, licensekey)
    return response


# Publicly exposed API will contain only JSON params
@application.route('/viewallnotepublic', methods=['GET','POST'])
def view_all_notes():
    data = request.get_json()
    if 'username' in data:
        username = data['username']
    else:
        username = 'elle_admin'
    if 'licensekey' in data:
        licensekey = data['licensekey']
    else:
        licensekey = 'SEUSSGEISEL'
    response = safe_note_get_all(username, licensekey)
    list_of_messages = []
    for i in response:
        list_of_messages.append(i.notes)
    return {"status": "SUCCESS", "messages": list_of_messages}


@application.route('/addnotetestprocess', methods=['POST'])
def add_note_to_admin_database():
    data = request.form
    username = current_user.username
    licensekey = current_user.license_key
    note = data.get('note')
    response = safe_note_entry(username, note, licensekey)
    return flask.render_template('status_template.html',
                                 status_message=response["message"],
                                 admin_status=current_user.check_admin())


@application.route('/addnotetest', methods=['GET', 'POST'])
@login_required
def add_note_admin_test():
    return flask.render_template('create_note.html',
                                 admin_status=current_user.check_admin())


@application.route('/notesummary', methods=['GET', 'POST'])
@login_required
def note_summary():
    data = Notes.query.filter()
    return flask.render_template('all_notes.html',
                                 data=data,
                                 admin_status=current_user.check_admin())


@application.route('/home', methods=['GET', 'POST'])
@login_required
def hello_world():
    status_message = "Welcome back user " + current_user.username
    return flask.render_template('status_template.html',
                                 status_message=status_message,
                                 admin_status=current_user.check_admin())


@application.route('/logout')
def logout():
    logout_user()
    return redirect(url_for('login'))


if __name__ == '__main__':
    application.run(host='0.0.0.0')
