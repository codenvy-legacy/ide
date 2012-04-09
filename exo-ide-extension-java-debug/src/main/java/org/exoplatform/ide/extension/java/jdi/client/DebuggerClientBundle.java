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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface DebuggerClientBundle extends ClientBundle
{
   DebuggerClientBundle INSTANCE = GWT.<DebuggerClientBundle> create(DebuggerClientBundle.class);
   /*
    * Buttons
    */
   @Source("org/exoplatform/ide/extension/java/jdi/images/resume_co.gif")
   ImageResource resumeButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/disconnect_co.gif")
   ImageResource disconnectButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/remove_all_breakpoints.gif")
   ImageResource removeAllBreakpointsButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/rundebug.gif")
   ImageResource runDebugButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/debugtt_obj.gif")
   ImageResource addBreakPointButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/export_brkpts.gif")
   ImageResource breakPointsIcon();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/update.gif")
   ImageResource checkEvents();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/cancel.png")
   ImageResource cancelButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/variable.gif")
   ImageResource variable();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/breakpoint.gif")
   ImageResource breakpoint();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/stepinto.gif")
   ImageResource stepIntoButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/stepover.gif")
   ImageResource stepOverButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/stepreturn.gif")
   ImageResource stepReturnButton();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/debug.png")
   ImageResource debug();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/debug_Disabled.png")
   ImageResource debugDisabled();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/stopApp.png")
   ImageResource stopApp();
   
   @Source("org/exoplatform/ide/extension/java/jdi/images/stopApp_Disabled.png")
   ImageResource stopAppDisabled();
   
}
