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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
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

   private static final String ADD_BUTTON_ID = "ideAddToIndexViewAddButton";

   private static final String CANCEL_BUTTON_ID = "ideAddToIndexViewCancelButton";

   private static final String UPDATE_FIELD_ID = "ideAddToIndexViewUpdaterField";

   private static final String MESSAGE_FIELD_ID = "ideAddToIndexViewMessageField";

   /*Elements titles*/
   @UiField
   ImageButton addButton;
   
   @UiField
   ImageButton cancelButton;
   
   @UiField
   CheckBox updateField;
   
   @UiField
   Label messageField;
   
   interface AddToIndexViewUiBinder extends UiBinder<Widget, AddToIndexView>
   {
   }
   
   private static AddToIndexViewUiBinder uiBinder = GWT.create(AddToIndexViewUiBinder.class);

   public AddToIndexView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.addToIndexTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      messageField.getElement().setId(MESSAGE_FIELD_ID);
      updateField.setName(UPDATE_FIELD_ID);
      addButton.setButtonId(ADD_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
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
