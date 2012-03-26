package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;


public class RunDebuggerView extends ViewImpl implements RunDebuggerPresenter.Display
{
   private static final String ID = "ideRunDebuggerView";

   private static final int WIDTH = 420;

   private static final int HEIGHT = 150;


   private static RunDebuggerViewUiBinder uiBinder = GWT.create(RunDebuggerViewUiBinder.class);

   interface RunDebuggerViewUiBinder extends UiBinder<Widget, RunDebuggerView>
   {
   }

   @UiField
   TextInput hostField;

   @UiField
   TextInput portField;

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
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasClickHandlers getRunButton()
   {
      return runButton;
   }

   /**
    * @see org.exoplatform.ide.extension.RunDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.RunDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getApplicationNameField()
    */
   @Override
   public HasValue<String> getHostField()
   {
      return hostField;
   }

   /**
    * @see org.exoplatform.ide.extension.RunDebuggerPresenter.client.create.CreateApplicationPresenter.Display#getWorkDirLocationField()
    */
   @Override
   public HasValue<String> getPortField()
   {
      return portField;
   }

   @Override
   public void enableRunButton(boolean enable)
   {
      runButton.setEnabled(enable);
   }

}
