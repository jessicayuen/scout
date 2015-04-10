var assert = require('assert'),
    test = require('selenium-webdriver/testing'),
    webdriver = require('selenium-webdriver');

var LARGE_TIMEOUT = 1000;
var SMALL_TIMEOUT = 50;

test.describe('Log In', function() {
    test.it('Test Webpages', function() {
        var driver = new webdriver.Builder().withCapabilities(webdriver.Capabilities.chrome()).build();

        driver.get('localhost:3000');

        // if the user is already logged in, log them out
        driver.findElement(webdriver.By.id('logout')).then(function() {
            driver.findElement(webdriver.By.id('logout')).click();
        }, function (err) { /* do nothing */ });


        // landing page
        driver.findElement(webdriver.By.id('login-btn')).click().then(function () {
            driver.sleep(LARGE_TIMEOUT);
        });

        // log into page
        driver.findElement(webdriver.By.name('email')).sendKeys('bob@example.com');
        driver.findElement(webdriver.By.id('editPassword')).sendKeys('bob');
        driver.findElement(webdriver.By.id('modal-submit-text')).click().then(function() {
            driver.sleep(LARGE_TIMEOUT);
        });


        // rewards
        driver.findElement(webdriver.By.id('rewards')).click();
        driver.findElement(webdriver.By.id('addbutton')).click().then(function() {
            driver.sleep(LARGE_TIMEOUT);
            driver.findElement(webdriver.By.id('modal-dismiss-text')).click().then(function() {
                driver.sleep(LARGE_TIMEOUT);
            });
        });

        // heatmap
        driver.findElement(webdriver.By.id('heatmap')).click();

        // dashboard
        driver.findElement(webdriver.By.id('dashboard')).click();

        // logout
        driver.findElement(webdriver.By.id('logout')).click();
    });
});
