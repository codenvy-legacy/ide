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
import org.exoplatform.gwtframework.ui.client.component.TextButton;
import org.exoplatform.gwtframework.ui.client.component.TextButton.TextAlignment;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TextButtonTestCase extends TestCase
{

   private static final String TEXT1 = "/home/vetal/ui/components";

   private static final String TEXT2 = "/rest/jcr/repository/production";

   private TextButton textButton;

   private ImageButton showHideStatusTextButton;

   private ImageButton changeStatusTextButton;

   private ImageButton setLeftAlignmentButton;

   private ImageButton setCenterAlignmentButton;

   private ImageButton setRightAlignmentButton;

   private ImageButton enableDisableCommandButton;

   private ImageButton setExecuteTypeButton;

   @Override
   public void draw()
   {
      FlowPanel panel = new FlowPanel();
      DOM.setStyleAttribute(panel.getElement(), "position", "relative");
      DOM.setStyleAttribute(panel.getElement(), "left", "50px");
      DOM.setStyleAttribute(panel.getElement(), "top", "50px");
      DOM.setStyleAttribute(panel.getElement(), "width", "z00px");
      DOM.setStyleAttribute(panel.getElement(), "height", "25px");
      //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
      testCasePanel().add(panel);

      textButton = new TextButton(TEXT1, textButtonCommand);
      textButton.setTitle("Title: " + TEXT1);
      textButton.setWidth("300px");
      panel.add(textButton);

      addButtonHeader("Visibility:");
      showHideStatusTextButton = createButton("Hide", showHideStatusTextButtonClickHandler);

      addButtonDelimiter("Text:");
      changeStatusTextButton = createButton("Change text", changeStatusTextButtonClickHandler);

      addButtonDelimiter("Text alignment");
      setLeftAlignmentButton = createButton("Align Text To Left", false, setLeftAlignmentButtonClickHandler);
      setCenterAlignmentButton = createButton("Align Text To Center", setCenterAlignmentButtonClickHandler);
      setRightAlignmentButton = createButton("Align Text To Right", setRightAlignmentButtonClickHandler);

      addButtonDelimiter("Command:");
      enableDisableCommandButton = createButton("Remove Command", enableDisableCommandButtonClickHandler);
      setExecuteTypeButton = createButton("Execute on Double Click", setExecuteTypeButtonClickHandler);
   }

   private ClickHandler showHideStatusTextButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (textButton.isVisible())
         {
            textButton.setVisible(false);
            showHideStatusTextButton.setText("Show");
         }
         else
         {
            textButton.setVisible(true);
            showHideStatusTextButton.setText("Hide");
         }
      }
   };

   private ClickHandler changeStatusTextButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (TEXT1.equals(textButton.getText()))
         {
            textButton.setText(TEXT2);
            textButton.setTitle("Title: " + TEXT2);
         }
         else
         {
            textButton.setText(TEXT1);
            textButton.setTitle("Title: " + TEXT1);
         }
      }
   };

   private ClickHandler setLeftAlignmentButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         textButton.setTextAlignment(TextAlignment.LEFT);
         setLeftAlignmentButton.setEnabled(false);
         setCenterAlignmentButton.setEnabled(true);
         setRightAlignmentButton.setEnabled(true);
      }
   };

   private ClickHandler setCenterAlignmentButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         textButton.setTextAlignment(TextAlignment.CENTER);
         setLeftAlignmentButton.setEnabled(true);
         setCenterAlignmentButton.setEnabled(false);
         setRightAlignmentButton.setEnabled(true);
      }
   };

   private ClickHandler setRightAlignmentButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         textButton.setTextAlignment(TextAlignment.RIGHT);
         setLeftAlignmentButton.setEnabled(true);
         setCenterAlignmentButton.setEnabled(true);
         setRightAlignmentButton.setEnabled(false);
      }
   };

   private Command textButtonCommand = new Command()
   {
      public void execute()
      {
         Window.alert("Click!");
      }
   };

   private ClickHandler enableDisableCommandButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (textButton.getCommand() != null)
         {
            textButton.setCommand(null);
            enableDisableCommandButton.setText("Set Command");
            setExecuteTypeButton.setVisible(false);
         }
         else
         {
            textButton.setCommand(textButtonCommand);
            enableDisableCommandButton.setText("Remove Command");
            setExecuteTypeButton.setVisible(true);
         }
      }
   };

   private ClickHandler setExecuteTypeButtonClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         if (textButton.isExecuteCommandOnSingleClick())
         {
            textButton.setExecuteCommandOnSingleClick(false);
            setExecuteTypeButton.setText("Execute on Single Click");
         }
         else
         {
            textButton.setExecuteCommandOnSingleClick(true);
            setExecuteTypeButton.setText("Execute on Double Click");
         }

      }
   };

}
