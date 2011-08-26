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
package org.exoplatform.ide.client.debug;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DOMInspector
{

   private EventTarget targetElement = null;

   private HandlerRegistration previewHandlerRegistration = null;

   public DOMInspector()
   {
      ImageButton test = new ImageButton("Inspect");
      test.setWidth("70px");
      int left = (Window.getClientWidth() - 70) / 2;
      int top = Window.getClientHeight() - 25;
      RootPanel.get().add(test, left, top);
      test.getElement().getStyle().setZIndex(Integer.MAX_VALUE - 1000);

      test.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            targetElement = null;
            previewHandlerRegistration = Event.addNativePreviewHandler(previewHandler);
         }
      });
   }

   private NativePreviewHandler previewHandler = new NativePreviewHandler()
   {
      @Override
      public void onPreviewNativeEvent(NativePreviewEvent event)
      {
         if (event.getTypeInt() == Event.ONMOUSEDOWN)
         {
            targetElement = event.getNativeEvent().getEventTarget();
            event.getNativeEvent().stopPropagation();
            
            previewHandlerRegistration.removeHandler();
            previewHandlerRegistration = null;
            
            inspect();
            
            return;
         }
      }
   };
   
   private AbsolutePanel outputPanel;
   
   private FlowPanel panel;
   
   private void inspect()
   {
      System.out.println("Element to inspect > " + targetElement);
      outputPanel = new AbsolutePanel();
      outputPanel.setPixelSize(Window.getClientWidth(), Window.getClientHeight());
      outputPanel.getElement().getStyle().setBackgroundColor("transparent");
      RootPanel.get().add(outputPanel, 0, 0);
      outputPanel.getElement().getStyle().setZIndex(Integer.MAX_VALUE - 999);
      
      ImageButton closeButton = new ImageButton("Close");
      closeButton.setWidth("70");
      outputPanel.add(closeButton, Window.getClientWidth() - 10 - 70, 3);
      closeButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            outputPanel.removeFromParent();
         }
      });
      
      AbsolutePanel opacityPanel = new AbsolutePanel();
      opacityPanel.setPixelSize(Window.getClientWidth() - 20, Window.getClientHeight() - 40);
      //opacityPanel.getElement().getStyle().setOpacity(0.8);
      opacityPanel.getElement().getStyle().setBackgroundColor("#FFFFFF");
      outputPanel.add(opacityPanel, 10, 30);
      
      ScrollPanel scrollPanel = new ScrollPanel();
      scrollPanel.setPixelSize(Window.getClientWidth() - 20, Window.getClientHeight() - 40);
      outputPanel.add(scrollPanel, 10, 30);
      scrollPanel.getElement().getStyle().setBorderStyle(BorderStyle.DOTTED);
      scrollPanel.getElement().getStyle().setBorderWidth(1, Unit.PX);
      scrollPanel.getElement().getStyle().setBorderColor("#616D7E");
      
      panel = new FlowPanel();
      scrollPanel.add(panel);
      
      
      
      showElementInfo();
   }
   
   private void showElementInfo() {
      JavaScriptObject o = targetElement.cast();
      String out = getDump2(o);
      panel.clear();
      panel.getElement().setInnerHTML(out);
   }

   private final native String getDump2(JavaScriptObject _obj) /*-{
   
        function Inspector() {
          
          this.inspect = function(obj, maxLevels, level)
                                          {
                                              var str = '', type, msg;
                                          
                                              // Start Input Validations
                                              // Don't touch, we start iterating at level zero
                                              if(level == null)
                                                  level = 0;
                                          
                                              // At least you want to show the first level
                                              if(maxLevels == null) maxLevels = 1;
                                              if(maxLevels < 1)     
                                                  return '<font color="red">Error: Levels number must be > 0</font>';
                                          
                                              // We start with a non null object
                                              if(obj == null)
                                                  return '<font color="red">Error: Object <b>NULL</b></font>';
                                              // End Input Validations
                                          
                                              // Each Iteration must be indented
                                              str += '<ul>';
                                          
                                              // Start iterations for all objects in obj
                                              for(property in obj)
                                              {
                                                try
                                                {
                                                    // Show "property" and "type property"
                                                    type =  typeof(obj[property]);
                                                    str += '<li>(' + type + ') ' + property + ( (obj[property]==null)?(': <b>null</b>'):(''));
                                                      
                                                      if (type == "string") {
                                                        if (obj[property] == "") {
                                                          str += "&nbsp;&nbsp;&nbsp;<b>empty</b>";
                                                        } else {
                                                           str += "&nbsp;&nbsp;&nbsp;<font style=\"color:DarkGreen;\"><b>" + obj[property] + "</b></font>";
                                                        }
                                                      } else if (type == "number") {
                                                        str += "&nbsp;&nbsp;&nbsp;<font style=\"color:DarkBlue;\"><b>" + obj[property] + "</b></font>";
                                                      } else if (type == "boolean") {
                                                        str += "&nbsp;&nbsp;&nbsp;<font style=\"color:LightSeaGreen;\"><b>" + obj[property] + "</b></font>";
                                                      }
                                                      
                                                    str += '</li>';
                                          
                                                    // We keep iterating if this property is an Object, non null
                                                    // and we are inside the required number of levels
                                                    if((type == 'object') && (obj[property] != null) && (level+1 < maxLevels))
                                                        str += this.inspect(obj[property], maxLevels, level+1);
                                                }
                                                catch(err)
                                                {
                                                  // Is there some properties in obj we can't access? Print it red.
                                                  if(typeof(err) == 'string')
                                                      msg = err;
                                                  else if(err.message)
                                                      msg = err.message;
                                                  else if(err.description)
                                                      msg = err.description;
                                                  else
                                                      msg = 'Unknown';
                                          
                                                  str += '<li><font color="red">(Error) ' + property + ': ' + msg +'</font></li>';
                                                }
                                              }
                                          
                                                // Close indent
                                                str += '</ul>';
                                          
                                              return str;
                                          }
        }
        
        var h = "Inspecting element:&nbsp;<b>" + _obj['nodeName'] + "</b><br><hr>";
        var inspector = new Inspector();
        h += inspector.inspect(_obj);

        return h; 
   }-*/;   


}
