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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.commit.RevisionGrid;
import org.exoplatform.ide.git.shared.Revision;

/**
 * View for reseting head to the commit. Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 15, 2011 12:00:39 PM anya $
 * 
 */
public class ResetToCommitView extends ViewImpl implements ResetToCommitPresenter.Display
{

   private static final int HEIGHT = 345;

   private static final int WIDTH = 610;

   public static final String ID = "ideResetToCommitView";

   /* Elements IDs */

   private static final String RESET_BUTTON_ID = "ideRevertToCommitViewRevertButton";

   private static final String CANCEL_BUTTON_ID = "ideRevertToCommitViewCancelButton";

   /**
    * Revert button.
    */
   @UiField
   ImageButton resetButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * Grid with revisions.
    */
   @UiField
   RevisionGrid revisionGrid;

   /**
    * Mixed mode radio button.
    */
   @UiField
   RadioButton mixedMode;

   /**
    * Soft mode radio button.
    */
   @UiField
   RadioButton softMode;

   /**
    * Hard mode radio button.
    */
   @UiField
   RadioButton hardMode;

   interface ResetToCommitViewUiBinder extends UiBinder<Widget, ResetToCommitView>
   {
   }

   private static ResetToCommitViewUiBinder uiBinder = GWT.create(ResetToCommitViewUiBinder.class);

   public ResetToCommitView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.resetCommitViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      // GitExtension.MESSAGES.resetSoftTypeTitle());
      addDescription(softMode, GitExtension.MESSAGES.resetSoftTypeDescription());
      // GitExtension.MESSAGES.resetMixedTypeTitle());
      addDescription(mixedMode, GitExtension.MESSAGES.resetMixedTypeDescription());
      // GitExtension.MESSAGES.resetHardTypeTitle());
      addDescription(hardMode, GitExtension.MESSAGES.resetHardTypeDescription());

      resetButton.setButtonId(RESET_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * Add description to radio button title.
    * 
    * @param radioItem radio button
    * @param description description to add
    */
   private void addDescription(RadioButton radioItem, String description)
   {
      Element descElement = DOM.createSpan();
      descElement.setInnerText(description);
      DOM.setStyleAttribute(descElement, "color", "#555");
      radioItem.getElement().appendChild(descElement);
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getResetButton()
    */
   @Override
   public HasClickHandlers getResetButton()
   {
      return resetButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getRevisionGrid()
    */
   @Override
   public ListGridItem<Revision> getRevisionGrid()
   {
      return revisionGrid;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getSelectedRevision()
    */
   @Override
   public Revision getSelectedRevision()
   {
      return revisionGrid.getSelectedRevision();
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getSoftMode()
    */
   @Override
   public HasValue<Boolean> getSoftMode()
   {
      return softMode;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getMixMode()
    */
   @Override
   public HasValue<Boolean> getMixMode()
   {
      return mixedMode;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#getHardMode()
    */
   @Override
   public HasValue<Boolean> getHardMode()
   {
      return hardMode;
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitPresenter.Display#enableResetButon(boolean)
    */
   @Override
   public void enableResetButon(boolean enabled)
   {
      resetButton.setEnabled(enabled);
   }
}
