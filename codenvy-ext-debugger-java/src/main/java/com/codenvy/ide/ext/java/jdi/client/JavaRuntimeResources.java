/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdi.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/** @author Vitaly Parfonov */
public interface JavaRuntimeResources extends ClientBundle {

    @Source("resume.svg")
    SVGResource resumeButton();

    @Source("disconnect.svg")
    SVGResource disconnectButton();

    @Source("cancel.png")
    ImageResource cancelButton();

    @Source("ok.png")
    ImageResource okButton();

    @Source("stepinto.svg")
    SVGResource stepIntoButton();

    @Source("stepover.svg")
    SVGResource stepOverButton();

    @Source("stepreturn.svg")
    SVGResource stepReturnButton();

    @Source("debugApp.png")
    ImageResource debugApp();

    @Source("debug.svg")
    SVGResource debug();

    @Source("edit.svg")
    SVGResource changeVariableValue();

    @Source("evaluate.svg")
    SVGResource evaluate();

    @Source("breakpoint.png")
    ImageResource breakpoint();

    @Source("remove.svg")
    SVGResource removeAllBreakpointsButton();

    @Source("breakpoints.png")
    ImageResource breakPointsIcon();
}