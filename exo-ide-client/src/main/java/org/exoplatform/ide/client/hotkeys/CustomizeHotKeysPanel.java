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
package org.exoplatform.ide.client.hotkeys;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
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

      final static String ERROR = "exo-cutomizeHotKey-label-error";

   }

   private static final int BUTTON_FORM_HEIGHT = 25;

   private static final int WIDTH = 600;

   private static final int HEIGHT = 350;

   private static final String ID = "ideallCustomizeHotKeysPanel";

   private static final int BUTTON_DELIMITER_WIDTH = 3;

   private static final String TITLE = "Customize hotkeys";

   private static final int TEXT_FIELD_WIDTH = 90;

   private static final int BUTTON_WIDTH = 90;

   private static final int BUTTON_HEIGHT = 22;

   private static final int FORM_DELIMITER_HEIGHT = 5;

   private HLayout hLayout;

   private VLayout vLayout;

   private IButton saveButton;

   private IButton cancelButton;

   private HandlerManager eventBus;

   //private ApplicationContext context;

   private CustomizeHotKeysPresenter presenter;

   private HotKeyItemListGrid hotKeyItemListGrid;

   private TextField hotKeyField;

   private IButton bindButton;

   private IButton unbindButton;

   private Label errorLabel;

   public CustomizeHotKeysPanel(HandlerManager eventBus, ApplicationSettings applicationSettings, List<Control> controls)
   {
      super(eventBus, WIDTH, HEIGHT, ID);

      setTitle(TITLE);
      setShowMaximizeButton(true);

      this.eventBus = eventBus;
      //this.context = applicationContext;

      vLayout = new VLayout();
      vLayout.setMargin(10);
      vLayout.setWidth100();
      vLayout.setAlign(VerticalAlignment.TOP);

      hLayout = new HLayout();
      hLayout.setAutoHeight();
      hLayout.setWidth100();
      hLayout.setMargin(0);
      hLayout.setPadding(0);

      createHotKeysListGrid();

      Canvas delimiter = new StatefulCanvas();
      delimiter.setHeight(FORM_DELIMITER_HEIGHT);

      vLayout.addMember(delimiter);

      hLayout.addMember(createHotKeyForm());
      hLayout.addMember(createBindButtonsForm());
      hLayout.addMember(createButtonsForm());

      vLayout.addMember(hLayout);

      createErrorLabel();

      addItem(vLayout);

      try {
         show();         
      } catch (Exception e) {
         e.printStackTrace();
      }

      presenter = new CustomizeHotKeysPresenter(eventBus, applicationSettings, controls);
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
      vLayout.addMember(hotKeyItemListGrid);
   }

   private DynamicForm createHotKeyForm()
   {
      DynamicForm hotKeyform = new DynamicForm();
      hotKeyform.setCellPadding(0);
      hotKeyform.setHeight(BUTTON_FORM_HEIGHT);

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
      bindForm.setCellPadding(0);
      bindForm.setHeight(BUTTON_FORM_HEIGHT);

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
      delimiter1.setWidth(BUTTON_DELIMITER_WIDTH);

      ToolbarItem bindTbi = new ToolbarItem();
      bindTbi.setButtons(bindButton, delimiter1, unbindButton);

      bindForm.setItems(bindTbi);

      return bindForm;
   }

   private DynamicForm createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setHeight(BUTTON_FORM_HEIGHT);
      buttonsForm.setLayoutAlign(Alignment.RIGHT);
      buttonsForm.setAutoWidth();
      buttonsForm.setCellPadding(0);

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
      delimiter2.setWidth(BUTTON_DELIMITER_WIDTH);
      tbi2.setAlign(Alignment.RIGHT);
      tbi2.setButtons(saveButton, delimiter2, cancelButton);

      buttonsForm.setFields(tbi2);

      return buttonsForm;
   }

   private void createErrorLabel()
   {
      errorLabel = new Label();
      errorLabel.setHeight("16px");
      errorLabel.setText("");
      errorLabel.setStyleName(Style.ERROR);
      vLayout.addMember(errorLabel);
   }

   public void showError(String text)
   {
      if (text == null)
      {
         errorLabel.setText("");
      }
      else
      {
         errorLabel.setText(text);
      }
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

   public void disableHotKeyField()
   {
      hotKeyField.setDisabled(true);
   }

   public void enableHotKeyField()
   {
      hotKeyField.setDisabled(false);
   }

   public void focusOnHotKeyField()
   {
      hotKeyField.focusInItem();
   }

}
