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
package org.exoplatform.ide.git.client.commit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for commiting from index to repository. Must be added to <b>View.gwt.xml file</b>.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 31, 2011 10:38:47 AM anya $
 * 
 */
public class CommitView extends ViewImpl implements CommitPresenter.Display
{
   private static final int HEIGHT = 240;

   private static final int WIDTH = 460;

   public static final String ID = "ideCommitView";

   /* Elements IDs */

   private static final String COMMIT_BUTTON_ID = "ideCommitViewCommitButton";

   private static final String CANCEL_BUTTON_ID = "ideCommitViewCancelButton";

   private static final String MESSAGE_FIELD_ID = "ideCommitViewMessageField";

   private static final String ALL_FIELD_ID = "ideCommitViewAllField";

   /* Elements titles */

   @UiField
   ImageButton commitButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   TextAreaInput messageField;

   @UiField
   CheckBox allField;

   interface CommitViewUiBinder extends UiBinder<Widget, CommitView>
   {
   }

   private static CommitViewUiBinder uiBinder = GWT.create(CommitViewUiBinder.class);

   public CommitView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.commitTitle(), null, WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      allField.setName(ALL_FIELD_ID);

      messageField.setName(MESSAGE_FIELD_ID);
      // ,GitExtension.MESSAGES.commitMessageFieldTitle());
      commitButton.setButtonId(COMMIT_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getCommitButton()
    */
   @Override
   public HasClickHandlers getCommitButton()
   {
      return commitButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getMessage()
    */
   @Override
   public HasValue<String> getMessage()
   {
      return messageField;
   }

   /**
    * @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#enableCommitButton(boolean)
    */
   @Override
   public void enableCommitButton(boolean enable)
   {
      commitButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#focusInMessageField()
    */
   @Override
   public void focusInMessageField()
   {
      messageField.focus();
   }

   /**
    * @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getAllField()
    */
   @Override
   public HasValue<Boolean> getAllField()
   {
      return allField;
   }
}
