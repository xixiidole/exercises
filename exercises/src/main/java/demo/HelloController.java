package demo;

import com.jfinal.core.ActionKey;
import com.jfinal.core.Controller;

public class HelloController extends Controller {
	
	public void index() {
	       renderText("Hello JFinal World.");
	}
	
	public void test() {
		getPara("layoutKey");
		renderText("Hello JFinal World2.");
	}
	
	public void doodle(){
		render("doodle.html");
	}
	
	public void iframe(){
		render("iframe.html");
	}
	
	public void bigPrice(){
		render("spin.html");
	}
	
	@ActionKey("/click")
	public void click(){
		render("click.html");
	}
	
}
