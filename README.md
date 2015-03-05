# Python-PBKDF2-Flask-Password-Encoder

The corresponding java code creates pbkdf2 sha512 encoded password. I use the code to create encrypted passwords in my mongo db using Bruno Rocha's 


![flask](https://github.com/quokkaproject/quokka/raw/master/docs/flask_powered.png)



1. You can reproduce the password with the following code (Hash is pbkdf2/sha512, password salt is  '6e95b1ed-a8c3-4da0-8bac-6fcb11c39ab4' and password is 'mypassword'

```python

from flask import Flask, render_template
from flask.ext.sqlalchemy import SQLAlchemy
from flask.ext.security import Security, SQLAlchemyUserDatastore, UserMixin, RoleMixin, login_required
from flask.ext.security.utils import encrypt_password, verify_password

# Create app
app = Flask(__name__)
app.config['DEBUG'] = True



# app.config['SECURITY_PASSWORD_HASH'] = 'pbkdf2_sha512'
# wir koennen nur folgende Sachen nehmen:
# bcrypt, des_crypt, pbkdf2_sha256, pbkdf2_sha512, sha256_crypt, sha512_crypt and plaintext


app.config['SECRET_KEY'] = 'S3cr3Tk3Y'
app.config['SECURITY_PASSWORD_HASH'] = 'pbkdf2_sha512'
app.config['SECURITY_PASSWORD_SALT'] = '6e95b1ed-a8c3-4da0-8bac-6fcb11c39ab4'



# Create database connection object
db = SQLAlchemy(app)

# Define models
roles_users = db.Table('roles_users',
        db.Column('user_id', db.Integer(), db.ForeignKey('user.id')),
        db.Column('role_id', db.Integer(), db.ForeignKey('role.id')))

class Role(db.Model, RoleMixin):
    id = db.Column(db.Integer(), primary_key=True)
    name = db.Column(db.String(80), unique=True)
    description = db.Column(db.String(255))

class User(db.Model, UserMixin):
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(255), unique=True)
    password = db.Column(db.String(255))
    active = db.Column(db.Boolean())
    confirmed_at = db.Column(db.DateTime())
    roles = db.relationship('Role', secondary=roles_users,
                            backref=db.backref('users', lazy='dynamic'))

# Setup Flask-Security
user_datastore = SQLAlchemyUserDatastore(db, User, Role)
security = Security(app, user_datastore)

# Create a user to test with
@app.before_first_request
def create_user():
    db.create_all()
    user_datastore.create_user(email='matt@nobien.net', password='password')
    db.session.commit()

# Views
@app.route('/')
#@login_required
def home():
    password = encrypt_password('mypassword')
    print verify_password('mypassword', password)
    return password
#    return render_template('index.html')

if __name__ == '__main__':
    app.run()

```

2. A key LIKE $pbkdf2-sha512$19000$dE4JgbC2ljJmDOF8750TIg$3L1YPrhJII.YhqxjOGKw11L8pVg/pa6j2iC1qER5lhaIubWjKQHikuu35F0SDKwxyvHeNqXZO9w4d5J2bTaoBQ will be produced (different key every time because of random bit generation)

3. The Java Class produces the same key (same salt length, iterations, etc.) :

```java
PBKDF2PasswordEncoder myPBKDF2PasswordEncoder = new PBKDF2PasswordEncoder();

try {
  System.out.println(myPBKDF2PasswordEncoder.encodePassword("mypassword","6e95b1ed-a8c3-4da0-8bac-6fcb11c39ab4", "pbkdf2-sha512"));
}
catch (Exception e) {
  e.printStackTrace();
}
        
```

Attention
=========

You need Java 1.8 to get it working (otherwise the algorithm sha512 is unknown)






