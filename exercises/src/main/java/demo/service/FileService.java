package demo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

public class FileService {

	@Before(Tx.class)
	public static Record startUpload(List<UploadFile> files){
		Record r = new Record();
		//创建存放地址
		String fileLoc = "";
		fileLoc = FileService.getFileFolderPath();
		if(StrKit.isBlank(fileLoc)){
			r.set("result", "error");
			r.set("msg", "上传失败，无法获取到文件保存路径！");
			return r;
		}else{
			//拼接时间
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			fileLoc+="uploadFiles"+File.separator+sdf.format(new Date());
		}
		File p = new File(fileLoc);
		if(!p.exists()){
			p.mkdirs();
		}
		if(null!=files && files.size()>0){
			for(UploadFile f : files){
				//保存
				String uid = UUID.randomUUID().toString();
				File fx = new File(fileLoc+File.separator+f.getFileName()+"__"+uid);
				f.getFile().renameTo(fx);
				//保存记录
				Record file = new Record();
				file.set("uid", uid);
				file.set("name", f.getFileName());
				file.set("contentType", f.getContentType());
				file.set("size", fx.length());//这里获取不到，试试fx
				file.set("uploadDt", new Date());
				file.set("uploadTime", new Date());
				file.set("location", fx.getAbsolutePath());
				System.err.println("上传的文件："+f.getFileName()+"-"+f.getOriginalFileName()+"-"+f.getContentType());
				Db.save("files", "uid", file);
			}
			r.set("result", "success");
			r.set("msg", "上传成功！");
		}
		return r;
	}
	
	public static String getFileFolderPath(){
		String path = PathKit.class.getClassLoader().getResource("").getPath();
		if(path.startsWith("/")){
			return path.substring(1);
		}
		return path;
	}
	
	/**
	 * 获取有上传文件的记录天数的文件记录
	 * @param days
	 * @return
	 */
	public static Map<String, List<Record>> getFileList(int days){
		String sql = "select * from files f2 where f2.uploadDt in ( select uploadDt from (select distinct f.uploadDt from files f order by f.uploadDt limit ? ) as x ) order by uploadTime desc";
		List<Record> data = Db.find(sql, days);
		Map<String, List<Record>> mapList = toMapList(data);
		return mapList;
	}
	
	public static Map<String, List<Record>> toMapList(List<Record> data){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, List<Record>> f1 = new LinkedHashMap<String, List<Record>>();
		if(null!=data && data.size()>0){
			for(Record f : data){
				List<Record> list = f1.get(sdf.format(f.getDate("uploadDt")));
				if(null==list ){
					list = new ArrayList<Record>();
				}
				list.add(f);
				f1.put(sdf.format(f.getDate("uploadDt")), list );
			}
		}
		return f1;
	}
	
}
