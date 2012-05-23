package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.user.client.ui.Button;

import com.google.gwt.uibinder.client.UiField;

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;

public class AppEngineProjectView extends ViewImpl implements AppEngineProjectPresenter.Display
{
   private static final String ID = "ideAppEngineProjectView";

   private static final String TITLE = "";

   private static final int WIDTH = 600;

   private static final int HEIGHT = 400;

   private static AppEngineProjectViewUiBinder uiBinder = GWT.create(AppEngineProjectViewUiBinder.class);

   @UiField
   ImageButton closeButton;

   private MainTabPain mainTabPain;

   // @UiField
   Button rollbackApplicationButton;

   // @UiField
   Button getLogsButton;

   // @UiField
   Button updateDosButton;

   // @UiField
   Button updateIndexesButton;

   // @UiField
   Button vacuumIndexesButton;

   // @UiField
   Button updatePageSpeedButton;

   // @UiField
   Button updateQueuesButton;

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
      applicationTabPanel.addTab("mainTab", null, "Application", mainTabPain, false);
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
      // TODO Auto-generated method stub
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
      // TODO Auto-generated method stub
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
      return getLogsButton;
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
      return rollbackApplicationButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateCronButton()
    */
   @Override
   public HasClickHandlers getUpdateCronButton()
   {
      // TODO
      return null;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateDosButton()
    */
   @Override
   public HasClickHandlers getUpdateDosButton()
   {
      return updateDosButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateIndexesButton()
    */
   @Override
   public HasClickHandlers getUpdateIndexesButton()
   {
      return updateIndexesButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getVacuumIndexesButton()
    */
   @Override
   public HasClickHandlers getVacuumIndexesButton()
   {
      return vacuumIndexesButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdatePageSpeedButton()
    */
   @Override
   public HasClickHandlers getUpdatePageSpeedButton()
   {
      return updatePageSpeedButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.AppEngineProjectPresenter.Display#getUpdateQueuesButton()
    */
   @Override
   public HasClickHandlers getUpdateQueuesButton()
   {
      return updateQueuesButton;
   }

}
