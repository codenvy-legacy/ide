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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Shell view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 2, 2011 11:03:40 AM anya $
 * 
 */
public class ShellView extends Composite implements ShellPresenter.Display
{
   private static ShellViewUiBinder uiBinder = GWT.create(ShellViewUiBinder.class);

   interface ShellViewUiBinder extends UiBinder<Widget, ShellView>
   {
   }

   /**
    * Content of the console.
    */
   @UiField
   PreElement content;

   /**
    * Text of the term.
    */
   @UiField
   TermText termText;

   /**
    * 
    */
   private TextBox textBox;

   public ShellView()
   {
      ShellClientBundle.INSTANCE.css().ensureInjected();
      initWidget(uiBinder.createAndBindUi(this));
      content.setId( "shellContent");
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

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#print(java.lang.String)
    */
   @Override
   public void print(String str)
   {
      content.setInnerHTML(content.getInnerText() + str);
      termText.clear();
      printPrompt();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#clearConsole()
    */
   @Override
   public void clearConsole()
   {
      content.setInnerHTML("");
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
      String buffer = termText.bufferSubmit();
      content.setInnerHTML(content.getInnerText() + termText.getState());
      termText.clear();
      return buffer;
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
      termText.printToTerm(getPrompt());
      termText.repaint();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#preparePaste()
    */
   @Override
   public void preparePaste()
   {
      textBox = new TextBox();
      textBox.getElement().setId("pasteBox");
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
      termText.printToTerm(str);
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#flush()
    */
   @Override
   public void flush()
   {
      printPrompt();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#println(java.lang.String)
    */
   @Override
   public void println(String str)
   {
      print(str + "\n");
   }

   /**
    * @see org.exoplatform.ide.shell.client.ConsoleWriter#getLength()
    */
   @Override
   public int getLength()
   {
      int cursorWidth = termText.getCursor().getOffsetWidth();
      cursorWidth = (cursorWidth > 0) ? cursorWidth : 1;
      return termText.getOffsetWidth() / cursorWidth;
   }

   public String getPrompt()
   {
      String path = "";
      if (Environment.get().getCurrentFolder() != null)
      {

         path = Environment.get().getCurrentFolder().getPath();
         if (!path.equals("/"))
         {
            path = path.substring(path.lastIndexOf("/") + 1, path.length());
         }

         path = Environment.get().getValue(EnvironmentVariables.USER_NAME) + ":" + path;
      }
      else
      {
         path = Environment.get().getValue(EnvironmentVariables.USER_NAME);
      }
      return path + "$ ";
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#moveLeft()
    */
   @Override
   public void moveLeft()
   {
      termText.moveLeft();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#moveRight()
    */
   @Override
   public void moveRight()
   {
      termText.moveRight();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#moveHome()
    */
   @Override
   public void moveHome()
   {
      termText.moveHome();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#moveEnd()
    */
   @Override
   public void moveEnd()
   {
      termText.moveEnd();
   }

   /**
    * @see org.exoplatform.ide.shell.client.ShellPresenter.Display#deleteSymbol()
    */
   @Override
   public void deleteSymbol()
   {
      termText.deleteSymbol();
   }
}
