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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 21, 2011 10:09:12 AM anya $
 *
 */
public interface Constants
{
   String NAME = "name";

   /* List */
   String URIS = "uris";

   /* int */
   String INSTANCES = "instances";

   /* int */
   String RUNNING_INSTANCES = "runningInstances";

   /* String */
   String STATE = "state";

   /* List */
   String SERVICES = "services";

   /* String */
   String VERSION = "version";

   /* List */
   String ENV = "env";

   /* {@link CloudfoundryApplicationResources} */
   String RESOURCES = "resources";

   /* {@link Staging} */
   String STAGING = "staging";

   //CloudfoundryApplicationResource
   /* int */
   String MEMORY = "memory";

   /* int */
   String DISK = "dist";

   //Staging
   String MODEL = "model";

   String STACK = "stack";
   
   String DISPLAY_NAME = "displayName";
   
   String TYPE = "type";
   
   String DESCRIPTION = "description";
   
   String LIMITS = "limits";

   String USAGE = "usage";

   String USER = "user";

   String SUPPORT = "support";

   String APPS = "apps";
}
