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
package org.exoplatform.ide.extension.heroku.client.stack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.shared.Stack;

import java.util.List;

/**
 * Change Heroku application's view. Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 29, 2011 11:16:03 AM anya $
 * 
 */
public class ChangeStackView extends ViewImpl implements ChangeStackPresenter.Display
{
   private static final String ID = "ideChangeStackView";

   private static final int WIDTH = 380;

   private static final int HEIGHT = 250;

   private static final String CHANGE_BUTTON_ID = "ideChangeStackViewChangeButton";

   private static final String CANCEL_BUTTON_ID = "ideChangeStackViewCancelButton";

   /**
    * UI binder of this view.
    */
   private static ChangeStackViewUiBinder uiBinder = GWT.create(ChangeStackViewUiBinder.class);

   interface ChangeStackViewUiBinder extends UiBinder<Widget, ChangeStackView>
   {
   }

   /**
    * Change stack button.
    */
   @UiField
   ImageButton changeButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * Grid with application's stacks.
    */
   @UiField
   StackGrid stackGrid;

   public ChangeStackView()
   {
      super(ID, ViewType.MODAL, HerokuExtension.LOCALIZATION_CONSTANT.changeStackViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      changeButton.setButtonId(CHANGE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter.Display#getStackGrid()
    */
   @Override
   public ListGridItem<Stack> getStackGrid()
   {
      return stackGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter.Display#getChangeButton()
    */
   @Override
   public HasClickHandlers getChangeButton()
   {
      return changeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter.Display#enableChangeButton(boolean)
    */
   @Override
   public void enableChangeButton(boolean enable)
   {
      changeButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.stack.ChangeStackPresenter.Display#getSelectedStack()
    */
   @Override
   public Stack getSelectedStack()
   {
      List<Stack> selected = stackGrid.getSelectedItems();
      return (selected != null && selected.size() > 0) ? selected.get(0) : null;
   }
}
