package com.koobest.socket;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.koobest.constant.ConfigConstant;
import com.koobest.constant.URLConstant;
import com.koobest.parse.DomParse;

public class HttpConnection {
	
	private String scheme = URLConstant.SCHEME;
	private String host = URLConstant.HOST;
	private int port = URLConstant.PORT;
	private String path = URLConstant.PATH;
	
	private int TIME_CONNECTION_OUT = 10000;
	private int TIME_OUT = 10000;
	//public static String token = null;
	public String type = null;
	//HTTP CONNECTION PARAMS
	private HttpParams httpParams = new BasicHttpParams();
	private HttpClient client = null;
	
	HttpResponse response = null;
	public HttpConnection(){
		//GET THE HTTPCLIENT INSTANCE
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
    	HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
    	HttpConnectionParams.setConnectionTimeout(httpParams,TIME_CONNECTION_OUT);
    	HttpConnectionParams.setSoTimeout(httpParams, TIME_OUT);
    	client = new DefaultHttpClient(httpParams);
	}
	
	public void setParams(String scheme, String host, int port, String path){
		this.scheme = scheme;
		this.host = host;
		this.port = port;
		this.path = path;
	}
	
	public String login(String name,String pwd){
    	URI uri = null;
    	String token = null;
    	String call = "route=common/login";
    	try{
    		//定义post参数，采用key-value形式。
    		List<NameValuePair> keyvalue = new ArrayList<NameValuePair>();
    	
    		//keyvalue.add(new BasicNameValuePair("route","commcon/login"));
    		keyvalue.add(new BasicNameValuePair("username",name));
    		keyvalue.add(new BasicNameValuePair("password",pwd));
    	
    		//定义URI，
			
    		uri = URIUtils.createURI(scheme, host , port , path , call ,null);
    		
    		//设置HTTP实体内容，将post参数写入。
    		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(keyvalue, HTTP.UTF_8);
    		//MultipartEntity m_entity = new UrlEncodedFormEntity(keyvalue, HTTP.UTF_8);
    		 //创建一个post实体
    		HttpPost post = new HttpPost(uri); 
    		post.setEntity(entity);
    		System.out.println(post.getURI().toString());
    		//调用客户端发送请求并获得响应
    		HttpResponse response = client.execute(post);
    	
    	
			//    	BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			//    	while(reader.readLine() != null){
			//    		System.out.println(reader.readLine());
			//    	}
    	//获得头信息，提取token
    	Header[] header = response.getHeaders("token");
    	
    	if(header.length != 0)
    		token = header[0].toString().split(": ")[1];
    	else 
    		return null;
    	//连接管家
//    	ClientConnectionManager cm = client.getConnectionManager();
//    	cm.shutdown();
    	System.out.println(token);
    	}catch(Exception e){
    		e.printStackTrace();
    		System.out.println(e.toString());
    	}
    	return token;
    }
    
	public HttpResponse getResultsByPost(List<NameValuePair> params) throws Exception{
		URI uri = null;
		HttpResponse responseFromPost = null;
		String queryfile = params.get(0).getName()+"="+params.get(0).getValue();
    	
    	
    		//定义URI，
			
    		uri = URIUtils.createURI(scheme, host , port , path , queryfile ,null);
    		//设置HTTP实体内容，将post参数写入。
    		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
    		//MultipartEntity m_entity = new UrlEncodedFormEntity(keyvalue, HTTP.UTF_8);
    		 //创建一个post实体
    		HttpPost post = new HttpPost(uri); 
    		post.setEntity(entity);
    		
    		//调用客户端发送请求并获得响应
    		responseFromPost = client.execute(post);
    		this.response = responseFromPost;
    
    
	    	//连接管家
	//    	ClientConnectionManager cm = client.getConnectionManager();
	//    	cm.shutdown();
    	
    	
    	
    	return responseFromPost;
	}
	
	/**
	 * 
	 * @param params
	 * @param savedPath
	 * @return If savedPath is null,it returns the DomParse instance with the inputstream,otherwise write to the xml 
	 * @throws Exception 
	 */
	public DomParse getResultsByGet(List<NameValuePair> params, String savedPath) throws URISyntaxException,IOException,SAXException,ParserConfigurationException,Exception {
		String routeHead = null;
		if(params.size() != 1)
			routeHead = params.get(0).getName()+"="+params.get(0).getValue()+"&";
		else
			routeHead = params.get(0).getName()+"="+params.get(0).getValue();
		DomParse dp = null;
		params.remove(0);
		
		
    	String query = routeHead + URLEncodedUtils.format(params, HTTP.UTF_8);
    	
    	
//    	try{
    	URI uri = URIUtils.createURI(scheme, host, port, path, query, null);
    		//获得HttpGet实体
    	HttpGet get = new HttpGet(uri);
    		
    		//发送请求
    	response = client.execute(get);
//    	System.out.println(response.getStatusLine().getReasonPhrase() + " Inside");
    		
    		
	    dp = new DomParse(response.getEntity().getContent());
	       
	    if(savedPath != null){	
	        dp.setWritedFilePath(savedPath);
	        
	        type = dp.getSingleContent("type");
	        dp.writeXML();
	        DomParse.createRCOrder(savedPath,new DomParse(ConfigConstant.MAINCFGFILEPATH),true);
	        System.out.println("write file finish");
	        
	        return null;
    	} else {
    		return dp;
    	}
	}
	
	
	
	public HttpResponse getResponse(){
		if(response == null)
			throw new NullPointerException("The Response is null, make sure call this function after send a get or post methos");
		return this.response;
	}
	
	public static boolean hasInternet(Activity activity) {
		ConnectivityManager manager = (ConnectivityManager) activity
		.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		if (info == null || !info.isConnected()) {
			return false;
		}
			if (info.isRoaming()) {
		// here is the roaming option you can change it if you want to
		// disable internet while roaming, just return false
				return true;
			}
				return true;
		 
		}
	
}
