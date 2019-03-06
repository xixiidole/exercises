package demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class CountingDownController extends Controller {
	@ActionKey("/CD")
	public void cdp() {
       render("cdp3.html");
    }
	
	public void index() {
       renderText("Not In Here!");
    }
	
	@ActionKey("/todo")
	public void todo() {
		//读取文件
		String realPath = getSession().getServletContext().getRealPath("download");
		setAttr("REALPATH", realPath);
		render("todo.html");
    }
	
}
