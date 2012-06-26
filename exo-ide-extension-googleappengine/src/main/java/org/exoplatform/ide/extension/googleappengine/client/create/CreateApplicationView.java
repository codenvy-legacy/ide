package org.exoplatform.ide.extension.googleappengine.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "exoCreateApplicationView";

   private static final String DEPLOY_BUTTON_ID = "exoCreateApplicationViewDeployButton";

   private static final String CANCEL_BUTTON_ID = "exoCreateApplicationViewCancelButton";

   private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationViewTitle();

   private static final int WIDTH = 545;

   private static final int HEIGHT = 180;

   private static final String GOOGLE_APP_ENGINE_URL = "https://appengine.google.com/start/createapp";

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }

   @UiField
   ImageButton deployButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   Anchor createButton;

   @UiField
   Label instructionLabel;

   public CreateApplicationView()
   {
      super(ID, ViewType.MODAL, TITLE, new Image(GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      deployButton.setButtonId(DEPLOY_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getDeployButton()
    */
   @Override
   public HasClickHandlers getDeployButton()
   {
      return deployButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public HasClickHandlers getCreateButton()
   {
      return createButton;
   }

   @Override
   public void enableDeployButton(boolean enable)
   {
      deployButton.setEnabled(enable);
   }

   @Override
   public void changeCreateButtonVisability(boolean visible)
   {
      createButton.setVisible(visible);
   }

   @Override
   public void setUserInstructions(String instructions)
   {
      instructionLabel.setValue(instructions);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#setCreateLink(java.lang.String)
    */
   @Override
   public void setCreateLink(String href)
   {
      createButton.setHref(href);
   }
}
