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
 * Package  : net.solosky.litefetion.notify
 * File     : ClientStateNotify.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-3
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.notify;

import net.solosky.litefetion.bean.ClientState;

/**
 * 
 * 客户端状态
 *
 * @author solosky <solosky772@qq.com>
 */
public class ClientStateNotify extends Notify
{

	private ClientState clientState;
	
	
	/**
     * @param clientState
     */
    public ClientStateNotify(ClientState clientState) {
	    this.clientState = clientState;
    }



	/* (non-Javadoc)
     * @see net.solosky.litefetion.notify.Notify#getType()
     */
    @Override
    public NotifyType getType() {
	   return NotifyType.CLIENT_STATE;
    }

	public ClientState getClientState() {
    	return clientState;
    }

	@Override
    public String toString() {
	    return "ClientStateNotify [clientState=" + clientState + "]";
    }
}
