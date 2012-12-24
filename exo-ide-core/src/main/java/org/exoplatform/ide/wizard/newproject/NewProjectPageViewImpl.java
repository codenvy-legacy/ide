/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.wizard.newproject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.json.JsonArray;

/**
 * NewProjectPageViewImpl is the view of new project page wizard.
 * Provides selecting type of technology for creating new project.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectPageViewImpl extends Composite implements NewProjectPageView
{
   private static NewProjectViewImplUiBinder uiBinder = GWT.create(NewProjectViewImplUiBinder.class);

   @UiField(provided = true)
   Grid technologies;

   private ActionDelegate delegate;

   private ToggleButton selectedButton;

   interface NewProjectViewImplUiBinder extends UiBinder<Widget, NewProjectPageViewImpl>
   {
   }

   /**
    * Create view with given instance of resources and list of wizard's data
    * 
    * @param wizardDatas aggregate information about available wizards
    */
   public NewProjectPageViewImpl(JsonArray<NewProjectWizardData> wizardDatas)
   {
      createTechnologiesTable(wizardDatas);

      initWidget(uiBinder.createAndBindUi(this));
   }

   /**
    * Create table with available technologies.
    * 
    * @param wizardDatas available technologies
    */
   private void createTechnologiesTable(JsonArray<NewProjectWizardData> wizardDatas)
   {
      //create table where contains kind of technology
      technologies = new Grid(2, wizardDatas.size());
      HTMLTable.CellFormatter formatter = technologies.getCellFormatter();

      //create button for each available wizard
      for (int i = 0; i < wizardDatas.size(); i++)
      {
         NewProjectWizardData wizardData = wizardDatas.get(i);

         Image icon = wizardData.getIcon();
         final ToggleButton btn;
         if (icon != null)
         {
            btn = new ToggleButton(icon);
         }
         else
         {
            btn = new ToggleButton();
         }
         btn.setSize("48px", "48px");

         final int id = i;
         btn.addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               //if user click on other button (the button isn't selected) then new button changes to selected.
               //otherwise the button must be selected.
               if (selectedButton != btn)
               {
                  if (selectedButton != null)
                  {
                     selectedButton.setDown(false);
                  }
                  selectedButton = btn;

                  delegate.onButtonPressed(id);
               }
               else
               {
                  selectedButton.setDown(true);
               }
            }
         });
         technologies.setWidget(0, i, btn);
         formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

         Label title = new Label(wizardData.getTitle());
         technologies.setWidget(1, i, title);
         formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);
      }
   }

   /**
    * {@inheritDoc}
    */
   public void setDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }
}