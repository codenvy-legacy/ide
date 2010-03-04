package org.exoplatform.ideall.client.editor.custom;

import org.exoplatform.gwtframework.ui.smartgwt.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.smartgwt.component.IButton;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.component.SimpleParameterEntryListGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class OpenFileWithForm extends DialogWindow implements OpenFileWithPresenter.Display
{
   private static final int WIDTH = 400;

   private static final int HEIGHT = 200;

   private static final String TITLE = "Open File With...";

   private SimpleParameterEntryListGrid editorsListGrid;

   private CheckboxItem useAsDef;

   private IButton okButton;

   private IButton cancelButton;
   
   private VLayout vLayout;

   public OpenFileWithForm(HandlerManager eventBus, ApplicationContext context)
   {

      super(eventBus, WIDTH, HEIGHT);
      setTitle(TITLE);

      editorsListGrid = new SimpleParameterEntryListGrid();
      useAsDef = new CheckboxItem();
      
      vLayout = new VLayout();
      vLayout.setMargin(2);
      
      vLayout.addMember(editorsListGrid);
      
      DynamicForm form = new DynamicForm();
      form.setItems(useAsDef);
      
      vLayout.addMember(form);
      createButtonsForm();
      
      addItem(vLayout);
      show();
      
      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

   }
   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setMargin(3);
      buttonsForm.setPadding(3);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      okButton = new IButton("Send");
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(okButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      vLayout.addMember(buttonsForm);
   }
   
   public void closeForm()
   {
      destroy();
   }
}
