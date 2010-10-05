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
 * Package  : net.solosky.litefetion.bean
 * File     : VerifyImage.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-2
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.bean;

/**
 *
 * 验证码
 *
 * @author solosky <solosky772@qq.com>
 */
public class VerifyImage
{
	/**
	 * 登录的验证图片
	 */
	public static final String TYPE_LOGIN = "ccpsession";
	
	/**
	 * 添加好友的验证图片
	 */
	public static final String TYPE_ADD_BUDDY = "addbuddy_ccpsession";
	
	/**
	 * 用户输入的验证码
	 */
	private String verifyCode;
	
	/**
	 * 原始的验证图片数据
	 */
	private byte[] imageData;
	
	/**
	 * 这个验证图片对应的验证图片ID
	 */
	private String sessionId;
	
	/**
	 * 验证类型，VerifyImage.TYPE_LOGIN or VerifyImage.TYPE_ADD_BUDDY
	 */
	private String verifyType;
	
	
	public String getVerifyType() {
    	return verifyType;
    }
	public void setVerifyType(String verifyType) {
    	this.verifyType = verifyType;
    }
	public String getVerifyCode() {
    	return verifyCode;
    }
	public void setVerifyCode(String verifyCode) {
    	this.verifyCode = verifyCode;
    }
	public byte[] getImageData() {
    	return imageData;
    }
	public void setImageData(byte[] imageData) {
    	this.imageData = imageData;
    }
	public String getSessionId() {
    	return sessionId;
    }
	public void setSessionId(String sessionId) {
    	this.sessionId = sessionId;
    }
	
	
}
