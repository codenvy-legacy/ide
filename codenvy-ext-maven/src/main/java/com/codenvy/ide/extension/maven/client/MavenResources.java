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
package com.codenvy.ide.extension.maven.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

import org.vectomatic.dom.svg.ui.SVGResource;

/**
 * Client resources.
 *
 * @author Ann Shumilova
 */
public interface MavenResources extends ClientBundle {
    @Source("build.svg")
    SVGResource build();

    @Source("xml.svg")
    SVGResource xmlFile();

    @Source("js.svg")
    SVGResource jsFile();

    @Source("json.svg")
    SVGResource jsonFile();

    @Source("jsp.svg")
    SVGResource jspFile();

    @Source("css.svg")
    SVGResource cssFile();

    @Source("html.svg")
    SVGResource htmlFile();

    @Source("image-icon.svg")
    SVGResource imageIcon();

    @Source("maven.svg")
    SVGResource maven();

    @Source("package.svg")
    SVGResource packageIcon();

    @Source("java.svg")
    SVGResource javaFile();

    @Source("jar_64.png")
    ImageResource mavenJarBigIcon();
}