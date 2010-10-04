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
 * File     : Settings.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-2
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.bean;

/**
 *
 * 一些配置，主要是关于url
 *
 * @author solosky <solosky772@qq.com>
 */
public class Settings
{
	public static final String WEBIM_DOMAIN = "webim.feixin.10086.cn";
	public static final String WEBIM_URL_ROOT = "http://webim.feixin.10086.cn/";
	public static final String WEBIM_URL_HTTPS_ROOT = "https://webim.feixin.10086.cn/";
	public static final String WEBIM_URL_MAIN = WEBIM_URL_ROOT + "main.aspx";
	public static final String WEBIM_URL_LOGIN = WEBIM_URL_HTTPS_ROOT + "WebIM/Login.aspx";
	public static final String WEBIM_URL_LOGOUT =WEBIM_URL_HTTPS_ROOT + "WebIM/logout.aspx?Version={0}";
	public static final String WEBIM_URL_GET_PIC = WEBIM_URL_ROOT + "WebIM/GetPicCode.aspx?Type={0}";
	
	public static final String WEBIM_URL_CONNECT = WEBIM_URL_ROOT + "WebIM/GetConnect.aspx?Version={0}";
	public static final String WEBIM_URL_GET_PERSONAL_INFO = WEBIM_URL_ROOT + "WebIM/GetPersonalInfo.aspx?Version={0}";
	public static final String WEBIM_URL_GET_CONTACT_LIST = WEBIM_URL_ROOT + "WebIM/GetContactList.aspx?Version={0}";
	public static final String WEBIM_URL_SEND_MESSAGE = WEBIM_URL_ROOT + "WebIM/SendMsg.aspx?Version={0}";
	public static final String WEBIM_URL_SET_PERSONAL_INFO = WEBIM_URL_ROOT + "WebIM/SetPersonalInfo.aspx?Version={0}";
	public static final String WEBIM_URL_SET_PRESENCE = WEBIM_URL_ROOT + "WebIM/SetPresence.aspx?Version={0}";
	public static final String WEBIM_URL_ADD_BUDDY = WEBIM_URL_ROOT + "WebIM/AddBuddy.aspx?Version={0}";
	public static final String WEBIM_URL_HANDLE_ADD_BUDDY = WEBIM_URL_ROOT + "WebIM/HandleAddBuddy.aspx?Version={0}";
	public static final String WEBIM_URL_GET_PORTRAIT = WEBIM_URL_ROOT + "WebIM/GetPortrait.aspx?did={0}&Size={1}&Crc={2}&mid={3}";
	public static final String WEBIM_URL_OP_BUDDY = WEBIM_URL_ROOT + "WebIM/OpBuddy.aspx?Version={0}";
	public static final int FEITON_MAX_REQUEST_EXECUTE_TIMES = 3;
	public static final int FETION_MAX_POLL_NOTIFY_FAILED = 3;
}
