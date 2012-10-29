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
package org.eclipse.jdt.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: LocalizationConstant.java Oct 26, 2012 vetal $
 *
 */
public interface LocalizationConstant extends Messages
{
   /*
    * Controls
    */
   @Key("control.updateDependency.id")
   String updateDependencyControlId();

   @Key("control.updateDependency.title")
   String updateDependencyControlTitle();

   @Key("control.updateDependency.prompt")
   String updateDependencyControlPrompt();

   /*
    * Messages
    */
   @Key("messages.updateDependency.started")
   String updateDependencyStarted(String project);

   @Key("messages.updateDependency.finished")
   String updateDependencyFinished(String project);
}
