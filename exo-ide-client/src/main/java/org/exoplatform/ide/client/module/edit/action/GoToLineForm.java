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
package org.exoplatform.ide.client.module.edit.action;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.module.vfs.api.File;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLineForm extends DialogWindow implements GoToLinePresenter.Display
{

   private static final int WIDTH = 400;

   private static final int HEIGHT = 160;
   
   private static final String ID = "ideGoToLineForm";
   
   private static final String ID_DYNAMIC_FORM = "ideGoToLineFormDynamicForm";
   
   private static final String ID_GO_BUTTON = "ideGoToLineFormGoButton";
   
   private static final String ID_CANCEL_BUTTON = "ideGoToLineFormCancelButton";
   
   private static final String LINE_NUMBER_FIELD = "ideGoToLineFormLineNumberField";
   
   private static final String RANGE_LABEL = "ideGoToLineFormLinesRangeField";
   
   private GoToLinePresenter presenter;
   
   private VLayout vLayout;

   private TextField lineNumberField;

   private IButton goButton;

   private IButton cancelButton;
   
   private StaticTextItem caption;
   
   private DynamicForm paramsForm; 
   
   private DynamicForm buttonsForm;
   
   public GoToLineForm(HandlerManager eventBus, File activeFile)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle("Go to Line");
      
      vLayout = new VLayout();
      addItem(vLayout);

      createFieldForm();
      createButtons();

      show();
      
      presenter = new GoToLinePresenter(eventBus, activeFile);
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
   
   private void createFieldForm()
   {
      paramsForm = new DynamicForm();
      paramsForm.setID(ID_DYNAMIC_FORM);
      paramsForm.setPadding(5);
      paramsForm.setWidth(340);
      paramsForm.setLayoutAlign(Alignment.CENTER);
      paramsForm.setPadding(15);
      paramsForm.setAutoFocus(true);

      caption = new StaticTextItem();
      caption.setName(RANGE_LABEL);
      //caption.setDefaultValue(labelCaption);
      caption.setShowTitle(false);
      caption.setColSpan(2);

      SpacerItem delimiter = new SpacerItem();
      delimiter.setColSpan(2);
      delimiter.setHeight(5);

      lineNumberField = new TextField();
      lineNumberField.setName(LINE_NUMBER_FIELD);
      lineNumberField.setShowTitle(false);
      lineNumberField.setWidth(340);
      lineNumberField.setMask("#######");
      lineNumberField.setMaskPromptChar("");
      paramsForm.setFields(caption, delimiter, lineNumberField);
      paramsForm.focusInItem(lineNumberField);
      
      vLayout.addMember(paramsForm);
   }

   private void createButtons()
   {
      buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      goButton = new IButton("Go");
      goButton.setID(ID_GO_BUTTON);
      goButton.setWidth(90);
      goButton.setHeight(22);
      goButton.setIcon(Images.Buttons.OK);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(goButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      vLayout.addMember(buttonsForm);
   }

   
   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#closeForm()
    */

   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#getGoButton()
    */
   public HasClickHandlers getGoButton()
   {
      return goButton;
   }

   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#getLineNumberField()
    */
   public com.smartgwt.client.widgets.form.fields.events.HasKeyUpHandlers getLineNumberField()
   {
      return lineNumberField;
   }
   
   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#setCaptionLabel(java.lang.String)
    */
   public void setCaptionLabel(String caption)
   {
      this.caption.setDefaultValue(caption);
   }

   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#getLineNumberValue()
    */
   public HasValue<String> getLineNumberValue()
   {
      return lineNumberField;
   }

   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#removeFocusFromLineNumber()
    */
   public void removeFocusFromLineNumber()
   {
      buttonsForm.focusInItem(0);
   }

}
