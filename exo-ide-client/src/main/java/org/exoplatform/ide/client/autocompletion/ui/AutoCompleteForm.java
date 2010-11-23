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

import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ide.client.framework.codeassistant.api.AutocompleteTokenSelectedHandler;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
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
public class AutoCompleteForm extends Composite implements ChangeHandler, ResizeHandler
{

   public interface Style
   {

      public static final String AUTO_PANEL = "exo-autocomplete-panel";

      public static final String AUTO_EDIT = "exo-autocomplete-edit";

      public static final String AUTO_LIST = "exo-autocomplete-list";

   }

   private static final int VISIBLE_ITEM = 10;

   private static final String WIDTH = "250px";

   private AbsolutePanel lockLayer;

   private LockLayer blockMouseEventsPanel;

   private ListBox listBox;

   private List<Token> tokens;

   private TextBox textBox;

   private AutoPanel panel;

   private int selectedItem;

   private AutoCompleteFormKeyboardManager keyboardManager;

   private HandlerRegistration keyboardManagerRegistration;

   private AutocompleteTokenSelectedHandler handler;

   private HashMap<String, Token> currentMapTokens;

   public AutoCompleteForm(HandlerManager eventBus, int left, int top, String startToken, List<Token> tokens,
      AutocompleteTokenSelectedHandler handler)
   {
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

      listBox = new ListBox();
      listBox.setWidth("100%");
      listBox.addChangeHandler(this);
      listBox.setStyleName(Style.AUTO_LIST);
      listBox.setVisibleItemCount(VISIBLE_ITEM);
      listBox.setHeight("160px");

      selectedItem = 0;
      currentMapTokens = new HashMap<String, Token>();

      //    int clientWidth = Window.getClientWidth();
      //      TODO Edit this
      int clientHeight = Window.getClientHeight();
      if (top + 170 < clientHeight)
      {
         panel.add(textBox);
         panel.add(listBox);
      }
      else
      {
         panel.add(listBox);
         panel.add(textBox);
         top = top - 160;
      }

      keyboardManager = new AutoCompleteFormKeyboardManager();
      keyboardManagerRegistration = Event.addNativePreviewHandler(keyboardManager);

      Window.addResizeHandler(this);

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

      Collections.sort(token, new TokenComparator());

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
         if (Token.TokenType.TEMPLATE.equals(s.getType()))
         {
            listBox.addItem(s.getName() + " " + s.getShortDescription() + " - " + s.getType().toString(), ""
               + s.hashCode());
         }
         else
         {
            listBox.addItem(s.getName() + " - " + s.getType().toString(), "" + s.hashCode());
         }
         currentMapTokens.put("" + s.hashCode(), s);
      }
      NodeList<Element> list = listBox.getElement().getElementsByTagName("OPTION");
      for (int i = 0; i < list.getLength(); i++)
      {
         list.getItem(i).setClassName("exo-autocomplete-list-" + currentMapTokens.get(listBox.getValue(i)).getType());
      }

      if (tokens.size() > 0)
         listBox.setSelectedIndex(0);
      //      if (tokens.size() <= 10)
      //      {
      //         if (tokens.size() < 3)
      //            listBox.setVisibleItemCount(3);
      //         else
      //            listBox.setVisibleItemCount(tokens.size());
      //      }
      //      else
      //      {
      //         listBox.setVisibleItemCount(VISIBLE_ITEM);
      //      }

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
         String s = listBox.getValue(selectedItem);
         textBox.setValue(currentMapTokens.get(s).getName());
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
            default:
               break;
         }
      }

   }

   /**
    * 
    */
   private void cancelAutocomplete()
   {
      lockLayer.removeFromParent();
      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }
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

      Token tokenResponse;
      if (listBox.getItemCount() == 0)
      {
         tokenResponse = new Token(textBox.getValue(), TokenType.VARIABLE);
      }
      else
      {
         String key = listBox.getValue(selectedItem);
         tokenResponse = currentMapTokens.get(key);

      }

      handler.onAutocompleteTokenSelected(tokenResponse);

      keyboardManagerRegistration.removeHandler();
      keyboardManagerRegistration = null;

      lockLayer.removeFromParent();

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
            default:
               break;
         }
      }
   }

   /**
    * @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent)
    */
   public void onResize(ResizeEvent event)
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

}
