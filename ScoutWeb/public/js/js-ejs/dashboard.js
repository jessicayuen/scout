$(document).ready(function() {
    $.get('/dashboard/index')
        .success( function(data) {
            console.log(data);
            // New customers
            $("#new-daily>span").text(data.new.daily);
            $("#new-monthly>span").text(data.new.monthly);
            // Customer demographics
            $("#demographics-duration").text(data.totcustomers);
            // Points
            $("#points-earned").text(data.points.earned);
            $("#points-redeemed").text(data.points.redeeemed);
    });
});
