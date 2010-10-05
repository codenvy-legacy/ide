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
import com.google.gwt.core.client.JsArray;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 1, 2010 $
 *
 */
public class UserPermissionsStoradge extends JavaScriptObject
{
   protected UserPermissionsStoradge()
   {
      
   }

   /**
    * @return the userPermissions
    */
   public final native JsArray<UserPermissions> getUserPermissions()/*-{
      return this.userPermissions;
   }-*/;
   

   /**
    * @param userPermissions the userPermissions to set
    */
   public final native void setUserPermissions(JsArray<UserPermissions> userPermissions)/*-{
      this.userPermissions = userPermissions;
   }-*/;
   
   public static final native UserPermissionsStoradge build(String json) /*-{
   return eval('(' + json + ')');
   }-*/;
   
}
