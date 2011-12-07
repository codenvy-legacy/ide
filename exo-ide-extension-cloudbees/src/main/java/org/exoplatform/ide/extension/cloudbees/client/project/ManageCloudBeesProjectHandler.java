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
package org.exoplatform.ide.extension.cloudbees.client.project;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link ManageCloudBeesProjectEvent} event.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 5, 2011 12:34:43 PM anya $
 *
 */
public interface ManageCloudBeesProjectHandler extends EventHandler
{
   /**
    * Perform actions, when user tries to manage project deployed to CloudBees.
    * 
    * @param event
    */
   void onManageCloudBeesProject(ManageCloudBeesProjectEvent event);
}
