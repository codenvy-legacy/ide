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
package org.exoplatform.ide.client.log;

import org.exoplatform.gwtframework.commons.util.Log;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupWindowLogger extends Log
{

   protected JavaScriptObject popupWindow = null;

   private boolean even = false;

   private native JavaScriptObject getLogWindowDocument() /*-{
      var w = this.@org.exoplatform.ide.client.log.PopupWindowLogger::popupWindow;
      if (w == null || w.document == null) {
         return null;
      }
                                                          
      return w.document;
   }-*/;

   private native boolean openPopupWindow(String innerHTML) /*-{
      var openedWindow = this.@org.exoplatform.ide.client.log.PopupWindowLogger::popupWindow;
      if (openedWindow == null || openedWindow.document == null) {
         var windowFeatures = "width=750, height=350";
         openedWindow = window.open(null, "ideConsole", windowFeatures);
         this.@org.exoplatform.ide.client.log.PopupWindowLogger::popupWindow = openedWindow;
         openedWindow.document.write(innerHTML);
         return true;
      }
                                             
      return false;
   }-*/;

   private native Document getDocument() /*-{
      var w = this.@org.exoplatform.ide.client.log.PopupWindowLogger::popupWindow;
      if (w == null || w.document == null) {
         return null;
      }
            
      return w.document;
   }-*/;

   private ScrollPanel scrollPanel = new ScrollPanel();

   private FlowPanel scrollPanelInner = new FlowPanel();

   private void checkLogWindowOpened()
   {
      String head = "" +
      		"<head>" +
      		"<style>" +
      		
      		   "html, body {" +
      		      "height: 100%;" +
      		      "width: 100%;" +
      		      "margin: 0px;" +
      		      "padding:0px;" +
      		    "}" +
      		    
      		    ".msg {" +
      		      "font-family: Verdana, Bitstream Vera Sans, sans-serif;" +
      		      "font-size: 11px;" +
      		      "font-weight: normal;" +
      		      "font-style: normal;" +
      		    "}" +
      		    
      		"</style>" +
      		"</head>";
      
      boolean windowOpened = openPopupWindow("<html>" + head + "<body style=\"background:#FFFFFF; margin:0; padding:0; overflow:hidden;\"></body></html>");
      if (windowOpened)
      {
         while (getDocument().getBody().getChildCount() > 0)
         {
            getDocument().getBody().getFirstChild().removeFromParent();
         }

         AbsolutePanel absolutePanel = new AbsolutePanel();
         absolutePanel.setWidth("100%");
         absolutePanel.setHeight("100%");
         absolutePanel.getElement().getStyle().setPosition(Position.ABSOLUTE);
         getDocument().getBody().appendChild(absolutePanel.getElement());

         scrollPanel.setSize("100%", "100%");
         absolutePanel.add(scrollPanel);

         scrollPanelInner.setWidth("100%");
         if (scrollPanel.getWidget() == null)
         {
            scrollPanel.add(scrollPanelInner);
         }
         
         scrollPanel.scrollToBottom();
      }
   }

   protected void log(String text)
   {
      checkLogWindowOpened();

      HTML label = new HTML(text);
      label.getElement().getStyle().setWidth(100, Unit.PCT);
      label.getElement().getStyle().setProperty("fontFamily", "Verdana, Bitstream Vera Sans, sans-serif");
      label.getElement().getStyle().setProperty("fontSize", "12px");
      label.getElement().getStyle().setProperty("fontWeight", "normal");
      label.getElement().getStyle().setProperty("fontStyle", "normal");
      label.getElement().getStyle().setProperty("lineHeight", "14px");
      
      if (even)
      {
         label.getElement().getStyle().setProperty("background", "#f1f5fc");
      }
      even = !even;

      scrollPanelInner.add(label);
      scrollPanel.scrollToBottom();
   }

   @Override
   public void _info(String message)
   {
      try
      {
         Date date = new Date();
         String time = "" + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
         
         log(time + " [INFO] " + message);
      }
      catch (Throwable e)
      {
         Window.alert("ERROR > " + e.getMessage());
      }
   }

}
