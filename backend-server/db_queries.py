from application import db, User, Role, Notes


def find_or_create_role(name):
    """ Find existing role or create new role """
    role = Role.query.filter(Role.name == name).first()
    if not role:
        role = Role(name=name)
        db.session.add(role)
    return role


def safe_user_entry(username, password, email, role, license_key):
    if license_key is None:
        return None
    user = User.query.filter(User.username == username).first()
    if not user:
        u = User(username=username, email=email, license_key=license_key)
        u.set_password(password)
        u.roles = [find_or_create_role(role)]
        try:
            db.session.add(u)
            db.session.commit()
        except Exception as e:
            print(e)
    return user


def safe_note_entry(username, note, license_key):
    user = User.query.filter(User.username == username and User.license_key == license_key).first()
    if not user:
        return {"status": "FAILURE",
                "message": "User could not be validated"}
    else:
        user_id = user.id
        n = Notes(user_id=user_id, username=user.username, notes=note)
        try:
            db.session.add(n)
            db.session.commit()
            return {"status": "SUCCESS", "message": "Note has been successfully added"}
        except Exception as e:
            print(e)
            return {"status": "FAILURE", "message": "Attempt to add note has failed"}
