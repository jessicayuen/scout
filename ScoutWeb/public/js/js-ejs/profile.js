var fadeAnimationTime = 3000;

$(document).ready(function() {
    $('.selectpicker').selectpicker({
        style: 'btn-info'
    });

    $('#updateSuccess').hide();
    $('#updateFailed').hide();

    loadOverlay(true);
    $.get('/profile/index')
        .success( function(data) {
            var profileForm = $("#profile-form");

            if(data.image !== undefined && data.image !== '') {
                profileForm.find('img[name="previewImgThumb"]').attr('src', data.image);
            }

            profileForm.find('input[name="businessname"]').val(data.businessname);

            if(data.points !== undefined) {
                profileForm.find('input[name="inputpoints"]').val(data.points);
            }

            if(data.rate !== undefined) {
                profileForm.find('select[name="rateselection"]').selectpicker('val', data.rate);
            }

            loadOverlay(false);
        }).error(function() {
            loadOverlay(false);
        });

    $("input").not("[type=submit]").jqBootstrapValidation({
        submitSuccess: function ($form, event) {

            var form = new FormData();
            form.append( 'image', $form.find('input[name="profileimg"]').prop('files')[0] );
            form.append( 'curpassword', $form.find('input[name="curpassword"]').val() );
            form.append( 'password', $form.find('input[name="password"]').val() );
            form.append( 'businessname', $form.find('input[name="businessname"]').val() );
            form.append( 'points', $form.find('input[name="inputpoints"]').val() );
            form.append( 'rate', $form.find('select[name="rateselection"]').val() );

            updateProfile(form);

            // will not trigger the default submission in favor of the ajax function
            event.preventDefault();
        }
    });

    function updateProfile(data) {
        loadOverlay(true);
        $.ajax({
            url: '/profile',
            type: 'POST',
            data: data,
            processData: false,
            contentType: false,
            success: function(data) {
                loadOverlay(false);
                $('#updateSuccess').fadeIn().delay(fadeAnimationTime).fadeOut();
            },
            error: function (xhr, textStatus, errorThrown) {
                loadOverlay(false);
                $('#updateFailed').text(xhr.responseText);
                $('#updateFailed').fadeIn().delay(fadeAnimationTime).fadeOut();
            }
        });
    }
});

var loadOverlay = function(show) {
    // add the overlay with loading image to the page
    var load = '<div id="overlay"> <img id="loading" src="img/loading.GIF"> </div>';

    if(show) {
        $('body').append(load);
    } else {
        $('#overlay').remove();
    }
};