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

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.window.ResizeableWindow;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class WindowRootPanelTestCase extends TestCase
{

   private AbsolutePanel region;

   @Override
   public void draw()
   {
      ImageButton showWindowButton = new ImageButton("Show Window Attached To Region");
      testCasePanel().add(showWindowButton);

      showWindowButton.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            showWindowInRegion();
         }
      });

      region = new AbsolutePanel();
      region.setPixelSize(500, 300);
      region.getElement().getStyle().setBackgroundColor("#99AAFF");
      region.getElement().getStyle().setLeft(150, Unit.PX);
      region.getElement().getStyle().setTop(50, Unit.PX);
      region.getElement().getStyle().setOverflow(Overflow.HIDDEN);
      testCasePanel().add(region);
   }

   private void showWindowInRegion()
   {
      final ResizeableWindow window = new ResizeableWindow("Window in region");
      window.setCanClose(true);
      window.setCanMaximize(true);
      window.setModal(false);
      window.setWidth(300);
      window.setHeight(150);

      FlowPanel contentWidget = new FlowPanel();
      contentWidget.setSize("100%", "100%");
      contentWidget.getElement().getStyle().setBackgroundColor("#CCFFCC");
      window.add(contentWidget);

      ImageButton button = new ImageButton("Close this window");
      contentWidget.add(button);
      button.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            window.destroy();
         }
      });

      window.showCentered(region);
   }

}
