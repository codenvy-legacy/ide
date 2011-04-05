/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.chromattic.client.ui;

import org.exoplatform.gwtframework.ui.client.component.Align;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.extension.chromattic.client.Images;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * View for generating node type definition.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class GenerateNodeTypeForm extends DialogWindow implements GenerateNodeTypePresenter.Display
{
   
   public static final int WIDTH = 450;

   public static final int HEIGHT = 160;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   public static final String ID = "ideGenerateNodeTypeForm";

   private final String TITLE = "Generate Node Type";

   //IDs for Selenium tests:

   private final String ID_CANCEL_BUTTON = "ideGenerateNodeTypeFormCancelButton";

   private final String ID_GENERATE_BUTTON = "ideGenerateNodeTypeFormGenerateButton";

   private final String ID_DYNAMIC_FORM = "ideGenerateNodeTypeFormDynamicForm";

   private final String ID_FORMAT_FIELD = "ideGenerateNodeTypeFormFormatField";

   private SelectItem formatField;

   private IButton generateButton;

   private IButton cancelButton;

   /**
    * @param eventBus handler manager
    */
   public GenerateNodeTypeForm(HandlerManager eventBus)
   {
      super(WIDTH, HEIGHT, ID);
      setTitle(TITLE);
      //TODO setCanDragResize(true);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(25);
      mainLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

      mainLayout.add(createMainForm());
      mainLayout.add(createButtonLayout());

      add(mainLayout);
      show();
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.window.Window#destroy()
    */
   @Override
   public void destroy()
   {
      //eventBus.fireEvent(new ViewClosedEvent(this));
      System.out.println("eventBus.fireEvent(new ViewClosedEvent(this));");
      super.destroy();
   }

   /**
    * Creates main form.
    * 
    * @return {@link VerticalPanel}
    */
   private VerticalPanel createMainForm()
   {
      VerticalPanel form = new VerticalPanel();
      form.getElement().setId(ID_DYNAMIC_FORM);

      formatField = new SelectItem();
      formatField.setName(ID_FORMAT_FIELD);
      formatField.setTitle("<nobr>" + "Node type format:" + "</nobr>");
      formatField.setShowTitle(true);
      formatField.setTitleAlign(Align.LEFT);
      formatField.setWidth(260);

      form.add(formatField);
      return form;
   }

   /**
    * Creates button's layout.
    * 
    * @return {@link HorizontalPanel} layout with buttons
    */
   private HorizontalPanel createButtonLayout()
   {
      HorizontalPanel hLayout = new HorizontalPanel();
      hLayout.setSpacing(5);
      hLayout.setHeight(BUTTON_HEIGHT + "px");

      generateButton = createButton(ID_GENERATE_BUTTON, "Generate", Images.Buttons.OK);
      cancelButton = createButton(ID_CANCEL_BUTTON, "Cancel", Images.Buttons.CANCEL);

      hLayout.add(generateButton);
      hLayout.add(cancelButton);
      return hLayout;
   }

   /**
    * Created button.
    * 
    * @param id button's id
    * @param title button's display title
    * @param icon button's icon
    * @return {@link IButton} created button
    */
   private IButton createButton(String id, String title, String icon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      button.setIcon(icon);
      return button;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#closeView()
    */
   @Override
   public void closeView()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#getGenerateButton()
    */
   @Override
   public HasClickHandlers getGenerateButton()
   {
      return generateButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#getNodeTypeFormat()
    */
   @Override
   public HasValue<String> getNodeTypeFormat()
   {
      return formatField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.GenerateNodeTypePresenter.Display#setNodeTypeFormatValues()
    */
   @Override
   public void setNodeTypeFormatValues(String[] values)
   {
      formatField.setValueMap(values);
      if (values.length > 0)
      {
         formatField.setValue(values[0]);
      }
   }
}
