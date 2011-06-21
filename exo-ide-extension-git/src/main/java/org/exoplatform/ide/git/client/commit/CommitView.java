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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for commiting from index to repository.
 * Must be added to <b>View.gwt.xml file</b>.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 31, 2011 10:38:47 AM anya $
 *
 */
public class CommitView extends ViewImpl implements CommitPresenter.Display
{
   private static final int HEIGHT = 240;

   private static final int WIDTH = 460;

   public static final String ID = "ideCommitView";

   private static final int BUTTON_HEIGHT = 22;

   private static final int BUTTON_WIDTH = 90;

   /*Elements IDs*/

   private static final String COMMIT_BUTTON_ID = "ideCommitViewCommitButton";

   private static final String CANCEL_BUTTON_ID = "ideCommitViewCancelButton";

   private static final String MESSAGE_FIELD_ID = "ideCommitViewMessageField";

   private static final String ALL_FIELD_ID = "ideCommitViewAllField";

   /*Elements titles*/

   private ImageButton commitButton;

   private ImageButton cancelButton;

   private TextAreaItem messageField;

   private CheckboxItem allField;

   public CommitView()
   {
      super(ID, ViewType.MODAL, GitExtension.MESSAGES.commitTitle(), null, WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(5);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      allField = new CheckboxItem(ALL_FIELD_ID, GitExtension.MESSAGES.commitAllFieldTitle());
      mainLayout.add(allField);
      mainLayout.setCellHeight(allField, "30px");

      messageField = createTextAreaField(MESSAGE_FIELD_ID,GitExtension.MESSAGES.commitMessageFieldTitle());

      mainLayout.add(messageField);
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
      buttonsLayout.setHeight(BUTTON_HEIGHT + 10 + "px");
      buttonsLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
      buttonsLayout.setSpacing(5);

      commitButton =
         createButton(COMMIT_BUTTON_ID, GitExtension.MESSAGES.buttonCommit(), GitClientBundle.INSTANCE.ok(),
            GitClientBundle.INSTANCE.okDisabled());
      cancelButton =
         createButton(CANCEL_BUTTON_ID, GitExtension.MESSAGES.buttonCancel(), GitClientBundle.INSTANCE.cancel(),
            GitClientBundle.INSTANCE.cancelDisabled());

      buttonsLayout.add(commitButton);
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
    * Creates {@link TextAreaItem} component.
    * 
    * @param id element's id
    * @param title title near text field
    * @return {@link TextAreaItem}
    */
   private TextAreaItem createTextAreaField(String id, String title)
   {
      TextAreaItem textArea = new TextAreaItem(id, title);
      textArea.setTitleOrientation(TitleOrientation.TOP);
      textArea.setWidth("100%");
      textArea.setHeight(60);
      return textArea;
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
      messageField.focusInItem();
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
