package com.xiangGo.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class HttpUtil {

    public static String sendGet(String strUrl) {
		
    	final StringBuilder result = new StringBuilder();
		
		doConnection(strUrl, new ConnConnDealer() {
			@Override
			public void isDeal(HttpURLConnection connection) throws IOException {
				super.isDeal(connection);
				result.append(connToString(connection));
			}
		});
		
		return result.toString();
    }

	public static String sendPost(String strUrl, final String param) {

		final StringBuilder result = new StringBuilder();
		
		//System.out.println("HttpUtil.sendPost, url:"+strUrl+"\n"+param);
		
		doConnection(strUrl, new ConnConnDealer() {
			
			@Override
			public void settingDeal(HttpURLConnection connection) throws ProtocolException {
				super.settingDeal(connection);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Accept-Charset", "utf-8");
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
			}
			
			@Override
			public void osDeal(HttpURLConnection connection) throws IOException {
				if(null != param){
					try {
						DataOutputStream out = new DataOutputStream(connection.getOutputStream());
						out.write(param.getBytes("utf-8"));//out.writeBytes(param);//out.write(param.getBytes("utf-8"));//encode err?
						out.flush();
						out.close();
					} catch (IOException e) {
					}
				}
				super.osDeal(connection);
			}
			
			@Override
			public void isDeal(HttpURLConnection connection) throws IOException {
				super.isDeal(connection);
				result.append(connToString(connection));
			}
		});
		
		return result.toString();
	}

	public static String makeParamString(Map<String, Object> postData) throws UnsupportedEncodingException{

		String param = null;
		if(null != postData){
			StringBuilder sb = new StringBuilder();

			Object[] ks = postData.keySet().toArray();
    		for (int i=0;i<ks.length;i++) {
    			if(i!=0){
    				sb.append("&");
    			}
    			
    			sb.append(ks[i]+"="+URLEncoder.encode(String.valueOf(postData.get(ks[i])), "UTF-8"));
    		}
    		param = sb.toString();
		}
		return param;
	}
    
	
    private static String connToString(HttpURLConnection connection) throws IOException{
    	String result = null;
    	
    	if(connection.getResponseCode() != 200){
    		HashMap hash = new HashMap();
    		hash.put("error", connection.getResponseCode() + ","+ connection.getResponseMessage());
    		result = JsonUtil.toJsonString(hash);
    		return result;
    	}
    	
    	result = convertStreamToString(connection.getInputStream());
		return result;
    }
    
    public static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {      
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));      
        StringBuilder sb = new StringBuilder();      
       
        String line = null;      
        try {
        	int i=0;
            while ((line = reader.readLine()) != null) {
            	if(i++!=0){
            		sb.append("\n");
            	}
                sb.append(line);
            }
        } catch (IOException e) {      
            //e.printStackTrace();      
        } finally {      
            try {      
                is.close();      
            } catch (IOException e) {      
               //e.printStackTrace();      
            }      
        }      
        return sb.toString();      
    }  

	
	private static void doConnection(String strUrl, ConnConnDealer dealer){
		
		HttpURLConnection connection = null;

		try {
			URL url = new URL(strUrl);
			connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setUseCaches(false);
			
			connection.setRequestProperty("User-Agent", "WebService");
			connection.setRequestProperty("accept", "*/*");
			
			if(null != dealer){
				dealer.settingDeal(connection);
			}
			
			connection.connect();

			if(null != dealer){
				dealer.osDeal(connection);
				if(connection.getRequestMethod() != "GET"){
					OutputStream os = connection.getOutputStream();
					if(null != os){
						try{
							os.flush();
						}catch(Exception ex){
						}finally{
							try{
								os.close();
							}catch(Exception ex){}
						}
					}
				}
			}

			if(null != dealer){
				dealer.isDeal(connection);
				InputStream is = connection.getInputStream();
				if(null != is){
					try{
					}catch(Exception ex){
					}finally{
						try{
							is.close();
						}catch(Exception ex){}
					}
				}
			}

		} catch (IOException e) {
			//e.printStackTrace();
			System.out.println("HttpUtil.doConnection error, url:"+strUrl);
			return;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}
	
	
	private static abstract class ConnConnDealer
	{
		public void settingDeal(HttpURLConnection connection) throws ProtocolException{
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			
			connection.setRequestMethod("GET");
		}
		
		/**
		 */
		public void osDeal(HttpURLConnection connection) throws IOException{
			
		}
		
		/**
		 */
		public void isDeal(HttpURLConnection connection) throws IOException{
			
		}
	}
	
	
}
