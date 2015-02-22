// Global JavaScript File

// error.ejs
//
// Used a callback for errors in a jQuery CRUD call ($.POST, $.GET)
var TemplateErrorDisplay = function (xhr, textStatus, errorThrown, callback) {
    $('#error-message').text(xhr.responseText);
    $('#error').show();

    if (callback != null) callback();
};
