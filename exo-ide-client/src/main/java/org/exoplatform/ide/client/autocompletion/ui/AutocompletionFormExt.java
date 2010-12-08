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
package org.exoplatform.ide.client.autocompletion.ui;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent;
import org.exoplatform.gwtframework.ui.client.event.WindowResizedHandler;
import org.exoplatform.ide.client.autocompletion.TokenSelectedHandler;
import org.exoplatform.ide.client.framework.codeassistant.TokenWidget;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class is UI component that represent autocompletion form.
 * This form works with any bean, but also required implementation of {@link TokenWidgetFactory}
 * to build {@link TokenWidget}
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 4:18:55 PM evgen $
 *
 */
public class AutocompletionFormExt<T> extends Composite implements ChangeHandler, WindowResizedHandler
{

   private AbsolutePanel absolutePanel;

   private AbsolutePanel lockLayer;

   private Widget descriptionPanel;

   private LockLayer blockMouseEventsPanel;

   private AutoCompleteScrollPanel scrollPanel;

   private FlowPanel flowPanel;

   private MousHandler mousHandler;

   private TextBox textBox;

   private TokenWidget<T> overedWidget;

   private TokenWidget<T> selectedWidget;

   private AutoCompleteFormKeyboardManager keyboardManager;

   private HandlerRegistration keyboardManagerRegistration;

   private VerticalPanel panel;

   private Handlers handlers;

   private TokenSelectedHandler handler;

   private List<TokenWidget<T>> widgets;

   private List<TokenWidget<T>> allWidgets;

   private boolean isTextBoxHasFocus = true;

   public AutocompletionFormExt(HandlerManager eventBus, int left, int top, String prefix, List<T> items,
      TokenWidgetFactory<T> widgetFactory, TokenSelectedHandler handler)
   {
      this.handler = handler;

      handlers = new Handlers(eventBus);
      handlers.addHandler(WindowResizedEvent.TYPE, this);

      absolutePanel = new AbsolutePanel();

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

      textBox = new TextBox();
      textBox.setWidth("100%");
      textBox.setText(prefix);
      textBox.setStyleName(Style.AUTO_EDIT);

      textBox.addFocusHandler(new FocusHandler()
      {

         public void onFocus(FocusEvent event)
         {
            isTextBoxHasFocus = true;
         }
      });

      textBox.addBlurHandler(new BlurHandler()
      {

         public void onBlur(BlurEvent event)
         {
            isTextBoxHasFocus = false;
         }
      });

      flowPanel = new FlowPanel();

      scrollPanel = new AutoCompleteScrollPanel();
      scrollPanel.setAlwaysShowScrollBars(true);
      scrollPanel.add(flowPanel);

      mousHandler = new MousHandler();
      flowPanel.setWidth("100%");

      scrollPanel.addMouseOutHandler(mousHandler);

      scrollPanel.setHeight("195px");
      scrollPanel.setWidth("300px");

      panel = new VerticalPanel();

      int clientHeight = Window.getClientHeight();
      if (top + 220 < clientHeight)
      {
         panel.add(textBox);
         panel.add(scrollPanel);
      }
      else
      {
         panel.add(scrollPanel);
         panel.add(textBox);
         top = top - 200;
      }

      panel.setStyleName(Style.AUTO_PANEL);

      lockLayer.add(panel, left, top);

      keyboardManager = new AutoCompleteFormKeyboardManager();

      keyboardManagerRegistration = Event.addNativePreviewHandler(keyboardManager);

      widgets = new ArrayList<TokenWidget<T>>();
      allWidgets = new ArrayList<TokenWidget<T>>();
      for (T t : items)
      {
         TokenWidget<T> w = widgetFactory.buildTokenWidget(t);
         w.addClickHandler(mousHandler);
         w.addMouseOverHandler(mousHandler);
         w.addDoubleClickHandler(mousHandler);
         allWidgets.add(w);
      }
      flowPanel.add(new Label("No Proposals"));

      DeferredCommand.addCommand(new Command()
      {

         public void execute()
         {
            textBox.setFocus(true);
            textBox.setCursorPos(textBox.getText().length());
            filterListToken();
         }
      });

   }

   private void filterListToken()
   {
      if (allWidgets.isEmpty())
      {
         return;
      }
      String editText = textBox.getText();
      editText = editText.substring(0, textBox.getCursorPos());

      widgets.clear();
      flowPanel.clear();
      for (TokenWidget<T> w : allWidgets)
      {
         if (w.getTokenName().toLowerCase().startsWith(editText.toLowerCase()))
         {
            widgets.add(w);
            flowPanel.add(w);
         }
      }
      if (!widgets.isEmpty())
      {
         selectWidget(0);
      }
   }

