(function ($) {
    //
	var defaultOpts = {
		'width':'200px',
		'height':'70px',
		'style':'normal',
		'show':'up',
		'close':'fade',
		'msg':'',
		'title':'',
		'time':'',
		'send':''
	};
	var msgList = [];
	function _createMsgBox(){
		$("<div class='tipBox'></div>").appendTo("body");
	};
	function _createMsgList(msgOpt){
		msgList.push(msgOpt);
	};
	function _closeMsg(self){
		console.log(11);
		$(self).parent().parent().slideUp(function(){
			this.remove();
		});
	};
	function _createMsg(opt){
		var msgDiv = $('<div class="tips"></div>');
		msgDiv.html('<div class="tiphead"><span class="tiptitle">'+opt.title+'</span><span class="tipdate">（'+opt.time+'）</span><span class="closeIcon">x</span></div><div class="tipdesc">'+opt.msg+'</div>');
		var _this = msgDiv.find(".closeIcon").eq(0);
		$(".tipBox").append(msgDiv);
		msgDiv.slideDown();
		_this.click(function(){
			_closeMsg(this);
		});
		if(opt.duration){
			msgDiv.animate({opacity:"1"},opt.duration).slideUp(function(){
				this.remove();
			});
		}
	};
	$.fn.extend({
		"myMessageBox": function (options) {
			var opts = $.extend({}, defaultOpts, options); //使用jQuery.extend 覆盖插件默认参数
			if(opts.msg){
				console.log(opts);
				_createMsg(opts);
			}
		}
	});
	_createMsgBox();
	//width:"300px",height:"200px",style:"normal",show:"up"
	$.fn.myMessageBox({title:"XX",time:"2020-01-01 01:12:22",msg:"DDD"});
	$.fn.myMessageBox({title:"提示",time:"2021-02-22 09:00:00",msg:"别忘了签到！"});
	$.fn.myMessageBox({title:"上报提醒",time:"2021-02-22 10:35:12",msg:"管理员（感染控制中心）上报了 传染病报卡 ( 丙肝 )"});
		 	
})(window.jQuery);