/**
 * ====================================================================
 * �������һ���ṩWeb���ʿͻ��˵Ľӿڣ�ͨ���ýӿڿ��Է���Ľ���Web���ʣ���
 * ȡ�������˷������ݣ�Ŀǰֻ�ṩGET��POST������
 * 
 * ��������ȫ��Դ����ӭ���һ������ѧϰ��������ַ���ʲô���⣬������ʹ��
 * һ������ѧϰ
 * ====================================================================
 *
 * author��chuter
 * mail��     liulong@ir.hit.edu.cn
 * data:   2009/10/27
 */
package edu.hit.ir.ltpService;
 
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header; 
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

/**
 * Web�ͻ��˷���API��ֻ�ṩGET��POST��������������httpcleint4.0
 * ��Դ��
 * 
 * @author chuter
 * @version 0.1.a01
 * 
 */
public class WebCleintAPI {
	
	public static final DefaultHttpClient httpclient = new DefaultHttpClient();
	private static String usname;
	private static String passwd;
	private static boolean author = false;
	
	public static void setAuthor(String username, String password) {
		usname = username;
		passwd = password;
		author = true;
	}
	public static void setAuthor(String authorization) {
		usname = authorization.substring(0, authorization.indexOf(":"));
		passwd = authorization.substring(authorization.indexOf(":") + 1);
//		System.out.println("username: " +usname + "\npassword: " + passwd);
	}
	/**
	 * ��վ��URL�����ò������γ���Ҫ���ʵ�URL
	 * @param site վ��·��
	 * @param parMap ������keyΪ��������valueΪ����ֵ
	 * @param charset URL�ı����ʽ
	 * @return ���úõ�URL
	 */
	@SuppressWarnings("unchecked")
	private static String genFullUrl(String site, HashMap<String, String> parMap,
			String charset) {
		String newUrl = site + "?";
		
		Iterator it = parMap.entrySet().iterator();
        while (it.hasNext()) {  
            Map.Entry element = (Map.Entry) it.next();
            String parVal = (String)element.getValue();
            String newParVal = "";
			try {
				newParVal = URLEncoder.encode(parVal, charset);
			} catch (UnsupportedEncodingException e) {
				// log
				e.printStackTrace();
			}
            newUrl += (String)element.getKey()+"="+newParVal;
            newUrl += "&";
        }
		
        newUrl = newUrl.substring(0, newUrl.length()-1);
		return newUrl;
	}
	
	//	���������Ĳ���
	//	HttpParams params = new BasicHttpParams();
	//	HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
	//	paramsBean.setVersion(HttpVersion.HTTP_1_1);
	//	paramsBean.setContentCharset("UTF-8");
	//	paramsBean.setUseExpectContinue(true);
	
	/**
	 * ������Ҫ���ò��������󣬸���վ���ַ�Ͳ�����Ӧ��HashMap����
	 * ����������·���󣬵���doGet(String url)����
	 * 
	 * @param url
	 * @param parMap
	 * @param urlCharset URL��������
	 * @param contCharset ���ı�������
	 * @return ���ܵ����������ݣ��������Żؿ�
	 * @throws IOException 
	 */
	public static String doGet( String url, HashMap<String, String> parMap,
			String urlCharset, String contCharset ) throws IOException {
		String newUrl = genFullUrl(url, parMap, urlCharset);
		return doGet(newUrl, contCharset);
	}
	
	/**
	 * ����GET���󣬽��ܵĲ�����վ���ַ�Ͳ��������������ķ���·
	 * �������doGetObj(String url)
	 * 
	 * @param url
	 * @parMap ������Ӧ��
	 * @return ���ܵ���Object
	 * @throws IOException 
	 */
	public static Object doGetObj(String url, HashMap<String, String> parMap,
			String charset) throws IOException {
		String newUrl = genFullUrl(url, parMap, charset);
		return doGetObj(newUrl);
	}
	
	/**
	 * ����GET���󣬸�����URL���������ķ���·�������ط������˷��ص�
	 * �ڴ����ݽṹ
	 * 
	 * @param url
	 * @return ���ܵ���Object�����������null
	 * @throws IOException 
	 */
	public static Object doGetObj(String url) throws IOException {
		HttpGet httpget = new HttpGet(url);
		
		Object retObj = null;
		HttpEntity res_entity = null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			res_entity = response.getEntity();
			if ( res_entity != null ) {
				InputStream fin = res_entity.getContent();
				ObjectInputStream foin = new ObjectInputStream(fin);
				retObj = foin.readObject();
				res_entity.consumeContent();
			}
		} catch (ClientProtocolException e1) {
			// log
			e1.printStackTrace();
		} catch (IOException e1) {
			// log
			e1.printStackTrace();
		} catch (ClassNotFoundException e) {
			// log
			e.printStackTrace();
		}
		
