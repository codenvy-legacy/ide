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
package org.exoplatform.ide.client.project.deploy;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployProjectToPaasEvent.java Dec 1, 2011 5:03:21 PM vereshchaka $
 */
public class DeployProjectToPaasEvent extends GwtEvent<DeployProjectToPaasHandler>
{

   private String projectName;

   private String projectType;

   private String templateName;

   public DeployProjectToPaasEvent(String projectName, String projectType, String templateName)
   {
      this.projectName = projectName;
      this.projectType = projectType;
      this.templateName = templateName;
   }

   public static final GwtEvent.Type<DeployProjectToPaasHandler> TYPE = new GwtEvent.Type<DeployProjectToPaasHandler>();

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<DeployProjectToPaasHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(DeployProjectToPaasHandler handler)
   {
      handler.onDeployProjectToPaas(this);
   }

   /**
    * @return the projectName
    */
   public String getProjectName()
   {
      return projectName;
   }

   /**
    * @return the projectType
    */
   public String getProjectType()
   {
      return projectType;
   }

   /**
    * @return the templateName
    */
   public String getTemplateName()
   {
      return templateName;
   }

}
