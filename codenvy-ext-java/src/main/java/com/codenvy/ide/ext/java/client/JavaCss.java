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
