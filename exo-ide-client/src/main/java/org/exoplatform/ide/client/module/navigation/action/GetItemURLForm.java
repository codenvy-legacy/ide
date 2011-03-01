/*
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
 */
package org.exoplatform.ide.client.module.navigation.action;

import com.google.gwt.user.client.ui.VerticalPanel;

import com.google.gwt.user.client.ui.HorizontalPanel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;

import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class GetItemURLForm extends DialogWindow
{

   private static final int WIDTH = 500;

   private static final int HEIGHT = 155;
   
   private static final int BUTTON_WIDTH = 90;

   private static final int BUTTON_HEIGHT = 22;

   public static final String ID = "ideGetItemURLForm";

   public static final String URL_FIELD = "ideGetItemURLFormURLField";

   public static final String ID_DYNAMIC_FORM = "ideGetItemURLFormDynamicForm";

   public static final String ID_OK_BUTTON = "ideGetItemURLFormOkButton";

   private static final String TITLE = "Item URL";

   private TextField itemURLField;

   private IButton okButton;
   
   private VerticalPanel mainPanel;

   public GetItemURLForm(HandlerManager eventBus, String url)
   {
      super(eventBus, WIDTH, HEIGHT, ID);

      setTitle(TITLE);
      mainPanel = new VerticalPanel();
      mainPanel.setWidth("100%");
      mainPanel.setHeight("100%");
      
      createFieldForm(url);
      createButtons();
      
      addItem(mainPanel);
      
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
      paramsForm.setID(ID_DYNAMIC_FORM);
      paramsForm.setPadding(5);
      paramsForm.setWidth(450);
      paramsForm.setPadding(15);

      itemURLField = new TextField(URL_FIELD, "WebDav item's URL:");
      itemURLField.setWidth(450);
      itemURLField.setHeight(22);
      itemURLField.setTitleOrientation(TitleOrientation.TOP);
      paramsForm.add(itemURLField);
      itemURLField.focusInItem();
      
      mainPanel.add(paramsForm);
      mainPanel.setCellHorizontalAlignment(paramsForm, HorizontalPanel.ALIGN_CENTER);
      mainPanel.setCellVerticalAlignment(paramsForm, HorizontalPanel.ALIGN_MIDDLE);
      itemURLField.setValue(url);
   }

   private void createButtons()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight((BUTTON_HEIGHT + 20)+ "px");

      okButton = new IButton("OK");
      okButton.setID(ID_OK_BUTTON);
      okButton.setWidth(BUTTON_WIDTH);
      okButton.setHeight(BUTTON_HEIGHT);
      okButton.setIcon(Images.Buttons.OK);

      buttonsLayout.add(okButton);

      okButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            destroy();
         }
      });
      
      mainPanel.add(buttonsLayout);
      mainPanel.setCellHorizontalAlignment(buttonsLayout, HorizontalPanel.ALIGN_CENTER);
      mainPanel.setCellVerticalAlignment(buttonsLayout, HorizontalPanel.ALIGN_TOP);
   }

}
