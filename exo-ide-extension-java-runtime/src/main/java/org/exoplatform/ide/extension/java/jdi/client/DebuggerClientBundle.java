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
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface DebuggerClientBundle extends ClientBundle
{
   DebuggerClientBundle INSTANCE = GWT.<DebuggerClientBundle> create(DebuggerClientBundle.class);

   @Source("org/exoplatform/ide/extension/java/jdi/images/resume.png")
   ImageResource resumeButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/resume_Disabled.png")
   ImageResource resumeButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/disconnect.png")
   ImageResource disconnectButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/disconnect_Disabled.png")
   ImageResource disconnectButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/ok_Disabled.png")
   ImageResource okButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stepinto.png")
   ImageResource stepIntoButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stepinto_Disabled.png")
   ImageResource stepIntoButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stepover.png")
   ImageResource stepOverButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stepover_Disabled.png")
   ImageResource stepOverButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stepreturn.png")
   ImageResource stepReturnButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stepreturn_Disabled.png")
   ImageResource stepReturnButtonDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/debugApp.png")
   ImageResource debugApp();

   @Source("org/exoplatform/ide/extension/java/jdi/images/debugApp_Disabled.png")
   ImageResource debugAppDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stopApp.png")
   ImageResource stopApp();

   @Source("org/exoplatform/ide/extension/java/jdi/images/stopApp_Disabled.png")
   ImageResource stopAppDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/runApp.png")
   ImageResource runApp();

   @Source("org/exoplatform/ide/extension/java/jdi/images/runApp_Disabled.png")
   ImageResource runAppDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/logs.png")
   ImageResource logs();

   @Source("org/exoplatform/ide/extension/java/jdi/images/logs_Disabled.png")
   ImageResource logsDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/variable.png")
   ImageResource variable();

   @Source("org/exoplatform/ide/extension/java/jdi/images/variable_Disabled.png")
   ImageResource variableDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/evaluate.png")
   ImageResource evaluate();

   @Source("org/exoplatform/ide/extension/java/jdi/images/evaluate_Disabled.png")
   ImageResource evaluateDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/breakpoint.png")
   ImageResource breakpoint();

   @Source("org/exoplatform/ide/extension/java/jdi/images/breakpoint_properties.png")
   ImageResource breakpointProperties();

   @Source("org/exoplatform/ide/extension/java/jdi/images/breakpoint_properties_Disabled.png")
   ImageResource breakpointPropertiesDisabled();

   @Source("org/exoplatform/ide/extension/java/jdi/images/remove_all_breakpoints.gif")
   ImageResource removeAllBreakpointsButton();

   @Source("org/exoplatform/ide/extension/java/jdi/images/breakpoints.png")
   ImageResource breakPointsIcon();

   @Source("org/exoplatform/ide/extension/java/jdi/images/ajax-loader.gif")
   ImageResource loader();
}
