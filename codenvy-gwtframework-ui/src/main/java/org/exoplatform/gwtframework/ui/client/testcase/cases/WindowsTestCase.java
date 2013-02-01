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
package org.exoplatform.gwtframework.ui.client.testcase.cases;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.window.Window;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WindowsTestCase extends TestCase
{

   @Override
   public void draw()
   {
      ImageButton showWindowButton = new ImageButton("Open Popup Window");
      testCasePanel().add(showWindowButton);
      showWindowButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            openPopupWindow();
         }
      });

      testCasePanel().add(new HTML("<br>"));

      ImageButton showModalWindowButton = new ImageButton("Open Modal Window");
      testCasePanel().add(showModalWindowButton);
      showModalWindowButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            openModalWindow();
         }
      });

   }

   private void openPopupWindow()
   {
      Window window = new Window("Popup Window");
      window.setAnimationEnabled(true);
      window.setCanClose(true);
      window.setModal(false);
      window.setWidth(500);
      window.setHeight(300);

      Widget contentWidget = new FlowPanel();
      contentWidget.setSize("100%", "100%");
      contentWidget.getElement().getStyle().setBackgroundColor("#CCCCFF");
      window.add(contentWidget);

      window.center();
      window.show();
   }

   private void openModalWindow()
   {
      Window window = new Window("Modal Window", new Image(ShowCaseImageBundle.INSTANCE.search()));
      window.setAnimationEnabled(true);
      window.setCanClose(true);
      window.setModal(true);
      window.setWidth(550);
      window.setHeight(250);
      window.setGlassEnabled(true);

      Widget contentWidget = new FlowPanel();
      contentWidget.setSize("100%", "100%");
      contentWidget.getElement().getStyle().setBackgroundColor("#AACC00");
      window.add(contentWidget);

      window.center();
      window.show();
   }

}
