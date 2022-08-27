package demo.controller;

import java.util.List;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import demo.service.MusicService;

public class PlaySomethingController extends Controller {
	
	
	public void index() {
		
		MusicService.createListener(getRequest());
		
		List<Record> songList = MusicService.getSongList();
		setAttr("songList", songList);
		
		render("playSomething.html");
    }
		
	@ActionKey("/playSomething/hb")
	public void heartbeat(){
		String ra = getRequest().getRemoteAddr();
		String curplay = getPara("curplay");
		Record listener = new Record();
		listener.set("ip", ra);
		listener.set("curplay", curplay);
		Db.save("listener", listener);
	}
	
	@ActionKey("/playSomething/getPlay")
	public void getPlay(){
		Record songSrc = MusicService.getSongSrc(getRequest());
		renderJson(songSrc);
	}
	
}
