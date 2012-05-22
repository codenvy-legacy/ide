package org.exoplatform.ide.extension.googleappengine.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.googleappengine.client.GAEClientBundle;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;

public class CreateApplicationView extends ViewImpl implements CreateApplicationPresenter.Display
{
   private static final String ID = "exoCreateApplicationView";

   private static final String READY_BUTTON_ID = "exoCreateApplicationViewReadyButton";

   private static final String CANCEL_BUTTON_ID = "exoCreateApplicationViewCancelButton";

   private static final String TITLE = GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationViewTitle();

   private static final int WIDTH = 1040;

   private static final int HEIGHT = 550;

   private static final String GOOGLE_APP_ENGINE_URL = "https://appengine.google.com/";

   private static CreateApplicationViewUiBinder uiBinder = GWT.create(CreateApplicationViewUiBinder.class);

   interface CreateApplicationViewUiBinder extends UiBinder<Widget, CreateApplicationView>
   {
   }

   @UiField
   Frame frame;

   @UiField
   CheckBox deployField;

   @UiField
   ImageButton readyButton;

   @UiField
   ImageButton cancelButton;

   public CreateApplicationView()
   {
      super(ID, ViewType.MODAL, TITLE, new Image(GAEClientBundle.INSTANCE.googleAppEngine()), WIDTH, HEIGHT);

      add(uiBinder.createAndBindUi(this));
      readyButton.setButtonId(READY_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);
      frame.getElement().setAttribute("frameborder", "no");

      frame.setUrl(GOOGLE_APP_ENGINE_URL);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getAppEngineFrameContent()
    */
   @Override
   public String getAppEngineFrameContent()
   {
      return frame.getElement().getInnerText();
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#setAppEngineFrameContent(java.lang.String)
    */
   @Override
   public void setAppEngineFrameContent(String content)
   {
      // TODO
   };

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getLoadHandler()
    */
   @Override
   public HasLoadHandlers getLoadHandler()
   {
      return frame;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getReadyButton()
    */
   @Override
   public HasClickHandlers getReadyButton()
   {
      return readyButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationPresenter.Display#getDeployValue()
    */
   @Override
   public HasValue<Boolean> getDeployValue()
   {
      return deployField;
   }
}
