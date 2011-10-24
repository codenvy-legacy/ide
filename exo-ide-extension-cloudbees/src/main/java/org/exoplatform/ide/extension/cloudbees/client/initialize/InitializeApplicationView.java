package org.exoplatform.ide.extension.cloudbees.client.initialize;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;

/**
 * View for setting application id.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationNameView.java Jun 27, 2011 12:03:57 PM vereshchaka $
 *
 */
public class InitializeApplicationView extends ViewImpl implements InitializeApplicationPresenter.Display
{
   private static final String ID = "ideCreateApplicationView";

   private static final int WIDTH = 520;

   private static final int HEIGHT = 240;

   private static final String CREATE_BUTTON_ID = "ideApplicationNameViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideApplicationNameViewCancelButton";

   private static final String DOMAIN_FIELD_ID = "ideApplicationNameViewDomainField";
   
   private static final String NAME_FIELD_ID = "ideApplicationNameViewNameField";

   private static final String ID_FIELD_ID = "ideApplicationNameViewIdField";

   private static ApplicationNameViewUiBinder uiBinder = GWT.create(ApplicationNameViewUiBinder.class);

   interface ApplicationNameViewUiBinder extends UiBinder<Widget, InitializeApplicationView>
   {
   }

   /**
    * Application domain field.
    */
   @UiField
   SelectItem domainField;
   
   /**
    * Remote repository name field.
    */
   @UiField
   TextInput nameField;

   /**
    * Git repository location field.
    */
   @UiField
   TextInput idField;

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

   public InitializeApplicationView()
   {
      super(ID, ViewType.MODAL, CloudBeesExtension.LOCALIZATION_CONSTANT.appNameTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      domainField.setName(DOMAIN_FIELD_ID);
      nameField.setName(NAME_FIELD_ID);
      idField.setName(ID_FIELD_ID);
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
   public HasValue<String> getDomainField()
   {
      return domainField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getWorkDirLocationField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return nameField;
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
      nameField.getElement().focus();
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.create.CreateApplicationPresenter.Display#getRemoteNameField()
    */
   @Override
   public HasValue<String> getApplicationIdField()
   {
      return idField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.initialize.DeployApplicationPresenter.Display#setDomainValues(java.lang.String[])
    */
   @Override
   public void setDomainValues(String[] domains)
   {
      domainField.clearValue();
      domainField.setValueMap(domains);
   }
}
