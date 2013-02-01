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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.PopupMenuButton;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupMenuButtonTestCase extends TestCase
{

   private PopupMenuButton popupButton;

   private String enabledIcon1;

   private String disabledIcon1;

   private String enabledIcon2;

   private String disabledIcon2;

   private ImageButton showHideButton;

   private ImageButton enableDisableButton;

   private ImageButton changeIconButton;
   
   @Override
   public void draw()
   {
      FlowPanel panel = new FlowPanel();
      DOM.setStyleAttribute(panel.getElement(), "position", "relative");
      DOM.setStyleAttribute(panel.getElement(), "left", "50px");
      DOM.setStyleAttribute(panel.getElement(), "top", "50px");
      DOM.setStyleAttribute(panel.getElement(), "width", "300px");
      DOM.setStyleAttribute(panel.getElement(), "height", "100px");
      //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
      testCasePanel().add(panel);

      enabledIcon1 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.ok());
      disabledIcon1 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.okDisabled());
      enabledIcon2 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.cancel());
      disabledIcon2 = ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.cancelDisabled());

      popupButton = new PopupMenuButton(enabledIcon1, disabledIcon1);
      panel.add(popupButton);

      popupButton.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.add()), "Add command", new Command()
      {
         public void execute()
         {
            Window.alert("Add command");
         }
      });
      popupButton.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()), "Remove command",
         new Command()
         {
            public void execute()
            {
               Window.alert("Remove command");
            }
         });
      popupButton.addItem(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.search()), "Search", new Command()
      {
         public void execute()
         {
            Window.alert("Search");
         }
      });

      addButtonHeader("Visibility:");
      showHideButton = createButton("Hide", showHideButtonClickHandler);
      addButtonDelimiter("Enabling:");
      enableDisableButton = createButton("Disable", enableDisableButtonClickHandler);
      addButtonDelimiter("Icon:");
      changeIconButton = createButton("Change Icon", changeIconButtonClickHandler);
   }
   
   private ClickHandler showHideButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (popupButton.isVisible())
         {
            popupButton.setVisible(false);
            showHideButton.setText("Show");
         }
         else
         {
            popupButton.setVisible(true);
            showHideButton.setText("Hide");
         }

      }
   };

   private ClickHandler enableDisableButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (popupButton.isEnabled())
         {
            popupButton.setEnabled(false);
            enableDisableButton.setText("Enable");
         }
         else
         {
            popupButton.setEnabled(true);
            enableDisableButton.setText("Disable");
         }
      }
   };

   private ClickHandler changeIconButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (enabledIcon1.equals(popupButton.getIcon()))
         {
            popupButton.setIcon(enabledIcon2);
            popupButton.setDisabledIcon(disabledIcon2);
         }
         else
         {
            popupButton.setIcon(enabledIcon1);
            popupButton.setDisabledIcon(disabledIcon1);
         }
      }
   };

}