		return retObj;
	}
	
	/**
	 * ����GET���󣬸�����URL���������ķ���·�������ַ�������ʽ
	 * ���ط������˷��ص��������ݣ�ȥ������ͷ��
	 * 
	 * @param url
	 * @return ���ܵ����������� �������Żؿ�
	 * @throws IOException 
	 */
	public static String doGet(String url, String charset) throws IOException {
		HttpGet httpget = new HttpGet(url);
//		httpclient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,  
//                new DefaultHttpRequestRetryHandler());  
		String retStr = "";
		HttpEntity entity = null;
//		System.out.println("hello!!!!!!\n");
		try {
			System.out.println(httpget.getProtocolVersion());
			
			HttpResponse response = httpclient.execute(httpget);
			
			entity = response.getEntity();
			
			if (entity != null) {
			    InputStream instream = entity.getContent();
			    int size;
			    byte[] tmp = new byte[2048];
			    while ((size = instream.read(tmp)) != -1)
			    	retStr += new String(tmp, 0, size, charset);
			    entity.consumeContent();
			}
			entity.consumeContent();
		} catch (Exception ex) {
			//log��¼
			ex.printStackTrace();
		}
		
		return retStr;
	}
	
	/**
	 * �����������������ƶ������ʽ�����POST��entity����
	 * 
	 * @param paraMap ������
	 * @param charset ��������
	 * @return entity
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("unchecked")
	private static UrlEncodedFormEntity genEntity(HashMap<String, String> paraMap,
		String charset) throws UnsupportedEncodingException {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		Iterator it = paraMap.entrySet().iterator();
        while (it.hasNext()) {  
            Map.Entry element = (Map.Entry) it.next();  
            NameValuePair nameValuePair = new BasicNameValuePair(
            		(String)element.getKey(), (String)element.getValue());
            params.add(nameValuePair);
        }
        
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, charset);
		return entity;
	}
	
	/**
	 * ����POST���󣬷��ط������˷��ص��ڴ����ݽṹ
	 * 
	 * @param url ����·��
	 * @param paraMap  ������
	 * @return ���ܵ���Object�����������null
	 * @throws IOException
	 */
	public static Object doPostObj(String url, 
						      HashMap<String, String> paraMap,
						      String charset) throws IOException {
		UrlEncodedFormEntity entity = genEntity(paraMap, charset);
		
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(entity);
		
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity res_entity = response.getEntity();
		Object retObj = null;
		
		if ( res_entity != null ) {
			InputStream fin = res_entity.getContent();
			ObjectInputStream foin = new ObjectInputStream(fin);
			try {
				retObj = foin.readObject();			
			} catch (ClassNotFoundException e) {
				// log
				e.printStackTrace();
			}
			entity.consumeContent();
		}

		return retObj;
	}
	
	/**
	 * ����POST�������ַ�������ʽ���ط������˷��ص��������ݣ����˱�ͷ��
	 * 
	 * @param url ����·��
	 * @param paraMap  ������
	 * @return ���ܵ����������ݣ����������null
	 * @throws IOException
	 */
	public static String doPost(String url,  
						      HashMap<String, String> paraMap,
						      String charset) throws IOException {
		UrlEncodedFormEntity entity = genEntity(paraMap, charset);
		
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(entity);
		/*
		if(author) {
			httpclient.getCredentialsProvider().setCredentials(new AuthScope("202.118.250.16", 54321)
				, new UsernamePasswordCredentials(usname, passwd));
		}
		//*
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute("preemptive-auth", basicAuth);

		httpclient.addRequestInterceptor((HttpRequestInterceptor) new PreemptiveAuth(), 0);
		
		HttpResponse response = httpclient.execute(httppost, localcontext);
		// */
		HttpResponse response = httpclient.execute(httppost);
		HttpEntity res_entity = response.getEntity();
		
		String retStr = "";
		if (res_entity != null) {
		    InputStream instream = res_entity.getContent();
		    int size;
		    byte[] tmp = new byte[2048];
		    while ((size = instream.read(tmp)) != -1)
		    	retStr += new String(tmp, 0, size, charset);
		    res_entity.consumeContent();
		}
		
		return retStr;
	}

	public static String doPost(String host, int port, String uri,  
						      HashMap<String, String> paraMap,
						      String charset) throws IOException {
		UrlEncodedFormEntity entity = genEntity(paraMap, charset);
		
		HttpPost httppost = new HttpPost("http://" + host + ":" + port + uri);
		httppost.setEntity(entity);
		
		httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, port)
			, new UsernamePasswordCredentials(usname, passwd));
		
		//*
		BasicScheme basicAuth = new BasicScheme();
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute("preemptive-auth", basicAuth);

		httpclient.addRequestInterceptor((HttpRequestInterceptor) new PreemptiveAuth(), 0);
		
		HttpResponse response = httpclient.execute(httppost, localcontext);
		// */
