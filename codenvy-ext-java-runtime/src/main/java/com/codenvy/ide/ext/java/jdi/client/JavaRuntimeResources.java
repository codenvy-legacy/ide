/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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