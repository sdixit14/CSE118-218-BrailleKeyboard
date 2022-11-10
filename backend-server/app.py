from flask import Flask

app = Flask(__name__)


@app.route('/')
def main_page():
    return 'Main page of the elle backend'


@app.route('/authenticate')
def authenticate():
    # TODO: Authentication code goes here
    return 'Authentication function'


@app.route('/persistToDatabase')
def persistToDatabase():
    # TODO: Persisting to database goes here
    # TODO: REST parameters will define database schema
    return 'Authentication function'


@app.route('/retreiveFromDatabase')
def persistDatabase():
    # TODO: Retreiving from database goes here
    # TODO: REST parameters will define database schema
    return 'Authentication function'


if __name__ == '__main__':
    app.run()
