import os

basedir = os.path.abspath(os.path.dirname(__file__))

# To store databases
DATABASE_DIRECTORY = os.path.join(basedir, "database")

ADMINS = ['elle_admin']
# Can inject from virtual environment or use raw passwords
ELLE_ADMIN_PASSWORD = os.environ.get('ELLE_ADMIN_PASSWORD') or "RIcARayTEriB"
ELLE_ADMIN_LICENSE_KEY = os.environ.get('ELLE_ADMIN_LICENSE_KEY') or "SEUSSGEISEL"