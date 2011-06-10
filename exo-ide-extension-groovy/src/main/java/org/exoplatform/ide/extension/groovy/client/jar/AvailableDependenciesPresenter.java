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
package org.exoplatform.ide.extension.groovy.client.jar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.groovy.client.service.groovy.GroovyService;
import org.exoplatform.ide.extension.groovy.shared.Jar;
import org.exoplatform.ide.extension.groovy.shared.Attribute;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AvailableDependenciesPresenter implements ShowAvailableDependencies, ViewClosedHandler
{

   public interface Display extends IsView
   {

      /**
       * Returns the list grid where the list of JAR files can be displayed.
       * 
       * @return
       */
      ListGridItem<Jar> getJarsListGrid();

      /**
       * Returns the list grid where displayed the list of attributes of selected JAR file.
       * 
       * @return
       */
      ListGridItem<Attribute> getAttributesGrid();

      /**
       * Returns OK button.
       * 
       * @return
       */
      HasClickHandlers getOkButton();

   }

   /**
    * Implementation of Display interface.
    */
   private Display display;

   /**
    * List of JAR files received from the server.
    */
   private List<Jar> jars;

   /**
    * Currently selected JAR file in the list grid.
    */
   private Jar selectedJAR;

   /**
    * Creates a new instance of this presenter.
    * 
    * @param eventBus
    */
   public AvailableDependenciesPresenter(HandlerManager eventBus)
   {
      eventBus.fireEvent(new RegisterControlEvent(new ShowAvailableDependenciesControl()));
      eventBus.addHandler(ShowAvailableDependenciesEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.groovy.client.ShowAvailableDependencies.ShowJarDiscoveryHandler#onShowAvailableDependencies(org.exoplatform.ide.extension.groovy.client.ShowAvailableDependenciesEvent.ShowJarDiscoveryEvent)
    */
   @Override
   public void onShowAvailableDependencies(ShowAvailableDependenciesEvent event)
   {
      if (display != null)
      {
         return;
      }

      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   /**
    * Binds display and refreshes the list of JAR files.
    */
   private void bindDisplay()
   {
      selectedJAR = null;

      display.getJarsListGrid().addSelectionHandler(jarsListGridSelectionHandler);

      display.getOkButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      refreshListOfJARs();
   }

   /**
    * Refreshes the list of JAR's
    */
   private void refreshListOfJARs()
   {
      GroovyService.getInstance().getAvailableJarLibraries(new AsyncRequestCallback<List<Jar>>()
      {
         @Override
         protected void onSuccess(List<Jar> result)
         {
            jars = result;
            Collections.sort(jars, jarsComparator);

            display.getJarsListGrid().setValue(jars);
            display.getAttributesGrid().setValue(new ArrayList<Attribute>());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            exception.printStackTrace();
            String message = "Can't get list of JAR packages.";
            fireEvent(new ExceptionThrownEvent(message));
         }
      });
   }

   /**
    * Handler when the user selects JAR file in the list.  
    */
   SelectionHandler<Jar> jarsListGridSelectionHandler = new SelectionHandler<Jar>()
   {
      @Override
      public void onSelection(SelectionEvent<Jar> event)
      {
         selectedJAR = event.getSelectedItem();
         Collections.sort(selectedJAR.getAttributes(), attributesComparator);
         display.getAttributesGrid().setValue(selectedJAR.getAttributes());
      }
   };

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
    * Comparator for sorting list of JAR files.
    */
   private Comparator<Jar> jarsComparator = new Comparator<Jar>()
   {
      @Override
      public int compare(Jar jar1, Jar jar2)
      {
         String name1 = jar1.getPath();
         if (name1.indexOf("/") >= 0)
         {
            name1 = name1.substring(name1.lastIndexOf("/") + 1);
         }

         String name2 = jar2.getPath();
         if (name2.indexOf("/") >= 0)
         {
            name2 = name2.substring(name2.lastIndexOf("/") + 1);
         }

         return name1.compareTo(name2);
      }
   };

   /**
    * Comparator for sorting list of properties.
    */
   private Comparator<Attribute> attributesComparator = new Comparator<Attribute>()
   {
      @Override
      public int compare(Attribute property1, Attribute property2)
      {
         return property1.getName().compareTo(property2.getName());
      }
   };

}
