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
package org.exoplatform.ideall.client.autocompletion.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.ideall.client.util.EnumTokenType;
import org.exoplatform.ideall.client.util.Token;
import org.exoplatform.ideall.client.util.TokenTemplate;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompleteForm extends Composite implements ChangeHandler, KeyPressHandler, KeyUpHandler
{

   public interface Style
   {

      public static String AUTO_PANEL = "exo-auto-panel";

      public static String AUTO_EDIT = "exo-auto-edit";

      public static String AUTO_LIST = "exo-auto-list";

   }

   private static int VISIBLE_ITEM = 10;

   private static String WIDTH = "250px";

   private static String HEIGHT = "178px";

   private HandlerManager eventBus;

   private AbsolutePanel lockLayer;

   private LockLayer blockMouseEventsPanel;

   private ListBox listBox;

   private List<Token> tokens;

   private TextBox textBox;

   private AutoPanel panel;

   private int selectedItem;

   private AutocompleteTokenSelectedHandler handler;

   private HashMap<Integer, Token> currentMapTokens;

   public AutoCompleteForm(HandlerManager eventBus, int left, int top, String startToken, List<Token> tokens,
      AutocompleteTokenSelectedHandler handler)
   {
      this.eventBus = eventBus;
      this.tokens = tokens;
      this.handler = handler;
      lockLayer = new AbsolutePanel();

      initWidget(lockLayer);

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

      panel = new AutoPanel();
      panel.setWidth(WIDTH);
      panel.addStyleName(Style.AUTO_PANEL);

      textBox = new TextBox();
      textBox.setWidth("100%");
      textBox.setText(startToken);
      textBox.setStyleName(Style.AUTO_EDIT);

      //      if (BrowserResolver.currentBrowser == Browser.FIREFOX)
      //      {
      //         textBox.addKeyPressHandler(this);
      //      }
      //      else
      //      {
      //      }
      textBox.addKeyUpHandler(this);

      listBox = new ListBox();
      listBox.setWidth("100%");
      listBox.addChangeHandler(this);
      listBox.setStyleName(Style.AUTO_LIST);
      listBox.setVisibleItemCount(VISIBLE_ITEM);

      selectedItem = 0;
      currentMapTokens = new HashMap<Integer, Token>();

      //      int clientWidth = Window.getClientWidth();
      //TODO Edit this
//      int clientHeight = Window.getClientHeight();
//      if (top + 200 < clientHeight)
//      {
//         panel.add(textBox);
//         panel.add(listBox);
//      }
//      else
//      {
//         panel.add(listBox);
//         panel.add(textBox);
//      }
      panel.add(textBox);
      panel.add(listBox);
      
      lockLayer.add(panel, left, top);
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
    * 
    */
   protected void filterListToken()
   {
      List<Token> token = new ArrayList<Token>();

      String editText = textBox.getText();
      editText = editText.substring(0, textBox.getCursorPos());

      for (Token s : tokens)
      {
         if (s.getName().startsWith(editText) || s.getName().equals(editText))
         {
            token.add(s);
         }
      }
      
      Collections.sort(token,new TokenComparator());
      
      fillList(token);
   }

   private class TokenComparator implements Comparator<Token>
   {

      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      public int compare(Token t1, Token t2)
      {         
         return t1.getName().compareTo(t2.getName());
      }
      
   }
   /**
    * 
    */
   private void fillList(List<Token> tokens)
   {
      listBox.clear();
      currentMapTokens.clear();
      
      for (Token s : tokens)
      {
         if(EnumTokenType.TEMPLATE.value().equals(s.getType().value()))
         {
            listBox.addItem(((TokenTemplate)s).getDescription() + "-" + s.getType().value(), "" + s.hashCode());
         }
         else
         {
            listBox.addItem(s.getName() + " - " + s.getType().value(), "" + s.hashCode());            
         }
         currentMapTokens.put(s.hashCode(), s);
      }
     
      NodeList<Element> list = listBox.getElement().getElementsByTagName("OPTION");
      for (int i = 0; i < list.getLength(); i++)
      {
         list.getItem(i).setClassName("exo-auto-list-" + currentMapTokens.get(Integer.parseInt(listBox.getValue(i))).getType().value());         
      }
      
      if (tokens.size() > 0)
         listBox.setSelectedIndex(0);
      if (tokens.size() <= 10)
      {
         if (tokens.size() < 3)
            listBox.setVisibleItemCount(3);
         else
            listBox.setVisibleItemCount(tokens.size());
      }
      else
      {
         listBox.setVisibleItemCount(VISIBLE_ITEM);
      }

      selectedItem = 0;
   }

   /**
    * @see com.google.gwt.event.dom.client.ChangeHandler#onChange(com.google.gwt.event.dom.client.ChangeEvent)
    */
   public void onChange(ChangeEvent event)
   {
      if (listBox.getItemCount() > 0)
      {
         selectedItem = listBox.getSelectedIndex();
         int i =Integer.parseInt(listBox.getValue(selectedItem));
         textBox.setValue(currentMapTokens.get(i).getName());
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
    * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
    */
   public void onKeyPress(KeyPressEvent event)
   {

      int keyCode = event.getNativeEvent().getKeyCode();

      if (keyCode == KeyCodes.KEY_ESCAPE)
      {
         cancelAutocomplete();
         return;
      }

      if (keyCode == KeyCodes.KEY_ENTER)
      {
         pasteAutocomplete();
         return;
      }

      if (keyCode == KeyCodes.KEY_UP)
      {
         listBoxUP();
         return;
      }

      if (keyCode == KeyCodes.KEY_DOWN)
      {
         listBoxDown();
         return;
      }

      new Timer()
      {

         @Override
         public void run()
         {

            filterListToken();
         }
      }.schedule(10);
   }

   /**
    * 
    */
   public void cancelAutocomplete()
   {
      lockLayer.removeFromParent();
      handler.onAutocompleteCancel();
   }

   /**
    * 
    */
   private void listBoxDown()
   {
      if (selectedItem < listBox.getItemCount() - 1)
      {
         listBox.setSelectedIndex(selectedItem + 1);
         onChange(null);
      }
   }

   /**
    * 
    */
   private void listBoxUP()
   {
      if (selectedItem > 0)
      {
         listBox.setSelectedIndex(selectedItem - 1);
         onChange(null);
      }

   }

   /**
    * 
    */
   private void pasteAutocomplete()
   {

      String tokenResponse;
      if (listBox.getItemCount() == 0)
      {
         tokenResponse = textBox.getValue();
      }
      else
      {
         int key = Integer.parseInt(listBox.getValue(selectedItem));
         Token t = currentMapTokens.get(key);
         if(t.getType().value().equals(EnumTokenType.TEMPLATE.value()))
            tokenResponse = ((TokenTemplate)t).getTemplate();         
         else
         tokenResponse = t.getName();
      }
            
      handler.onAutocompleteTokenSelected(tokenResponse);
      lockLayer.removeFromParent();
      
   }

   /**
    * @see com.google.gwt.event.dom.client.KeyUpHandler#onKeyUp(com.google.gwt.event.dom.client.KeyUpEvent)
    */
   public void onKeyUp(KeyUpEvent event)
   {
      int keyCode = event.getNativeKeyCode();

      if (keyCode == KeyCodes.KEY_ESCAPE)
      {
         cancelAutocomplete();
         return;
      }

      if (keyCode == KeyCodes.KEY_ENTER)
      {
         pasteAutocomplete();
         return;
      }

      if (keyCode == KeyCodes.KEY_UP)
      {
         listBoxUP();
         return;
      }

      if (keyCode == KeyCodes.KEY_DOWN)
      {
         listBoxDown();
         return;
      }

      filterListToken();

   }

   private class AutoPanel extends VerticalPanel
   {
      public AutoPanel()
      {
         sinkEvents(Event.ONDBLCLICK | Event.ONKEYUP);
      }

      /**
       * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user.client.Event)
       */
      @Override
      public void onBrowserEvent(Event event)
      {
         switch (DOM.eventGetType(event))
         {
            case Event.ONDBLCLICK :
               pasteAutocomplete();
               break;
            case Event.ONKEYUP :
               if (event.getKeyCode() == KeyCodes.KEY_ENTER)
               {
                  pasteAutocomplete();
                  return;
               }
               if (event.getKeyCode() == KeyCodes.KEY_ESCAPE)
               {
                  cancelAutocomplete();
                  return;
               }
               break;
         }
      }
   }

}
