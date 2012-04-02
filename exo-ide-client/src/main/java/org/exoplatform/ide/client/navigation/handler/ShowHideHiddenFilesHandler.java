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
package org.exoplatform.ide.client.navigation.handler;

import com.google.gwt.event.shared.EventHandler;

import org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent;

/**
 * Class, that implements this handler, will listen to click on Show/Hide Hidden Files control in View menu.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHideHiddenFilesHandler.java Mar 30, 2012 12:31:54 PM azatsarynnyy $
 *
 */
public interface ShowHideHiddenFilesHandler extends EventHandler
{
   /**
    * @param event event generated after pressing on Show/Hide Hidden Files control on View menu
    */
   void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event);
}
