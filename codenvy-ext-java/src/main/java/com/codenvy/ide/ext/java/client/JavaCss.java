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
package com.codenvy.ide.ext.java.client;

import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface JavaCss extends CssResource {

    @ClassName("exo-autocomplete-fqn")
    String fqnStyle();

    @ClassName("exo-codeassistant-counter")
    String counter();

    @ClassName("outline-root")
    String outlineRoot();

    @ClassName("outline-icon")
    String outlineIcon();

    @ClassName("outline-label")
    String outlineLabel();

    @ClassName("imports")
    String imports();

    @ClassName("importItem")
    String importItem();

    @ClassName("classItem")
    String classItem();

    @ClassName("interfaceItem")
    String interfaceItem();

    @ClassName("enumItem")
    String enumItem();

    @ClassName("annotationItem")
    String annotationItem();

    @ClassName("publicMethod")
    String publicMethod();

    @ClassName("protectedMethod")
    String protectedMethod();

    @ClassName("privateMethod")
    String privateMethod();

    @ClassName("defaultMethod")
    String defaultMethod();

    @ClassName("publicField")
    String publicField();

    @ClassName("protectedField")
    String protectedField();

    @ClassName("privateField")
    String privateField();

    @ClassName("defaultField")
    String defaultField();

    @ClassName("packageItem")
    String packageItem();

    @ClassName("overview-bottom-mark-error")
    String overviewBottomMarkError();

    @ClassName("overview-mark-warning")
    String overviewMarkWarning();

    @ClassName("overview-bottom-mark-warning")
    String overviewBottomMarkWarning();

    @ClassName("overview-mark-error")
    String overviewMarkError();

    @ClassName("overview-mark-task")
    String overviewMarkTask();

    @ClassName("mark-element-icon")
    String markElementIcon();

    @ClassName("mark-element")
    String markElement();
}
