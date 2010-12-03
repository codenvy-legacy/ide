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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent;
import org.exoplatform.gwtframework.ui.client.event.WindowResizedHandler;
import org.exoplatform.ide.client.autocompletion.AutocompleteTokenSelectedHandler;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompleteForm extends Composite implements ChangeHandler, WindowResizedHandler
{

   private AbsolutePanel absolutePanel;

   private AbsolutePanel lockLayer;

   private SimplePanel descriptionPanel;

   private LockLayer blockMouseEventsPanel;

   private List<Token> items;

   private AutoCompleteScrollPanel scrollPanel;

   private FlowPanel flowPanel;

   private MousHandler mousHandler;

   private List<TokenWidget> widgets;

   private TextBox textBox;

   private TokenWidget overedWidget;

   private TokenWidget selectedWidget;

   private AutoCompleteFormKeyboardManager keyboardManager;

   private HandlerRegistration keyboardManagerRegistration;

   private AutocompleteTokenSelectedHandler handler;

   private HashMap<TokenType, ImageResource> images;

   private VerticalPanel panel;

   private Handlers handlers;

   public AutoCompleteForm(HandlerManager eventBus, int left, int top, String prefix, List<Token> items,
      HashMap<TokenType, ImageResource> images, AutocompleteTokenSelectedHandler handler)
   {
      //super(true);
      this.items = items;
      this.images = images;
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

      flowPanel = new FlowPanel();

      scrollPanel = new AutoCompleteScrollPanel();

      scrollPanel.add(flowPanel);

      mousHandler = new MousHandler();
      flowPanel.setWidth("100%");

      scrollPanel.addMouseOutHandler(mousHandler);

      scrollPanel.setHeight("200px");
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

      //      panel.add(textBox);
      //      panel.add(scrollPanel);
      panel.setStyleName(Style.AUTO_PANEL);

      lockLayer.add(panel, left, top);

      widgets = new ArrayList<TokenWidget>();

      this.items = items;

      keyboardManager = new AutoCompleteFormKeyboardManager();

      keyboardManagerRegistration = Event.addNativePreviewHandler(keyboardManager);

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

   /**
    * @param tokens
    */
   private void filterListToken()
   {
      List<Token> list = new ArrayList<Token>();

      String editText = textBox.getText();
      editText = editText.substring(0, textBox.getCursorPos());

      for (Token t : items)
      {
         if (t.getName() == null)
            continue;
         if (t.getName().startsWith(editText))
         {
            list.add(t);
         }
      }

      Collections.sort(list, new TokenComparator());
      fillListToken(list);
   }

   private void fillListToken(List<Token> tokens)
   {
      widgets.clear();
      flowPanel.clear();
      if (tokens.size() == 0)
         return;

      for (int i = 0; i < tokens.size(); i++)
      {
         Token t = tokens.get(i);
         TokenWidget widget = new TokenWidget(t, i);
         widget.addClickHandler(mousHandler);
         widget.addMouseOverHandler(mousHandler);
         widget.addDoubleClickHandler(mousHandler);
         widget.setWidth("100%");
         widgets.add(widget);
         flowPanel.add(widget);
      }

      selectToken(widgets.get(0));
   }

   private class TokenComparator implements Comparator<Token>
   {

      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      public int compare(Token t1, Token t2)
      {
         //tag more then attribute
         if (t1.getType().equals(TokenType.TAG))
         {
            if (t2.getType().equals(TokenType.ATTRIBUTE))
            {
               return -1;
            }
         }
         return t1.getName().compareTo(t2.getName());
      }

   }

   /**
    * @see com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt.event.dom.client.ChangeEvent)
    */
   public void onChange(ChangeEvent event)
   {
      if (widgets.size() > 0)
      {
         textBox.setValue(selectedWidget.getToken().getName());
      }
   }

   protected class MousHandler implements ClickHandler, MouseOverHandler, DoubleClickHandler, MouseOutHandler
   {

      /**
       * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
       */
      public void onClick(ClickEvent event)
      {
         TokenWidget t = (TokenWidget)event.getSource();
         selectToken(t);
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOverHandler#onMouseOver(com.google.gwt.event.dom.client.MouseOverEvent)
       */
      public void onMouseOver(MouseOverEvent event)
      {
         TokenWidget t = (TokenWidget)event.getSource();

         overWidget(t);
      }

      /**
       * @see com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.google.gwt.event.dom.client.DoubleClickEvent)
       */
      public void onDoubleClick(DoubleClickEvent event)
      {
         pasteAutocomplete();
      }

      /**
       * @see com.google.gwt.event.dom.client.MouseOutHandler#onMouseOut(com.google.gwt.event.dom.client.MouseOutEvent)
       */
      public void onMouseOut(MouseOutEvent event)
      {
         overWidget(null);
      }

   }

   private void selectWidget(int i)
   {
      scrollPanel.ensureVisible(widgets.get(i));
      //         DOM.scrollIntoView(widgets.get(i).getElement());
      //      ensureVisibleImpl(scrollPanel.getElement(), widgets.get(i).getElement());
      selectToken(widgets.get(i));
   }

   /**
    * @param widget
    */
   public void selectToken(TokenWidget widget)
   {
      if (widget.equals(selectedWidget))
      {
         return;
      }

      if (selectedWidget != null)
      {
         selectedWidget.setStyleName(Style.AUTO_LIST_ITEM);
      }

      selectedWidget = widget;

      if (widget.equals(overedWidget))
      {
         selectedWidget.setStyleName(Style.AUTO_LIST_ITEM_OVERED);
      }
      else
      {
         selectedWidget.setStyleName(Style.AUTO_LIST_ITEM_SELECTED);
      }

      timer.cancel();
      if (descriptionPanel != null)
      {
         descriptionPanel.removeFromParent();
         descriptionPanel = null;
      }
      if (selectedWidget.getToken().getFullDescription() != null)
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
         descriptionPanel = new SimplePanel();
         descriptionPanel.setWidth(width + "px");
         descriptionPanel.setHeight("" + (panel.getOffsetHeight() - 2));
         descriptionPanel.getElement().setInnerHTML(selectedWidget.getToken().getFullDescription());
         descriptionPanel.setStyleName(Style.AUTO_DESCRIPTION_PANEL);
         int clientWidth = Window.getClientWidth();

         if (clientWidth < panel.getAbsoluteLeft() + panel.getOffsetWidth() + 3 + width)
            lockLayer.add(descriptionPanel, panel.getAbsoluteLeft() - width - 4, panel.getAbsoluteTop());
         else
            lockLayer.add(descriptionPanel, panel.getAbsoluteLeft() + panel.getOffsetWidth() + 3,
               panel.getAbsoluteTop());
      }
   };

   /**
    * 
    */
   public void listBoxDown()
   {
      if (selectedWidget == null)
         return;
      int i = selectedWidget.getNumber();
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

      int i = selectedWidget.getNumber();
      if (0 < i)
      {
         selectWidget(i - 1);
      }
   }

   /**
    * 
    */
   private void cancelAutocomplete()
   {
      timer.cancel();
      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }
      handlers.removeHandlers();
      handler.onAutocompleteCancel();
      lockLayer.removeFromParent();
   }

   /**                             
    * 
    */
   private void pasteAutocomplete()
   {
      timer.cancel();

      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }

      Token token;
      if (widgets.size() == 0)
      {
         token = new Token(textBox.getValue(), TokenType.VARIABLE);
      }
      else
      {
         token = selectedWidget.getToken();
      }

      lockLayer.removeFromParent();
      handler.onAutocompleteTokenSelected(token);

   }

   /**
    * @param t
    */
   private void overWidget(TokenWidget t)
   {

      if (t == overedWidget)
      {
         return;
      }

      if (t == null)
      {
         if (overedWidget.equals(selectedWidget))
         {
            overedWidget.setStyleName(Style.AUTO_LIST_ITEM_SELECTED);
         }
         else
         {
            overedWidget.setStyleName(Style.AUTO_LIST_ITEM);
         }
         overedWidget = null;
         return;
      }

      if (overedWidget != null)
      {
         if (overedWidget.equals(selectedWidget))
         {
            overedWidget.setStyleName(Style.AUTO_LIST_ITEM_SELECTED);
         }
         else
         {
            overedWidget.setStyleName(Style.AUTO_LIST_ITEM);
         }
      }

      overedWidget = t;
      overedWidget.setStyleName(Style.AUTO_LIST_ITEM_OVERED);
   }

   protected class TokenWidget extends Composite implements HasClickHandlers, HasMouseOverHandlers,
      HasDoubleClickHandlers
   {

      private Grid grid;

      private Token token;

      private int number;

      public TokenWidget(Token token, int number)
      {
         this.token = token;
         this.number = number;

         grid = new Grid(1, 3);
         grid.setStyleName(Style.AUTO_LIST_ITEM);

         Image i = new Image(images.get(token.getType()));
         i.setHeight("16px");
         grid.setWidget(0, 0, i);

         if (token.getShortDescription() != null && !"".equals(token.getShortDescription()))
            grid.setWidget(0, 1, new Label(token.getName() + " " + token.getShortDescription(), false));
         else
            grid.setWidget(0, 1, new Label(token.getName(), false));

         grid.setWidget(0, 2, new Label(token.getType().name()));

         grid.getCellFormatter().setWidth(0, 0, "16px");
         grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
         grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
         grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_RIGHT);

         initWidget(grid);

      }

      /**
       * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
       */
      public HandlerRegistration addClickHandler(ClickHandler handler)
      {

         return addDomHandler(handler, ClickEvent.getType());
      }

      /**
       * @return the number
       */
      public int getNumber()
      {
         return number;
      }

      /**
       * @return the token
       */
      public Token getToken()
      {
         return token;
      }

      /**
       * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
       */
      public HandlerRegistration addMouseOverHandler(MouseOverHandler handler)
      {
         return addDomHandler(handler, MouseOverEvent.getType());
      }

      /**
       * @see com.google.gwt.event.dom.client.HasDoubleClickHandlers#addDoubleClickHandler(com.google.gwt.event.dom.client.DoubleClickHandler)
       */
      public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler)
      {
         return addDomHandler(handler, DoubleClickEvent.getType());
      }

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
         if (type == Event.ONKEYDOWN)
         {
            switch (nativeEvent.getKeyCode())
            {
               case KeyCodes.KEY_DOWN :
                  listBoxDown();
                  break;

               case KeyCodes.KEY_UP :
                  listBoxUP();
                  break;

               case KeyCodes.KEY_ENTER :
            	  event.cancel();
                  pasteAutocomplete();
                  break;

               case KeyCodes.KEY_ESCAPE :
                  cancelAutocomplete();
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

   /**
    * @see org.exoplatform.gwtframework.ui.client.event.WindowResizedHandler#onWindowResized(org.exoplatform.gwtframework.ui.client.event.WindowResizedEvent)
    */
   public void onWindowResized(WindowResizedEvent event)
   {
      cancelAutocomplete();
   }

}
