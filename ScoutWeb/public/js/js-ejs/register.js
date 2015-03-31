$(document).ready(function(){
    $("input").not("[type=submit]").jqBootstrapValidation();

    $("#register-form").submit(function(event) {
        event.preventDefault();
        var data = 
            {
                email: $(this).find('input[name="email"]').val(), 
                password: $(this).find('input[name="password"]').val(), 
                confirmpassword: $(this).find('input[name="confirmpassword"]').val(),
                businessname: $(this).find('input[name="businessname"]').val(),
                firstname: $(this).find('input[name="firstname"]').val(),
                lastname: $(this).find('input[name="lastname"]').val()
            };
        $.post('/register', data)
            .success(function (data) {
                window.location.href = '/dashboard';
            })
            .error(TemplateErrorDisplay);
    });
});