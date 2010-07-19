package org.exoplatform.ideall.client.editor.custom;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.CheckboxItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.ui.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;

public class OpenFileWithForm extends DialogWindow implements OpenFileWithPresenter.Display
{
   private static final int WIDTH = 400;

   private static final int HEIGHT = 250;
   
   private static final String ID = "ideallOpenFileWithForm";
   
   private static final String TITLE = "Open File With";

   private EditorsListGrid editorsListGrid;

   private CheckboxItem useAsDef;

   private IButton openButton;

   private IButton cancelButton;

   private OpenFileWithPresenter presenter;

   public OpenFileWithForm(HandlerManager eventBus, ApplicationContext context)
   {

      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle(TITLE);

      createEditorListGrid();
      createChecBoxField();

      createButtonsForm();
      
      show();

      presenter = new OpenFileWithPresenter(eventBus, context);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

   }

   private void createEditorListGrid()
   {
      editorsListGrid = new EditorsListGrid();
      editorsListGrid.setHeight(135);
      editorsListGrid.setMargin(10);
      addItem(editorsListGrid);
   }

   private void createChecBoxField()
   {

      DynamicForm form = new DynamicForm();
      form.setHeight(30);
     
      //form.setMargin(5);
      form.setLayoutAlign(Alignment.LEFT);
      
      useAsDef = new CheckboxItem("Default", "&nbsp;Use as default editor");
      
      useAsDef.setAlign(Alignment.LEFT);
      //useAsDef.setShowTitle(false);
      useAsDef.setColSpan(2);
      //useAsDef.setHeight(40);
      form.setItems(useAsDef);
      
      form.setAutoWidth();

      addItem(form);
   }

   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);
      buttonsForm.setMargin(5);

      openButton = new IButton("Open");
      openButton.setWidth(90);
      openButton.setHeight(22);
      openButton.setIcon(Images.Buttons.OK);
      openButton.disable();

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(openButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      addItem(buttonsForm);
   }

   public void closeForm()
   {
      destroy();
   }
   
   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   public EditorsListGrid getEditorsListGrid()
   {
      return editorsListGrid;
   }

   public HasValue<Boolean> getIsDefaultCheckItem()
   {
      return useAsDef;
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getOkButton()
   {
      return openButton;
   }
   
   public void enableOpenButton()
   {
      openButton.enable();
   }
}
