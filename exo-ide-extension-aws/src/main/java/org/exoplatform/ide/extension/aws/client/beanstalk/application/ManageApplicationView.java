package org.exoplatform.ide.extension.aws.client.beanstalk.application;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

public class ManageApplicationView extends ViewImpl implements ManageApplicationPresenter.Display
{
   private static final String ID = "ideManageApplicationView";

   private static final int WIDTH = 645;

   private static final int HEIGHT = 340;

   private static final String CLOSE_BUTTON_ID = "ideManageApplicationViewCloseButton";

   private static final String GENERAL_TAB_ID = "ideManageApplicationViewGeneralTab";

   private static ManageApplicationViewUiBinder uiBinder = GWT.create(ManageApplicationViewUiBinder.class);

   interface ManageApplicationViewUiBinder extends UiBinder<Widget, ManageApplicationView>
   {
   }

   @UiField
   TabPanel applicationTabPanel;

   @UiField
   ImageButton closeButton;

   private MainTabPain mainTabPain;

   public ManageApplicationView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.manageApplicationViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      closeButton.setButtonId(CLOSE_BUTTON_ID);

      mainTabPain = new MainTabPain();
      applicationTabPanel.addTab(GENERAL_TAB_ID, null, AWSExtension.LOCALIZATION_CONSTANT.generalTab(), mainTabPain,
         false);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getApplicationNameField()
    */
   @Override
   public HasValue<String> getApplicationNameField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getDescriptionField()
    */
   @Override
   public HasValue<String> getDescriptionField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getCreateDateField()
    */
   @Override
   public HasValue<String> getCreateDateField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getUpdatedDateField()
    */
   @Override
   public HasValue<String> getUpdatedDateField()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getUpdateDescriptionButton()
    */
   @Override
   public HasClickHandlers getUpdateDescriptionButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

}
