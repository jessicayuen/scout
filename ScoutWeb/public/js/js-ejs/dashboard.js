$(document).ready(function() {
    $.get('/dashboard/index')
        .success( function(data) {
            
            // New customers
            $("#new-daily>span").text(data.new.daily);
            $("#new-monthly>span").text(data.new.monthly);

            // Customer demographics
            $("#demographics-duration>span").text(data.totcustomers);
            // Points
            $("#points-earned>span").text(data.points.earned);
            $("#points-redeemed>span").text(data.points.redeemed);
    });
});
