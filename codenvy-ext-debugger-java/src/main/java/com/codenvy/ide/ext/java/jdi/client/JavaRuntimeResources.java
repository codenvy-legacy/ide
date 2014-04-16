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
import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Vitaly Parfonov */
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
    
    @Source("debug.svg")
    SVGResource debug();

    @Source("changeVariableValue.png")
    ImageResource changeVariableValue();

    @Source("evaluate.png")
    ImageResource evaluate();

    @Source("breakpoint.png")
    ImageResource breakpoint();

    @Source("remove_all_breakpoints.gif")
    ImageResource removeAllBreakpointsButton();

    @Source("breakpoints.png")
    ImageResource breakPointsIcon();
}