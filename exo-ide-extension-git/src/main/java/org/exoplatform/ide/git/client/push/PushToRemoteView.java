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
package org.exoplatform.ide.git.client.push;

import com.google.gwt.user.client.DOM;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;

import java.util.LinkedHashMap;

/**
 * View for pushing changes to remote repository.
 * Must be pointed in View.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 4, 2011 10:48:28 AM anya $
 *
 */
public class PushToRemoteView extends ViewImpl implements PushToRemotePresenter.Display
{
   public static final String ID = "idePushToRemoteView";

   public static final String TYPE = "modal";

   public static final String TITLE = "Push to remote repository";

   /*Elements IDs*/

   private static final String PUSH_BUTTON_ID = "idePushToRemoteViewPushButton";

   private static final String CANCEL_BUTTON_ID = "idePushToRemoteViewCancelButton";

   private static final String REMOTE_FIELD_ID = "idePushToRemoteViewRemoteField";

   private static final String LOCAL_BRANCHES_FIELD_ID = "idePushToRemoteViewLocalBranchesField";

   private static final String REMOTE_BRANCHES_FIELD_ID = "idePushToRemoteViewRemoteBranchesField";

   /*Elements titles*/

   private static final String PUSH_BUTTON_TITLE = "Push";

   private static final String CANCEL_BUTTON_TITLE = "Cancel";

   private static final String REMOTE_FIELD_TITLE = "Choose remote repository :";

   private static final String LOCAL_BRANCHES_TITLE = "Push from branch:";

   private static final String REMOTE_BRANCHES_TITLE = "To branch:";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   private IButton pushButton;

   private IButton cancelButton;

   private SelectItem remoteField;

   private SelectItem localBranchesField;

   private ComboBoxField remoteBranchesField;

   public PushToRemoteView()
   {
      super(ID, TYPE, TITLE, null, 490, 200);
      setWidth("100%");
      setHeight("100%");

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      mainLayout.setSpacing(5);

      remoteField = new SelectItem(REMOTE_FIELD_ID, REMOTE_FIELD_TITLE);
      remoteField.setWidth(280);
      mainLayout.add(remoteField);
      mainLayout.setCellVerticalAlignment(remoteField, HasVerticalAlignment.ALIGN_MIDDLE);

      addRefsLayout(mainLayout);

      addButtonsLayout(mainLayout);
      add(mainLayout);
   }

   /**
    * Create the layout for displaying refs (local and remote ones).
    * 
    * @param panel parent panel for refs layout 
    */
   private void addRefsLayout(VerticalPanel panel)
   {
      HorizontalPanel refsLayout = new HorizontalPanel();
      refsLayout.setWidth("100%");
      refsLayout.setSpacing(3);

      localBranchesField = new SelectItem(LOCAL_BRANCHES_FIELD_ID, LOCAL_BRANCHES_TITLE);
      localBranchesField.setTitleOrientation(TitleOrientation.TOP);
      localBranchesField.setWidth(210);

      Image arrow = new Image(GitClientBundle.INSTANCE.arrow());
      arrow.setWidth("16px");
      arrow.setHeight("16px");
      DOM.setStyleAttribute(arrow.getElement(), "marginTop", "15px");

      remoteBranchesField = createComboBoxField(REMOTE_BRANCHES_FIELD_ID, REMOTE_BRANCHES_TITLE);

      refsLayout.add(localBranchesField);
      refsLayout.add(arrow);
      refsLayout.setCellVerticalAlignment(arrow, HasVerticalAlignment.ALIGN_MIDDLE);
      refsLayout.add(remoteBranchesField);

      panel.add(refsLayout);
   }

   /**
    * Creates combobox field.
    * 
    * @param id element's id
    * @param title element's title
    * @return {@link ComboBoxField} created combobox
    */
   private ComboBoxField createComboBoxField(String id, String title)
   {
      ComboBoxField combobox = new ComboBoxField();
      combobox.setTitleOrientation(TitleOrientation.TOP);
      combobox.setShowTitle(true);
      combobox.setTitle(title);
      combobox.setName(id);
      combobox.setWidth(210);
      combobox.setHeight(18);
      combobox.setPickListHeight(100);
      return combobox;
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
      buttonsLayout.setSpacing(5);

      pushButton =
         createButton(PUSH_BUTTON_ID, PUSH_BUTTON_TITLE, GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, CANCEL_BUTTON_TITLE, GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(pushButton);
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
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getPushButton()
    */
   @Override
   public HasClickHandlers getPushButton()
   {
      return pushButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getRemoteValue()
    */
   @Override
   public HasValue<String> getRemoteValue()
   {
      return remoteField;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#setRemoteValues(java.lang.String[])
    */
   @Override
   public void setRemoteValues(LinkedHashMap<String, String> values)
   {
      remoteField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getRemoteBranchesValue()
    */
   @Override
   public HasValue<String> getRemoteBranchesValue()
   {
      return remoteBranchesField;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getLocalBranchesValue()
    */
   @Override
   public HasValue<String> getLocalBranchesValue()
   {
      return localBranchesField;
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#setRemoteBranches(java.lang.String[])
    */
   @Override
   public void setRemoteBranches(String[] values)
   {
      System.out.println("PushToRemoteView.setRemoteBranches()"+remoteBranchesField.getValue());
      remoteBranchesField.setValueMap(values);
      if (values != null && values.length > 0)
      {
         remoteBranchesField.setValue(values[0], true);
      }
         
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#setLocalBranches(java.lang.String[])
    */
   @Override
   public void setLocalBranches(String[] values)
   {
      localBranchesField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#enablePushButton(boolean)
    */
   @Override
   public void enablePushButton(boolean enable)
   {
      pushButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.git.client.push.PushToRemotePresenter.Display#getRemoteDisplayValue()
    */
   @Override
   public String getRemoteDisplayValue()
   {
      return remoteField.getDisplayValue();
   }
}
