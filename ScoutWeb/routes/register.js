var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;

router.get('/', function(req, res, next) {
  if (Parse.User.current() == null) {
    res.render('register', { title: 'Scout' });
  } else {
    res.redirect('/dashboard');
  }
});

router.post('/', function (req, res) {
  var email = req.body['email'];
  var password = req.body['password'];
  var confirmPassword = req.body['confirmpassword'];
  var businessName = req.body['businessname'];
  var firstName = req.body['firstname'];
  var lastName = req.body['lastname'];

  var user = new Parse.User();
  user.set("username", email);
  user.set("password", password);
  user.set("email", email);
  user.set("firstname", firstName);
  user.set("lastname", lastName);

  user.signUp(null, {
    success: function(user) {
      var businessObj = Parse.Object.extend('Business');
      var businessRec = new businessObj();

      businessRec.set("name", businessName);
      businessRec.set("owner", Parse.User.current());

      businessRec.save(null, {
        success: function(business) {
          res.redirect('/dashboard');
        },
        error: function(error) {
          // This should never happen
          console.log('ERROR: cannot save business '+businessName);
          console.log(error.message);

          res.redirect('/register');
        }
      });
    },
    error: function(error) {
      // Show the error message somewhere and let the user try again.
      console.log('ERROR: Unable to signup user '+ email);
      console.log(error.message);

      res.redirect('/register');
    }
  });
});

module.exports = router;
