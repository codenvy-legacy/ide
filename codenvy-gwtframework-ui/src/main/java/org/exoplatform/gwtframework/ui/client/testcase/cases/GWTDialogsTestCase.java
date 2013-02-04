/**
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
 *
 */

package org.exoplatform.gwtframework.ui.client.testcase.cases;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialog.GWTDialogs;
import org.exoplatform.gwtframework.ui.client.dialog.StringValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GWTDialogsTestCase extends TestCase
{

   @Override
   public void draw()
   {
      new GWTDialogs();

      ImageButton askForValueButton = new ImageButton("Ask For Value");
      testCasePanel().add(askForValueButton);

      askForValueButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            Dialogs.getInstance().askForValue("Ask-value-title", "Ask-value-message", "default value",
               new StringValueReceivedHandler()
               {
                  public void stringValueReceived(String value)
                  {
                     Window.alert("String value received: [" + value + "]");
                  }
               });
         }
      });

      testCasePanel().add(new HTML("<br>"));

      ImageButton askButton = new ImageButton("Ask");
      testCasePanel().add(askButton);

      askButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            Dialogs.getInstance().ask("Ask-title", "Ask-message", new BooleanValueReceivedHandler()
            {
               public void booleanValueReceived(Boolean value)
               {
                  Window.alert("Boolean value received: [" + value + "]");
               }
            });
         }
      });

      testCasePanel().add(new HTML("<br>"));

      ImageButton warningButton = new ImageButton("Error");
      testCasePanel().add(warningButton);

      warningButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {

            Dialogs.getInstance().showError("Error-title", "Error-message", new BooleanValueReceivedHandler()
            {
               public void booleanValueReceived(Boolean value)
               {
                  Window.alert("Boolean value received: [" + value + "]");
               }
            });

         }
      });

      testCasePanel().add(new HTML("<br>"));

      ImageButton infoButton = new ImageButton("Info");
      testCasePanel().add(infoButton);

      infoButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            Dialogs.getInstance().showInfo("Info-title", "Info-message", new BooleanValueReceivedHandler()
            {
               public void booleanValueReceived(Boolean value)
               {
                  Window.alert("Boolean value received: [" + value + "]");
               }
            });

         }
      });

   }

}
