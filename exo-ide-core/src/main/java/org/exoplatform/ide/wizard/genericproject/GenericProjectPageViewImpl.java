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
package org.exoplatform.ide.wizard.genericproject;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * GenericProjectPageViewImpl is the view of generic project page wizard.
 * Provides entering project's name for new generic project.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 *
 */
public class GenericProjectPageViewImpl implements GenericProjectPageView
{
   private FlowPanel mainPanel;

   private TextBox projectName;

   private ActionDelegate delegate;

   /**
    * Create generic project page view
    * 
    * @param resources
    */
   public GenericProjectPageViewImpl(GenericProjectWizardResource resources)
   {
      mainPanel = new FlowPanel();
      mainPanel.setStyleName(resources.genericProjectWizardCss().genericProjectWizard());
      
      Label projNameText = new Label("Project name:");
      projNameText.setStyleName(resources.genericProjectWizardCss().component());
      mainPanel.add(projNameText);

      projectName = new TextBox();
      projectName.setStyleName(resources.genericProjectWizardCss().component());
      //checks entered project's name and updates navigation buttons
      projectName.addKeyUpHandler(new KeyUpHandler()
      {        
         public void onKeyUp(KeyUpEvent event)
         {
            delegate.checkProjectName();
         }
      });
      
      mainPanel.add(projectName);
   }

   /**
    * {@inheritDoc}
    */
   public Widget asWidget()
   {
      return mainPanel;
   }

   /**
    * {@inheritDoc}
    */
   public String getProjectName()
   {
      return projectName.getText();
   }

   /**
    * {@inheritDoc}
    */
   public void setCheckProjNameDelegate(ActionDelegate delegate)
   {
      this.delegate = delegate;
   }
}