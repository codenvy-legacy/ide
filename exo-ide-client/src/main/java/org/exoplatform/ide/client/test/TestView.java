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
package org.exoplatform.ide.client.test;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.gwt.impl.AbstractView;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestView extends AbstractView implements TestPresenter.Display
{

   private Button askOnCloseButton;
   
   private Button setTitle1Button;
   
   private Button setTitle2Button;

   public TestView(String viewId, String type, String title)
   {
      super(viewId, type, title, new Image(IDEImageBundle.INSTANCE.workspace()), DEFAULT_WIDTH, DEFAULT_HEIGHT);

      askOnCloseButton = new Button("Ask User Before Closing");
      add(askOnCloseButton);
      
      add(new HTML("<br>"));
      
      setTitle1Button = new Button("Set Title 1");
      add(setTitle1Button);
      
      setTitle2Button = new Button("Set Title 2");
      add(setTitle2Button);
      
      
   }

   @Override
   public HasClickHandlers getAskOnCloseButton()
   {
      return askOnCloseButton;
   }

   @Override
   public void setAskingEnabled(boolean askingEnabled)
   {
      if (askingEnabled)
      {
         askOnCloseButton.setText("Don't Ask Before Closing");
      }
      else
      {
         askOnCloseButton.setText("Ask User Before Closing");
      }
   }

   @Override
   public HasClickHandlers getSetIcon1Button()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasClickHandlers getSetIcon2Button()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public HasClickHandlers getSetTitle1Button()
   {
      return setTitle1Button;
   }

   @Override
   public HasClickHandlers getSetTitle2Button()
   {
      return setTitle2Button;
   }

}
