/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EditPropertyPresenter implements ViewClosedHandler
{
   
   public interface Display extends IsView
   {
      
      HasValue<String> getNameField();
      
      HasValue<String> getValueField();
      
      HasClickHandlers getOkButton();
      
      void setOkButtonText(String text);
      
      HasClickHandlers getCancelButton();
      
   }
   
   private Display display;
   
   private Property currentProperty;
   
   public EditPropertyPresenter() {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }
   
   public void editProperty(Property property) {
      ensureViewOpened();
      
      currentProperty = property;
      display.setOkButtonText("Ok");
      display.getNameField().setValue(currentProperty.getName());
      display.getValueField().setValue(getPropertyValue(currentProperty.getValue()));
   }
   
   private String getPropertyValue(List valueList) {
      String value = "";
      for (Object v : valueList) {
         if (!value.isEmpty()) {
            value += "<br>";
         }
         
         value += v;
      }
      
      return value;      
   }
   
   public void createProperty() {
      ensureViewOpened();
      
      display.setOkButtonText("Create");
      display.getNameField().setValue("");
      display.getValueField().setValue("");
   }
   
   private void ensureViewOpened() {
      if (display != null) {
         return;
      }
      
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }
   
   private void bindDisplay() {
      display.getOkButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            editComplete();
         }
      });
      
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
      
   }
   
   private void editComplete() {
      IDE.getInstance().closeView(display.asView().getId());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display) {
         display = null;
      }
      
   }

}
