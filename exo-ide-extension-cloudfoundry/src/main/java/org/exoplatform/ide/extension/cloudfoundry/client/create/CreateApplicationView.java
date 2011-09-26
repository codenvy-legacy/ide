package org.exoplatform.ide.extension.cloudfoundry.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * View for creating application on CloudFoundry.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateApplicationView.java Jul 12, 2011 10:26:54 AM vereshchaka $
 *
 */
public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "ideCloudFoundryCreateAppView";

   private static final int WIDTH = 500;

   private static final int HEIGHT = 350;

   private static final String CREATE_BUTTON_ID = "ideCloudFoundryAppViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCloudFoundryAppViewCancelButton";

   private static final String TYPE_FIELD_ID = "ideCloudFoundryAppViewTypeField";
   
   private static final String NAME_FIELD_ID = "ideCloudFoundryAppViewNameField";

   private static final String URL_FIELD_ID = "ideCloudFoundryAppViewUrlField";
   
   private static final String INSTANCES_FIELD_ID = "ideCloudFoundryAppViewInstancesField";
   
   private static final String MEMORY_FIELD_ID = "ideCloudFoundryAppViewMemoryField";
   
   private static final String SERVER_FIELD_ID = "ideCloudFoundryAppViewServerField";

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }
   
   /**
    * Server field (location of Cloud Foundry instance where application must be created, e.g.
    *           http://api.cloudfoundry.com)
    */
   @UiField
   ComboBoxField serverField;

   /**
    * Application type field.
    */
   @UiField
   SelectItem typeField;
   
   /**
    * Checkbox, that indicate, is type will be detected automatically
    * or selected by user.
    */
   @UiField
   CheckboxItem changeTypeField;
   
   /**
    * Application name field.
    */
   @UiField
   TextField nameField;
   
   /**
    * Application URL field.
    */
   @UiField
   TextField urlField;
   
   /**
    * Checkbox, that indicate, is URL will be added automatically
    * or set by user.
    */
   @UiField
   CheckboxItem customUrlField;
   
   /**
    * Number of instanses of application field.
    */
   @UiField
   TextField instansesField;
   
   /**
    * Memory field (needed for application).
    */
   @UiField
   TextField memoryField;
   
   /**
    * Is start application after creation.
    */
   @UiField
   CheckboxItem startAfterCreationField;

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
      super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.createApplicationTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      serverField.setName(SERVER_FIELD_ID);
      serverField.setWidth(208);
      typeField.setName(TYPE_FIELD_ID);
      typeField.setHeight(22);
      nameField.setName(NAME_FIELD_ID);
      nameField.setHeight(22);
      urlField.setName(URL_FIELD_ID);
      urlField.setHeight(22);
      instansesField.setName(INSTANCES_FIELD_ID);
      instansesField.setHeight(22);
      memoryField.setName(MEMORY_FIELD_ID);
      memoryField.setHeight(22);
      createButton.setButtonId(CREATE_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getCreateButton()
    */
   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getApplicationNameField()
    */
   @Override
   public HasValue<String> getTypeField()
   {
      return typeField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getWorkDirLocationField()
    */
   @Override
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#enableCreateButton(boolean)
    */
   @Override
   public void enableCreateButton(boolean enable)
   {
      createButton.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#focusInNameField()
    */
   @Override
   public void focusInNameField()
   {
      nameField.focusInItem();
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getRemoteNameField()
    */
   @Override
   public HasValue<String> getUrlField()
   {
      return urlField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.initialize.DeployApplicationPresenter.Display#setTypeValues(java.lang.String[])
    */
   @Override
   public void setTypeValues(String[] domains)
   {
      typeField.clearValue();
      typeField.setValueMap(domains);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getInstancesField()
    */
   @Override
   public HasValue<String> getInstancesField()
   {
      return instansesField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getMemoryField()
    */
   @Override
   public HasValue<String> getMemoryField()
   {
      return memoryField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getAutodetectTypeCheckItem()
    */
   @Override
   public HasValue<Boolean> getAutodetectTypeCheckItem()
   {
      return changeTypeField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getUrlCheckItem()
    */
   @Override
   public HasValue<Boolean> getUrlCheckItem()
   {
      return customUrlField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getIsStartAfterCreationCheckItem()
    */
   @Override
   public HasValue<Boolean> getIsStartAfterCreationCheckItem()
   {
      return startAfterCreationField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#enableTypeField(boolean)
    */
   @Override
   public void enableTypeField(boolean enable)
   {
      typeField.setEnabled(enable);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#enableUrlField(boolean)
    */
   @Override
   public void enableUrlField(boolean enable)
   {
      urlField.setEnabled(enable);
      urlField.setShowDisabled(!enable);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#enableMemoryField(boolean)
    */
   @Override
   public void enableMemoryField(boolean enable)
   {
      memoryField.setEnabled(enable);
      memoryField.setShowDisabled(!enable);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#setSelectedIndexForTypeSelectItem(int)
    */
   @Override
   public void setSelectedIndexForTypeSelectItem(int index)
   {
      typeField.setSelectedIndex(index);
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#focusInUrlField()
    */
   @Override
   public void focusInUrlField()
   {
      urlField.focusInItem();
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#getServerField()
    */
   @Override
   public HasValue<String> getServerField()
   {
      return serverField;
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter.Display#setServerValues(java.lang.String[])
    */
   @Override
   public void setServerValues(String[] servers)
   {
      serverField.setValueMap(servers);
   }

}
