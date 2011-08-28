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
 * Package  : net.solosky.litefetion
 * File     : LiteFetion.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-2
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.imageio.ImageIO;

import net.solosky.litefetion.bean.ActionResult;
import net.solosky.litefetion.bean.Buddy;
import net.solosky.litefetion.bean.BuddyState;
import net.solosky.litefetion.bean.ClientState;
import net.solosky.litefetion.bean.Cord;
import net.solosky.litefetion.bean.Presence;
import net.solosky.litefetion.bean.Relation;
import net.solosky.litefetion.bean.Settings;
import net.solosky.litefetion.bean.User;
import net.solosky.litefetion.bean.VerifyImage;
import net.solosky.litefetion.http.Cookie;
import net.solosky.litefetion.http.HttpClient;
import net.solosky.litefetion.http.HttpRequest;
import net.solosky.litefetion.http.HttpResponse;
import net.solosky.litefetion.notify.ApplicationConfirmedNotify;
import net.solosky.litefetion.notify.BuddyApplicationNotify;
import net.solosky.litefetion.notify.BuddyMessageNotify;
import net.solosky.litefetion.notify.BuddyStateNotify;
import net.solosky.litefetion.notify.ClientStateNotify;
import net.solosky.litefetion.notify.Notify;
import net.solosky.litefetion.util.StringHelper;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * LiteFetion的主客户端
 * 使用WebFetion的协议完成和服务器的交互
 *
 * @author solosky <solosky772@qq.com>
 */
public class LiteFetion
{

	/**
	 * HttpClient,封装了对http访问
	 */
	private HttpClient client;
	
	/**
	 * 用户对象
	 */
	private User user;
	
	/**
	 * 好友列表
	 */
	private ArrayList<Buddy> buddyList;
	
	/**
	 * 分组列表
	 */
	private ArrayList<Cord> cordList;
	
	/**
	 * 客户端状态
	 */
	private volatile ClientState clientState;
	
	/**
	 * 请求的版本,每请求一次递增
	 */
	private int requestVersion;
	
	/**
	 * SessionId
	 */
	private String sessionId;
	
	/**
	 * 获取Notify的失败次数
	 */
	private int pollNotifyFailed;
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(LiteFetion.class);
	
	
	/**
	 * 默认的构造函数
	 */
	public LiteFetion() {
		this.client = new HttpClient();
		this.user = new User();
		this.buddyList = new ArrayList<Buddy>();
		this.cordList = new ArrayList<Cord>();
		this.clientState = ClientState.NEW;
		this.requestVersion = 0;
		this.pollNotifyFailed = 0;
	}
	

	/**
	 * 登录，会进行一些的操作，页面登录，获取个人信息，获取好友列表，获取好友状态等。。
	 * 如果一切都没有出现错误，才表明登录正确完成，
	 * 
	 * @param account		用户名，可以是手机号，飞信号，注册的邮件
	 * @param password		密码
	 * @param presence		登录状态
	 * @param verifyImage	验证码，登录的过程需要验证码
	 * @return				操作结果
	 */
	public ActionResult login(String account, String password, 
			Presence presence, VerifyImage verifyImage){
		
		this.updateClientState(ClientState.LOGGING);
        
		//登录
		ActionResult result = this.signIn(account, password, presence, verifyImage);
        logger.debug("[Login] #1 SignIn:"+result.toString());
        if(result!=ActionResult.SUCCESS)	return this.processLoginFailed(result, 1);
        
        //获取个人信息
        result = this.retirePersonalInfo();
        logger.debug("[Login] #2 retirePersonalInfo:"+result.toString());
        if(result!=ActionResult.SUCCESS)	return this.processLoginFailed(result, 2);
        
        //获取好友列表
        result = this.retireBuddyList();
        logger.debug("[Login] #3 retireBuddyList:"+result.toString());
        if(result!=ActionResult.SUCCESS)	return this.processLoginFailed(result, 3);
        
        
        //获取好友在线信息
        this.pollNotify();
        logger.debug("[Login] #4 Poll Buddy State Notify: Success.");
        
        logger.debug("[Login] Login Success.");
        this.updateClientState(ClientState.ONLINE);
        return ActionResult.SUCCESS;
	}
	
	
	/**
	 * 退出登录
	 * @return		操作结果
	 */
	public ActionResult logout(){
		this.signOut();
		this.updateClientState(ClientState.LOGOUT);
		return ActionResult.SUCCESS;
	}
	
