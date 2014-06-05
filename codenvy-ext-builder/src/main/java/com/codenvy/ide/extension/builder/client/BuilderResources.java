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
package com.codenvy.ide.extension.builder.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Client resources.
 *
 * @author Ann Shumilova
 */
public interface BuilderResources extends ClientBundle {
    @Source("build.svg")
    SVGResource build();

    @Source("clear-logs.svg")
    SVGResource clear();

    public interface Css extends CssResource {
        @ClassName("info-panel")
        String infoPanel();

        @ClassName("data-label")
        String dataLabel();
    }

    @Source({"builder.css"})
    Css builder();
}