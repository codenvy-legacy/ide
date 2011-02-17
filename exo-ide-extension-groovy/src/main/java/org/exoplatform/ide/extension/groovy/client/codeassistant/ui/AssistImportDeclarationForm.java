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
package org.exoplatform.ide.extension.groovy.client.codeassistant.ui;

import java.util.List;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent;
import org.exoplatform.gwtframework.ui.client.event.WindowResizedHandler;
import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.extension.groovy.client.GroovyClientBundle;
import org.exoplatform.ide.extension.groovy.client.codeassistant.AssistImportDeclarationHandler;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 4:12:46 PM evgen $
 *
 */
public class AssistImportDeclarationForm extends Composite implements WindowResizedHandler
{

   private AbsolutePanel lockLayer;

   private AbsolutePanel absolutePanel;

   private LockLayer blockMouseEventsPanel;

   private AssistantImportDeclarationPanel<GroovyClassTokenWidget> flowPanel;

   private CodeAssitantScrollPanel scrollPanel;

   private MousHandler mousHandler;

   private AssistImportDeclarationHandler handler;

   private GroovyClassTokenWidget overedWidget;

   private GroovyClassTokenWidget selectedWidget;

   private CodeAssistantFormKeyboardManager keyboardManager;

   private HandlerRegistration keyboardManagerRegistration;

   public AssistImportDeclarationForm(HandlerManager eventBus, int left, int top, List<TokenExt> items,
      AssistImportDeclarationHandler handler)
   {
      absolutePanel = new AbsolutePanel();
      this.handler = handler;

      initWidget(absolutePanel);

      lockLayer = new AbsolutePanel();
      RootPanel.get().add(lockLayer, 0, 0);

      lockLayer.setWidth("" + Window.getClientWidth() + "px");
      lockLayer.setHeight("" + Window.getClientHeight() + "px");
      DOM.setElementAttribute(lockLayer.getElement(), "id", "menu-lock-layer-id");
      DOM.setStyleAttribute(lockLayer.getElement(), "zIndex", "" + (Integer.MAX_VALUE));

      blockMouseEventsPanel = new LockLayer();
      blockMouseEventsPanel.setStyleName("exo-lockLayer");
      blockMouseEventsPanel.setWidth("" + Window.getClientWidth() + "px");
      blockMouseEventsPanel.setHeight("" + Window.getClientHeight() + "px");
      lockLayer.add(blockMouseEventsPanel, 0, 0);

      flowPanel = new AssistantImportDeclarationPanel<GroovyClassTokenWidget>();

      scrollPanel = new CodeAssitantScrollPanel();

      keyboardManager = new CodeAssistantFormKeyboardManager();
      keyboardManagerRegistration = Event.addNativePreviewHandler(keyboardManager);

      FocusPanel focusPanel = new FocusPanel();

      focusPanel.add(scrollPanel);

      scrollPanel.add(flowPanel);
      scrollPanel.setHeight("100%");

      mousHandler = new MousHandler();
      flowPanel.setWidth("100%");

      scrollPanel.addMouseOutHandler(mousHandler);

      scrollPanel.setStylePrimaryName(GroovyClientBundle.INSTANCE.css().panelStyle());

      focusPanel.setHeight("100px");
      focusPanel.setWidth("350px");

      lockLayer.add(focusPanel, left, top);
      flowPanel.add(new Label("No Proposals."));

      fillTokens(items);
      focusPanel.setFocus(true);

   }

   /**
    * @param items
    */
   private void fillTokens(List<TokenExt> items)
   {
      if (items.isEmpty())
      {
         return;
      }

      flowPanel.clear();
      for (TokenExt token : items)
      {
         GroovyClassTokenWidget widget = new GroovyClassTokenWidget(token, "");
         widget.addClickHandler(mousHandler);
         widget.addMouseOverHandler(mousHandler);
         widget.addDoubleClickHandler(mousHandler);
         flowPanel.addWidget(widget);
      }

      selectTokenWidget(flowPanel.getEntries().get(0));
   }

   private void selectTokenWidget(GroovyClassTokenWidget widget)
   {
      if (widget.equals(selectedWidget))
      {
         return;
      }

      if (selectedWidget != null)
      {
         selectedWidget.setDefaultStyle();
      }

      selectedWidget = widget;

      if (widget.equals(overedWidget))
      {
         selectedWidget.setOveredStyle();
      }
      else
      {
         selectedWidget.setSelectedStyle();
      }
   }

