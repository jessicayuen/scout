$(document).ready(function() {
    $.get('/dashboard/index')
        .success( function(data) {
            
            // New customers
            $("#new-daily>span").text(data.new.daily);
            $("#new-monthly>span").text(data.new.monthly);

            // Customer demographics
            $("#demographics-duration>span").text(data.visitlength);
            // Points
            $("#points-earned>span").text(data.points.earned);
            $("#points-avg>span").text(data.points.avg);
    });
});
