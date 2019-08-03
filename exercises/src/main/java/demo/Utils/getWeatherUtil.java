package demo.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

public class getWeatherUtil {

	/**
	 * �򵥵���
	 * @param url webService��ַ
	 * @param namespace �����ռ�
	 * @param method ���÷���
	 * @param returnType ��������
	 * @param inParams �������
	 * @return Result<Object> ���ص�����
	 */
	public static Map<String,Object> SimpleCall(String url, String namespace, String method, QName returnType, List<Map<String,Object>> inParams){
		Map<String,Object> rm = new HashMap<String, Object>();
		try {
			// ֱ������Զ�̵�wsdl�ļ�
			Service service = new Service();
			Call call = (Call) service.createCall();
			
			// ��������ռ䣬Ҫ���õķ�����
			call.setOperationName(new QName(namespace, method));
			call.setTargetEndpointAddress(url);
			call.setUseSOAPAction(true); 
			if(null==returnType){
				call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); //���ز���������
			}else{
				call.setReturnType(returnType); //���ز���������
			}
			call.setSOAPActionURI(namespace + method);
			
			Object[] paramValues = null;
			if(null!=inParams && !inParams.isEmpty()){
				paramValues = new Object[inParams.size()];
				//��Ϊ��
				for(int i=0; i<inParams.size(); i++){
					//������д������ʱ�����"δ��XX���õ�ʵ��"�Ĵ���
					call.addParameter(new QName(namespace,(String)inParams.get(i).get("paramName")), (QName)inParams.get(i).get("paramType"), javax.xml.rpc.ParameterMode.IN);// �ӿڵĲ���
					paramValues[i] = (String)inParams.get(i).get("paramValue");
				}
			}else{
				paramValues = new Object[0];
			}
			
			// ���������ݲ��������ҵ��÷���
			Object returnValue =  (Object) call.invoke(paramValues);
			
			rm.put("result", "success");
			rm.put("data", returnValue);
			
		} catch (Exception e) {
			rm.put("result", "error");
			rm.put("msg", "���ó���δ���á�");
			e.printStackTrace();
		}
		
		return rm;
		
	}
}
