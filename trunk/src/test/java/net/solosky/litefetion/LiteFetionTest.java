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

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;

import net.solosky.litefetion.bean.ActionResult;
import net.solosky.litefetion.bean.Buddy;
import net.solosky.litefetion.bean.ClientState;
import net.solosky.litefetion.bean.Cord;
import net.solosky.litefetion.bean.Presence;
import net.solosky.litefetion.bean.VerifyImage;
import net.solosky.litefetion.notify.ApplicationConfirmedNotify;
import net.solosky.litefetion.notify.BuddyApplicationNotify;
import net.solosky.litefetion.notify.BuddyMessageNotify;
import net.solosky.litefetion.notify.BuddyStateNotify;
import net.solosky.litefetion.notify.ClientStateNotify;
import net.solosky.litefetion.notify.Notify;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class LiteFetionTest
{
	public static void main(String [] arg) throws Exception {
		final LiteFetion client = new LiteFetion();
		
		//首先获取登录验证码
		VerifyImage image = client.retireVerifyImage(VerifyImage.TYPE_LOGIN);
		VerifyDialog dialog = new VerifyDialog(image, client);
		dialog.setVisible(true);
		image = dialog.waitOK();
		//执行登录
		ActionResult r = client.login("138xxxxxx","xxxx", Presence.AWAY, image);
		System.out.println("LoginReulst:"+r.toString());
		if(r==ActionResult.SUCCESS) {
			
			//默认分组
			Iterator<Buddy> bit = client.getBuddyListWithoutCord().iterator();
			System.out.println("-------------[ 默认分组 ]------------------");
			while(bit.hasNext()) {
				System.out.println(bit.next());
				
			}
			
			//其他分组
			Iterator<Cord> cit = client.getCordList().iterator();
			while(cit.hasNext()) {
				Cord cord = cit.next();
				System.out.println("----------["+cord.getId()+"::"+cord.getTitle()+"]------------");
				
				bit = client.getBuddyListByCord(cord).iterator();
				while(bit.hasNext()) {
					System.out.println(bit.next());
				}
			}
			
			//陌生人
			bit = client.getStrangerList().iterator();
			System.out.println("-------------[ 陌生人 ]------------------");
			while(bit.hasNext()) {
				System.out.println(bit.next());
				
			}
			
			//黑名单
			bit = client.getBlackList().iterator();
			System.out.println("-------------[ 黑名单 ]------------------");
			while(bit.hasNext()) {
				System.out.println(bit.next());
				
			}
			
			Buddy testBuddy = client.getBuddyByUserId(335284404);
			
			//尝试给自己发送消息
			System.out.println("SendSMStoSelf:"+client.sendSelfSMS("来自LiteFetion的短信"));
			
			//给好友发送飞信
			System.out.println("SendMessage:"+client.sendMessage(testBuddy, "来自LiteFetion的飞信。", false));
			
			//给好友发送短信
			System.out.println("SendSMS:"+client.sendMessage(testBuddy, "来自LiteFetion的短信。", true));

            //发送收费短信
            System.out.println("SendDirectSMS:"+client.sendDirectSMS("15652273767", "来自LiteFetion的短信。"));
			
			//设置心情短语
			System.out.println("SetImpresa:"+client.setImpresa("ABDEE"));
			
			//设置在线状态
			System.out.println("SetPresence:"+client.setPresence(Presence.AWAY, "我吃饭去了~~~"));
			
			//添加好友
			VerifyImage vc = null;
			vc = client.retireVerifyImage(VerifyImage.TYPE_ADD_BUDDY);
			dialog = new VerifyDialog(vc, client);
			dialog.setVisible(true);
			vc = dialog.waitOK();
			System.out.println("AddBuddy:"+client.addBuddy("13887654321`", "小牛", "峰子", null ,vc ));
			
			
			//加入黑名单
			System.out.println("BlackBuddy:"+client.blackBuddy(testBuddy));
			
			//获取头像
			System.out.println("RetirePortrait:"+client.retirePortrait(testBuddy, 4));
			if(testBuddy.getPortrait()!=null) {
				ImageIO.write(testBuddy.getPortrait(), "JPG", new File("portrait-"+testBuddy.getUserId()+".jpg"));
			}
			
			
			ArrayList<Buddy> toBuddies = new ArrayList<Buddy>();
			toBuddies.add(client.getUser());
			toBuddies.add(testBuddy);
			
			//批量发送短信
			System.out.println("BatchSendSMS:"+client.batchSendSMS(toBuddies,"这是来自LiteFetion的批量发送短信。"));
			//发送定时短信
			Calendar calMin = Calendar.getInstance();
			calMin.add(Calendar.MINUTE, 10);	//时间不对。。
			System.out.println("SendScheduleSMS(Failed):"+client.sendScheduleSMS(toBuddies, "这是来自LiteFetion的定时短信", calMin.getTime()));
			calMin.add(Calendar.MINUTE, 11);
			System.out.println("SendScheduleSMS:"+client.sendScheduleSMS(toBuddies, "这是来自LiteFetion的定时短信", calMin.getTime()));

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
							System.out.println("自动同意添加好友请求:"+client.handleBuddyApplication(an.getBuddy(), true, null, null));
							break;
							
						case APPLICATION_CONFIRMED:
							ApplicationConfirmedNotify fn = (ApplicationConfirmedNotify) notify;
							System.out.println("收到添加好友的回复："+fn.getBuddy().getDisplayName()+" "+(fn.isAgreed()?"同意":"拒绝")+"了你添加好友的请求。");
							
							if(!fn.isAgreed()) {
								vc = client.retireVerifyImage(VerifyImage.TYPE_ADD_BUDDY);
								dialog = new VerifyDialog(vc, client);
								dialog.setVisible(true);
								vc = dialog.waitOK();
								System.out.println("未同意添加好友请求，重新发起添加好友请求："+client.addBuddy(""+fn.getBuddy().getMobile(), "xxx", null, null, vc));
							}
							break;
								
					}
				}
				
			}
			client.logout();
		}
	}
}