//		HttpResponse response = httpclient.execute(httppost);
		
		if( response.getStatusLine().toString().indexOf("401") >= 0) {
			throw new RuntimeException("Authorization is denied!");
		}
		HttpEntity res_entity = response.getEntity();
		
		String retStr = "";
		if (res_entity != null) {
		    InputStream instream = res_entity.getContent();
		    int size;
		    byte[] tmp = new byte[2048];
		    while ((size = instream.read(tmp)) != -1)
		    	retStr += new String(tmp, 0, size, charset);
		    res_entity.consumeContent();
		}
		
		return retStr;
	}
	static class PreemptiveAuth implements HttpRequestInterceptor {  		  
		        public void process(  
		                final HttpRequest request,   
		                final HttpContext context) throws HttpException, IOException {  
		              
		            AuthState authState = (AuthState) context.getAttribute(  
		                    ClientContext.TARGET_AUTH_STATE);  
		              
		            // If no auth scheme avaialble yet, try to initialize it preemptively  
		            if (authState.getAuthScheme() == null) {  
		                AuthScheme authScheme = (AuthScheme) context.getAttribute(  
		                        "preemptive-auth");  
		                CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(  
		                        ClientContext.CREDS_PROVIDER);  
		                HttpHost targetHost = (HttpHost) context.getAttribute(  
		                        ExecutionContext.HTTP_TARGET_HOST);  
		                if (authScheme != null) {  
		                    Credentials creds = credsProvider.getCredentials(  
		                            new AuthScope(  
		                                    targetHost.getHostName(),   
		                                    targetHost.getPort()));  
		                    if (creds == null) {  
		                        throw new HttpException("No credentials for preemptive authentication");  
		                    }  
		                    authState.setAuthScheme(authScheme);  
		                    authState.setCredentials(creds);  
		                }  
		            }  
		              
		        }  
		          
		    }  
	public static void main(String[] argv) throws ClientProtocolException, IOException {
//		System.out.println(CleitTest.doGet("http://www.baidu.com/s?wd=%C1%F5%C1%FA"));;
		HashMap<String, String> parMap = new HashMap<String, String>();

		//����POST��������Object
//		parMap.put("text", "2009�ܽ����ڹ������ٰ�����ݳ��ᣬ�����ݳ��ᳪ��һ�ס��໨�ɡ����ǳ�������2009�ܽ����ڹ������ٰ�����ݳ��ᣬ�����ݳ��ᳪ��һ�ס��໨�ɡ����ǳ�������");
//		Document doc = (Document) CleitTest.doPostObj("http://localhost:8087/tmstest", parMap, "UTF-8");;
//		for (Token token : doc.getTokens())
//			System.out.println(token.getContent()+": "+token.getPos());
		
		//����POST���������ַ���
		parMap.put("s", "2009�ܽ����ڹ������ٰ�����ݳ��ᣬ�����ݳ��ᳪ��һ�ס��໨�ɡ����ǳ�������2009�ܽ����ڹ������ٰ�����ݳ��ᣬ�����ݳ��ᳪ��һ�ס��໨�ɡ����ǳ�������");
	//	System.out.println(WebCleintAPI.doPost("http://192.168.3.134:12345/ltp", parMap, "gb2312"));
//		System.out.println(WebCleintAPI.doPostObj("http://192.168.3.134:12345/ltp", parMap, "gb2312").toString());
		
		//����GET���������ַ������޲���
//		System.out.println(WebCleintAPI.doGet("http://202.118.250.16:12345/ltp?s=%E6%B5%8B%E8%AF%95", "gb2312"));
//		System.out.println(WebCleintAPI.doGet("http://192.168.3.134:12345/ltp?s=���Ƕ����й��ˡ�", "gb2312"));
//		System.out.println(WebCleintAPI.doGet("http://www.hit.edu.cn", "gb2312"));
		
		//����GET���������ַ������в�������
//		parMap.put("s", "�ܽ���");
//		System.out.println(WebCleintAPI.doGet("http://202.118.250.16:12345/ltp", parMap, "GBk", "GBK"));
		
		//����GET��������Object
//		parMap.put("text", "�ܽ����ڹ������ٰ�����ݳ���");
//		Document doc = (Document) CleitTest.doGetObj("http://localhost:8087/tmstest", parMap);
//		for (Token token : doc.getTokens())
//			System.out.println(token.getContent()+": "+token.getPos());
	}
}
