/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client.add;

import com.google.gwt.user.client.DOM;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for adding changes togit index.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 30, 2011 9:25:02 AM anya $
 *
 */
public class AddToIndexView extends ViewImpl implements AddToIndexPresenter.Display
{
   public static final int HEIGHT = 180;

   public static final int WIDTH = 420;

   public static final String ID = "ideAddToIndexView";

   public static final String TYPE = "modal";

   public static final String TITLE = "Add to index";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   private static final String ADD_BUTTON_ID = "ideAddToIndexViewAddButton";

   private static final String CANCEL_BUTTON_ID = "ideAddToIndexViewCancelButton";

   private static final String UPDATE_FIELD_ID = "ideAddToIndexViewUpdaterField";

   private static final String MESSAGE_FIELD_ID = "ideAddToIndexViewMessageField";

   /*Elements titles*/

   private IButton addButton;

   private IButton cancelButton;

   private CheckboxItem updateField;

   private Label messageField;

   public AddToIndexView()
   {
      super(ID, TYPE, TITLE, null, WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);

      messageField = new Label();
      messageField.setID(MESSAGE_FIELD_ID);
      messageField.setIsHTML(true);
      DOM.setStyleAttribute(messageField.getElement(), "padding", "3px");
      mainLayout.add(messageField);

      updateField = new CheckboxItem(UPDATE_FIELD_ID, GitExtension.MESSAGES.addToIndexUpdateFieldTitle());
      mainLayout.add(updateField);

      addButtonsLayout(mainLayout);

      add(mainLayout);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(VerticalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + 20 + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
      buttonsLayout.setSpacing(5);

      addButton =
         createButton(ADD_BUTTON_ID, GitExtension.MESSAGES.buttonAdd(), GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, GitExtension.MESSAGES.buttonCancel(), GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(addButton);
      buttonsLayout.add(cancelButton);

      panel.add(buttonsLayout);
      panel.setCellHorizontalAlignment(buttonsLayout, HasHorizontalAlignment.ALIGN_CENTER);
   }

   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link IButton}
    */
   private IButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon.getURL(), disabledIcon.getURL());
      button.setHeight(BUTTON_HEIGHT);
      button.setWidth(BUTTON_WIDTH);
      return button;
   }

   /**
    * @see org.exoplatform.ide.git.client.add.AddToIndexPresenter.Display#getAddButton()
    */
   @Override
   public HasClickHandlers getAddButton()
   {
      return addButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.add.AddToIndexPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.add.AddToIndexPresenter.Display#getUpdateValue()
    */
   @Override
   public HasValue<Boolean> getUpdateValue()
   {
      return updateField;
   }

   /**
    * @see org.exoplatform.ide.git.client.add.AddToIndexPresenter.Display#getMessage()
    */
   @Override
   public HasValue<String> getMessage()
   {
      return messageField;
   }
}