   /**
    * @see com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt.event.dom.client.ChangeEvent)
    */
   public void onChange(ChangeEvent event)
   {
      if (widgets.size() > 0)
      {
         textBox.setValue(selectedWidget.getTokenName());
      }
   }

   /**
    * 
    */
   public void listBoxDown()
   {
      if (selectedWidget == null)
         return;
      int i = widgets.indexOf(selectedWidget);
      if (widgets.size() - 1 > i)
      {
         selectWidget(i + 1);
      }
   }

   /**
    * 
    */
   public void listBoxUP()
   {
      if (selectedWidget == null)
         return;

      int i = widgets.indexOf(selectedWidget);
      if (0 < i)
      {
         selectWidget(i - 1);
      }
   }

   private native void scroll(Element scroll, int pos)/*-{
      scroll.scrollTop =  scroll.scrollTop + pos;
   }-*/;

   private void selectWidget(int i)
   {

      Element scroll = scrollPanel.getElement();
      Element item = widgets.get(i).getElement();
      if (item.getAbsoluteTop() < scroll.getAbsoluteTop())
      {
         scroll(scroll, -item.getOffsetHeight());
      }
      else if (item.getAbsoluteBottom() + 15 > scroll.getAbsoluteBottom())
      {
         scroll(scroll, item.getAbsoluteBottom() - scroll.getAbsoluteBottom() + 15);
      }

      selectToken(widgets.get(i));
   }

   /**
    * @param widget
    */
   public void selectToken(TokenWidget<T> widget)
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

      timer.cancel();
      if (descriptionPanel != null)
      {
         descriptionPanel.removeFromParent();
         descriptionPanel = null;
      }
      if (selectedWidget.getTokenDecription() != null)
      {
         timer.schedule(1000);
      }

   }
   
   private Timer timer = new Timer()
   {

      @Override
      public void run()
      {
         if (descriptionPanel != null)
         {
            descriptionPanel.removeFromParent();
         }
         int width = 300;
         descriptionPanel = new Frame(selectedWidget.getTokenDecription());
         descriptionPanel.setWidth(width + "px");
         descriptionPanel.setHeight("" + (panel.getOffsetHeight() - 2));
         
//         descriptionPanel.getElement().setInnerText(selectedWidget.getTokenDecription());
         descriptionPanel.setStyleName(Style.AUTO_DESCRIPTION_PANEL);
         int clientWidth = Window.getClientWidth();

         if (clientWidth < panel.getAbsoluteLeft() + panel.getOffsetWidth() + 3 + width)
            lockLayer.add(descriptionPanel, panel.getAbsoluteLeft() - width - 4, panel.getAbsoluteTop());
         else
            lockLayer.add(descriptionPanel, panel.getAbsoluteLeft() + panel.getOffsetWidth() + 3,
               panel.getAbsoluteTop());
      }
   };

   private void overWidget(TokenWidget<T> t)
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

   /**
    * 
    */
   private void cancelAutocomplete()
   {
      timer.cancel();
      removeHandlers();
      handler.onCancelAutoComplete();
      lockLayer.removeFromParent();
   }

   /**
    * 
    */
   public void tokenSelected()
   {
      timer.cancel();
      removeHandlers();
      if (selectedWidget == null)
      {
         handler.onTokenSelected(textBox.getText());
      }
      else
      {
         handler.onTokenSelected(selectedWidget.getTokenValue());
      }
      lockLayer.removeFromParent();
   }

   /**
    * 
    */
   private void removeHandlers()
   {
      handlers.removeHandlers();
      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.event.WindowResizedHandler#onWindowResized(org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent)
    */
   public void onWindowResized(WindowResizedEvent event)
   {
      cancelAutocomplete();
   }

   protected class AutoCompleteFormKeyboardManager implements Event.NativePreviewHandler
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
                  event.cancel();
                  listBoxDown();
                  break;

               case KeyCodes.KEY_UP :
                  event.cancel();
                  listBoxUP();
                  break;

               case KeyCodes.KEY_ENTER :
                  event.cancel();
                  tokenSelected();
                  break;

               case KeyCodes.KEY_ESCAPE :
                  cancelAutocomplete();
                  break;

               case KeyCodes.KEY_LEFT :
               case KeyCodes.KEY_RIGHT :
                  if (!isTextBoxHasFocus)
                     break;

               default :
                  new Timer()
                  {

                     @Override
                     public void run()
                     {
                        filterListToken();
                     }
                  }.schedule(10);
                  break;
            }
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
         @SuppressWarnings("unchecked")
         TokenWidget<T> t = (TokenWidget<T>)event.getSource();
         selectToken(t);
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
       */
      public void onMouseOver(MouseOverEvent event)
      {
         @SuppressWarnings("unchecked")
         TokenWidget<T> t = (TokenWidget<T>)event.getSource();

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
               cancelAutocomplete();
               break;

         }
      }

   }

}
