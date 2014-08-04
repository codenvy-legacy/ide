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

    @Source("com/codenvy/ide/extension/maven/client/xml.svg")
    SVGResource xmlFile();

    @Source("com/codenvy/ide/extension/maven/client/js.svg")
    SVGResource jsFile();

    @Source("com/codenvy/ide/extension/maven/client/json.svg")
    SVGResource jsonFile();

    @Source("com/codenvy/ide/extension/maven/client/jsp.svg")
    SVGResource jspFile();

    @Source("com/codenvy/ide/extension/maven/client/css.svg")
    SVGResource cssFile();

    @Source("com/codenvy/ide/extension/maven/client/html.svg")
    SVGResource htmlFile();

    @Source("com/codenvy/ide/extension/maven/client/image-icon.svg")
    SVGResource imageIcon();

    @Source("com/codenvy/ide/extension/maven/client/maven.svg")
    SVGResource maven();

    @Source("com/codenvy/ide/extension/maven/client/package.svg")
    SVGResource packageIcon();

    @Source("com/codenvy/ide/extension/maven/client/java.svg")
    SVGResource javaFile();

    @Source("com/codenvy/ide/extension/maven/client/jar_64.png")
    ImageResource mavenJarBigIcon();
}