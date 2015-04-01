$(document).ready(function(){

    var login = function (event) {
        event.preventDefault();
        var data = 
            {
                email: $(this).find('input[name="email"]').val(), 
                password: $(this).find('input[name="password"]').val()
            };
        $.post('/', data)
            .success(function (data) {
                window.location.href = '/dashboard';
            })
            .error(function (xhr, textStatus, errorThrown) {
                $('#modal-error-div').show();
                TemplateErrorDisplay(xhr, textStatus, errorThrown);
            });
    }

    $('#login-btn').on('click', function() {
        var formBody =
            '<form id="login-form">'+
            '<div class="form-group">'+
            '<label for="editEmail" class="control-label">Email:</label>'+
            '<input type="text" name="email" id="editEmail" class="form-control" placeholder="Email" required>'+
            '</div>'+
            '<div class="form-group">'+
            '<label for="editPassword" class="control-label">Password:</label>'+
            '<input type="password" name="password" id="editPassword" class="form-control" placeholder="Password" required>'+
            '</div></form>';

        $('#modal-title-text').text('Merchant Login');
        $('#modal-body-div').empty().append(formBody);
        $('#modal-dismiss-text').text('Close');
        $('#modal-submit-text').text('Log In');
        $('#modal').modal('show');
        $('#modal-form').submit(login);
    });

    $('#modal-dismiss-text').on('click', function () {
        $('#modal-error-div').hide();
    })
});