	/**
	 * 获取验证码图片
	 * @param type			验证码类型，也就是验证图片的sessionId， 定义在VerifyImage.TYPE_*
	 * @return				验证码，如果失败返回null
	 */
	public VerifyImage retireVerifyImage(String type){
		try {
	        String picurl = StringHelper.format(Settings.WEBIM_URL_GET_PIC, type);
	        HttpRequest request = this.createHttpRequest(picurl, "GET");
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        VerifyImage image = new VerifyImage();
	        image.setVerifyType(type);
	        image.setImageData(response.getResponseData());
	        image.setSessionId(this.client.getCookie(type).getValue());
	        return image;
        } catch (IOException e) {
        	return null;
        }
	}
	
	
	/**
	 * 创建请求，会自动设置相应的头部
	 * @param url		地址
	 * @param method	方法
	 * @return
	 */
	private HttpRequest createHttpRequest(String url, String method) {
		HttpRequest request = new HttpRequest(url, method);
		request.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2.10) Gecko/20100914 Firefox/3.6.10 (.NET CLR 3.5.30729)");
		request.addHeader("Accept", "text/html,application/xhtml+xml,application/xml,image/png,image/*;q=0.9,*/*;q=0.8");
		request.addHeader("Accept-Language", "en-us,en;q=0.5");
		request.addHeader("Accept-Encoding", "gzip,deflate");
		request.addHeader("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		return request;
	}
	
	/**
	 * 创建操作请求，会自动的设置version和ssid
	 * @param url
	 * @return
	 */
	private HttpRequest createActionHttpRequest(String url) {
		url = StringHelper.format(url, this.nextRequestVersion());
		HttpRequest request = this.createHttpRequest(url, "POST");
		request.addPostValue("ssid", this.sessionId);
		return request;
	}
	
