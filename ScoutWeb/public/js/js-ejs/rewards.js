var populateRewardsTable = function (data) {
    if (data.length > 0) {
        var heading = '<tr><th>Image</th><th>Points</th><th>Description</th><th>Edit/Remove</th></tr>';
        $("#rewards-table").append(heading);

        data.forEach(function (reward) {
            var objectid = reward['objectId'];
            var imageUrl = reward['image'] ? reward['image'] : 'noimage.jpg';
            var points = reward['points'];
            var description = reward['description'];

            var row = 
                $('<tr><td class="image-col"><a href="#" class="thumbnail"><img src="img/'+imageUrl+'" alt="'+imageUrl+'"></a></td>'+
                '<td class="points-col">'+points+'</td>'+
                '<td class="descrip-col">'+description+'</td>'+
                '<td class="edit-remove-col">'+
                  '<button type="submit" class="btn btn-primary edit" id="'+objectid+'">Edit</button>'+
                  '<button type="submit" class="btn btn-danger remove" id="'+objectid+'">Remove</button>'+
                '</td></tr>');
            row.hide();

            $('#rewards-table tr:last-child').after(row);
            row.fadeIn(1000);
        });
    } else {
        $("#rewards-table").append('<td><h3>No available rewards.</h3></td>');
    }
}

var refreshTable = function () {
    $.get('/rewards/getrewards')
        .success(function (data) {
            $('#rewards-table').empty();
            populateRewardsTable(data);
        })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
}

$('#rewards-table').ready(refreshTable);

$('#rewards-table').on('click', '.remove', function() {
    $.post('/rewards/removereward', { objectid: this.id })
        .done(refreshTable);
});

$('#rewards-table').on('click', '.edit', function() {
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
});

$('#modal-form').submit(function(event) {
  event.preventDefault();
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
    success: function(data) {
      $('#modal').modal('hide');
      $('#modal-body-div').empty();
      refreshTable();
    },
    error: function (xhr, textStatus, errorThrown) {
        alert(xhr.responseText)
    }
  });
});

$('#addbutton').click(function() {
    var addform = 
      '<form id="addForm">' +
        '<input type="text" id="name" placeholder="Name">' +
        '<input type="text" id="description" placeholder="Description">' +
        '<input type="number" id="points" placeholder="Points">' +
        '<input type="submit" value="Submit!"></form>';
    $("#addform").append(addform);
});

$("#addform").submit(function(event) {
    event.preventDefault();
    var data = 
        {
            name: $("#name").val(), 
            description: $("#description").val(), 
            points: $("#points").val()
        };
    $.post('/rewards/addreward', data)
        .success(refreshTable)
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
});