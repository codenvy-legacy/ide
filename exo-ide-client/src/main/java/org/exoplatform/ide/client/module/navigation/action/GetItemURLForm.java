/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.action;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GetItemURLForm extends DialogWindow
{

   private static final int WIDTH = 500;

   private static final int HEIGHT = 160;
   
   public static final String ID = "ideallGetItemURLForm";
   
   private static final String TITLE = "Item URL";

   private TextField itemURLField;

   private IButton okButton;

   public GetItemURLForm(HandlerManager eventBus, String url)
   {
      super(eventBus, WIDTH, HEIGHT, ID);

      setTitle(TITLE);

      createFieldForm(url);
      createButtons();
      show();

      //draw();

      new Timer()
      {

         @Override
         public void run()
         {
            itemURLField.selectValue();
         }

      }.schedule(500);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   private void createFieldForm(String url)
   {
      DynamicForm paramsForm = new DynamicForm();
      paramsForm.setPadding(5);
      paramsForm.setWidth(450);
      paramsForm.setLayoutAlign(Alignment.CENTER);
      paramsForm.setPadding(15);
      paramsForm.setAutoFocus(true);

      StaticTextItem caption = new StaticTextItem();
      caption.setDefaultValue("WebDav item's URL:");
      caption.setShowTitle(false);
      caption.setColSpan(2);

      SpacerItem delimiter = new SpacerItem();
      delimiter.setColSpan(2);
      delimiter.setHeight(5);

      itemURLField = new TextField();
      itemURLField.setShowTitle(false);
      itemURLField.setWidth(450);
      
      itemURLField.setSelectOnFocus(true);

      paramsForm.setFields(caption, delimiter, itemURLField);
      paramsForm.focusInItem(itemURLField);
      
      addItem(paramsForm);

      itemURLField.setValue(url);
   }

   private void createButtons()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      okButton = new IButton("OK");
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.OK);

      ToolbarItem tbi = new ToolbarItem();

      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(3);
      tbi.setButtons(delimiter1, okButton, delimiter2);
      tbi.setWidth(90);

      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      addItem(buttonsForm);

      okButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            destroy();
         }
      });
   }

}