   private void overWidget(GroovyClassTokenWidget t)
   {

      if (t == overedWidget)
      {
         return;
      }

      if (t == null)
      {
         if (overedWidget.equals(selectedWidget))
         {
            overedWidget.setSelectedStyle();
         }
         else
         {
            overedWidget.setDefaultStyle();
         }
         overedWidget = null;
         return;
      }

      if (overedWidget != null)
      {
         if (overedWidget.equals(selectedWidget))
         {
            overedWidget.setSelectedStyle();
         }
         else
         {
            overedWidget.setDefaultStyle();
         }
      }

      overedWidget = t;
      overedWidget.setOveredStyle();
   }

   private void cancelAssistant()
   {
      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }

      handler.onImportCancel();
      lockLayer.removeFromParent();
   }

   private void tokenSelected()
   {
      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }

      handler.onImportTokenSelected(selectedWidget.getToken());
      lockLayer.removeFromParent();
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.event.WindowResizedHandler#onWindowResized(org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent)
    */
   public void onWindowResized(WindowResizedEvent event)
   {
      // TODO Auto-generated method stub
      cancelAssistant();
   }

   /**
    * 
    */
   public void keyPressDown()
   {
      if (selectedWidget == null)
         return;
      int i = flowPanel.getEntries().indexOf(selectedWidget);
      if (flowPanel.getEntries().size() - 1 > i)
      {
         selectWidget(i + 1);
      }
   }

   /**
    * 
    */
   public void keyPressUP()
   {
      if (selectedWidget == null)
         return;

      int i = flowPanel.getEntries().indexOf(selectedWidget);
      if (0 < i)
      {
         selectWidget(i - 1);
      }
   }

   /**
    * @param i
    */
   private void selectWidget(int i)
   {
      scrollPanel.ensureVisible(flowPanel.getEntries().get(i));
      //         DOM.scrollIntoView(widgets.get(i).getElement());
      //      ensureVisibleImpl(scrollPanel.getElement(), widgets.get(i).getElement());
      selectTokenWidget(flowPanel.getEntries().get(i));

   }

   private class CodeAssitantScrollPanel extends ScrollPanel implements HasMouseOutHandlers, HasKeyDownHandlers
   {

      /**
       * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
       */
      public HandlerRegistration addMouseOutHandler(MouseOutHandler handler)
      {
         return addDomHandler(handler, MouseOutEvent.getType());
      }

      /**
       * @see com.google.gwt.event.dom.client.HasKeyDownHandlers#addKeyDownHandler(com.google.gwt.event.dom.client.KeyDownHandler)
       */
      public HandlerRegistration addKeyDownHandler(KeyDownHandler handler)
      {
         return addDomHandler(handler, KeyDownEvent.getType());
      }

   }

   /**
    *  Lock Layer uses for locking of screen. Uses for hiding popups.
    */
   private class LockLayer extends AbsolutePanel
   {

      public LockLayer()
      {
         sinkEvents(Event.ONMOUSEDOWN);
      }

      @Override
      public void onBrowserEvent(Event event)
      {
         switch (DOM.eventGetType(event))
         {
            case Event.ONMOUSEDOWN :
               cancelAssistant();
               break;

         }
      }

   }

   protected class MousHandler implements ClickHandler, MouseOverHandler, DoubleClickHandler, MouseOutHandler
   {

      /**
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent event)
      {
         GroovyClassTokenWidget t = (GroovyClassTokenWidget)event.getSource();
         selectTokenWidget(t);
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
       */
      public void onMouseOver(MouseOverEvent event)
      {
         GroovyClassTokenWidget t = (GroovyClassTokenWidget)event.getSource();

         overWidget(t);
      }

      /**
       * @see com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.google.gwt.event.dom.client.DoubleClickEvent)
       */
      public void onDoubleClick(DoubleClickEvent event)
      {
         tokenSelected();
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
       */
      public void onMouseOut(MouseOutEvent event)
      {
         overWidget(null);
      }

   }

   protected class CodeAssistantFormKeyboardManager implements Event.NativePreviewHandler
   {

      /**
       * @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
       */
      public void onPreviewNativeEvent(NativePreviewEvent event)
      {
         NativeEvent nativeEvent = event.getNativeEvent();

         int type = event.getTypeInt();
         int typeEvent = Event.ONKEYDOWN;
         if (BrowserResolver.CURRENT_BROWSER.equals(Browser.FIREFOX))
         {
            typeEvent = Event.ONKEYPRESS;
         }

         if (type == typeEvent)
         {
            switch (nativeEvent.getKeyCode())
            {
               case KeyCodes.KEY_DOWN :
                  keyPressDown();
                  break;

               case KeyCodes.KEY_UP :
                  keyPressUP();
                  break;

               case KeyCodes.KEY_ENTER :
                  tokenSelected();
                  break;

               case KeyCodes.KEY_ESCAPE :
                  cancelAssistant();
                  break;

            }
         }
      }
   }

}
