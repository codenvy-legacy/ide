package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.model.CronEntry;

public class AppEngineProjectView extends ViewImpl implements AppEngineProjectPresenter.Display
{
   private static final String ID = "ideAppEngineProjectView";

   private static final String GENERAL_TAB_ID = "ideAppEngineProjectViewGeneralTab";

   private static final String CRONS_TAB_ID = "ideAppEngineProjectViewCronsTab";

   private static final String BACKENDS_TAB_ID = "ideAppEngineProjectViewBackendsTab";

   private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.manageApplicationViewTitle();

   private static final int WIDTH = 600;

   private static final int HEIGHT = 400;

   private static AppEngineProjectViewUiBinder uiBinder = GWT.create(AppEngineProjectViewUiBinder.class);

   @UiField
   ImageButton closeButton;

   private MainTabPain mainTabPain;

   private CronTabPane cronTabPane;

   private BackendsTabPane backendsTabPane;

   @UiField
   TabPanel applicationTabPanel;

   interface AppEngineProjectViewUiBinder extends UiBinder<Widget, AppEngineProjectView>
   {
   }

   public AppEngineProjectView()
   {
      super(ID, ViewType.MODAL, TITLE, new Image(GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));

      mainTabPain = new MainTabPain();
      applicationTabPanel.addTab(GENERAL_TAB_ID, new Image(GAEClientBundle.INSTANCE.general()),
         GoogleAppEngineExtension.GAE_LOCALIZATION.manageApplicationGeneralTab(), mainTabPain, false);

      cronTabPane = new CronTabPane();
   /*   applicationTabPanel.addTab(CRONS_TAB_ID, new Image(GAEClientBundle.INSTANCE.crons()),
         GoogleAppEngineExtension.GAE_LOCALIZATION.manageApplicationCronsTab(), cronTabPane, false);*/

      backendsTabPane = new BackendsTabPane();
      /*
       * applicationTabPanel.addTab(BACKENDS_TAB_ID, new Image(GAEClientBundle.INSTANCE.backends()),
       * GoogleAppEngineExtension.GAE_LOCALIZATION.manageApplicationBackendsTab(), backendsTabPane, false);
       */
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getConfigureBackendButton()
    */
   @Override
   public HasClickHandlers getConfigureBackendButton()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getDeleteBackendButton()
    */
   @Override
   public HasClickHandlers getDeleteBackendButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateBackendButton()
    */
   @Override
   public HasClickHandlers getUpdateBackendButton()
   {
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getRollbackBackendButton()
    */
   @Override
   public HasClickHandlers getRollbackBackendButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getRollbackAllBackendsButton()
    */
   @Override
   public HasClickHandlers getRollbackAllBackendsButton()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getLogsButton()
    */
   @Override
   public HasClickHandlers getLogsButton()
   {
      return mainTabPain.getGetLogsButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateButton()
    */
   @Override
   public HasClickHandlers getUpdateButton()
   {
      return mainTabPain.getUpdateApplicationButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getRollbackButton()
    */
   @Override
   public HasClickHandlers getRollbackButton()
   {
      return mainTabPain.getRollbackApplicationButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateCronButton()
    */
   @Override
   public HasClickHandlers getUpdateCronButton()
   {
      return cronTabPane.getUpdateCronButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateDosButton()
    */
   @Override
   public HasClickHandlers getUpdateDosButton()
   {
      return mainTabPain.getUpdateDosButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateIndexesButton()
    */
   @Override
   public HasClickHandlers getUpdateIndexesButton()
   {
      return mainTabPain.getUpdateIndexesButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getVacuumIndexesButton()
    */
   @Override
   public HasClickHandlers getVacuumIndexesButton()
   {
      return mainTabPain.getVacuumIndexesButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdatePageSpeedButton()
    */
   @Override
   public HasClickHandlers getUpdatePageSpeedButton()
   {
      return mainTabPain.getUpdatePageSpeedButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateQueuesButton()
    */
   @Override
   public HasClickHandlers getUpdateQueuesButton()
   {
      return mainTabPain.getUpdateQueuesButton();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getCronGrid()
    */
   @Override
   public ListGridItem<CronEntry> getCronGrid()
   {
      return cronTabPane.getCronGrid();
   }

}
