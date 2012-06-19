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
import com.google.gwt.user.client.ui.Widget;

public class RunDebuggerView extends ViewImpl implements ReLaunchDebuggerPresenter.Display
{
   private static final String ID = "ideRunDebuggerView";

   private static final int WIDTH = 320;

   private static final int HEIGHT = 130;

   private static RunDebuggerViewUiBinder uiBinder = GWT.create(RunDebuggerViewUiBinder.class);

   interface RunDebuggerViewUiBinder extends UiBinder<Widget, RunDebuggerView>
   {
   }

   
   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   public RunDebuggerView()
   {
      super(ID, ViewType.MODAL, DebuggerExtension.LOCALIZATION_CONSTANT.debug(), null, WIDTH, HEIGHT, false);
      
      add(uiBinder.createAndBindUi(this));
      setCanBeClosed(false);
   }

   /**
    * @see org.exoplatform.ide.extension.ReLaunchDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

}
