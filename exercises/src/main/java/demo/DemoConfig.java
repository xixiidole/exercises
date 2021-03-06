package demo;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.render.ViewType;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;
 
public class DemoConfig extends JFinalConfig {
 
    public static void main(String[] args) {
        UndertowServer.start(DemoConfig.class, 80, true);
    }
 
    public void configConstant(Constants me) {
       me.setDevMode(true);
       me.setViewType(ViewType.FREE_MARKER);
    }
    
    public void configRoute(Routes me) {
    	me.setMappingSuperClass(false);
        me.setBaseViewPath("/WEB-INF/view");
        //me.addInterceptor();
       me.add("/", HelloController.class);
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