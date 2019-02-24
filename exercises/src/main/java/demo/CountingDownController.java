package demo;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class CountingDownController extends Controller {
	@ActionKey("/countingDown1")
	public void cdp() {
       render("cdp.html");
    }
	
	@ActionKey("/countingDown2")
	public void cdp2() {
       render("cdp2.html");
    }
	
	@ActionKey("/countingDown3")
	public void cdp3() {
       render("cdp3.html");
    }
}
