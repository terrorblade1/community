$(function () {

    //初始化参数
    WIDGET = {FID: 'xCcqEgEQqB'}
    //var css = "<link rel='stylesheet' href='https://apip.weatherdt.com/float/static/css/tqw_widget_float.css?v=0101'>";
    var js = "<script type='text/javascript' src='https://apip.weatherdt.com/float/static/js/r.js?v=1111'></script>";

    // 2.把文件放到页面
    //$("head").append(css);
    $("head").append(js);

    // 3.把目标放到页面，target为页面元素
    var target = "#weather";
    $(target).append("<div id='weather-float-he'></div>")


});