	/**
	 * 返回当前请求版本，并递增请求版本
	 * @return
	 */
	private synchronized int nextRequestVersion() {
		return this.requestVersion++;
	}
	
	
	/**
	 * 登录入口登录,完成主页面登录请求
	 * @return
	 */
	private ActionResult signIn(String account, String password, 
			Presence presence , VerifyImage verifyImage)
	{
		try {
	        HttpRequest request = this.createHttpRequest(Settings.WEBIM_URL_LOGIN, "POST");
	        request.addPostValue("UserName", account);
	        request.addPostValue("Pwd", password);
	        if(verifyImage != null ) {
		        request.addPostValue("Ccp", verifyImage.getVerifyCode());
	        }
	        request.addPostValue("OnlineStatus", Integer.toString(presence.getValue()));
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status =  json.getInt("rc");
	        if(status==200) {
	        	Cookie cookie = this.client.getCookie("webim_sessionid");
	        	if(cookie!=null) {
	        		this.sessionId = cookie.getValue();
	        		this.client.getCookieList().remove(cookie);
	        	}
	        	
	        	return ActionResult.SUCCESS;
	        }else if(status==312){
	        	return ActionResult.VERIFY_FAILED;
	        }else if(status==404) {
	        	return ActionResult.USER_NOT_FOUND;
	        }else if(status==321) {
	        	return ActionResult.PASSWORD_NOT_MATCH;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
        } catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 退出登录，完成主页面退出请求
	 * @return
	 */
	private ActionResult signOut()
	{
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_LOGOUT);
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status =  json.getInt("rc");
	        return status==200 ? ActionResult.SUCCESS : ActionResult.REQUEST_FAILED;
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
}
	
	/**
	 * 处理登录失败
	 */
	private ActionResult processLoginFailed(ActionResult result, int step) {
		this.updateClientState(ClientState.LOGIN_FAIL);
		logger.warn("Login failed: [result="+result+", step="+step+"]");
		return result;
	}
	
	/**
	 * 更新客户端状态
	 */
	private synchronized void updateClientState(ClientState state) {
		this.clientState = state;
	}
	
	/**
	 * 返回当前客户端状态
	 * @return
	 */
	public ClientState getClientState() {
		return this.clientState;
	}
	
	//////////////////////////////////操作开始///////////////////////////////////////
	
	/**
	 * 获取个人信息
	 */
	private ActionResult retirePersonalInfo()
	{
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_GET_PERSONAL_INFO);
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        if(json.getInt("rc")==200) {
	        	
	        	json = json.getJSONObject("rv");
	        	user.setMobile(json.getLong("mn"));
	        	user.setNickName(json.getString("nn"));
	        	user.setUri(json.getString("uri"));
	        	user.setSid(json.getInt("sid"));
	        	user.setUserId(json.getInt("uid"));
	        	user.setImpresa(json.getString("i"));
	        	
	        	return ActionResult.SUCCESS;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 获取好友的列表
	 * @return		返回操作结果
	 */
	private ActionResult retireBuddyList()
	{
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_GET_CONTACT_LIST);
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        if(json.getInt("rc")==200) {
	        	json = json.getJSONObject("rv");
	        	//好友列表
	        	this.buddyList.clear();
	        	JSONArray buddies = json.getJSONArray("bds");
	        	for(int i=0;i<buddies.length(); i++) {
	        		JSONObject jo = buddies.getJSONObject(i);
	        		Buddy buddy = new Buddy();
	        		buddy.setUri(jo.getString("uri"));
	        		buddy.setUserId(jo.getInt("uid"));
	        		buddy.setBlack(jo.getInt("isBk")==1);
	        		buddy.setLocalName(jo.getString("ln"));
	        		buddy.setCordIds(jo.getString("bl"));
	        		buddy.setRelation(Relation.valueOf(jo.getInt("rs")));
	        		
	        		this.buddyList.add(buddy);
	        	}
	        	
	        	//分组列表
	        	this.cordList.clear();
	        	JSONArray cords = json.getJSONArray("bl");
	        	for(int i=0; i<cords.length(); i++) {
	        		JSONObject jo = cords.getJSONObject(i);
	        		Cord cord = new Cord();
	        		cord.setId(jo.getInt("id"));
	        		cord.setTitle(jo.getString("n"));
	        		
	        		this.cordList.add(cord);
	        	}
	        	
	        	return ActionResult.SUCCESS;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 发送消息
	 * @param buddy		好友对象
	 * @param message	消息内容
	 * @param isSendSMS	是否发送短信		
	 * @return 			操作结果
	 */
	public ActionResult sendMessage(Buddy buddy, String message, boolean isSendSMS)
	{
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_SEND_MESSAGE);
	        request.addPostValue("To", Integer.toString(buddy.getUserId()));
	        request.addPostValue("msg", message);
	        request.addPostValue("IsSendSms", isSendSMS?"1":"0");
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status = json.getInt("rc");
	        if(status==200) {
	        	return ActionResult.SUCCESS;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	
	/**
	 * 给自己发送短信
	 * @param message		消息内容
	 * @return				操作结果
	 */
	public ActionResult sendSelfSMS(String message) {
		return this.sendMessage(this.getUser(), message, true);
	}
	
	/**
	 * 设置心情短语
	 * @param impresa	心情短语
	 * @return			操作结果
	 */
	public ActionResult setImpresa(String impresa) {
        try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_SET_PERSONAL_INFO);
	        request.addPostValue("Impresa", impresa);
	        //request.addPostValue("NickName", "haha");
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status = json.getInt("rc");
	        if(status==200) {
	        	this.user.setImpresa(impresa);
	        	return ActionResult.SUCCESS;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
        } catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 设置状态
	 * @param presence		状态:ONLINE, AWAY, BUSY, OFFLINE(HIDDEN)
	 * @param custom		对这个状态的自定义说明，比如当状态为AWAY时，状态的说明可以是 "我吃饭去啦~"
	 * @return				操作结果
	 */
	public ActionResult setPresence(Presence presence, String custom) {
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_SET_PERSONAL_INFO);
	        request.addPostValue("Presence", Integer.toString(presence.getValue()));
	        request.addPostValue("Custom", custom);
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status = json.getInt("rc");
	        if(status==200) {
	        	this.user.setPresence(presence.getValue());
	        	return ActionResult.SUCCESS;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 添加好友，需要首先获取验证码,验证码的类型为VerifyImage.TYPE_ADD_BUDDY
	 * @param account			飞信号或者手机号
	 * @param desc				对自己的说明(我是{$desc})
	 * @param localName			设置显示本地姓名
	 * @param cord				所属分组，如果为null添加到默认分组
	 * @param verifyImage		验证码，验证码的类型为VerifyImage.TYPE_ADD_BUDDY，需要首先获取
	 * @return					操作结果
	 */
	public ActionResult addBuddy(String account, String desc, String localName, Cord cord, VerifyImage verifyImage) {
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_ADD_BUDDY);
	        request.addPostValue("AddType", account.length()==11?"1":"0");		//手机号为1,飞信号为0
	        request.addPostValue("UserName", account);
	        request.addPostValue("Desc", desc==null?"":desc);
	        request.addPostValue("LocalName", localName==null?"":localName);
	        request.addPostValue("Ccp", verifyImage.getVerifyCode());
	        request.addPostValue("CcpId", verifyImage.getSessionId());

	        request.addPostValue("BuddyLists", cord==null?"0":Integer.toString(cord.getId()));
	        request.addPostValue("PhraseId", "0");
	        request.addPostValue("SubscribeFlag", "0");
	        
	        this.client.removeCookie(VerifyImage.TYPE_ADD_BUDDY);
	        
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status = json.getInt("rc");
	        if(status==200) {
	        	
	        	JSONObject jo = json.getJSONObject("rv");
        		Buddy buddy = new Buddy();
        		buddy.setUri(jo.getString("uri"));
        		buddy.setUserId(jo.getInt("uid"));
        		buddy.setBlack(false);
        		buddy.setLocalName(jo.getString("ln"));
        		buddy.setRelation(Relation.UNCONFIRMED);
        		
        		this.buddyList.add(buddy);
        		
	        	return ActionResult.SUCCESS;
	        }else if(status==312) {
	        	return ActionResult.VERIFY_FAILED;		//验证失败
	        }else if(status==404) {
	        	return ActionResult.USER_NOT_FOUND;		//用户不存在
	        }else if(status==521) {
	        	return ActionResult.BUDDY_EXISTS;		//已经在好友列表中
	        }else {
	        	logger.debug("addBuddy failed, unkown status:"+status);
	        	return ActionResult.REQUEST_FAILED;
	        }
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 处理添加好友的请求
	 * @param buddy			发起请求的好友
	 * @param isAgree		是否同意，如果同意，默认添加到默认分组
	 * @param localName		如果同意，可以设置本地显示的名字； 如果不同意，直接传递null即可
	 * @param cord			如果同意，添加好友的分组列表； 如果不同意，直接传递null即可
	 * @return				操作结果
	 */
	public ActionResult handleBuddyApplication(Buddy buddy, boolean isAgree, String localName, Cord cord) {
		 try {
		        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_HANDLE_ADD_BUDDY);
		        request.addPostValue("BuddyId", Integer.toString(buddy.getUserId()));
		        request.addPostValue("Result", isAgree?"1":"0");
		        request.addPostValue("BuddyList", cord==null?"0":Integer.toString(cord.getId()));
		        request.addPostValue("LocalName", localName==null?"":localName);
		        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
		        JSONObject json = new JSONObject(response.getResponseString());
		        int status = json.getInt("rc");
		        if(status==200) {
		        	if(isAgree) {
		        		buddy.setRelation(Relation.BUDDY);	//更新关系为好友关系
		        	}else {
		        		this.buddyList.remove(buddy);		//不同意，就从好友列表中删除这个临时好友
		        	}
		        	return ActionResult.SUCCESS;
		        }else {
		        	return ActionResult.REQUEST_FAILED;
		        }
		        
		} catch (IOException e) {
	        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 添加好友到黑名单
	 * @param buddy		好友对象
	 * @return
	 */
	public ActionResult blackBuddy(Buddy buddy) {
		try {
		 	HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_OP_BUDDY);
	        request.addPostValue("Op", "1");
	        request.addPostValue("To", Integer.toString(buddy.getUserId()));
	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
	        JSONObject json = new JSONObject(response.getResponseString());
	        int status = json.getInt("rc");
	        if(status==200) {
	        	buddy.setBlack(true);
	        	return ActionResult.SUCCESS;
	        }else {
	        	return ActionResult.REQUEST_FAILED;
	        }
	        
		} catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        } catch (JSONException e) {
        	return ActionResult.JSON_FAILED;
        }
	}
	
	/**
	 * 批量发送短信
	 * @param toBuddies		接受好友的列表，可以包含用户对象(litefetion.getUser())
	 * @param message		原始消息内容，无需编码，发送时会自动编码
	 * @return				操作状态
	 */
	public ActionResult batchSendSMS(List<Buddy> toBuddies, String message) {
		try {
			if(toBuddies==null || toBuddies.size()==0)	return ActionResult.WRONG_PARAM;
			
            //接受者参数应该是 346339663,346379375这样的格式
            Iterator<Buddy> it = toBuddies.iterator();
            StringBuffer recievers = new StringBuffer();
            while(it.hasNext()) {
            	  Buddy b = it.next();
            	  recievers.append(Integer.toString(b.getUserId()));
            	  if(it.hasNext()) {	//不是最后一个，则添加,
            		  recievers.append(",");
            	  }
            }
			
    		HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_SEND_SMS);
            request.addPostValue("UserName", Integer.toString(this.user.getUserId()));
            request.addPostValue("Message", message);
            request.addPostValue("Receivers", recievers.toString());
            
            HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
            JSONObject json = new JSONObject(response.getResponseString());
            int status = json.getInt("rc");
            if(status==200) {
            	return ActionResult.SUCCESS;
            }else {
            	return ActionResult.REQUEST_FAILED;
            }
		} catch (IOException e) {
         	return ActionResult.HTTP_FAILED;
         } catch (JSONException e) {
          	return ActionResult.JSON_FAILED;
         }
	}
	
	/**
	 * 发送定时短信
	 * 
	 * @param toBuddies		接受好友的列表，可以包含用户对象(litefetion.getUser())
	 * @param message		原始消息内容，无需编码，发送时会自动编码
	 * @param sendDate		发送的时间，最短时间是 当前时间+11分钟-一年后当前时间,否则返回错误
	 * @return				操作状态
	 */
	
	public ActionResult sendScheduleSMS(List<Buddy> toBuddies, String message, Date sendDate) {
		try {
			if(toBuddies==null || toBuddies.size()==0)	return ActionResult.WRONG_PARAM;
			
			//检查定时短信的定时时间，最短时间是 当前时间+11分钟-一年后当前时间
			//比如当前时间是 2007.7.1 22:56 有效的时间是 2010.7.1 23:07 - 2011.7.1 22:56，在这个时间之内的才是有效时间，
			Calendar calMin = Calendar.getInstance();
			calMin.add(Calendar.MINUTE, 11);
			Calendar calMax = Calendar.getInstance();
			calMax.add(Calendar.YEAR, 1);
			//判断是否在有效的范围内，如果不在这个范围内则返回发送时间错误
			if(sendDate.before(calMin.getTime()) || sendDate.after(calMax.getTime())){
				return ActionResult.WRONG_PARAM;
			}
            
            //接受者参数应该是 346339663,346379375这样的格式
            Iterator<Buddy> it = toBuddies.iterator();
            StringBuffer recievers = new StringBuffer();
            while(it.hasNext()) {
            	  Buddy b = it.next();
            	  recievers.append(Integer.toString(b.getUserId()));
            	  if(it.hasNext()) {	//不是最后一个，则添加,
            		  recievers.append(",");
            	  }
            }
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d H:m:s");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT 0"));
            HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_SET_SCHEDULESMS);
            request.addPostValue("UserName", Integer.toString(this.user.getUserId()));
            request.addPostValue("Message", message);
            request.addPostValue("Receivers", recievers.toString());
            request.addPostValue("SendTime", sdf.format(sendDate));
            
            HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
            JSONObject json = new JSONObject(response.getResponseString());
            int status = json.getInt("rc");
            if(status==200) {
            	return ActionResult.SUCCESS;
            }else {
            	return ActionResult.REQUEST_FAILED;
            }
		} catch (IOException e) {
         	return ActionResult.HTTP_FAILED;
         } catch (JSONException e) {
          	return ActionResult.JSON_FAILED;
         }
	}
	
