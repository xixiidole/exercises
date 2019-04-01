package demo;

import java.util.zip.ZipFile;

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
	
	
}
