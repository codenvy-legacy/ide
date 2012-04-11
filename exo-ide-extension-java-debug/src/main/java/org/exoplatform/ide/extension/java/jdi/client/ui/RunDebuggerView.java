package org.exoplatform.ide.extension.java.jdi.client.ui;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.ReLaunchDebuggerPresenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class RunDebuggerView extends ViewImpl implements ReLaunchDebuggerPresenter.Display
{
   private static final String ID = "ideRunDebuggerView";

   private static final int WIDTH = 420;

   private static final int HEIGHT = 150;

   private static RunDebuggerViewUiBinder uiBinder = GWT.create(RunDebuggerViewUiBinder.class);

   interface RunDebuggerViewUiBinder extends UiBinder<Widget, RunDebuggerView>
   {
   }

   @UiField
   FlowPanel webUrl;

   /**
    * Create application button.
    */
   @UiField
   ImageButton runButton;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public RunDebuggerView()
   {
      super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.debug(), null, WIDTH, HEIGHT, false);
      //      webUrl.se
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasClickHandlers getRunButton()
   {
      return runButton;
   }

   /**
    * @see org.exoplatform.ide.extension.ReLaunchDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setAppWebUrl(String webUrl)
   {
      if (!webUrl.startsWith("http"))
      {
         webUrl = "http://" + webUrl;
      }
      this.webUrl.getElement().setInnerHTML("<a href=\"" + webUrl + "\" target=\"_blank\">Your application starting here</a>");
   }

}
