var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;
var parseHandler = require('parse-handler');

router.get('/', function(req, res, next) {

    if (Parse.User.current()) {
        res.render('profile', { title: 'Scout', jumbotron: 'Profile', filename: 'profile' });
    } else {
        res.redirect('/');
    }
});

router.get('/index', function(req, res) {
    var businessSuccess = function (business) {

        data = {};

        //Load the pre-existing business values
        var profileImage = business.get("image");
        if(profileImage != undefined) {
            data.image = profileImage.url();
        }

        data.businessname = business.get("name");
        data.points = business.get("points");
        data.rate = business.get("rate");

        res.json(data);
    };

    var businessFailure = function (error) {
        var msg = 'ERROR: Unable to query business for owner'+Parse.User.current();

        console.log(msg);
        console.log(error.message);

        res.status(400).send(msg);
    };

    var businessArgs = {'user': Parse.User.current()};

    parseHandler.retrieveBusiness(businessSuccess, businessFailure, businessArgs);
});

router.post('/', function (req, res) {
    var businessSuccess = function (business) {
        data = {};

        var password = req.body['password'];
        var businessName = req.body['businessname'];
        var points = req.body['points'];
        var rate = req.body['rate'];
        var curPassword = req.body['curpassword'];
        var image = req.files['image'];

        if (businessName != undefined &&
            businessName.trim().length > 0 &&
            businessName.trim() !== business.get("name")) {
            business.set("name", businessName);
        }

        business.set("points", points);
        business.set("rate", rate);


        var curLoggedUser = Parse.User.current();
        //checks if current password is the same as the one provided
        //only change the password if current password and a new password is entered
        if (curPassword != undefined && curPassword.trim().length > 0 &&
            password != undefined && password.trim().length > 0) {
            //keep track of current logged in user so that we can
            //log him back if the current password entered in edit profile is invalid
            var curUser = curLoggedUser.getSessionToken();
            var curUserName = curLoggedUser.get("username");
            Parse.User.logOut();
            Parse.User.logIn(curUserName, curPassword, {
                success: function (user) {
                    curLoggedUser.set("password", password);
                    curLoggedUser.save(null, {
                        success: function () {
                            saveImage(image, business, res);
                        },
                        error: function (myObject, error) {
                            res.status(502).send('Failed to update personal information.');
                        }
                    });
                },
                error: function (user, error) {
                    Parse.User.become(curUser);
                    res.status(403).send('Current password entered is not correct.');
                }
            });
        } else {
            saveImage(image, business, res);
        }
    };

    var businessFailure = function (error) {
        var msg = 'ERROR: Unable to query business for owner'+Parse.User.current();

        console.log(msg);
        console.log(error.message);

        res.status(400).send(msg);
    };

    var businessArgs = {'user': Parse.User.current()};

    parseHandler.retrieveBusiness(businessSuccess, businessFailure, businessArgs);
});

//Save the business object
function saveBusiness(business, res) {
    business.save(null, {
        success: function(myObject) {
            res.status(200).send();
        },
        error: function(myObject, error) {
            res.status(502).send('Failed to update business.');
        }
    });
}

//Save images into cloud then add it to the business if successful
function saveImage(image, business, res) {
    if (image != undefined && image != null) {
        var base64Img = new Buffer(image.buffer).toString('base64');
        var parseFile = new Parse.File(image.name, { base64: base64Img });
        parseFile.save().then(function () {
            // The file has been saved to Parse. Now attach it to our business model
            business.set("image", parseFile);
            saveBusiness(business, res);
        }, function (error) {
            console.log(error);
            res.status(502).send('Failed to update business profile picture.');
        });
    } else {
        saveBusiness(business, res);
    }
}

module.exports = router;
