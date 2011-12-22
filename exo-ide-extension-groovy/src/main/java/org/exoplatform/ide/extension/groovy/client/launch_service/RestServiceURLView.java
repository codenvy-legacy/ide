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
package org.exoplatform.ide.extension.groovy.client.launch_service;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.util.ImageFactory;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.groovy.client.Images;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RestServiceURLView extends ViewImpl implements
   org.exoplatform.ide.extension.groovy.client.launch_service.RestServiceURLPresenter.Display
{

   private static final int WIDTH = 500;

   private static final int HEIGHT = 160;

   private static final String TITLE = "REST Service URL";

   private static final String ID = "ideGetRestServiceURLForm";

   private static final String ID_OK = "ideGetRestServiceURLFormOkButton";

   private static final String NAME_URL = "ideGetItemURLFormURLField";

   private TextField urlField;

   private ImageButton okButton;

   private VerticalPanel mainLayout;

   public RestServiceURLView()
   {
      super(ID, "modal", TITLE, ImageFactory.getImage("url"), WIDTH, HEIGHT);

      mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

      createFieldForm();
      createButtons();

      add(mainLayout);
   }

   private void createFieldForm()
   {
      DynamicForm paramsForm = new DynamicForm();
      paramsForm.setWidth(450);
      paramsForm.setPadding(10);

      urlField = new TextField(NAME_URL, "REST Service URL:");
      urlField.setTitleOrientation(TitleOrientation.TOP);
      urlField.setWidth(450);
      urlField.setHeight(20);

      paramsForm.add(urlField);
      urlField.focus();

      mainLayout.add(paramsForm);
      mainLayout.setCellVerticalAlignment(paramsForm, VerticalPanel.ALIGN_MIDDLE);
   }

   private void createButtons()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight((22 + 20) + "px");
      buttonsLayout.setSpacing(5);

      okButton = new ImageButton("OK");
      okButton.setWidth("90px");
      okButton.setHeight("22px");
      okButton.setImage(new Image(Images.Buttons.OK));
      okButton.setButtonId(ID_OK);

      buttonsLayout.add(okButton);

      mainLayout.add(buttonsLayout);
   }

   @Override
   public void setURL(String url)
   {
      urlField.setValue(url);

      new Timer()
      {
         @Override
         public void run()
         {
            urlField.selectValue();
         }
      }.schedule(500);
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

}
