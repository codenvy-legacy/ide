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
package com.codenvy.ide.extension.runner.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Client resources.
 *
 * @author Artem Zatsarynnyy
 */
public interface RunnerResources extends ClientBundle {

    @Source("run.svg")
    SVGResource launchApp();

    @Source("logs.svg")
    SVGResource getAppLogs();

    @Source("stop.svg")
    SVGResource stopApp();

    @Source("update.png")
    ImageResource updateApp();

    @Source("clear-logs.svg")
    SVGResource clear();

    public interface Css extends CssResource {
        @ClassName("info-panel")
        String infoPanel();

        @ClassName("data-label")
        String dataLabel();
    }

    @Source({"runner.css", "com/codenvy/ide/api/ui/style.css"})
    Css runner();
}
