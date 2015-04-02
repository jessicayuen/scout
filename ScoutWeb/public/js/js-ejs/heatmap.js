$(document).ready(function(){

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
            console.log(data);
        })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
    }

    function getBeaconsJSONAjax() {
        return $.get('/heatmap/retrieveBeaconsJSON')
        .success(function (data) {
            console.log(data);
        })
        .error(function (xhr, textStatus, errorThrown) {
            alert(xhr.responseText);
        });
    }

});