	/**
	 * 获取好友头像
	 * @param buddy		好友对象
	 * @param size		头像大小，可以取1,2,3,4,5,6四个值，不同的值代表不同的头像大小:
	 * 		 			如下：1=24x24, 2=32x32, 3=64x64, 4=96x96, 5=16x16, 6=48x48
	 * @return			操作结果，如果成功放入buddy.的portrait属性中
	 */
	public ActionResult retirePortrait(Buddy buddy, int size) {
		try {
			if(size<1 || size>6)	throw new IllegalArgumentException("size should be those values:{1,2,3,4,5,6}") ;
			if(buddy.getCrc()!=null) {
    	        String url = Settings.WEBIM_URL_GET_PORTRAIT;
    	        url = StringHelper.format(url, buddy.getUserId(), size, buddy.getCrc(), buddy.getUserId());
    	        HttpRequest request = this.createHttpRequest(url, "GET");
    	        HttpResponse response = this.client.tryExecute(request, Settings.FEITON_MAX_REQUEST_EXECUTE_TIMES);
    	        BufferedImage portrait = ImageIO.read(new ByteArrayInputStream(response.getResponseData()));
    	        buddy.setPortrait(portrait);
    	        return ActionResult.SUCCESS;
			}else {
				return ActionResult.PORTRAIT_NOT_FOUND;
			}
        } catch (IOException e) {
        	return ActionResult.HTTP_FAILED;
        }
	}
	
	
	/**
	 * 发起长连接，获取服务主动发送的通知
	 * 建立长连接是方便服务器可以及时的返回给客户端数据
	 */
	public List<Notify> pollNotify()
	{
		List<Notify> notifyList = new ArrayList<Notify>();
		try {
	        HttpRequest request = this.createActionHttpRequest(Settings.WEBIM_URL_CONNECT);
	        HttpResponse response = this.client.execute(request);
	        JSONObject json = new JSONObject(response.getResponseString());

	        int status = json.getInt("rc");
	        if(status==200) {
	        	JSONArray dataArr = json.getJSONArray("rv");
	        	for(int i=0;i<dataArr.length(); i++) {
	        		JSONObject jo = dataArr.getJSONObject(i);
	        		Notify notify = this.processNotify(jo);
	        		if(notify!=null) {
	        			notifyList.add(notify);
	        		}
	        	}
	        }else if(status==302) {
	        	//No Data..
	        }else {}
        } catch (IOException e) {
        	//如果发生了IO异常，递增获取通知的失败次数，如果大于给定的次数，表明客户端已经离线，并且返回一个网络错误的通知
        	this.pollNotifyFailed++;
        	if(this.pollNotifyFailed>Settings.FETION_MAX_POLL_NOTIFY_FAILED) {
        		this.updateClientState(ClientState.NET_ERROR);
        		notifyList.add(new ClientStateNotify(ClientState.NET_ERROR));
        		this.pollNotifyFailed = 0;	//清零，防止再次登录
        	}
	        logger.warn("Poll Notify failed." , e);
        } catch (JSONException e) {
        	 logger.warn("Poll Notify failed." , e);
        }
		logger.debug("Poll Notify: notify size:"+notifyList.size());
		return notifyList;
	}
	
