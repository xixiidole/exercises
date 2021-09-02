package demo;
import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import com.jfinal.server.undertow.UndertowServer;
import com.jfinal.template.Engine;

import demo.controller.CountingDownController;
import demo.controller.HelloController;
import demo.controller.MyMessageBoxController;
 
public class DemoConfig extends JFinalConfig {
 
    public static void main(String[] args) {
        UndertowServer.start(DemoConfig.class, 80, true);
    }
 
    public void configConstant(Constants me) {
       //me.setDevMode(true);
       //在调用getPropertyToBoolean之前需要先调用loadPropertyFile
       loadPropertyFile("config.properties");
       loadPropertyFile("datasource.properties");
       loadPropertyFile("uploadfile.properties");
       //设置jfinal的开发模式
       me.setDevMode(getPropertyToBoolean("devMode",true));
       me.setViewType(ViewType.FREE_MARKER);
       me.setMaxPostSize(100*1024*1024);//上传大小
    }
    
    public void configRoute(Routes me) {
    	me.setMappingSuperClass(false);
        me.setBaseViewPath("/WEB-INF/view");
        //me.addInterceptor();
       me.add("/", HelloController.class);
       me.add("/countingDown", CountingDownController.class);
       me.add("/myMessageBox", MyMessageBoxController.class);
        
    }
    
    public void configPlugin(Plugins plugins) {
    	int initialSize = PropKit.use("datasource.properties").getInt("initialSize");
        int minIdle = PropKit.use("datasource.properties").getInt("minIdle");
        int maxActive = PropKit.use("datasource.properties").getInt("maxActive");
        //获取jdbc连接池
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.use("datasource.properties").get("jdbcUrl"), PropKit.use("datasource.properties").get("user"), PropKit.use("datasource.properties").get("password").trim());
        druidPlugin.set(initialSize, minIdle, maxActive);
        plugins.add(druidPlugin);
        //配置ActiveRecordPlugin插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        //控制台显示sql语句
        arp.setShowSql(getPropertyToBoolean("showSql",true));
        plugins.add(arp);
        //controller对应的数据表 "user"对应的是数据库中表名，
        // User.class对应的是model中的User模型
        //arp.addMapping("user", User.class);
        
        //plugins.add(new EhCachePlugin());//缓存插件
        //plugins.add(new SqlInXmlPlugin());//sql xml插件
    	
    }
    public void configInterceptor(Interceptors me) {}
    public void configHandler(Handlers me) {
    	System.err.println("Handlers");
    	
    }

	@Override
	public void configEngine(Engine me) {
		// TODO Auto-generated method stub
		
	}
}