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
package org.exoplatform.ide.client.model.permissions;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 1, 2010 $
 *
 */
public class UserPermissions extends JavaScriptObject
{
//   private String userID;
//   
//   private String[] permissions;

   protected UserPermissions()
   {
      
   }
   /**
    * @return the userID
    */
   public final native String getUserID()/*-{
      return this.userID;
   }-*/;

   /**
    * @param userID the userID to set
    */
   public final native void setUserID(String userID)/*-{
      this.userID = userID;
   }-*/;

   /**
    * @return the permissions
    */
   public final native JsArrayString getPermissions()/*-{
      return this.permissions;
   }-*/;

   /**
    * @param permissions the permissions to set
    */
   public final native void setPermissions(JsArrayString permissions) /*-{
      this.permissions = permissions;
   }-*/;
}
