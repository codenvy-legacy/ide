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
package org.exoplatform.ide.shell.client;

import com.google.gwt.user.client.DOM;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.shell.client.ShellPresenter;
import org.exoplatform.ide.shell.client.TermText;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 2, 2011 11:03:40 AM anya $
 *
 */
public class ShellView extends FlowPanel implements ShellPresenter.Display
{
   public static final int HEIGHT = 500;

   public static final int BUTTON_WIDTH = 90;

   public static final int BUTTON_HEIGHT = 22;

   public ImageButton clearButton;

   public TermText termText;

   private TextBox textBox;

   public ShellView()
   {
      setWidth("100%");
      setStyleName("shell-container");
      clearButton = createButton("clearButton", "Clear");

      termText = new TermText(32);
      add(termText);
      RootPanel.get().add(this);
      focusInConsole();
      getElement().setTabIndex(0);
      sinkEvents(Event.ONFOCUS);
   }

   /**
    * @see com.google.gwt.user.client.ui.Widget#onBrowserEvent(com.google.gwt.user.client.Event)
    */
   @Override
   public void onBrowserEvent(Event event)
   {
      focusInConsole();
      super.onBrowserEvent(event);
   }

   private ImageButton createButton(String id, String title)
   {
      ImageButton button = new ImageButton(title);
      button.setButtonId(id);
      button.setWidth(BUTTON_WIDTH + "px");
      button.setHeight(BUTTON_HEIGHT + "px");
      return button;
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#getClearButton()
    */
   @Override
   public HasClickHandlers getClearButton()
   {
      return clearButton;
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#print(java.lang.String)
    */
   @Override
   public void print(String str)
   {
      termText.print(str);
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#clearConsole()
    */
   @Override
   public void clearConsole()
   {
      termText.clear();
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#getKeyPressHandler()
    */
   @Override
   public HasKeyPressHandlers getKeyPressHandler()
   {
      return termText;
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#getKeyDownHandler()
    */
   @Override
   public HasKeyDownHandlers getKeyDownHandler()
   {
      return termText;
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#appendBuffer(char)
    */
   @Override
   public void appendBuffer(char c)
   {
      termText.bufferAppend(c);
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#appendBuffer(java.lang.CharSequence)
    */
   @Override
   public void appendBuffer(CharSequence c)
   {
      termText.bufferAppend(c);
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#removeFromBuffer()
    */
   @Override
   public void removeFromBuffer()
   {
      termText.bufferDrop();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#submitBuffer()
    */
   @Override
   public String submitBuffer()
   {
      return termText.bufferSubmit();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#refreshConsole()
    */
   @Override
   public void refreshConsole()
   {
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#getBuffer()
    */
   @Override
   public String getBuffer()
   {
      return termText.getBuffer();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#focusInConsole()
    */
   @Override
   public void focusInConsole()
   {
      termText.setFocus(true);
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#clearBuffer()
    */
   @Override
   public void clearBuffer()
   {
      termText.bufferClear();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#printPrompt()
    */
   @Override
   public void printPrompt()
   {
      termText.printPrompt();
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#preparePaste()
    */
   @Override
   public void preparePaste()
   {
      textBox = new TextBox();
      textBox.getElement().setId("paste-box");
      RootPanel.get().add(textBox, -1000, getElement().getOffsetHeight() - 2);
      textBox.setFocus(true);
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#finishPaste()
    */
   @Override
   public void finishPaste()
   {
      termText.bufferAppend(textBox.getText());
      textBox.removeFromParent();
      refreshConsole();
      focusInConsole();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#printToBuffer(java.lang.String)
    */
   @Override
   public void printToBuffer(String str)
   {
      termText.printToBuffer(str);
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#flush()
    */
   @Override
   public void flush()
   {
      termText.printPrompt();
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#println(java.lang.String)
    */
   @Override
   public void println(String str)
   {
      termText.print(str + "\n");
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#getLengts()
    */
   @Override
   public int getLengts()
   {
      return termText.getOffsetWidth() / DOM.getElementById("crash-cursor").getOffsetWidth();
   }
}
