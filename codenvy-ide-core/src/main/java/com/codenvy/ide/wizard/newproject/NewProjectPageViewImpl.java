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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.api.paas.PaaS;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
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

   @UiField(provided = true)
   Grid paases;

   private ActionDelegate delegate;

   private ToggleButton selectedProjectType;

   private ToggleButton selectedPaaS;

   private JsonArray<ToggleButton> paasButton = JsonCollections.createArray();

   private JsonArray<PaaS> availablePaaS;

   interface NewProjectViewImplUiBinder extends UiBinder<Widget, NewProjectPageViewImpl>
   {
   }

   /**
    * Create view with given instance of resources and list of wizard's data
    * 
    * @param wizardDatas aggregate information about available wizards
    */
   public NewProjectPageViewImpl(JsonArray<NewProjectWizardData> wizardDatas, JsonArray<PaaS> paases)
   {
      createTechnologiesTable(wizardDatas);
      createPaasTable(paases);
      availablePaaS = paases;

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
         final NewProjectWizardData wizardData = wizardDatas.get(i);

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
               if (selectedProjectType != btn)
               {
                  if (selectedProjectType != null)
                  {
                     selectedProjectType.setDown(false);
                  }
                  selectedProjectType = btn;

                  // TODO need improvement if it is possible
                  JsonArray<String> natures = wizardData.getNatures();
                  for (int i = 0; i < availablePaaS.size(); i++)
                  {
                     PaaS paas = availablePaaS.get(i);
                     JsonArray<String> paases = paas.getRequiredProjectTypes();
                     ToggleButton button = paasButton.get(i);
                     button.setEnabled(false);
                     button.setDown(false);

                     // TODO constant
                     if (!paas.getId().equals("None"))
                     {
                        for (int j = 0; j < natures.size(); j++)
                        {
                           String nature = natures.get(j);
                           if (paases.contains(nature))
                           {
                              button.setEnabled(true);
                           }
                        }
                     }
                     else
                     {
                        button.setEnabled(true);
                     }
                  }

                  selectedPaaS = null;

                  delegate.onProjectTypeSelected(id);
               }
               else
               {
                  selectedProjectType.setDown(true);
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

   private void createPaasTable(JsonArray<PaaS> paases)
   {
      this.paases = new Grid(2, paases.size());
      HTMLTable.CellFormatter formatter = this.paases.getCellFormatter();

      //create button for each paas
      for (int i = 0; i < paases.size(); i++)
      {
         PaaS paas = paases.get(i);

         ImageResource icon = paas.getImage();
         final ToggleButton btn;
         if (icon != null)
         {
            btn = new ToggleButton(new Image(icon));
         }
         else
         {
            btn = new ToggleButton();
         }
         btn.setSize("48px", "48px");
         btn.setEnabled(false);

         final int id = i;
         btn.addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               //if user click on other button (the button isn't selected) then new button changes to selected.
               //otherwise the button must be selected.
               if (selectedPaaS != btn)
               {
                  if (selectedPaaS != null)
                  {
                     selectedPaaS.setDown(false);
                  }
                  selectedPaaS = btn;

                  delegate.onPaaSSelected(id);
               }
               else
               {
                  selectedPaaS.setDown(true);
               }
            }
         });
         this.paases.setWidget(0, i, btn);
         formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

         Label title = new Label(paas.getTitle());
         this.paases.setWidget(1, i, title);
         formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);

         paasButton.add(btn);
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