/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.opensocial.data.model;

/**
 * Person network state.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 *
 */
public enum NetworkState {
   /** Currently Online. */
   ONLINE("ONLINE", "Online"),
   /** Currently Offline. */
   OFFLINE("OFFLINE", "Offline"),
   /** Currently online but away. */
   AWAY("AWAY", "Away"),
   /** In a chat or available to chat. */
   CHAT("CHAT", "Chat"),
   /** Online, but don't disturb. */
   DND("DND", "Do Not Disturb"),
   /** Gone away for a longer period of time. */
   XA("XA", "Extended Away");

   /**
    * Status.
    */
   private final String status;

   /**
    * The value used for display purposes.
    */
   private final String displayValue;

   /**
    * @param status status
    * @param displayValue display value
    */
   private NetworkState(String status, String displayValue)
   {
      this.status = status;
      this.displayValue = displayValue;
   }

   /**
    * @return {@link String} display name
    */
   public String getDisplayValue()
   {
      return displayValue;
   }

   /**
    * @return the status
    */
   public String getStatus()
   {
      return status;
   }
}