	/**
	 * 处理一个服务器主动发送的通知
	 * @param jo
	 * @throws JSONException 
	 */
	private Notify processNotify(JSONObject jo) throws JSONException {
		int dataType = jo.getInt("DataType");
		JSONObject data = jo.getJSONObject("Data");
		Buddy buddy = null;
		switch(dataType) {
		
			case 2:		//好友状态信息
				int userId  = data.getInt("uid");
				buddy = this.getBuddyByUserId(userId);
				if(buddy!=null) {
					BuddyState beforeState = buddy.getState();
					if(data.optLong("mn") != 0) {
                        buddy.setMobile(data.optLong("mn"));
					}    				
                    if(data.optString("nn")!=null && data.optString("nn").length()>0 ) {
                        buddy.setNickName(data.optString("nn"));
                    }    				
                    buddy.setImpresa(data.optString("i"));
                    if(data.optString("sms")!=null && data.optString("sms").length()>0 ) {
        				buddy.setSMSPolicy(data.optString("sms"));
                    }
                    buddy.setSid(data.optInt("sid"));
    				buddy.setPresence(data.optInt("pb"));
                    buddy.setCrc(data.optString("crc"));
    				BuddyState currentState = buddy.getState();
    				logger.debug("BuddyState changed: buddy="+buddy.getDisplayName()+", before="+beforeState+", current="+currentState);
    				return new BuddyStateNotify(beforeState, currentState, buddy);
				}
				break;

			case 3:	//好友消息
				int fromUserId = data.getInt("fromUid");
				String message = data.getString("msg");
				int msgType  = data.getInt("msgType");
				buddy = this.getBuddyByUserId(fromUserId);
				if(msgType==2 && buddy!=null) {		//接收到消息
					logger.debug("Buddy Message received: buddy="+buddy.getDisplayName()+", text="+message);
					return new BuddyMessageNotify(buddy, message, new Date());
				}else if(msgType==3 || msgType==4) {		//TODO ..发送消息失败，暂不处理..
				}
				break;
				
			case 4:	//退出信息
				int exitCode = data.getInt("ec");
				ClientState state = ClientState.LOGOUT;
				if(exitCode==900) {
					state = ClientState.OTHER_LOGIN;
				}else if(exitCode>=902 && exitCode<=905) {
					state = ClientState.LOGOUT;
				}else {
					state = ClientState.LOGOUT;
				}
				logger.debug("ClientState changed: clientState="+state);
				return new ClientStateNotify(state);
				
			case 5:	//添加好友请求
				buddy = new Buddy();
				buddy.setUserId(data.getInt("uid"));
				buddy.setUri(data.getString("uri"));
				buddy.setRelation(Relation.STRANGER);
				this.buddyList.add(buddy);	//暂时添加到好友列表中
				String desc = data.getString("desc");
				logger.debug("Buddy Application received: buddy="+buddy+", desc="+desc);
				return new BuddyApplicationNotify(buddy, desc);
				
				
			case 6:	//添加好友的回复,会发三个通知，这里只处理ba=1的那个
				if(data.getInt("ba")==1) {
					buddy = this.getBuddyByUserId(data.getInt("uid"));
					if(buddy!=null) {
						Relation relation = Relation.valueOf(data.getInt("rs"));
						buddy.setRelation(relation);
						logger.debug("Buddy confirmed application: buddy="+buddy+", isAgreed="+(relation==Relation.DECLINED));
						return new ApplicationConfirmedNotify(buddy, relation==Relation.BUDDY);
					}
				}
		}
		
		return null;
	}
	//////////////////////////////////操作结束///////////////////////////////////////
	
	
	//////////////////////////////////好友列表查询开始///////////////////////////////
	
