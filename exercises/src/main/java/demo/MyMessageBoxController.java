package demo;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class MyMessageBoxController extends Controller {
	
	
	public void index() {
       render("myMessageBox.html");
    }
	
	@ActionKey("/tip")
	public void tip() {
		render("tip.html");
	}
}
