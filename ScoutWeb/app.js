var express = require('express');
var path = require('path');
var favicon = require('serve-favicon');
var logger = require('morgan');
var cookieParser = require('cookie-parser');
var bodyParser = require('body-parser');
var Parse = require('parse').Parse;

var index = require('./routes/index');
var register = require('./routes/register');
var dashboard = require('./routes/dashboard');
var rewards = require('./routes/rewards');
var logout = require('./routes/logout');

var app = express();


Parse.initialize("DiEded8eK6muPcH8cdHGj8iqYUny65Mva143CpQ3", "haRBk6ltEVIbnNmwsBkMYneYefjS9JSLOWyjxbjb");

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'ejs');

// uncomment after placing your favicon in /public
//app.use(favicon(__dirname + '/public/favicon.ico'));
app.use(logger('dev'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// url routing
app.use('/', index);
app.use('/register', register);
app.use('/dashboard', dashboard);
app.use('/rewards', rewards);
app.use('/logout', logout);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
    var err = new Error('Not Found');
    err.status = 404;
    next(err);
});

// error handlers

// development error handler
// will print stacktrace
if (app.get('env') === 'development') {
    app.use(function(err, req, res, next) {
        res.status(err.status || 500);
        res.render('error', {
            message: err.message,
            error: err
        });
    });
}

// production error handler
// no stacktraces leaked to user
app.use(function(err, req, res, next) {
    res.status(err.status || 500);
    res.render('error', {
        message: err.message,
        error: {}
    });
});


module.exports = app;

var port = process.env.PORT || 3000;

var server = app.listen(port, function () {
  console.log('listening on http://localhost:%d', port);
});
