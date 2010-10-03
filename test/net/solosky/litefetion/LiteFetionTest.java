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
 * File     : LiteFetionTest.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-2
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;

import net.solosky.litefetion.bean.ActionResult;
import net.solosky.litefetion.bean.Buddy;
import net.solosky.litefetion.bean.BuddyState;
import net.solosky.litefetion.bean.ClientState;
import net.solosky.litefetion.bean.Presence;
import net.solosky.litefetion.bean.VerifyImage;
import net.solosky.litefetion.notify.ApplicationConfirmedNotify;
import net.solosky.litefetion.notify.BuddyApplicationNotify;
import net.solosky.litefetion.notify.BuddyMessageNotify;
import net.solosky.litefetion.notify.BuddyStateNotify;
import net.solosky.litefetion.notify.ClientStateNotify;
import net.solosky.litefetion.notify.Notify;
import junit.framework.TestCase;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class LiteFetionTest extends TestCase
{

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link net.solosky.litefetion.LiteFetion#login(java.lang.String, java.lang.String, net.solosky.litefetion.bean.VerifyImage)}.
	 * @throws Exception 
	 * @throws IOException 
	 * @throws JSONException 
	 * @throws InterruptedException 
	 */
	public void testLogin() throws Exception{
		main(null);
	}

	
	public static void main(String [] arg) throws Exception {
		final LiteFetion client = new LiteFetion();
		
		//首先获取登录验证码
		VerifyImage image = client.fetchVerifyImage(VerifyImage.TYPE_LOGIN);
		OutputStream out = new FileOutputStream(new File("v.jpg"));
		out.write(image.getImageData());
		out.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("请输入登录验证码>>");
		image.setVerifyCode(reader.readLine());
		
		//执行登录
		ActionResult r = client.login("15982070573","xu1234", Presence.AWAY, image);
		System.out.println("LoginReulst:"+r.toString());
		if(r==ActionResult.SUCCESS) {
			Iterator<Buddy> it = client.getBuddyList().iterator();
			System.out.println("-------------BuddyList------------------");
			while(it.hasNext()) {
				System.out.println(it.next());
			}
			
			//尝试给自己发送消息
			System.out.println("SendSMStoSelf:"+client.sendMessage(client.getUser(),"GOODD", true));
			
			//设置心情短语
			System.out.println("SetImpresa:"+client.setImpresa("ABDEE"));
			
			//设置在线状态
			System.out.println("SetPresence:"+client.setPresence(Presence.AWAY, "我吃饭去了~~~"));
			
			//添加好友
			VerifyImage vc = client.fetchVerifyImage(VerifyImage.TYPE_ADD_BUDDY);
			OutputStream out2 = new FileOutputStream(new File("v.jpg"));
			out2.write(vc.getImageData());
			out2.close();
			System.out.print("请输入添加好友验证码>>");
			vc.setVerifyCode(reader.readLine());
			System.out.println("AddBuddy:"+client.addBuddy("13880918689", "XXX", "Good", vc ));
			
			//启动类似于windows的消息循环，获取服务器发送的通知，比如好友在线状态，接受消息等...
			boolean isLogout = false;
			while(!isLogout) {
				List<Notify> list = client.pollNotify();	//总是返回一个size>=0的通知列表 ，所以直接迭代，不用判断null
				Iterator<Notify> nit = list.iterator();
				while(nit.hasNext()) {
					Notify notify = nit.next();
					switch(notify.getType()) {
						case BUDDY_MESSAGE:		//好友消息消息
							BuddyMessageNotify bn = (BuddyMessageNotify) notify;
							System.out.println("收到好友消息:text="+bn.getMessage()+"; from="+bn.getBuddy().getDisplayName());
							
							//尝试发送相同的消息
							client.sendMessage(bn.getBuddy(), "你说:"+bn.getMessage(), false);
							break;
							
						case CLIENT_STATE:		//客户端状态
							ClientStateNotify cn = (ClientStateNotify) notify;
							System.out.println("客户端状态改变了:"+cn.getClientState());
							if(cn.getClientState()==ClientState.NET_ERROR || cn.getClientState()==ClientState.OTHER_LOGIN) {
								isLogout = true;
								client.logout();
							}
							break;
							
						case BUDDY_STATE:		//好友状态改变
							BuddyStateNotify sn = (BuddyStateNotify) notify;
							System.out.println("好友状态改变了:buddy="+sn.getBuddy().getDisplayName()+", current="+sn.getCurrentState()+", before="+sn.getBeforeState());
							break;
							
						case BUDDY_APPLICATION:
							BuddyApplicationNotify an = (BuddyApplicationNotify) notify;
							System.out.println("收到了好友请求:buddy="+an.getBuddy().getDisplayName()+", 说明:"+an.getDesc());
							System.out.println("自动同意添加好友请求:"+client.handleBuddyApplication(an.getBuddy(), true));
							break;
							
						case APPLICATION_CONFIRMED:
							ApplicationConfirmedNotify fn = (ApplicationConfirmedNotify) notify;
							System.out.println("收到添加好友的回复："+fn.getBuddy().getDisplayName()+" "+(fn.isAgreed()?"同意":"拒绝")+"了你添加好友的请求。");
							break;
								
					}
				}
				
			}
			client.logout();
		}
	}
}
