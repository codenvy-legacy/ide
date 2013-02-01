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

import org.exoplatform.gwtframework.ui.client.component.TextButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.testcase.TestCaseEntryPoint;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarAndStatusbarTestCase extends TestCase
{

   private Toolbar toolbar;

   private Toolbar statusbar;

   private String statusTextContent;

   private CheckBox dockingTypeCheckBox;
   
   @Override
   public void draw()
   {
      FlowPanel panel = new FlowPanel();
      DOM.setStyleAttribute(panel.getElement(), "position", "relative");
      DOM.setStyleAttribute(panel.getElement(), "left", "10px");
      DOM.setStyleAttribute(panel.getElement(), "top", "50px");
      DOM.setStyleAttribute(panel.getElement(), "width", "600px");
      DOM.setStyleAttribute(panel.getElement(), "height", "250px");
      //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
      DOM.setStyleAttribute(panel.getElement(), "borderWidth", "1px");
      DOM.setStyleAttribute(panel.getElement(), "borderStyle", "solid");
      DOM.setStyleAttribute(panel.getElement(), "borderColor", "#AAAAAA");
      testCasePanel().add(panel);

      toolbar = new Toolbar();
      panel.add(toolbar);

      HTML html = new HTML();
      html.setHeight("" + (250 - 32 - 50) + "px");
      //DOM.setStyleAttribute(html.getElement(), "borderBottom", "#AAAAAA 1px solid");
      panel.add(html);

      statusbar = new Toolbar();
      statusbar.setHeight("30px");
      String background =
         UIHelper.getGadgetImagesURL() + "../eXoStyle/skin/default/images/component/toolbar/statusbar_Background.png";
      statusbar.setBackgroundImage(background);
      statusbar.setItemsTopPadding(3);
      panel.add(statusbar);

      createButton("Add <b>Status Text</b> in Toolbar", addIconButtonInToolbar);
      createButton("Add <b>Status Text</b> in Statusbar", addIconButtonInStatusbar);

      dockingTypeCheckBox = new CheckBox("Right docking");
      controlsPanel().add(dockingTypeCheckBox);

      String originalText = "/dev-monit/my folder";

      statusTextContent =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height:16px; border-collapse: collapse;\"><tr>"
            + "<td style=\"width:3px;\"><div style=\"width:1px; height:1px;\"></td>"
            + "<td style=\"width:16px; height:16px;\">"
            + "<img src=\""
            + TestCaseEntryPoint.Images.SEARCH
            + "\" style=\"width:16px; height:16px;\">"
            + "</td><td "
            + " style=\"border: none; font-family:Verdana,Bitstream Vera Sans,sans-serif; font-size:11px; font-style:normal; \"><nobr>&nbsp;"
            + originalText
            + "</nobr></td><td style=\"width:3px;\"><div style=\"width:1px; height:1px;\"></td></tr></table>";
   }

   private Command addIconButtonInToolbar = new Command()
   {
      public void execute()
      {
         TextButton statusText = new TextButton(statusTextContent);
         statusText.setCommand(new Command()
         {
            public void execute()
            {
               Window.alert("On click!");
            }
         });

         boolean right = dockingTypeCheckBox.getValue();
         toolbar.addItem(statusText, right);
      }
   };

   private Command addIconButtonInStatusbar = new Command()
   {
      public void execute()
      {
         TextButton statusText = new TextButton(statusTextContent);
         statusText.setCommand(new Command()
         {
            public void execute()
            {
               Window.alert("On click!");
            }
         });

         boolean right = dockingTypeCheckBox.getValue();
         statusbar.addItem(statusText, right);
      }
   };

}
