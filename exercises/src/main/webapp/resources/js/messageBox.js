(function ($) {
    //
	var defaultOpts = {
			'width':'200px',
			'height':'70px',
			'style':'normal',
			'show':'up',
			'close':'fade',
			'msg':'',
			
	};
	
	$.fn.extend({
		"myMessageBox": function (options) {
			var opts = $.extend({}, defaultOpts, options); //使用jQuery.extend 覆盖插件默认参数
			console.log(opts);
		}
	});
	//width:"300px",height:"200px",style:"normal",show:"up"
	$.fn.myMessageBox({title:"XX",msg:"DDD"});
		 	
})(window.jQuery);