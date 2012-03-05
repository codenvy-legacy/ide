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

import org.eclipse.jdt.client.event.CleanProjectEvent;
import org.eclipse.jdt.client.event.CleanProjectHandler;
import org.eclipse.jdt.client.event.ParseActiveFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  10:51:51 AM Mar 5, 2012 evgen $
 *
 */
public class CleanProjectCommandHandler implements CleanProjectHandler
{

   /**
    * 
    */
   public CleanProjectCommandHandler()
   {
      IDE.addHandler(CleanProjectEvent.TYPE, this);
   }
   
   /**
    * @see org.eclipse.jdt.client.event.CleanProjectHandler#onCleanProject(org.eclipse.jdt.client.event.CleanProjectEvent)
    */
   @Override
   public void onCleanProject(CleanProjectEvent event)
   {
      TypeInfoStorage.get().clear();
      IDE.fireEvent(new ParseActiveFileEvent());
   }

}
