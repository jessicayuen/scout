$(document).ready(function(){
    
    var intervalJsonData;
    var intervalRecordsJsonData;

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

    function getheatmapAjax() {
        return $.get('/heatmap/getheatmap')
        .success(function (data) {
            intervalJsonData = data;
            console.log(data);
        })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
    }

    function getIntervalJSONAjax() {
        return $.get('/heatmap/retrieveIntervalJSON')
        .success(function (data) {
            console.log(data);
        })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
    }

    function getIntervalRecordsJSONAjax() {
        return $.get('/heatmap/retrieveIntervalRecordsJSON')
        .success(function (data) {
            intervalRecordsJsonData = data
            console.log(data);
        })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
    }

    // Refreshes heatmap
    var refreshHeatmap = function (callback) {
        $.when(getheatmapAjax(), getIntervalJSONAjax()).done(function(a1, a2){
            // the code here will be executed when all four ajax requests resolve.
            // a1, a2, a3 are lists of length 3 containing the response text,
            // status, and jqXHR object for each of the 3 ajax calls respectively.
            $('#heatmapContainer').empty();
            // initHeatmap(intervalJsonData);
        });
        if (callback != null) callback();
    }

    // Refresh heatmap when page is loaded
    $('#heatmapContainer').ready(refreshHeatmap);
});
