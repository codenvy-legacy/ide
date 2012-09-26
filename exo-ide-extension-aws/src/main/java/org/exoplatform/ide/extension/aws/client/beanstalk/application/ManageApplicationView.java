package org.exoplatform.ide.extension.aws.client.beanstalk.application;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSClientBundle;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.HasVersionActions;
import org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.VersionsTabPain;
import org.exoplatform.ide.extension.aws.client.beanstalk.environment.EnvironmentTabPain;
import org.exoplatform.ide.extension.aws.client.beanstalk.environment.HasEnvironmentActions;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

public class ManageApplicationView extends ViewImpl implements ManageApplicationPresenter.Display
{
   private static final String ID = "ideManageApplicationView";

   private static final int WIDTH = 745;

   private static final int HEIGHT = 360;

   private static final String CLOSE_BUTTON_ID = "ideManageApplicationViewCloseButton";

   private static final String GENERAL_TAB_ID = "ideManageApplicationViewGeneralTab";

   private static final String VERSIONS_TAB_ID = "ideManageApplicationViewVersionsTab";

   private static final String ENVIRONMENTS_TAB_ID = "ideManageApplicationViewEnvironmentsTab";

   private static ManageApplicationViewUiBinder uiBinder = GWT.create(ManageApplicationViewUiBinder.class);

   interface ManageApplicationViewUiBinder extends UiBinder<Widget, ManageApplicationView>
   {
   }

   @UiField
   TabPanel applicationTabPanel;

   @UiField
   ImageButton closeButton;

   private MainTabPain mainTabPain;

   private VersionsTabPain versionsTabPain;
   
   private EnvironmentTabPain environmentTabPain;;

   public ManageApplicationView()
   {
      super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.manageApplicationViewTitle(), null, WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      closeButton.setButtonId(CLOSE_BUTTON_ID);

      mainTabPain = new MainTabPain();
      applicationTabPanel.addTab(GENERAL_TAB_ID, new Image(AWSClientBundle.INSTANCE.general()),
         AWSExtension.LOCALIZATION_CONSTANT.generalTab(), mainTabPain, false);

      versionsTabPain = new VersionsTabPain();
      applicationTabPanel.addTab(VERSIONS_TAB_ID, new Image(AWSClientBundle.INSTANCE.versions()),
         AWSExtension.LOCALIZATION_CONSTANT.versionsTab(), versionsTabPain, false);
      
      environmentTabPain = new EnvironmentTabPain();
      applicationTabPanel.addTab(ENVIRONMENTS_TAB_ID, new Image(AWSClientBundle.INSTANCE.versions()),
         "Environments", environmentTabPain, false);
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getApplicationNameField()
    */
   @Override
   public HasValue<String> getApplicationNameField()
   {
      return mainTabPain.getNameField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getDescriptionField()
    */
   @Override
   public HasValue<String> getDescriptionField()
   {
      return mainTabPain.getDescriptionField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getCreateDateField()
    */
   @Override
   public HasValue<String> getCreateDateField()
   {
      return mainTabPain.getCreationDateField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getUpdatedDateField()
    */
   @Override
   public HasValue<String> getUpdatedDateField()
   {
      return mainTabPain.getUpdatedDateField();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return mainTabPain.getDeleteApplicationButton();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getUpdateDescriptionButton()
    */
   @Override
   public HasClickHandlers getUpdateDescriptionButton()
   {
      return mainTabPain.getEditDescriptionButton();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getVersionsGrid()
    */
   @Override
   public ListGridItem<ApplicationVersionInfo> getVersionsGrid()
   {
      return versionsTabPain.getVersionsGrid();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getVersionActions()
    */
   @Override
   public HasVersionActions getVersionActions()
   {
      return versionsTabPain.getVersionsGrid();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getCreateVersionButton()
    */
   @Override
   public HasClickHandlers getCreateVersionButton()
   {
      return mainTabPain.getCreateVersionButton();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#getLaunchEnvironmentButton()
    */
   @Override
   public HasClickHandlers getLaunchEnvironmentButton()
   {
      return mainTabPain.getLaunchEnvironmentButton();
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.ManageApplicationPresenter.Display#selectVersionsTab()
    */
   @Override
   public void selectVersionsTab()
   {
      applicationTabPanel.selectTab(VERSIONS_TAB_ID);
   }
   
   @Override
   public void selectEnvironmentTab()
   {
      applicationTabPanel.selectTab(ENVIRONMENTS_TAB_ID);
   }
   
   @Override
   public HasEnvironmentActions getEnvironmentActions()
   {
      return environmentTabPain.getVersionsGrid();
   }
   
   @Override
   public ListGridItem<EnvironmentInfo> getEnvironmentGrid()
   {
      return environmentTabPain.getVersionsGrid();
   }
}
