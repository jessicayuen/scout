var express = require('express');
var router = express.Router();
var Parse = require('parse').Parse;


router.get('/', function(req, res, next) {

    if (Parse.User.current()) {
        res.render('heatmap', { title: 'Scout', filename: 'heatmap'});
    } else {
        res.redirect('/');
    }
});


router.get('/heatmap', function(req, res, next) {

});

module.exports = router;
