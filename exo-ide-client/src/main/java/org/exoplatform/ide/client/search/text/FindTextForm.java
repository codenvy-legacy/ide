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
package org.exoplatform.ide.client.search.text;

import com.google.gwt.user.client.DOM;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.HLayout;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.search.Search;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextForm extends DialogWindow implements FindTextPresenter.Display
{

   private static final int WIDTH = 470;

   private static final int HEIGHT = 240;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private final int FIELD_WIDTH = 340;

   private final int FIELD_HEIGHT = 20;

   private final int BUTTONS_SPACE = 5;

   private final String REPLACE_FIELD = "ideFindReplaceTextFormReplaceField";

   private final String FIND_FIELD = "ideFindReplaceTextFormFindField";

   private final String ID_FIND_RESULT = "ideFindReplaceTextFormFindResult";

   private final String CASE_SENSITIVE_FIELD = "ideFindReplaceTextFormCaseSensitiveField";

   private final String ID_DYNAMIC_FORM = "ideFindReplaceTextFormDynamicForm";

   private final String ID_FIND_BUTTON = "ideFindReplaceTextFormFindButton";

   private final String ID_REPLACE_FIND_BUTTON = "ideFindReplaceTextFormReplaceFindButton";

   private final String ID_REPLACE_BUTTON = "ideFindReplaceTextFormReplaceButton";

   private final String ID_REPLACE_ALL_BUTTON = "ideFindReplaceTextFormReplaceAllButton";

   private final String ID_CANCEL_BUTTON = "ideFindReplaceTextFormCancelButton";

   private final String TITLE = "Find/Replace";

   private IButton findButton;

   private IButton cancelButton;

   private IButton replaceButton;

   private IButton replaceFindButton;

   private IButton replaceAllButton;

   private TextField findField;

   private TextField replaceField;

   private CheckboxItem caseSensitiveField;

   private Label findResultLabel;

   private FindTextPresenter presenter;

   /**
    * @param eventBus
    * @param width
    * @param height
    */
   public FindTextForm(HandlerManager eventBus, File activeFile)
   {
      super(eventBus, WIDTH, HEIGHT, Search.FORM_ID);
      setTitle(TITLE);
      setModal(false);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
      mainLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      DOM.setStyleAttribute(mainLayout.getElement(), "padding", "10");

      VerticalPanel inputForm = createFindForm();
      mainLayout.add(inputForm);
      mainLayout.setCellHorizontalAlignment(inputForm, HorizontalPanel.ALIGN_CENTER);
      mainLayout.add(createButtonsLayout());

      HorizontalPanel hLayout = new HorizontalPanel();
      hLayout.setSpacing(BUTTONS_SPACE);
      hLayout.setHeight(BUTTON_HEIGHT + "px");
      hLayout.add(createFindResultLabel());
      hLayout.add(cancelButton);

      mainLayout.add(hLayout);

      setWidget(mainLayout);

      show();

      presenter = new FindTextPresenter(eventBus, activeFile);
      presenter.bindDisplay(this);

      findField.focusInItem();
   }

   private Label createFindResultLabel()
   {
      findResultLabel = new Label();
      findResultLabel.setID(ID_FIND_RESULT);
      findResultLabel.setHeight("" + BUTTON_HEIGHT);
      findResultLabel.setWidth("319px");
      findResultLabel.setValue("");
      return findResultLabel;
   }

   /**
    * Create layout for buttons
    * 
    * @return {@link HLayout}
    */
   protected VerticalPanel createButtonsLayout()
   {
      findButton = createButton("Find", "", ID_FIND_BUTTON);
      cancelButton = createButton("Cancel", Images.Buttons.CANCEL, ID_CANCEL_BUTTON);
      replaceButton = createButton("Replace", "", ID_REPLACE_BUTTON);
      replaceFindButton = createButton("Replace/Find", "", ID_REPLACE_FIND_BUTTON);
      replaceAllButton = createButton("Replace All", "", ID_REPLACE_ALL_BUTTON);

      VerticalPanel buttonsLayout = new VerticalPanel();
      HorizontalPanel upPanel = new HorizontalPanel();
      upPanel.setHeight(BUTTON_HEIGHT + "px");
      upPanel.setSpacing(BUTTONS_SPACE);
      upPanel.add(findButton);
      upPanel.add(replaceFindButton);

      HorizontalPanel downPanel = new HorizontalPanel();
      downPanel.setHeight(BUTTON_HEIGHT + "px");
      downPanel.setSpacing(BUTTONS_SPACE);
      downPanel.add(replaceButton);
      downPanel.add(replaceAllButton);

      buttonsLayout.add(upPanel);
      buttonsLayout.add(downPanel);
      return buttonsLayout;
   }

   private VerticalPanel createFindForm()
   {
      VerticalPanel form = new VerticalPanel();
      form.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      form.getElement().setId(ID_DYNAMIC_FORM);
      form.setSpacing(3);

      findField = createTextField("Find", FIND_FIELD);
      replaceField = createTextField("Replace with", REPLACE_FIELD);

      caseSensitiveField = createCheckBoxItem("Case sensitive", CASE_SENSITIVE_FIELD);
      DOM.setStyleAttribute(caseSensitiveField.getElement(), "marginLeft", "76px");

      form.add(findField);
      form.add(replaceField);
      form.add(caseSensitiveField);
      form.setCellHorizontalAlignment(caseSensitiveField, HorizontalPanel.ALIGN_LEFT);
      return form;
   }

   private TextField createTextField(String title, String id)
   {
      TextField textField = new TextField();
      textField.setName(id);
      textField.setWidth(FIELD_WIDTH);
      textField.setHeight(FIELD_HEIGHT);
      textField.setTitle(title);
      textField.setShowTitle(true);
      return textField;
   }

   private CheckboxItem createCheckBoxItem(String title, String id)
   {
      CheckboxItem checkboxItem = new CheckboxItem();
      checkboxItem.setName(id);
      checkboxItem.setText(title);
      return checkboxItem;
   }

   /**
    * Create button with title and icon
    * 
    * @param title
    * @param icon
    * @return {@link IButton}
    */
   private IButton createButton(String title, String icon, String id)
   {
      IButton button = new IButton();
      button.setID(id);
      button.setTitle(title);
      button.setIcon(icon);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      return button;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getCaseSensitiveField()
    */
   public HasValue<Boolean> getCaseSensitiveField()
   {
      return caseSensitiveField;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getFindButton()
    */
   public HasClickHandlers getFindButton()
   {
      return findButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getFindField()
    */
   public TextFieldItem getFindField()
   {
      return findField;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceAllButton()
    */
   public HasClickHandlers getReplaceAllButton()
   {
      return replaceAllButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceButton()
    */
   public HasClickHandlers getReplaceButton()
   {
      return replaceButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceField()
    */
   public TextFieldItem getReplaceField()
   {
      return replaceField;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableFindButton(boolean)
    */
   public void enableFindButton(boolean isEnable)
   {
      findButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getReplaceFindButton()
    */
   public HasClickHandlers getReplaceFindButton()
   {
      return replaceFindButton;
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceFindButton(boolean)
    */
   public void enableReplaceFindButton(boolean isEnable)
   {
      replaceFindButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceAllButton(boolean)
    */
   public void enableReplaceAllButton(boolean isEnable)
   {
      replaceAllButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceButton(boolean)
    */
   public void enableReplaceButton(boolean isEnable)
   {
      replaceButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getResultLabel()
    */
   public HasValue<String> getResultLabel()
   {
      return findResultLabel;
   }

}
