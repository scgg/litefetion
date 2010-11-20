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
 * File     : BuddyState.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-3
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.bean;

/**
 * 
 * 好友状态
 *
 * @author solosky <solosky772@qq.com>
 */
public enum BuddyState {
	/**
	 * 离线
	 */
	OFFLINE,
	
	/**
	 * 客户端在线
	 */
	ONLINE,	
	
	/**
	 * 忙碌
	 */
	BUSY,
	
	/**
	 * 离开
	 */
	AWAY,
	
	/**
	 * 机器人在线
	 */
	ROBOT,
	
	/**
	 * 关闭飞信服务
	 */
	CLOSED,	
	
	/**
	 * 短信在线
	 */
	SMS;
	
	
	
	public static BuddyState valueOf(int value) {
		switch(value) {
			case 0:	return OFFLINE;
			case 400:return ONLINE;
			case 600:return BUSY;
			case 100:return AWAY;
			case 499:return ROBOT;
			case 1:return SMS;
			default:return OFFLINE;
		}
	}
}
