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
package org.exoplatform.ide.git.client.init;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * UI for initializing the repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 24, 2011 10:35:37 AM anya $
 *
 */
public class InitRepositoryView extends ViewImpl implements
   org.exoplatform.ide.git.client.init.InitRepositoryPresenter.Display
{

   public static final String ID = "ideInitRepositoryView";

   /*Elements IDs*/

   private static final String INIT_BUTTON_ID = "ideInitRepositoryViewInitButton";

   private static final String CANCEL_BUTTON_ID = "ideInitRepositoryViewCancelButton";

   private static final String WORKDIR_FIELD_ID = "ideInitRepositoryViewWorkDirField";

   private static final String BARE_FIELD_ID = "ideInitRepositoryViewBareField";

   @UiField
   TextInput workdirField;
   
   @UiField
   CheckBox bareField;
   
   @UiField
   ImageButton initButton;

   @UiField
   ImageButton cancelButton;

   interface InitRepositoryViewUiBinder extends UiBinder<Widget, InitRepositoryView>
   {
   }

   private static InitRepositoryViewUiBinder uiBinder = GWT.create(InitRepositoryViewUiBinder.class);

   public InitRepositoryView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.createTitle(), null, 475, 195);
      add(uiBinder.createAndBindUi(this));

      bareField.setName(BARE_FIELD_ID);
      workdirField.setName(WORKDIR_FIELD_ID);
      initButton.setButtonId(INIT_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.git.client.init.InitRepositoryPresenter.Display#getBareValue()
    */
   @Override
   public HasValue<Boolean> getBareValue()
   {
      return bareField;
   }

   /**
    * @see org.exoplatform.ide.git.client.init.InitRepositoryPresenter.Display#getWorkDirValue()
    */
   @Override
   public HasValue<String> getWorkDirValue()
   {
      return workdirField;
   }

   /**
    * @see org.exoplatform.ide.git.client.init.InitRepositoryPresenter.Display#getInitButton()
    */
   @Override
   public HasClickHandlers getInitButton()
   {
      return initButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.init.InitRepositoryPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }
}