	/**
	 * 返回用户对象
	 */
	public User getUser() {
		return this.user;
	}
	
	/**
	 * 返回所有的好友列表,包含黑名单和陌生人
	 */
	public List<Buddy> getBuddyList(){
		return this.buddyList;
	}
	
	/**
	 * 返回分组列表
	 * @return
	 */
	public List<Cord> getCordList(){
		return this.cordList;
	}
	
	/**
	 * 根据用户编号返回好对象
	 * @return 好友对象
	 */
	public Buddy getBuddyByUserId(int userId) {
		Iterator<Buddy> it = this.buddyList.iterator();
		while(it.hasNext()) {
			Buddy buddy = it.next();
			if(buddy.getUserId()==userId) {
				return buddy;
			}
		}
		return null;
	}
	
	/**
	 * 根据用户uri返回好友对象
	 * @param uri
	 * @return
	 */
	public Buddy getBuddyByUri(String uri) {
		Iterator<Buddy> it = this.buddyList.iterator();
		while(it.hasNext()) {
			Buddy buddy = it.next();
			if(uri.equals(buddy.getUri())) {
				return buddy;
			}
		}
		return null;
	}
	
	/**
	 * 返回在黑名单的好友列表
	 */
	public List<Buddy> getBlackList(){
		ArrayList<Buddy> list = new ArrayList<Buddy>();
		Iterator<Buddy> it = this.buddyList.iterator();
		while(it.hasNext()) {
			Buddy buddy = it.next();
			if(buddy.isBlack()) {
				list.add(buddy);
			}
		}
		return list;
	}
	
