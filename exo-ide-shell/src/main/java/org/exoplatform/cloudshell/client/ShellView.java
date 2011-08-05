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
package org.exoplatform.cloudshell.client;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;

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

   public ShellView()
   {
      setWidth("100%");
      setHeight("100%");

      clearButton = createButton("clearButton", "Clear");
      //      add(clearButton);

      termText = new TermText(32);
      termText.setWidth("800px");
      termText.setHeight("450px");
      add(termText);
      setStyleName("shell-container");
      //      setWidth("600px");
      //      setHeight("400px");
      RootPanel.get().add(this);
      focusInConsole();
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
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#getClearButton()
    */
   @Override
   public HasClickHandlers getClearButton()
   {
      return clearButton;
   }

   /**
    * @see org.exoplatform.cloudshell.client.ConsoleWriter#print(java.lang.String)
    */
   @Override
   public void print(String str)
   {
      termText.print(str);
      termText.getElement().setScrollTop(termText.getElement().getScrollHeight());
      termText.repaint();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ConsoleWriter#clearConsole()
    */
   @Override
   public void clearConsole()
   {
      termText.clear();
      termText.repaint();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#getKeyPressHandler()
    */
   @Override
   public HasKeyPressHandlers getKeyPressHandler()
   {
      return termText;
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#getKeyDownHandler()
    */
   @Override
   public HasKeyDownHandlers getKeyDownHandler()
   {
      return termText;
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#appendBuffer(char)
    */
   @Override
   public void appendBuffer(char c)
   {
      termText.bufferAppend(c);
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#appendBuffer(java.lang.CharSequence)
    */
   @Override
   public void appendBuffer(CharSequence c)
   {
      termText.bufferAppend(c);
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#removeFromBuffer()
    */
   @Override
   public void removeFromBuffer()
   {
      termText.bufferDrop();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#submitBuffer()
    */
   @Override
   public String submitBuffer()
   {
      return termText.bufferSubmit();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#refreshConsole()
    */
   @Override
   public void refreshConsole()
   {
      termText.repaint();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#getBuffer()
    */
   @Override
   public String getBuffer()
   {
      return termText.getBuffer();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#focusInConsole()
    */
   @Override
   public void focusInConsole()
   {
      termText.setFocus(true);
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#clearBuffer()
    */
   @Override
   public void clearBuffer()
   {
      termText.bufferClear();
   }

   /**
    * @see org.exoplatform.cloudshell.client.ShellPresenter.Display#printPrompt()
    */
   @Override
   public void printPrompt()
   {
      termText.printPrompt();
      termText.getElement().setScrollTop(termText.getElement().getScrollHeight());
      termText.repaint();
   }
}
