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

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.testcase.ShowCaseImageBundle;
import org.exoplatform.gwtframework.ui.client.testcase.TestCase;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarTestCase extends TestCase
{

   private Toolbar toolbar;

   @Override
   public void draw()
   {
      FlowPanel panel = new FlowPanel();
      DOM.setStyleAttribute(panel.getElement(), "position", "relative");
      DOM.setStyleAttribute(panel.getElement(), "left", "10px");
      DOM.setStyleAttribute(panel.getElement(), "top", "50px");
      DOM.setStyleAttribute(panel.getElement(), "width", "600px");
      DOM.setStyleAttribute(panel.getElement(), "height", "100px");
      //DOM.setStyleAttribute(panel.getElement(), "background", "#FFAAEE");
      DOM.setStyleAttribute(panel.getElement(), "borderWidth", "1px");
      DOM.setStyleAttribute(panel.getElement(), "borderStyle", "solid");
      DOM.setStyleAttribute(panel.getElement(), "borderColor", "#AAAAAA");
      testCasePanel().add(panel);

      toolbar = new Toolbar();
      panel.add(toolbar);

      addButtonHeader("Icon button");
      createButton("Add Left", addButtonLeftClickHandler);
      createButton("Add Right", addButtonRightClickHandler);
      addButtonDelimiter("Loader");
//      createButton("Add Left", addLoaderLeftClickHandler);
//      createButton("Add Right", addLoaderRightClickHandler);

      addButtonDelimiter("Toolbar Delimiter");
      createButton("Add Left", addDelimiterLeftClickHandler);
      createButton("Add Right", addDelimiterRightClickHandler);

      addButtonDelimiter("Actions");
      //createButtonGroup();
      createButton("Clear", true, clearToolbarClickHandler);
      createButton("Show All Items", true, showAllItemsClickHandler);
      createButton("Hide Duplicated Delimiters", true, removeDuplicatedDelimitersClickHandler);
   }

   private ClickHandler clearToolbarClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         toolbar.clear();
      }
   };

   private ClickHandler addButtonLeftClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         IconButton iconButton =
            new IconButton(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.add()),
               ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.addDisabled()));

         toolbar.addItem(iconButton);
      }
   };

   private ClickHandler addButtonRightClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         IconButton iconButton =
            new IconButton(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.add()),
               ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.addDisabled()));
         toolbar.addItem(iconButton, true);
      }
   };

//   private ClickHandler addLoaderLeftClickHandler = new ClickHandler()
//   {
//      public void onClick(ClickEvent event)
//      {
//         LoadingIndicator loader =
//            new LoadingIndicator(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()));
//         loader.setCommand(new Command()
//         {
//            public void execute()
//            {
//               Window.alert("Click!");
//            }
//         });
//
//         toolbar.addItem(loader);
//      }
//   };
//
//   private ClickHandler addLoaderRightClickHandler = new ClickHandler()
//   {
//      public void onClick(ClickEvent event)
//      {
//         LoadingIndicator loader =
//            new LoadingIndicator(ImageHelper.getImageHTML(ShowCaseImageBundle.INSTANCE.remove()));
//         loader.setCommand(new Command()
//         {
//            public void execute()
//            {
//               Window.alert("Click!");
//            }
//         });
//
//         toolbar.addItem(loader, true);
//      }
//   };

   private ClickHandler addDelimiterLeftClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         toolbar.addDelimiter();
      }
   };

   private ClickHandler addDelimiterRightClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         toolbar.addDelimiter(true);
      }
   };

   private ClickHandler showAllItemsClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         toolbar.showAllItems();
      }
   };

   private ClickHandler removeDuplicatedDelimitersClickHandler = new ClickHandler()
   {
      public void onClick(ClickEvent event)
      {
         toolbar.hideDuplicatedDelimiters();
      }
   };

}
