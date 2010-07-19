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
package org.exoplatform.ideall.client.search.text;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.Label;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.ui.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextForm extends DialogWindow implements FindTextPresenter.Display
{
   public static final String ID = "ideallFindReplaceForm";
   
   private static final int WIDTH = 450;

   private static final int HEIGHT = 210;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private final int FIELD_WIDTH = 310;

   private final int FIELD_HEIGHT = 20;

   private final int BUTTONS_SPACE = 5;

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
   public FindTextForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle(TITLE);
      setIsModal(false);

      VLayout mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setPadding(15);

      mainLayout.setMembersMargin(5);

      mainLayout.addMember(createFindForm());
      mainLayout.addMember(createButtonsLayout());

      HLayout hLayout = new HLayout();
      hLayout.setWidth100();
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.addMember(createFindResultLabel());
      hLayout.addMember(cancelButton);

      mainLayout.addMember(hLayout);

      addItem(mainLayout);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      show();

      presenter = new FindTextPresenter(eventBus, context);
      presenter.bindDisplay(this);

      findField.focusInItem();
   }

   private Label createFindResultLabel()
   {
      findResultLabel = new Label();
      findResultLabel.setHeight(BUTTON_HEIGHT);
      findResultLabel.setWidth100();
      findResultLabel.setValue("");
      return findResultLabel;
   }

   /**
    * Create layout for buttons
    * 
    * @return {@link HLayout}
    */
   protected VLayout createButtonsLayout()
   {
      findButton = createButton("Find", "");
      cancelButton = createButton("Cancel", Images.Buttons.CANCEL);
      cancelButton.setLayoutAlign(Alignment.RIGHT);
      replaceButton = createButton("Replace", "");
      replaceFindButton = createButton("Replace/Find", "");
      replaceAllButton = createButton("Replace All", "");

      VLayout buttonsLayout = new VLayout();
      buttonsLayout.setAutoWidth();
      buttonsLayout.setLayoutAlign(Alignment.RIGHT);
      buttonsLayout.setMembersMargin(BUTTONS_SPACE);

      HLayout vLayoutLeft = new HLayout();
      vLayoutLeft.setHeight(BUTTON_HEIGHT);
      vLayoutLeft.setMembersMargin(BUTTONS_SPACE);
      vLayoutLeft.addMember(findButton);
      vLayoutLeft.addMember(replaceFindButton);

      HLayout vLayoutRight = new HLayout();
      vLayoutRight.setHeight(BUTTON_HEIGHT);
      vLayoutRight.setMembersMargin(BUTTONS_SPACE);
      vLayoutRight.addMember(replaceButton);
      vLayoutRight.addMember(replaceAllButton);

      buttonsLayout.addMember(vLayoutLeft);
      buttonsLayout.addMember(vLayoutRight);
      // buttonsLayout.addMember(cancelButton);
      return buttonsLayout;
   }

   private DynamicForm createFindForm()
   {
      DynamicForm form = new DynamicForm();
      form.setWrapItemTitles(true);
      //form.setTitleWidth("100%");

      findField = createTextField("Find");
      replaceField = createTextField("Replace with");

      caseSensitiveField = createCheckBoxItem("Case sensitive");
      // fromStartField = createCheckBoxItem("From start");

      form.setFields(findField, replaceField, caseSensitiveField);
      return form;
   }

   private TextField createTextField(String title)
   {
      TextField textField = new TextField();
      textField.setWidth(FIELD_WIDTH);
      textField.setHeight(FIELD_HEIGHT);
      textField.setTitle(title);
      textField.setShowTitle(true);
      textField.setTitleAlign(Alignment.LEFT);
      return textField;
   }

   private CheckboxItem createCheckBoxItem(String title)
   {
      CheckboxItem checkboxItem = new CheckboxItem();
      checkboxItem.setTitle(title);
      checkboxItem.setTitleAlign(Alignment.LEFT);
      return checkboxItem;
   }

   /**
    * Create button with title and icon
    * 
    * @param title
    * @param icon
    * @return {@link IButton}
    */
   private IButton createButton(String title, String icon)
   {
      IButton button = new IButton();
      button.setTitle(title);
      button.setIcon(icon);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      return button;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getCaseSensitiveField()
    */
   public HasValue<Boolean> getCaseSensitiveField()
   {
      return caseSensitiveField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getFindButton()
    */
   public HasClickHandlers getFindButton()
   {
      return findButton;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getFindField()
    */
   public TextFieldItem getFindField()
   {
      return findField;
   }

   /* *//**
       * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getFromStartField()
       */
   /*
      public HasValue<Boolean> getFromStartField()
      {
         return fromStartField;
      }
   */
   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getReplaceAllButton()
    */
   public HasClickHandlers getReplaceAllButton()
   {
      return replaceAllButton;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getReplaceButton()
    */
   public HasClickHandlers getReplaceButton()
   {
      return replaceButton;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getReplaceField()
    */
   public TextFieldItem getReplaceField()
   {
      return replaceField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#enableFindButton(boolean)
    */
   public void enableFindButton(boolean isEnable)
   {
      findButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getReplaceFindButton()
    */
   public HasClickHandlers getReplaceFindButton()
   {
      return replaceFindButton;
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#enableReplaceFindButton(boolean)
    */
   public void enableReplaceFindButton(boolean isEnable)
   {
      replaceFindButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#enableReplaceAllButton(boolean)
    */
   public void enableReplaceAllButton(boolean isEnable)
   {
      replaceAllButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#enableReplaceButton(boolean)
    */
   public void enableReplaceButton(boolean isEnable)
   {
      replaceButton.setDisabled(!isEnable);
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.FindTextPresenter.Display#getResultLabel()
    */
   public HasValue<String> getResultLabel()
   {
      return findResultLabel;
   }

}
