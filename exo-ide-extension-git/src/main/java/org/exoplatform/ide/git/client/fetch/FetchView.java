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
package org.exoplatform.ide.git.client.fetch;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxFieldOld;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItemOld;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;

import java.util.LinkedHashMap;

/**
 * View for fetching changes from remote repository.
 * Point view in Views.gwt.xml 
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 20, 2011 4:23:02 PM anya $
 *
 */
public class FetchView extends ViewImpl implements FetchPresenter.Display
{
   public static final int HEIGHT = 222;

   public static final int WIDTH = 510;

   public static final String ID = "ideFetchView";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   private static final String FETCH_BUTTON_ID = "ideFetchViewFetchButton";

   private static final String CANCEL_BUTTON_ID = "ideFetchViewCancelButton";

   private static final String REMOTE_FIELD_ID = "ideFetchViewRemoteField";

   private static final String REMOTE_BRANCHES_FIELD_ID = "ideFetchViewRemoteBranchesField";

   private static final String LOCAL_BRANCHES_FIELD_ID = "ideFetchViewLocalBranchesField";

   private static final String REMOVE_DELETED_REFS_FIELD_ID = "ideFetchViewRemoveDeletedRefsField";

   /**
    * Fetch button.
    */
   private ImageButton fetchButton;

   /**
    * Cancel button.
    */
   private ImageButton cancelButton;

   /**
    * Remote repository field.
    */
   private SelectItemOld remoteField;

   /**
    * Local branches field.
    */
   private ComboBoxFieldOld localBranchesField;

   /**
    * Remote branches field.
    */
   private ComboBoxFieldOld remoteBranchesField;

   /**
    * Remove deleted refs field.
    */
   private CheckboxItem removeDeletedRefsField;

   /**
    * 
    */
   public FetchView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.fetchTitle(), null, WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(5);

      remoteField = new SelectItemOld(REMOTE_FIELD_ID, GitExtension.MESSAGES.fetchRemoteFieldTitle());
      remoteField.setWidth(290);
      mainLayout.add(remoteField);
      mainLayout.setCellVerticalAlignment(remoteField, HasVerticalAlignment.ALIGN_MIDDLE);

      addRefsLayout(mainLayout);

      removeDeletedRefsField =
         new CheckboxItem(REMOVE_DELETED_REFS_FIELD_ID, GitExtension.MESSAGES.fetchRemoveDeletedRefsTitle());
      mainLayout.add(removeDeletedRefsField);

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

      fetchButton =
         createButton(FETCH_BUTTON_ID, GitExtension.MESSAGES.buttonFetch(), GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, GitExtension.MESSAGES.buttonCancel(), GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(fetchButton);
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
    * Create the layout for displaying refs (local and remote ones).
    * 
    * @param panel parent panel for refs layout 
    */
   private void addRefsLayout(VerticalPanel panel)
   {
      HorizontalPanel refsLayout = new HorizontalPanel();
      refsLayout.setWidth("100%");
      refsLayout.setSpacing(3);

      localBranchesField =
         createComboBoxField(LOCAL_BRANCHES_FIELD_ID, GitExtension.MESSAGES.fetchLocalBranchesTitle());

      Image arrow = new Image(GitClientBundle.INSTANCE.arrow());
      arrow.setWidth("16px");
      arrow.setHeight("16px");
      DOM.setStyleAttribute(arrow.getElement(), "marginTop", "15px");

      remoteBranchesField =
         createComboBoxField(REMOTE_BRANCHES_FIELD_ID, GitExtension.MESSAGES.fetchRemoteBranchesTitle());

      refsLayout.add(remoteBranchesField);
      refsLayout.add(arrow);
      refsLayout.setCellVerticalAlignment(arrow, HasVerticalAlignment.ALIGN_MIDDLE);
      refsLayout.add(localBranchesField);

      panel.add(refsLayout);
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getFetchButton()
    */
   @Override
   public HasClickHandlers getFetchButton()
   {
      return fetchButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getRemoteName()
    */
   @Override
   public HasValue<String> getRemoteName()
   {
      return remoteField;
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getRemoteBranches()
    */
   @Override
   public HasValue<String> getRemoteBranches()
   {
      return remoteBranchesField;
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getLocalBranches()
    */
   @Override
   public HasValue<String> getLocalBranches()
   {
      return localBranchesField;
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getRemoveDeletedRefs()
    */
   @Override
   public HasValue<Boolean> getRemoveDeletedRefs()
   {
      return removeDeletedRefsField;
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#setRemoteBranches(java.lang.String[])
    */
   @Override
   public void setRemoteBranches(String[] values)
   {
      remoteBranchesField.setValueMap(values);

      if (values != null && values.length > 0)
      {
         remoteBranchesField.setValue(values[0], true);
      }
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#setLocalBranches(java.lang.String[])
    */
   @Override
   public void setLocalBranches(String[] values)
   {
      localBranchesField.setValueMap(values);
      if (values != null && values.length > 0)
      {
         localBranchesField.setValue(values[0], true);
      }
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#enableFetchButton(boolean)
    */
   @Override
   public void enableFetchButton(boolean enable)
   {
      fetchButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#setRemoteValues(java.util.LinkedHashMap)
    */
   @Override
   public void setRemoteValues(LinkedHashMap<String, String> values)
   {
      remoteField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.git.client.fetch.FetchPresenter.Display#getRemoteDisplayValue()
    */
   @Override
   public String getRemoteDisplayValue()
   {
      return remoteField.getDisplayValue();
   }

   /**
    * Creates combobox field.
    * 
    * @param id element's id
    * @param title element's title
    * @return {@link ComboBoxField} created combobox
    */
   private ComboBoxFieldOld createComboBoxField(String id, String title)
   {
      ComboBoxFieldOld combobox = new ComboBoxFieldOld();
      combobox.setTitleOrientation(TitleOrientation.TOP);
      combobox.setShowTitle(true);
      combobox.setTitle(title);
      combobox.setName(id);
      combobox.setWidth(210);
      combobox.setHeight(18);
      combobox.setPickListHeight(100);
      return combobox;
   }
}
