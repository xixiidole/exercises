package demo;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;

import com.jfinal.config.*;
import com.jfinal.core.Controller;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
 
public class DemoConfig extends JFinalConfig {
 
    public static void main(String[] args) {
        UndertowServer.start(DemoConfig.class, 80, true);
    }
 
    public void configConstant(Constants me) {
       me.setDevMode(true);
    }
    
    public void configRoute(Routes me) {
    	me.setMappingSuperClass(false);
        me.setBaseViewPath("/WEB-INF/view");
        //me.addInterceptor();
       //me.add("/hello", HelloController.class);
       me.add("/countingDown", CountingDownController.class);
       me.add("/myMessageBox", MyMessageBoxController.class);
        
    }
    
    public void configPlugin(Plugins me) {}
    public void configInterceptor(Interceptors me) {}
    public void configHandler(Handlers me) {
    	System.err.println("Handlers");
    	
    }

	@Override
	public void configEngine(Engine me) {
		// TODO Auto-generated method stub
		
	}
}