	/**
	 * 返回在陌生人列表
	 */
	public List<Buddy> getStrangerList(){
		ArrayList<Buddy> list = new ArrayList<Buddy>();
		Iterator<Buddy> it = this.buddyList.iterator();
		while(it.hasNext()) {
			Buddy buddy = it.next();
			if(buddy.getRelation()==Relation.STRANGER) {
				list.add(buddy);
			}
		}
		return list;
	}
	
	/**
	 * 返回指定分组的好友列表
	 * @param cord		分组对象
	 * @return
	 */
	public List<Buddy> getBuddyListByCord(Cord cord)
	{
		ArrayList<Buddy> list = new ArrayList<Buddy>();
		Iterator<Buddy> it = this.buddyList.iterator();
		Buddy buddy = null;
		String [] buddyCordIds = null;
		while(it.hasNext()) {
			buddy = it.next();
			if(buddy.getCordIds()!=null){
				buddyCordIds = buddy.getCordIds().split(";");
				for(String cid : buddyCordIds){
					if(cid.equals(Integer.toString(cord.getId()))){
						list.add(buddy);
					}
				}
			}
		}
		return list;
	}
	
	/**
	 * 返回没有分组的好友列表，在客户端里就是默认的分组列表中的好友
	 * @return
	 */
    public synchronized List<Buddy> getBuddyListWithoutCord()
    {
    	ArrayList<Buddy> list = new ArrayList<Buddy>();
		Iterator<Buddy> it = this.buddyList.iterator();
		Buddy buddy = null;
		String  buddyCordId = null;
		while(it.hasNext()) {
			buddy = it.next();
			buddyCordId = buddy.getCordIds();
			if(buddyCordId==null || buddyCordId.length()==0) {
				list.add(buddy);
			}
		}
		return list;
    }
	
	//////////////////////////////////好友列表查询结束///////////////////////////////
}
