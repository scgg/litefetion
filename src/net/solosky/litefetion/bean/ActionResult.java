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
 * File     : ActionResult.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-1
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.bean;

/**
 *
 * 操作结果
 *
 * @author solosky <solosky772@qq.com>
 */
public enum ActionResult {
	
	SUCCESS(200),				//成功
	BUDDY_EXISTS(410),			//好友已经存在
	VERIFY_FAILED(420),			//验证码验证失败
	REQUEST_FAILED(430),		//请求失败，一般是网络问题或者服务器出了问题
	PORTRAIT_NOT_FOUND(510),	//头像不存在
	
	/*登录出错*/
	USER_INVALID(700),			//账号无效
	PASSWORD_NOT_MATCH(701),	//密码错误
	USER_SUSPEND(702),			//已经欠费
	USER_NOT_FOUND(703),		//未开通飞信
	
	;
	
	
	int value;
	ActionResult(int value) {
		this.value = value;
	}
}
