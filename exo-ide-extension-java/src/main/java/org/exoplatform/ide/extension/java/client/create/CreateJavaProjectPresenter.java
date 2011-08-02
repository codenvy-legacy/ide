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
package org.exoplatform.ide.extension.java.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.java.client.JavaClientService;
import org.exoplatform.ide.extension.java.client.JavaExtension;
import org.exoplatform.ide.extension.java.client.MavenResponseCallback;
import org.exoplatform.ide.extension.java.client.ProjectType;
import org.exoplatform.ide.extension.java.shared.MavenResponse;

import java.util.List;

/**
 * Presenter for create java project view.<p/>
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CreateJavaProjectPresenter.java Jun 22, 2011 9:52:37 AM vereshchaka $
 *
 */
public class CreateJavaProjectPresenter implements ViewClosedHandler, CreateJavaProjectHandler, ItemsSelectedHandler,
   CleanProjectHandler
{

   interface Display extends IsView
   {
      /**
       * Get create button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCreateButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get application name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getProjectNameField();

      /**
       * Set focus in project name field.
       */
      void focusInProjectNameField();

      /**
       * Disable create button.
       */
      void disableCreateButton();

      /**
       * Enable create button.
       */
      void enableCreateButton();
   }

   private static final String DEFAULT_PROJECT_NAME = JavaExtension.LOCALIZATION_CONSTANT
      .createJavaProjectDefaultName();

   private Display display;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;

   public CreateJavaProjectPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;

      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CreateJavaProjectEvent.TYPE, this);
      eventBus.addHandler(CleanProjectEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCreateProject();
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

      display.getProjectNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue() != null && !event.getValue().isEmpty())
            {
               display.enableCreateButton();
            }
            else
            {
               display.disableCreateButton();
            }
         }
      });

   }

   private void doCreateProject()
   {
      final String name = display.getProjectNameField().getValue();
      Item item = selectedItems.get(0);
      String workDir = item.getHref();
      if (item instanceof File)
      {
         workDir = workDir.substring(0, workDir.lastIndexOf("/") + 1);
      }
      
      String groupId = name;
      String artifactId = name;
      
      String archetypeGroupId;
      String archetypeArtifactId;

      if (projectType == ProjectType.SPRING) {
         archetypeGroupId = "org.springframework.osgi";
         archetypeArtifactId = "spring-osgi-bundle-archetype";
      } else {
         archetypeGroupId = "org.apache.maven.archetypes";
         archetypeArtifactId = "maven-archetype-webapp";
      }
      
      JavaClientService.getInstance().createJavaProject(groupId, artifactId, archetypeGroupId, archetypeArtifactId, workDir, new MavenResponseCallback(eventBus)
      {
         @Override
         protected void onSuccess(MavenResponse result)
         {
            eventBus.fireEvent(new RefreshBrowserEvent());
            IDE.getInstance().closeView(display.asView().getId());
            eventBus.fireEvent(new OutputEvent(JavaExtension.LOCALIZATION_CONSTANT.createJavaProjectSuccess(name),
               Type.INFO));
         }
      });

   }

   private void openView()
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.focusInProjectNameField();
         display.getProjectNameField().setValue(DEFAULT_PROJECT_NAME);
      }
   }
   
   private ProjectType projectType;

   /**
    * @see org.exoplatform.ide.extension.java.client.create.CreateJavaProjectHandler#onCreateJavaProject(org.exoplatform.ide.extension.java.client.create.CreateJavaProjectEvent)
    */
   @Override
   public void onCreateJavaProject(CreateJavaProjectEvent event)
   {
      projectType = event.getProjectType();
      openView();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.java.client.create.CleanProjectHandler#onCleanProject(org.exoplatform.ide.extension.java.client.create.CleanProjectEvent)
    */
   @Override
   public void onCleanProject(final CleanProjectEvent event)
   {
      JavaClientService.getInstance().cleanProject(event.getProjectDir(), new MavenResponseCallback(eventBus)
      {
         @Override
         protected void onSuccess(MavenResponse result)
         {
            eventBus.fireEvent(new RefreshBrowserEvent());
            eventBus.fireEvent(new OutputEvent(JavaExtension.LOCALIZATION_CONSTANT.cleanJavaProjectSuccess(event
               .getProjectDir()), Type.INFO));
         }
      });
   }
}
