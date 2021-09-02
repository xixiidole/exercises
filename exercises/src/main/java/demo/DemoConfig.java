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
       //�ڵ���getPropertyToBoolean֮ǰ��Ҫ�ȵ���loadPropertyFile
       loadPropertyFile("config.properties");
       loadPropertyFile("datasource.properties");
       loadPropertyFile("uploadfile.properties");
       //����jfinal�Ŀ���ģʽ
       me.setDevMode(getPropertyToBoolean("devMode",true));
       me.setViewType(ViewType.FREE_MARKER);
       me.setMaxPostSize(100*1024*1024);//�ϴ���С
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
        //��ȡjdbc���ӳ�
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.use("datasource.properties").get("jdbcUrl"), PropKit.use("datasource.properties").get("user"), PropKit.use("datasource.properties").get("password").trim());
        druidPlugin.set(initialSize, minIdle, maxActive);
        plugins.add(druidPlugin);
        //����ActiveRecordPlugin���
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        //����̨��ʾsql���
        arp.setShowSql(getPropertyToBoolean("showSql",true));
        plugins.add(arp);
        //controller��Ӧ�����ݱ� "user"��Ӧ�������ݿ��б�����
        // User.class��Ӧ����model�е�Userģ��
        //arp.addMapping("user", User.class);
        
        //plugins.add(new EhCachePlugin());//������
        //plugins.add(new SqlInXmlPlugin());//sql xml���
    	
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