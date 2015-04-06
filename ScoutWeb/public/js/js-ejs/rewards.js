$(document).ready(function(){
    // Populates reward records for rewards table
    var populateRewardsTable = function (data) {
        if (data.length > 0) {
            // var heading = '<tr><th>Image</th><th>Points</th><th>Description</th><th>Edit/Remove</th></tr>';
            // $('#rewards-table').append(heading);
            var tileColors = ["pink", "purple", "cyan", "amber", "blue", "red", "indigo", "deep-orange", "green", "light-blue"];
            var i = 0;
            data.forEach(function (reward) {
                var objectid = reward['objectId'];
                var imageUrl = reward['image'] ? reward['image'] : 'noimage.jpg';
                var points = reward['points'];
                var description = reward['description'];

                // var row =
                //     $('<tr><td class="image-col"><a href="#" class="thumbnail"><img src="img/'+imageUrl+'" alt="'+imageUrl+'"></a></td>'+
                //             '<td class="points-col">'+points+'</td>'+
                //             '<td class="descrip-col">'+description+'</td>'+
                //             '<td class="edit-remove-col">'+
                            // '<button type="submit" class="btn btn-primary edit" id="'+objectid+'">Edit</button>'+
                            // '<button type="submit" class="btn btn-danger remove" id="'+objectid+'">Remove</button>'+
                //             '</td></tr>');
                var color = tileColors[i];
                var row =
                $('<a class="tile tile-lg tile-sqr tile-'+ color +' ripple-effect" href="#">' +
                    '<span class="content-wrapper">' +
                      '<span class="tile-content">' +
                        '<span class="tile-holder tile-holder-sm" style = "top: 20px">' +
                        '<span class="title">' + description + '</span>' +
                        '</span>' +
                        '<span class="tile-img" style="background-image: url(http://www.radiolaurier.com/wp-content/uploads/2014/10/coffee1.jpeg);"></span>' +
                        '<span class="tile-holder tile-holder-sm" style = "top: 310px;">' +
                        '<span class="title"><font color="yellow">Points: ' + points + '</font></span>' +
                        '</span>' +
                        '<span class="tile-holder tile-holder-sm">' +
                            '<div>'+
                                '<button type="submit" style="width:110px;" class="btn btn-primary edit" id="'+objectid+'">Edit</button>'+
                                '<button type="submit" style="width:110px;margin-left:5px;" class="btn btn-danger remove" id="'+objectid+'">Remove</button>'+
                            '</div>'+
                        '</span>' +
                      '</span>' +
                    '</span>' +
                '</a>');


                $('#content').append(row);
                // $('#rewards-table tr:last-child').after(row);
                i++;
                if (i >= tileColors.length)
                {
                    i = 0;
                }
            });

            /**
             * Created by Kupletsky Sergey on 16.09.14.
             *
             * Hierarchical timing
             * Add specific delay for CSS3-animation to elements.
             */

            (function($) {
                var speed = 2000;
                var container =  $('.display-animation');
                container.each(function() {
                    var elements = $(this).children();
                    elements.each(function() {
                        var elementOffset = $(this).offset();
                        var offset = elementOffset.left*0.8 + elementOffset.top;
                        var delay = parseFloat(offset/speed).toFixed(2);
                        $(this)
                            .css("-webkit-animation-delay", delay+'s')
                            .css("-o-animation-delay", delay+'s')
                            .css("animation-delay", delay+'s')
                            .addClass('animated');
                    });
                });
            })(jQuery);

            /**
             * Created by Kupletsky Sergey on 04.09.14.
             *
             * Ripple-effect animation
             * Tested and working in: ?IE9+, Chrome (Mobile + Desktop), ?Safari, ?Opera, ?Firefox.
             * JQuery plugin add .ink span in element with class .ripple-effect
             * Animation work on CSS3 by add/remove class .animate to .ink span
            */

            (function($) {
                $(".ripple-effect").click(function(e){
                    var rippler = $(this);

                    // create .ink element if it doesn't exist
                    if(rippler.find(".ink").length == 0) {
                        rippler.append("<span class='ink'></span>");
                    }

                    var ink = rippler.find(".ink");

                    // prevent quick double clicks
                    ink.removeClass("animate");

                    // set .ink diametr
                    if(!ink.height() && !ink.width())
                    {
                        var d = Math.max(rippler.outerWidth(), rippler.outerHeight());
                        ink.css({height: d, width: d});
                    }

                    // get click coordinates
                    var x = e.pageX - rippler.offset().left - ink.width()/2;
                    var y = e.pageY - rippler.offset().top - ink.height()/2;

                    // set .ink position and add class .animate
                    ink.css({
                        top: y+'px',
                        left:x+'px'
                    }).addClass("animate");
                })
            })(jQuery);

        } else {
            $("#alert").append('<td style><h3>No available rewards.</h3></td>');
        }
    }
    // Refreshes rewards table
    var refreshTable = function (callback) {
        $.get('/rewards/getrewards')
            .success(function (data) {
                $('#content').empty();
                $('#alert').empty();
                populateRewardsTable(data);
            })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });

        if (callback != null) callback();
    }

    // Refresh table when page is loaded
    $('#content').ready(refreshTable);

    // Remove reward upon clicking of remove
    $('#content').on('click', '.remove', function() {
        $.post('/rewards/removereward', { objectid: this.id })
            .done(refreshTable);
    });

    // Add a reward
    var addReward = function(event) {
        event.preventDefault();
        event.stopImmediatePropagation();
        var data =
        {
            description: $(this).find('input[name="description"]').val(),
            points: $(this).find('input[name="points"]').val()
        };

        $.post('/rewards/addreward', data)
            .success(refreshTable(function() {
                $('#modal').modal('hide');
                $('#modal-body-div').empty();
            }))
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
    };
    // Edit a reward
    var editReward = function(event) {
        event.preventDefault();
        event.stopImmediatePropagation();
        var data =
        {
            objectId: $(this).find('input[name="objectId"]').val(),
            description: $(this).find('input[name="description"]').val(),
            points: $(this).find('input[name="points"]').val()
        };

        $.ajax({
            url: '/rewards/editreward',
            type: 'PUT',
            data: data,
            success: refreshTable(function() {
                $('#modal').modal('hide');
                $('#modal-body-div').empty();
            }),
            error: function (xhr, textStatus, errorThrown) {
                alert(xhr.responseText);
            }
        });
    };
    // An reward editting modal is shown to user when edit is clicked
    $('#content').on('click', '.edit', function() {
        var formBody =
            '<div class="form-group">'+
            '<label for="editDescription" class="control-label">Description:</label>'+
            '<input type="text" name="description" id="editDescription" class="form-control" placeholder="Description" required>'+
            '</div>'+
            '<div class="form-group">'+
            '<label for="editPoints" class="control-label">Points:</label>'+
            '<input type="number" name="points" id="editPoints" class="form-control" placeholder="Points" required>'+
            '</div>'+
            '<input type="hidden" name="objectId" value="'+this.id+'">';

        $('#modal-title-text').text('Edit Reward');
        $('#modal-body-div').empty().append(formBody);
        $('#modal-dismiss-text').text('Close');
        $('#modal-submit-text').text('Save Changes');
        $('#modal').modal('show');
        $('#modal-form').submit(editReward);
    });

    // When the reward editting modal is submitted, the modal will be closed,
    // and the rewards are refreshed

    // When the add rewards button is pressed, a form will be shown
    $('#addbutton').on('click', function() {
        var formBody =
            '<div class="form-group">'+
            '<label for="description" class="control-label">Description:</label>'+
            '<input type="text" name="description" class="form-control" placeholder="Description" required></div>' +
            '<div class="form-group">'+
            '<label for="points" class="control-label">Points:</label>'+
            '<input type="number" name="points" class="form-control" placeholder="Points" required></div>';

        $('#modal-title-text').text('Add Reward');
        $('#modal-body-div').empty().append(formBody);
        $('#modal-dismiss-text').text('Close');
        $('#modal-submit-text').text('Add Reward');
        $('#modal').modal('show');
        $('#modal-form').submit(addReward);
    });

});
