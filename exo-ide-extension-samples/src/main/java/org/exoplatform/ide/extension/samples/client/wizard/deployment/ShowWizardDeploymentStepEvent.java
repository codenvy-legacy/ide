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
package org.exoplatform.ide.extension.samples.client.wizard.deployment;

import org.exoplatform.ide.extension.samples.client.ProjectProperties;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event, to call Step 3 of Wizard for creation Java project (Deployment)
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ShowDeploymentWizardEvent.java Sep 7, 2011 5:37:50 PM vereshchaka $
 *
 */
public class ShowWizardDeploymentStepEvent extends GwtEvent<ShowWizardDeploymentStepHandler>
{
   
   private ProjectProperties projectProperties;
   
   public ShowWizardDeploymentStepEvent()
   {
   }
   
   public ShowWizardDeploymentStepEvent(ProjectProperties projectProperties)
   {
      this.projectProperties = projectProperties;
   }
   
   public static final GwtEvent.Type<ShowWizardDeploymentStepHandler> TYPE = new GwtEvent.Type<ShowWizardDeploymentStepHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ShowWizardDeploymentStepHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ShowWizardDeploymentStepHandler handler)
   {
      handler.onShowDeploymentWizard(this);
   }
   
   /**
    * @return the projectProperties
    */
   public ProjectProperties getProjectProperties()
   {
      return projectProperties;
   }

}
