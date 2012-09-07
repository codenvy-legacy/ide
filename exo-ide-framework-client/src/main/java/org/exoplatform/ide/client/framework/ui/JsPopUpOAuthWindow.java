/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: JsPopUpWindow.java Sep 4, 2012
 */
public class JsPopUpOAuthWindow
{

   private String authUrl;

   private String errorPageUrl;

   private int popupWindowWidth;

   private int popupWindowHeight;

   private int clientWidth;

   private int clientHeight;

   public JsPopUpOAuthWindow(String authUrl, String errorPageUrl, int popupWindowWidth, int popupWindowHeight,
      int clientWidth, int clientHeight)
   {
      this.authUrl = authUrl;
      this.errorPageUrl = errorPageUrl;
      this.popupWindowWidth = popupWindowWidth;
      this.popupWindowHeight = popupWindowHeight;
      this.clientWidth = clientWidth;
      this.clientHeight = clientHeight;
   }

   public JsPopUpOAuthWindow(String authUrl, String errorPageUrl, int popupWindowWidth, int popupWindowHeight)
   {
      this(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight, Window.getClientWidth(), Window
         .getClientHeight());
   }

   public void loginWithOAuth()
   {
      loginWithOAuth(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight, clientWidth, clientHeight);
   }

   private native void loginWithOAuth(String authUrl, String errorPageUrl, int popupWindowWidth,
      int popupWindowHeight, int clientWidth, int clientHeight) /*-{
                                                                function Popup(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight) {
                                                                this.authUrl = authUrl;
                                                                this.errorPageUrl = errorPageUrl;
                                                                this.popupWindowWidth = popupWindowWidth;
                                                                this.popupWindowHeight = popupWindowHeight;

                                                                var popup_close_handler = function() {
                                                                if (!popupWindow || popupWindow.closed)
                                                                {
                                                                //console.log("closed popup")
                                                                popupWindow = null;
                                                                if (popupCloseHandlerIntervalId)
                                                                {
                                                                window.clearInterval(popupCloseHandlerIntervalId);
                                                                //console.log("stop interval " + popupCloseHandlerIntervalId);
                                                                }
                                                                }
                                                                else
                                                                {
                                                                var href;
                                                                try
                                                                {
                                                                href = popupWindow.location.href;
                                                                }
                                                                catch (error)
                                                                {}

                                                                if (href)
                                                                {
                                                                //console.log(href);
                                                                var path = popupWindow.location.pathname;
                                                                if (path == "/IDE/Application.html" // for local ide bundle
                                                                || path == "/cloud/profile.jsp"
                                                                || path == "/cloud/ide.jsp")
                                                                {
                                                                popupWindow.close();
                                                                popupWindow = null;
                                                                if (popupCloseHandlerIntervalId)
                                                                {
                                                                window.clearInterval(popupCloseHandlerIntervalId);
                                                                //console.log("stop interval " + popupCloseHandlerIntervalId);
                                                                }
                                                                }
                                                                else if (path.match("j_security_check$"))
                                                                {
                                                                //console.log("login failed");
                                                                popupWindow.location.replace(errorPageUrl);
                                                                }
                                                                }
                                                                }
                                                                }

                                                                this.open_window = function() {
                                                                var x = Math.max(0, Math.round(clientWidth / 2) - Math.round(this.popupWindowWidth / 2));
                                                                var y = Math.max(0, Math.round(clientHeight / 2) - Math.round(this.popupWindowHeight / 2));
                                                                popupWindow = window.open(this.authUrl, 'popup', 'width=' + this.popupWindowWidth + ',height=' + this.popupWindowHeight + ',left=' + x + ',top=' + y);
                                                                popupCloseHandlerIntervalId = window.setInterval(popup_close_handler, 50);
                                                                }
                                                                }

                                                                var popup = new Popup(authUrl, errorPageUrl, popupWindowWidth, popupWindowHeight);
                                                                popup.open_window();
                                                                }-*/;

}
