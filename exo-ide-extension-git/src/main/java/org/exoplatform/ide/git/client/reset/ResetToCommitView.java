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

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.Border;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.RadioItem;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.commit.RevisionGrid;
import org.exoplatform.ide.git.shared.Revision;

/**
 * View for reseting head to the commit.
 * Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 15, 2011 12:00:39 PM anya $
 *
 */
public class ResetToCommitView extends ViewImpl implements ResetToCommitPresenter.Display
{
   private static final int HEIGHT = 345;

   private static final int WIDTH = 610;

   public static final String ID = "ideResetToCommitView";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/

   private static final String RESET_BUTTON_ID = "ideRevertToCommitViewRevertButton";

   private static final String CANCEL_BUTTON_ID = "ideRevertToCommitViewCancelButton";

   private static final String MODE_ID = "ideRevertToCommitViewMode";

   /**
    *Revert button.
    */
   private ImageButton resetButton;

   /**
    * Cancel button.
    */
   private ImageButton cancelButton;

   /**
    * Grid with revisions.
    */
   private RevisionGrid revisionGrid;

   /**
    * Mixed mode radio button.
    */
   private RadioItem mixedMode;
   
   /**
    * Soft mode radio button.
    */
   private RadioItem softMode;
   
   /**
    * Hard mode radio button.
    */
   private RadioItem hardMode;

   public ResetToCommitView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.resetCommitViewTitle(), null, WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      
      Border border = new Border();
      border.setWidth("100%");
      revisionGrid = new RevisionGrid();
      revisionGrid.setWidth("100%");
      revisionGrid.setHeight(140);
      border.add(revisionGrid);
      mainLayout.add(border);

      softMode = new RadioItem(MODE_ID, GitExtension.MESSAGES.resetSoftTypeTitle());
      addDescription(softMode,GitExtension.MESSAGES.resetSoftTypeDescription());
      mixedMode = new RadioItem(MODE_ID, GitExtension.MESSAGES.resetMixedTypeTitle());
      addDescription(mixedMode, GitExtension.MESSAGES.resetMixedTypeDescription());
      hardMode = new RadioItem(MODE_ID, GitExtension.MESSAGES.resetHardTypeTitle());
      addDescription(hardMode, GitExtension.MESSAGES.resetHardTypeDescription());

      VerticalPanel modeLayout = new VerticalPanel();
      modeLayout.setWidth("100%");
      modeLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      modeLayout.setSpacing(2);

      modeLayout.add(softMode);
      modeLayout.add(mixedMode);
      modeLayout.add(hardMode);

      mainLayout.add(modeLayout);
      addButtonsLayout(mainLayout);
      add(mainLayout);
   }

   /**
    * Add description to radio button title.
    * 
    * @param radioItem radio button
    * @param description description to add
    */
   private void addDescription(RadioItem radioItem, String description)
   {
      Element descElement = DOM.createSpan();
      descElement.setInnerText(description);
      DOM.setStyleAttribute(descElement, "color", "#555");
      radioItem.getElement().appendChild(descElement);
   }

   /**
    * Add buttons to the pointed panel.
    * 
    * @param panel
    */
   private void addButtonsLayout(VerticalPanel panel)
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      buttonsLayout.setSpacing(5);

      resetButton =
         createButton(RESET_BUTTON_ID, GitExtension.MESSAGES.buttonReset(), GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, GitExtension.MESSAGES.buttonCancel(), GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(resetButton);
      buttonsLayout.add(cancelButton);

      panel.add(buttonsLayout);
   }

   /**
    * Creates button.
    * 
    * @param id button's id
    * @param title button's title
    * @param icon button's normal icon
    * @param disabledIcon button's icon in disabled state
    * @return {@link ImageButton}
    */
   private ImageButton createButton(String id, String title, ImageResource icon, ImageResource disabledIcon)
   {
      ImageButton button = new ImageButton(title);
      button.setButtonId(id);
      button.setImages(new Image(icon), new Image(disabledIcon));
      button.setHeight(BUTTON_HEIGHT + "px");
      button.setWidth(BUTTON_WIDTH + "px");
      return button;
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
