/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client;

/**
 * 
 * This class was creates to helps developers to use native browser's alert function. When any file is opened, CKEditor overwrites
 * native function and any trying to do alert prevents to opens CKEditor's dialog window.
 * 
 * Call init() function at the start of application to remember browser's alert function and then use alert(...) like
 * Window.alert(...)
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Alert
{

   public static final native void init() /*-{
                                          var alertFunc = $wnd.alert;
                                          $wnd.nativeAlertFunction = alertFunc;
                                          }-*/;

   public static final native void alert(String message) /*-{
                                                         $wnd.nativeAlertFunction(message);      
                                                         }-*/;

}
