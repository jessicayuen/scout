$(document).ready(function(){
    // Populates heatmap with points
    var initHeatmap = function (data) {
            // Load floorplan
            var src = "/img/floorplan.jpg";
            $('#heatmapContainer').prepend('<img id="floorplan" src="' + src + '" />');

            $("<img />")
                .attr("src", src)
                .load(function() {
                    $("#heatmapContainer").height(this.height);
                    $("#heatmapContainer").width(this.width);
                                                                //create a heatmap instance
                        var heatmap = h337.create({
                          container:  $('#heatmapContainer')[0],
                          maxOpacity: .6,

                          radius: 30,
                          blur: .90,
                          // backgroundColor with alpha so you can see through it
                          backgroundColor: 'rgba(100, 100, 100, 0)'
                        });

                        heatmap.setData(data)
                        $('#heatmapContainer').hide().fadeIn(1000);
            });
    }

    // Refreshes heatmap
    var refreshHeatmap = function (callback) {
        $.get('/heatmap/getheatmap')
            .success(function (data) {
                console.log(data);
                $('#heatmapContainer').empty();
                initHeatmap(data);
            })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });

        if (callback != null) callback();
    }
    // Refresh heatmap when page is loaded
    $('#heatmapContainer').ready(refreshHeatmap);
});
