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
package org.exoplatform.ide.client.edit.ui;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.edit.GoToLinePresenter;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLineForm extends DialogWindow implements org.exoplatform.ide.client.edit.GoToLinePresenter.Display
{

   private static final int WIDTH = 400;

   private static final int HEIGHT = 165;

   private static final String ID = "ideGoToLineForm";

   private static final String ID_DYNAMIC_FORM = "ideGoToLineFormDynamicForm";

   private static final String ID_GO_BUTTON = "ideGoToLineFormGoButton";

   private static final String ID_CANCEL_BUTTON = "ideGoToLineFormCancelButton";

   private static final String LINE_NUMBER_FIELD = "ideGoToLineFormLineNumberField";

   private static final String RANGE_LABEL = "ideGoToLineFormLinesRangeField";

   private GoToLinePresenter presenter;

   private VerticalPanel vLayout;

   private TextField lineNumberField;

   private IButton goButton;

   private IButton cancelButton;

   private DynamicForm paramsForm;

   public GoToLineForm(HandlerManager eventBus, File activeFile)
   {
      super(WIDTH, HEIGHT, ID);
      setTitle("Go to Line");

      vLayout = new VerticalPanel();
      vLayout.setWidth("100%");
      vLayout.setHeight("100%");

      setWidget(vLayout);

      createFieldForm();
      createButtons();

      show();

      presenter = new GoToLinePresenter(eventBus, activeFile);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick()
         {
            destroy();
         }
      });

   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.window.Window#destroy()
    */
   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   private void createFieldForm()
   {
      paramsForm = new DynamicForm();
      paramsForm.setID(ID_DYNAMIC_FORM);
      paramsForm.setPadding(5);
      paramsForm.setWidth(300);

      lineNumberField = new TextField(LINE_NUMBER_FIELD, RANGE_LABEL);
      lineNumberField.setWidth(300);
      lineNumberField.setHeight(22);
      lineNumberField.setTitleOrientation(TitleOrientation.TOP);
      paramsForm.add(lineNumberField);
      lineNumberField.focusInItem();

      vLayout.add(paramsForm);
      vLayout.setCellHorizontalAlignment(paramsForm, HorizontalPanel.ALIGN_CENTER);
      vLayout.setCellVerticalAlignment(paramsForm, VerticalPanel.ALIGN_MIDDLE);
   }

   private void createButtons()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setSpacing(5);
      buttonsLayout.setHeight((22 + 25) + "px");

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

      buttonsLayout.add(goButton);
      buttonsLayout.add(cancelButton);

      vLayout.add(buttonsLayout);
      vLayout.setCellHorizontalAlignment(buttonsLayout, HorizontalPanel.ALIGN_CENTER);
      vLayout.setCellVerticalAlignment(buttonsLayout, HorizontalPanel.ALIGN_MIDDLE);
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
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#setCaptionLabel(java.lang.String)
    */
   public void setCaptionLabel(String caption)
   {
      lineNumberField.setTitle(caption);
   }

   /**
    * @see org.exoplatform.ide.client.action.GoToLinePresenter.Display#removeFocusFromLineNumber()
    */
   public void removeFocusFromLineNumber()
   {
      goButton.focus();
   }

   /**
    * @see org.exoplatform.ide.client.module.edit.action.GoToLinePresenter.Display#getLineNumber()
    */
   @Override
   public TextFieldItem getLineNumber()
   {
      return lineNumberField;
   }

}
