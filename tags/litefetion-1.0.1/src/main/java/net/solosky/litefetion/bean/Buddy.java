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
 * File     : Buddy.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-1
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.bean;

import java.awt.image.BufferedImage;

/**
 *
 * 飞信好友
 *
 * @author solosky <solosky772@qq.com>
 */
public class Buddy
{
	/**
	 * 用户ID，唯一的标识符
	 */
	private int userId;
	
	/**
	 * URI，没有实际的含义，也可以标识一个用户
	 */
	private String uri;
	
	/**
	 * 飞信号，仅在开通飞信的时候有效
	 */
	private int sid;
	
	/**
	 * 分组编号，可以有多个分组编号，用；隔开
	 */
	private String cordIds;
	
	/**
	 * 用户设置的好友的备注
	 */
	private String localName;
	
	/**
	 * 昵称
	 */
	private String nickName;
	
	/**
	 * 是否在黑名单中
	 */
	private boolean isBlack;
	
	/**
	 * 该好友和用户的关系
	 */
	private Relation relation;
	
	/**
	 * 手机号，权限关系，不一定有效
	 */
	private long mobile;
	
	/**
	 * 在线状态
	 */
	private Presence presense;
	
	/**
	 * 心情短语
	 */
	private String impresa;
	
	/**
	 * 定义为在某段时间内不把飞信发送到手机上
	 */
	private SMSPolicy smsPolicy;
	
	/**
	 * 头像，默认为空，需要手动获取
	 */
	private BufferedImage portrait;
	
	/**
	 * 头像的crc校验码
	 */
	private String crc;
	
	public Buddy() {
		this.smsPolicy = new SMSPolicy();
		this.presense  = Presence.OFFLINE;
	}
	
	public int getUserId() {
    	return userId;
    }
	public void setUserId(int userId) {
    	this.userId = userId;
    }
	public String getUri() {
    	return uri;
    }
    public void setUri(String uri)
    {
    	this.uri = uri.trim();
    	if(uri.startsWith("sip")) {
    		this.sid = Integer.parseInt(uri.substring(4, (uri.indexOf('@')==-1?uri.length()-1:uri.indexOf('@'))));
    	}else if(uri.startsWith("tel")){
    		this.mobile = Long.parseLong(uri.substring(4));
    	}else {
    		throw new IllegalArgumentException("Illegal uri:"+uri);
    	}
    }
	public int getSid() {
    	return sid;
    }
	public void setSid(int sid) {
    	this.sid = sid;
    }
	public String getCordIds() {
    	return cordIds;
    }
	public void setCordIds(String cordIds) {
    	this.cordIds = cordIds;
    }
	public String getLocalName() {
    	return localName;
    }
	public void setLocalName(String localName) {
    	this.localName = localName;
    }
	public String getNickName() {
    	return nickName;
    }
	public void setNickName(String nickName) {
    	this.nickName = nickName;
    }
	public boolean isBlack() {
    	return isBlack;
    }
	public void setBlack(boolean isBlack) {
    	this.isBlack = isBlack;
    }
	public Relation getRelation() {
    	return relation;
    }
	public void setRelation(Relation relation) {
    	this.relation = relation;
    }
	public long getMobile() {
    	return mobile;
    }
	public void setMobile(long mobile) {
    	this.mobile = mobile;
    }
	public Presence getPresence() {
    	return presense;
    }
	public void setPresence(int presence) {
    	this.presense = Presence.valueOf(presence);
    }
	public SMSPolicy getSMSPolicy() {
    	return smsPolicy;
    }
	public void setSMSPolicy(String smsPolicy) {
    	this.smsPolicy.parse(smsPolicy);
    }
	public String getImpresa() {
    	return impresa;
    }
	public void setImpresa(String impresa) {
    	this.impresa = impresa;
    }
	
	public boolean isBuddy() {
		return this.relation == Relation.BUDDY;
	}
	
	public BufferedImage getPortrait() {
    	return portrait;
    }

	public void setPortrait(BufferedImage portrait) {
    	this.portrait = portrait;
    }
	
	public String getCrc() {
    	return crc;
    }

	public void setCrc(String crc) {
    	this.crc = crc;
    }

	public BuddyState getState() {
		BuddyState state = BuddyState.OFFLINE;
		if(this.getRelation()==Relation.BUDDY) {
			state = BuddyState.valueOf(this.presense.value);
    		if(state==BuddyState.OFFLINE) {
    			state = this.smsPolicy.isSMSOnline()? BuddyState.SMS : BuddyState.OFFLINE;
    		}
		}else {	//非好友，只能是离线状态
			state = BuddyState.OFFLINE;
		}
		return state;
	}
	
	/**
     * 返回可以显示的名字
     */
    public String getDisplayName()
    {
    	if(getLocalName()!=null && getLocalName().length()>0)
    		return getLocalName();
    	if(getNickName()!=null && getNickName().length()>0)
    		return getNickName();
    	if(getSid()>0)
    		return Integer.toString(getSid());
    	if(getMobile()>0)
    		return Long.toString(getMobile());
    	return Integer.toString(getUserId());
    }
	
	@Override
    public String toString() {
	    return "Buddy [userId=" + userId + ", uri=" + uri + ", displayName="
	            + getDisplayName() + ", relation=" + relation + ", status=" + getState() + "]";
    }
	
}
