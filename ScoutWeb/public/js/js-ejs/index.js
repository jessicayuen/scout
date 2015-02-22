$(document).ready(function(){
    $("#login-form").submit(function(event) {
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
            .error(TemplateErrorDisplay);
    });
});
