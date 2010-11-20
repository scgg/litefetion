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
 * File     : ClientState.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-2
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.bean;

/**
 * 
 * 客户端状态
 *
 * @author solosky <solosky772@qq.com>
 */
public enum ClientState {
	/**
	 * 刚刚建立没有执行过登录过程
	 */
	NEW,
	
	/**
	 * 正在登录
	 */
	LOGGING,
	
	/**
	 * 登录失败
	 */
	LOGIN_FAIL,	
	
	/**
	 * 在线
	 */
	ONLINE,
	
	/**
	 * 网络出错，或者服务器连接不上
	 */
	NET_ERROR,
	
	/**
	 * 从其他客户端登录
	 */
	OTHER_LOGIN,
	
	/**
	 * 已经成功退出
	 */
	LOGOUT,
}
