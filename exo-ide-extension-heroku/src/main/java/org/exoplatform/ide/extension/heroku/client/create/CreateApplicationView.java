package org.exoplatform.ide.extension.heroku.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for creation new application on Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 30, 2011 3:07:34 PM anya $
 * 
 */
public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "ideCreateApplicationView";

   private static final int WIDTH = 520;

   private static final int HEIGHT = 220;

   private static final String CREATE_BUTTON_ID = "ideCreateApplicationViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateApplicationViewCancelButton";

   private static final String NAME_FIELD_ID = "ideCreateApplicationViewNameField";

   private static final String REMOTE_NAME_FIELD_ID = "ideCreateApplicationViewRemoteNameField";

   private static final String WORK_DIR_FIELD_ID = "ideCreateApplicationViewWorkDirField";

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }

   /**
    * Application name field.
    */
   @UiField
   TextInput nameField;

   /**
    * Remote repository name field.
    */
   @UiField
   TextInput remoteField;

   /**
    * Git repository location field.
    */
   @UiField
   TextInput gitRepoField;

   /**
    * Create application button.
    */
   @UiField
   ImageButton createButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public CreateApplicationView()
   {
      super(ID, ViewType.MODAL, HerokuExtension.LOCALIZATION_CONSTANT.createApplicationViewTitle(), null, WIDTH,
         HEIGHT, false);
      add(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD_ID);
      remoteField.setName(REMOTE_NAME_FIELD_ID);
      gitRepoField.setName(WORK_DIR_FIELD_ID);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getApplicationNameField()
    */
   @Override
   public HasValue<String> getApplicationNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getWorkDirLocationField()
    */
   @Override
   public HasValue<String> getWorkDirLocationField()
   {
      return gitRepoField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#enableCreateButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enable)
   {
      createButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#focusInApplicationNameField()
    */
   @Override
   public void focusInApplicationNameField()
   {
      nameField.focus();
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getRemoteNameField()
    */
   @Override
   public HasValue<String> getRemoteNameField()
   {
      return remoteField;
   }

}
