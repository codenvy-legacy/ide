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
package com.codenvy.ide.ext.java.jdi.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
public interface JavaRuntimeResources extends ClientBundle {
    @Source("resume.png")
    ImageResource resumeButton();

    @Source("disconnect.png")
    ImageResource disconnectButton();

    @Source("cancel.png")
    ImageResource cancelButton();

    @Source("ok.png")
    ImageResource okButton();

    @Source("stepinto.png")
    ImageResource stepIntoButton();

    @Source("stepover.png")
    ImageResource stepOverButton();

    @Source("stepreturn.png")
    ImageResource stepReturnButton();

    @Source("debugApp.png")
    ImageResource debugApp();

    @Source("stopApp.png")
    ImageResource stopApp();

    @Source("runApp.png")
    ImageResource runApp();

    @Source("updateApp.png")
    ImageResource updateApp();

    @Source("logs.png")
    ImageResource logs();

    @Source("variable.png")
    ImageResource variable();

    @Source("evaluate.png")
    ImageResource evaluate();

    @Source("breakpoint.png")
    ImageResource breakpoint();

    @Source("breakpoint_properties.png")
    ImageResource breakpointProperties();

    @Source("remove_all_breakpoints.gif")
    ImageResource removeAllBreakpointsButton();

    @Source("breakpoints.png")
    ImageResource breakPointsIcon();

    @Source("ajax-loader.gif")
    ImageResource loader();
}