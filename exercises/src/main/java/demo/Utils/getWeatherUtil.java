package demo.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class getWeatherUtil {

	/**
	 * 简单调用
	 * @param url webService地址
	 * @param namespace 命名空间
	 * @param method 调用方法
	 * @param returnType 返回类型
	 * @param inParams 传入参数
	 * @return Result<Object> 返回的数据
	 */
	public static Map<String,Object> SimpleCall(String url, String namespace, String method, QName returnType, List<Map<String,Object>> inParams){
		Map<String,Object> rm = new HashMap<String, Object>();
		try {
			// 直接引用远程的wsdl文件
			Service service = new Service();
			Call call = (Call) service.createCall();
			
			// 添加命名空间，要调用的方法名
			call.setOperationName(new QName(namespace, method));
			call.setTargetEndpointAddress(url);
			call.setUseSOAPAction(true); 
			if(null==returnType){
				call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); //返回参数的类型
			}else{
				call.setReturnType(returnType); //返回参数的类型
			}
			call.setSOAPActionURI(namespace + method);
			
			Object[] paramValues = null;
			if(null!=inParams && !inParams.isEmpty()){
				paramValues = new Object[inParams.size()];
				//不为空
				for(int i=0; i<inParams.size(); i++){
					//不这样写，测试时会出现"未将XX设置到实例"的错误
					call.addParameter(new QName(namespace,(String)inParams.get(i).get("paramName")), (QName)inParams.get(i).get("paramType"), javax.xml.rpc.ParameterMode.IN);// 接口的参数
					paramValues[i] = (String)inParams.get(i).get("paramValue");
				}
			}else{
				paramValues = new Object[0];
			}
			
			// 给方法传递参数，并且调用方法
			Object returnValue =  (Object) call.invoke(paramValues);
			
			rm.put("result", "success");
			rm.put("data", returnValue);
			
		} catch (Exception e) {
			rm.put("result", "error");
			rm.put("msg", "调用出错！未调用。");
			e.printStackTrace();
		}
		
		return rm;
		
	}
}
