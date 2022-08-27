package demo.service;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

public class MusicService {
	
	public static List<Record> songList = new ArrayList<Record>();	
	
	public void getJsonContent(String filename){
		
	}
	
	public static List<Record> getSongList(){
		songList = Db.find("select uid,name,location from files f where f.contentType like 'audio%'");
		return songList;
	}
	
	public static Record getSongSrc(HttpServletRequest request){
		Record r = new Record();
		String ra = request.getRemoteAddr();
		String uid = request.getParameter("uid");
		if(songList.isEmpty()){
			getSongList();
		}
		r.set("result", "404");
		for(Record song : songList){
			if(uid.equals(song.getStr("uid"))){
				String x = FileService.readMusicFile(song.getStr("location"));
				if(StrKit.notBlank(x)){
					r.set("result", "200");
					r.set("uid", uid);
					r.set("src", x);
					//
					Record listener = new Record();
					listener.set("ip", ra);
					listener.set("curplay", uid);
					Db.update("listener", "ip", listener);
				}
				break;
			}
		}
		return r;
	}
	
	public static void createListener(HttpServletRequest request){
		String ra = request.getRemoteAddr();
		String curplay = request.getParameter("curplay");
		
		Integer exists = Db.queryInt("select count(*) from listener r where r.ip = ?",ra);
		if(exists==0){
			Record listener = new Record();
			listener.set("ip", ra);
			listener.set("curplay", curplay);
			Db.save("listener", listener);
		}
	}
	
	public void listenerHB(HttpServletRequest request){
		String ra = request.getRemoteAddr();
		String curplay = request.getParameter("curplay");
		Record listener = new Record();
		listener.set("ip", ra);
		listener.set("curplay", curplay);
		Db.save("listener", listener);
	}
	
}
