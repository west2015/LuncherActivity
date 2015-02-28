package com.nhd.mall.util;
import java.io.IOException;
import android.util.Log;

import com.nhd.mall.entity.Status;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

/**
 * 类<code>ReadJson</code>本地json解析
 * 
 * @author vendor
 * @version 2012年11月10日 16:00:50
 * @see Class
 * @since JDK1.0
 * 
 */
public class ParseJson {
	
	private final static String TAG = "Parse Status";
	
	private static ObjectMapper objectMapper;
	
	/**
	 * 获取当前返回的状态文字
	 * 
	 * @return String
	 */
	public static String getStatusAsString(String json) {
		
		getObjectMapper();
		
		Status status = null;

		try {
			status = objectMapper.readValue(json, Status.class);
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			Log.e(TAG, json);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if(status != null){
			if("success".equals(status.getStatus())){
				return "操作成功";
			}else if("error".equals(status.getStatus()) && status.getErrorMessage() != null){
				return status.getErrorMessage();
			}
		}
		
		return "操作失败,无法分析错误原因";
	}

	public static Status getStatusAsModel(String json) {
		
		getObjectMapper();
		
		try {
			return objectMapper.readValue(json, Status.class);
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			Log.e(TAG, json);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public static <T> T parse(String json, Class<T> cls) {
		
		getObjectMapper();
		
		try {
			return objectMapper.readValue(json, cls);
		} catch (JsonParseException e1) {
			// TODO Auto-generated catch block
			Log.e(TAG, json);
		} catch (JsonMappingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace(); 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
				e1.printStackTrace();
		}
		return null;
	}
	
	public static ObjectMapper getObjectMapper(){
		if( objectMapper == null){
			objectMapper = new ObjectMapper();
			
			// Add the Jackson message converter
			MappingJacksonHttpMessageConverter stringHttpMessageConverternew = new MappingJacksonHttpMessageConverter();

			// 忽略不被自身识别的json节点
			objectMapper.configure(org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			stringHttpMessageConverternew.setObjectMapper(objectMapper);
		}
		
		return objectMapper;
	}
	
	public static String getObjectJackson(Object object){
		
		getObjectMapper();
		
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
