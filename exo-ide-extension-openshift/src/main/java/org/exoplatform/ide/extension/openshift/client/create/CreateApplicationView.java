package org.exoplatform.ide.extension.openshift.client.create;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;

/**
 * View for creation new application on Heroku.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 30, 2011 3:07:34 PM anya $
 *
 */
public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "ideCreateApplicationView";

   private static final int WIDTH = 520;

   private static final int HEIGHT = 240;

   private static final String TYPE = "modal";

   private static final String TITLE = OpenShiftExtension.LOCALIZATION_CONSTANT.createApplicationViewTitle();

   private static final String CREATE_BUTTON_ID = "ideCreateApplicationViewCreateButton";

   private static final String CANCEL_BUTTON_ID = "ideCreateApplicationViewCancelButton";

   private static final String NAME_FIELD_ID = "ideCreateApplicationViewNameField";

   private static final String TYPE_FIELD_ID = "ideCreateApplicationViewTypeField";

   private static final String WORK_DIR_FIELD_ID = "ideCreateApplicationViewWorkDirField";

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }

   /**
    * Application name field.
    */
   @UiField
   TextField nameField;

   /**
    * Application's type.
    */
   @UiField
   SelectItem typeField;

   /**
    * Application's location field.
    */
   @UiField
   TextField workDirField;

   /**
    * Create application button.
    */
   @UiField
   IButton createButton;

   /**
    * Cancel button.
    */
   @UiField
   IButton cancelButton;

   public CreateApplicationView()
   {
      super(ID, TYPE, TITLE, null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      nameField.setName(NAME_FIELD_ID);
      nameField.setHeight(22);
      typeField.setName(TYPE_FIELD_ID);
      typeField.setHeight(22);
      workDirField.setName(WORK_DIR_FIELD_ID);
      workDirField.setHeight(22);
      createButton.setID(CREATE_BUTTON_ID);
      cancelButton.setID(CANCEL_BUTTON_ID);
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
      return workDirField;
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
      nameField.focusInItem();
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.create.CreateApplicationPresenter.Display#getTypeField()
    */
   @Override
   public HasValue<String> getTypeField()
   {
      return typeField;
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.create.CreateApplicationPresenter.Display#setApplicationTypeValues(java.lang.String[])
    */
   @Override
   public void setApplicationTypeValues(String[] values)
   {
      typeField.setValueMap(values);
   }
}
