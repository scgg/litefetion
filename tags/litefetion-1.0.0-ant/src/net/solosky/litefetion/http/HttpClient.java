 /*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 /**
 * Project  : LiteFetion
 * Package  : net.solosky.litefetion.http
 * File     : HttpClient.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-1
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class HttpClient
{
	/**
	 * 当前所有的Cookie列表
	 */
	private List<Cookie> cookieList;
	

	/**
	 * 默认的构造函数
	 */
	public HttpClient()
	{
		this.cookieList = new ArrayList<Cookie>();
	}

	/**
	 * 
	 * 执行一个请求
	 * 默认使用HttpURLConnection来完成
	 * @param request		请求对象
	 * @return				回复结果
	 * @throws IOException	如果发送读取错误，抛出IO异常
	 */
	
	public HttpResponse execute(HttpRequest request) throws IOException
	{
		//建立连接
		URL url = new URL(request.getUrl());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		//设置请求头，首先设置默认的请求头，再设置请求里面包含的请求头，请求头里面的请求头回覆盖和默认的请求头
		Map<String, String> headers = request.getHeaderMap();
		Iterator<String> it = request.getHeaderMap().keySet().iterator();
		while(it.hasNext()) {
			String key = it.next();
			String val = headers.get(key);
			conn.addRequestProperty(key, val);
		}
		
		//设置Cookie
		Iterator<Cookie> cit = this.cookieList.iterator();
		StringBuffer buffer = new StringBuffer();
		while(cit.hasNext()) {
			Cookie cookie = cit.next();
			if(cookie.getExpired()!=null && cookie.getExpired().before(new Date())) {
				cit.remove();	//已经过期，删除
			}else if( /*url.getHost().endsWith(cookie.getDomain()) && */
					url.getPath().startsWith(cookie.getPath())) {
				buffer.append(cookie.getName());
				buffer.append("=");
				buffer.append(cookie.getValue());
				buffer.append("; ");
			}else {}
		}
		conn.addRequestProperty("Cookie", buffer.toString());
		
		
		//如果有发送数据，发送数据
		if(request.getInputStream()!=null) {
			conn.setDoOutput(true);
			InputStream in = request.getInputStream();
			OutputStream out = conn.getOutputStream();
			byte[] buff = new byte[255];
			int len = -1;
			while((len=in.read(buff, 0, 255))!=-1) {
				out.write(buff, 0, len);
			}
			in.close();
		}
		
		//连接
		conn.connect();
		
		int responseCode = conn.getResponseCode();
		String responseMessage = conn.getResponseMessage();
		Map<String, List<String>> headerFields = conn.getHeaderFields();
		byte[] responseData = null;
		
		//读取服务器返回的cookie
		List<String> newCookies = conn.getHeaderFields().get("Set-Cookie");
		if(newCookies!=null) {
    		Iterator<String> nit = newCookies.iterator();
    		while(nit.hasNext()) {
    			Cookie cookie = new Cookie(nit.next());
    			Cookie oldCookie = this.getCookie(cookie.getName());
    			//如果有之前相同名字的Cookie,删除之前的cookie
    			if(oldCookie!=null) {
    					this.cookieList.remove(oldCookie);
            			//如果新cookie的值不为空，就添加到新的cookie到列表中
            			if(cookie.getValue()!=null && cookie.getValue().length()>0) {
            				this.cookieList.add(cookie);
            			}
    			}else {
    				this.cookieList.add(cookie);
    			}
    		}
		}
		
		//如果有数据，读取数据
		InputStream in = conn.getInputStream();
		if(in==null) {
			in = conn.getErrorStream();
		}
		if(in!=null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buff = new byte[255];
			int len = -1;
			while((len=in.read(buff, 0, 255))!=-1) {
				out.write(buff, 0, len);
			}
			
			responseData = out.toByteArray();
			out.close();
		}
		
		//断开连接
		conn.disconnect();
		
		//返回结果
		return new HttpResponse(responseCode, responseMessage, headerFields, responseData);
	}
	
	/**
	 * 尝试执行这个请求，如果出现IO异常，继续执行一次，直到执行成功或者到达最多执行次数
	 * 如果达到最多执行次数仍未成功，则抛出最后一次出现的IO异常
	 * @param request		请求对象
	 * @param times			尝试次数
	 * @return				回复对象
	 * @throws IOException	如果达到最多执行次数仍未成功，则抛出最后一次出现的IO异常
	 */
	public HttpResponse tryExecute(HttpRequest request, int times) throws IOException
	{
		IOException lastException = null;
		for(int i=0; i<times; i++) {
			try {
	            return this.execute(request);
            } catch (IOException e) {
            	lastException = e;
            }
		}
		throw lastException;
	}
	
	/**
	 * 获取指定名字的cookie
	 * @param name
	 * @return
	 */
	public Cookie getCookie(String name)
	{
		Iterator<Cookie> it = this.cookieList.iterator();
		while(it.hasNext()) {
			Cookie cookie = it.next();
			if(cookie.getName().equals(name)) {
				return cookie;
			}
		}
		return null;
	}
	
	/**
	 * 删除指定名字的cookie
	 * @param name
	 */
	public void removeCookie(String name) {
		Cookie cookie = this.getCookie(name);
		if(cookie!=null) {
			this.cookieList.remove(cookie);
		}
	}
	
	/**
	 * 返回所有的cookie列表
	 * @return
	 */
	public List<Cookie> getCookieList()
	{
		return this.cookieList;
	}
}
