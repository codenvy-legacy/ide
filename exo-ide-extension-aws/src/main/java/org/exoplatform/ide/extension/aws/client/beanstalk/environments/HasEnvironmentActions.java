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
package org.exoplatform.ide.extension.aws.client.beanstalk.environments;

import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: HasEnvironmentActions.java Sep 28, 2012 4:57:58 PM azatsarynnyy $
 *
 */
public interface HasEnvironmentActions
{
   void addViewConfigurationHandler(SelectionHandler<EnvironmentInfo> handler);

   void addRestartHandler(SelectionHandler<EnvironmentInfo> handler);

   void addTerminateHandler(SelectionHandler<EnvironmentInfo> handler);

   void addRebuildHandler(SelectionHandler<EnvironmentInfo> handler);
}
