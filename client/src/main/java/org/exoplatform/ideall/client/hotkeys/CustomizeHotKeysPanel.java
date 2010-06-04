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
package org.exoplatform.ideall.client.hotkeys;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CustomizeHotKeysPanel extends DialogWindow implements CustomizeHotKeysPresenter.Display
{
   
   public static interface Style
   {

      final static String TEXT_INPUT_ERROR = "exo-cutomizeHotKey-textField-error";

      final static String TEXT_INPUT = "exo-cutomizeHotKey-textField";

   }
   
   private static final int WIDTH = 670;

   private static final int HEIGHT = 400;
   
   private static final String TITLE = "Hot Keys...";
   
   private static final int TEXT_FIELD_WIDTH = 90;
   
   private static final int BUTTON_WIDTH = 90;
   
   private static final int BUTTON_HEIGHT = 22;
   
   private HLayout hLayout;

   private VLayout vLayout;
   
   private IButton saveButton;
   
   private IButton cancelButton;

   private HandlerManager eventBus;
   
   private ApplicationContext applicationContext;
   
   private CustomizeHotKeysPresenter presenter;
   
   private HotKeyItemListGrid hotKeyItemListGrid;
   
   private TextField hotKeyField;
   
   private IButton bindButton;
   
   private IButton unbindButton;

   public CustomizeHotKeysPanel(HandlerManager eventBus, ApplicationContext applicationContext)
   {
      super(eventBus, WIDTH, HEIGHT);
      
      setTitle(TITLE);
      setShowMaximizeButton(true);
      
      this.eventBus = eventBus;
      this.applicationContext = applicationContext;
      
      vLayout = new VLayout();
      vLayout.setMargin(10);
      vLayout.setWidth100();
      vLayout.setAlign(VerticalAlignment.TOP);

      hLayout = new HLayout();
      //hLayout.setMargin(8);
      hLayout.setAutoHeight();
      hLayout.setWidth100();
      hLayout.setPadding(10);

      
      
      createHotKeysListGrid();
      
      hLayout.addMember(createHotKeyForm());
      hLayout.addMember(createBindButtonsForm());
      hLayout.addMember(createButtonsForm());
      
      vLayout.addMember(hLayout);

      addItem(vLayout);
      
      show();
      
      presenter = new CustomizeHotKeysPresenter(eventBus, applicationContext);
      presenter.bindDisplay(this);
      
      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }
   
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }
   
   public HotKeyItemListGrid getHotKeyItemListGrid()
   {
      return hotKeyItemListGrid;
   }
   
   private void createHotKeysListGrid()
   {
      hotKeyItemListGrid = new HotKeyItemListGrid();
      hotKeyItemListGrid.setCanEdit(false);
      vLayout.addMember(hotKeyItemListGrid);
   }
   
   private DynamicForm createHotKeyForm()
   {
      DynamicForm hotKeyform = new DynamicForm();
      hotKeyform.setMargin(3);
      hotKeyform.setHeight(34);
      
      hotKeyField = new TextField();
      hotKeyField.setWidth(TEXT_FIELD_WIDTH);
      hotKeyField.setShowTitle(false);
      
      hotKeyform.setItems(hotKeyField);
      hotKeyform.setAutoHeight();
      
      return hotKeyform;
   }
   
   private DynamicForm createBindButtonsForm()
   {
      DynamicForm bindForm = new DynamicForm();
      bindForm.setMargin(3);
      bindForm.setHeight(34);
      
      bindButton = new IButton("Bind");
      bindButton.setWidth(BUTTON_WIDTH);
      bindButton.setHeight(BUTTON_HEIGHT);
      bindButton.setIcon(Images.Buttons.YES);
      bindButton.setDisabled(true);
      
      unbindButton = new IButton("Unbind");
      unbindButton.setWidth(BUTTON_WIDTH);
      unbindButton.setHeight(BUTTON_HEIGHT);
      unbindButton.setIcon(Images.Buttons.YES);
      unbindButton.setDisabled(true);
      
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      
      ToolbarItem bindTbi = new ToolbarItem();
      bindTbi.setButtons(bindButton, delimiter1, unbindButton);
      
      bindForm.setItems(bindTbi);
      
      return bindForm;
   }
   
   private DynamicForm createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setMargin(3);
      buttonsForm.setHeight(34);
      buttonsForm.setLayoutAlign(Alignment.RIGHT);
      buttonsForm.setAutoWidth();
      
      saveButton = new IButton("Save");
      saveButton.setWidth(BUTTON_WIDTH);
      saveButton.setHeight(BUTTON_HEIGHT);
      saveButton.setIcon(Images.Buttons.YES);
      saveButton.setDisabled(true);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(BUTTON_WIDTH);
      cancelButton.setHeight(BUTTON_HEIGHT);
      cancelButton.setIcon(Images.Buttons.NO);
      cancelButton.setLayoutAlign(Alignment.RIGHT);

      ToolbarItem tbi2 = new ToolbarItem();
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(3);
      tbi2.setAlign(Alignment.RIGHT);
      tbi2.setButtons(saveButton, delimiter2, cancelButton);
      
      buttonsForm.setFields(tbi2);
      
      return buttonsForm;
   }
   
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }
   
   public HasClickHandlers getSaveButton()
   {
      return saveButton;
   }

   public void closeForm()
   {
      destroy();
   }
   
   public void disableSaveButton(boolean disabled)
   {
      saveButton.setDisabled(disabled);
   }

   public void disableSaveButton()
   {
      saveButton.setDisabled(true);
   }

   public void enableSaveButton()
   {
      saveButton.setDisabled(false);
   }

   public void disableBindButton()
   {
      bindButton.disable();
   }

   public void disableUnbindButton()
   {
      unbindButton.disable();
   }

   public void enableBindButton()
   {
      bindButton.enable();
   }

   public void enableUnbindButton()
   {
      unbindButton.enable();
   }

   public HasClickHandlers getBindButton()
   {
      return bindButton;
   }

   public HasClickHandlers getUnbindButton()
   {
      return unbindButton;
   }
   
   public HasValue<String> getHotKeyField()
   {
      return hotKeyField;
   }
   
   public void clearHotKeyField()
   {
      hotKeyField.clearValue();
   }
   
   public void enableHotKeyField()
   {
      hotKeyField.enable();
   }
   
   public void disableHotKeyField()
   {
      hotKeyField.setDisabled(true);
   }
   
   public void focusInHotKeyField()
   {
      hotKeyField.focusInItem();
   }
}
