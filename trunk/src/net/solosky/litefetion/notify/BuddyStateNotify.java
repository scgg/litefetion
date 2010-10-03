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
 * File     : BuddyStateNotify.java
 * Author   : solosky < solosky772@qq.com >
 * Created  : 2010-10-3
 * License  : Apache License 2.0 
 */
package net.solosky.litefetion.notify;

import net.solosky.litefetion.bean.Buddy;
import net.solosky.litefetion.bean.BuddyState;

/**
 *
 *
 * @author solosky <solosky772@qq.com>
 */
public class BuddyStateNotify extends Notify
{

	private BuddyState beforeState;
	private BuddyState currentState;
	private Buddy buddy;
	
	
	/**
     * @param beforeState
     * @param currentState
     * @param buddy
     */
    public BuddyStateNotify(BuddyState beforeState, BuddyState currentState,
            Buddy buddy) {
	    super();
	    this.beforeState = beforeState;
	    this.currentState = currentState;
	    this.buddy = buddy;
    }



	/* (non-Javadoc)
     * @see net.solosky.litefetion.notify.Notify#getType()
     */
    @Override
    public NotifyType getType() {
    	return NotifyType.BUDDY_STATE;
    }



	public BuddyState getBeforeState() {
    	return beforeState;
    }



	public BuddyState getCurrentState() {
    	return currentState;
    }



	public Buddy getBuddy() {
    	return buddy;
    }



	@Override
    public String toString() {
	    return "BuddyStateNotify [buddy=" + buddy.getDisplayName() + ", beforeState="
	            + beforeState + ", currentState=" + currentState + "]";
    }
	
	

}
