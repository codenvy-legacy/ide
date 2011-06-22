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
import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextView extends ViewImpl implements org.exoplatform.ide.client.edit.FindTextPresenter.Display
{

   private static final int DEFAULT_WIDTH = 470;

   private static final int DEFAULT_HEIGHT = 230;

   private final String BUTTON_WIDTH = "100px";

   private final String BUTTON_HEIGHT = "22px";

   private final int FIELD_WIDTH = 340;

   private final int FIELD_HEIGHT = 20;

   private final int BUTTONS_SPACE = 5;

   private final String REPLACE_FIELD = "ideFindReplaceTextFormReplaceField";

   private final String FIND_FIELD = "ideFindReplaceTextFormFindField";

   private final String ID_FIND_RESULT = "ideFindReplaceTextFormFindResult";

   private final String CASE_SENSITIVE_FIELD = "ideFindReplaceTextFormCaseSensitiveField";

   private final String ID_FIND_BUTTON = "ideFindReplaceTextFormFindButton";

   private final String ID_REPLACE_FIND_BUTTON = "ideFindReplaceTextFormReplaceFindButton";

   private final String ID_REPLACE_BUTTON = "ideFindReplaceTextFormReplaceButton";

   private final String ID_REPLACE_ALL_BUTTON = "ideFindReplaceTextFormReplaceAllButton";

   private final String ID_CANCEL_BUTTON = "ideFindReplaceTextFormCancelButton";

   private ImageButton findButton;

   private ImageButton cancelButton;

   private ImageButton replaceButton;

   private ImageButton replaceFindButton;

   private ImageButton replaceAllButton;

   private TextField findField;

   private TextField replaceField;

   private CheckboxItem caseSensitiveField;

   private Label findResultLabel;

   private VerticalPanel layout;

   private static final String TITLE = IDE.EDITOR_CONSTANT.findTextTitle();

   private static final String FIND = IDE.EDITOR_CONSTANT.findTextFind();

   private static final String REPLACE_WITH = IDE.EDITOR_CONSTANT.findTextReplaceWith();

   private static final String CASE_SENSITIVE = IDE.EDITOR_CONSTANT.findTextCaseSensitive();

   private static final String FIND_BUTTON = IDE.EDITOR_CONSTANT.findTextFindButton();

   private static final String REPLACE_BUTTON = IDE.EDITOR_CONSTANT.findTextReplaceButton();

   private static final String REPLACE_FIND_BUTTON = IDE.EDITOR_CONSTANT.findTextReplaceFindButton();

   private static final String REPLACE_ALL_BUTTON = IDE.EDITOR_CONSTANT.findTextReplaceAllButton();

   public FindTextView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.findText()), DEFAULT_WIDTH, DEFAULT_HEIGHT);

      layout = new VerticalPanel();
      layout.setSize("100%", "100%");
      layout.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
      layout.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      layout.setSpacing(BUTTONS_SPACE);
      add(layout);

      createTextFields();
      createButtons();
      createBottomPanel();
   }

   private Widget getDelimiter()
   {
      FlowPanel delimiter = new FlowPanel();
      delimiter.setSize("5px", "5px");
      return delimiter;
   }

   private void createTextFields()
   {
      findField = createTextField(FIND, FIND_FIELD);
      replaceField = createTextField(REPLACE_WITH, REPLACE_FIELD);

      caseSensitiveField = createCheckBoxItem(CASE_SENSITIVE, CASE_SENSITIVE_FIELD);
      DOM.setStyleAttribute(caseSensitiveField.getElement(), "marginLeft", "76px");

      layout.add(findField);
      layout.add(replaceField);
      layout.add(caseSensitiveField);
      layout.setCellHorizontalAlignment(caseSensitiveField, HorizontalPanel.ALIGN_LEFT);
   }

   private void createButtons()
   {
      findButton = createButton(FIND_BUTTON, "", ID_FIND_BUTTON);
      cancelButton =
         createButton(IDE.IDE_LOCALIZATION_CONSTANT.cancelButton(), Images.Buttons.CANCEL, ID_CANCEL_BUTTON);
      replaceButton = createButton(REPLACE_BUTTON, "", ID_REPLACE_BUTTON);
      replaceFindButton = createButton(REPLACE_FIND_BUTTON, "", ID_REPLACE_FIND_BUTTON);
      replaceAllButton = createButton(REPLACE_ALL_BUTTON, "", ID_REPLACE_ALL_BUTTON);

      HorizontalPanel upPanel = new HorizontalPanel();
      upPanel.setHeight(BUTTON_HEIGHT);

      upPanel.add(findButton);
      upPanel.add(getDelimiter());
      upPanel.add(replaceFindButton);
      upPanel.add(getDelimiter());
      layout.add(upPanel);

      HorizontalPanel downPanel = new HorizontalPanel();
      downPanel.setHeight(BUTTON_HEIGHT);

      downPanel.add(replaceButton);
      downPanel.add(getDelimiter());
      downPanel.add(replaceAllButton);
      downPanel.add(getDelimiter());
      layout.add(downPanel);
   }

   private void createBottomPanel()
   {
      HorizontalPanel hLayout = new HorizontalPanel();
      hLayout.setWidth("100%");
      hLayout.setHeight(BUTTON_HEIGHT);

      createFindResultLabel();

      hLayout.add(findResultLabel);
      hLayout.setCellHorizontalAlignment(findResultLabel, HorizontalPanel.ALIGN_LEFT);
      hLayout.setCellWidth(findResultLabel, "100%");

      hLayout.add(cancelButton);
      hLayout.setCellHorizontalAlignment(cancelButton, HorizontalPanel.ALIGN_RIGHT);

      hLayout.add(getDelimiter());

      layout.add(hLayout);
   }

   private Label createFindResultLabel()
   {
      findResultLabel = new Label();
      findResultLabel.setID(ID_FIND_RESULT);
      findResultLabel.setHeight(BUTTON_HEIGHT);
      findResultLabel.setWidth("100%");
      findResultLabel.setValue("");
      return findResultLabel;
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
    * @return {@link ImageButton}
    */
   private ImageButton createButton(String title, String icon, String id)
   {
      ImageButton button = new ImageButton();
      button.setButtonId(id);
      button.setText(title);
      if (icon != null && !icon.isEmpty())
         button.setImage(new Image(icon));
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      return button;
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
      findButton.setEnabled(isEnable);
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
      replaceFindButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceAllButton(boolean)
    */
   public void enableReplaceAllButton(boolean isEnable)
   {
      replaceAllButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#enableReplaceButton(boolean)
    */
   public void enableReplaceButton(boolean isEnable)
   {
      replaceButton.setEnabled(isEnable);
   }

   /**
    * @see org.exoplatform.ide.client.search.text.FindTextPresenter.Display#getResultLabel()
    */
   public HasValue<String> getResultLabel()
   {
      return findResultLabel;
   }

   /**
    * @see org.exoplatform.ide.client.edit.FindTextPresenter.Display#focusInFindField()
    */
   @Override
   public void focusInFindField()
   {
      findField.focusInItem();
   }

}
