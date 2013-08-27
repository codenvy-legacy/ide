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
package org.exoplatform.ide.editor.java.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface JavaClientBundle extends ClientBundle {
    JavaClientBundle INSTANCE = GWT.create(JavaClientBundle.class);

    @Source("org/exoplatform/ide/editor/java/client/styles/java.css")
    JavaCss css();

    @Source("org/exoplatform/ide/editor/java/client/images/annotation.gif")
    ImageResource annotationItem();

    @Source("org/exoplatform/ide/editor/java/client/images/class.gif")
    ImageResource classItem();

    @Source("org/exoplatform/ide/editor/java/client/images/innerinterface_public.gif")
    ImageResource interfaceItem();

    @Source("org/exoplatform/ide/editor/java/client/images/enum_item.gif")
    ImageResource enumItem();

    @Source("org/exoplatform/ide/editor/java/client/images/default-field.png")
    ImageResource defaultField();

    @Source("org/exoplatform/ide/editor/java/client/images/private-field.png")
    ImageResource privateField();

    @Source("org/exoplatform/ide/editor/java/client/images/protected-field.png")
    ImageResource protectedField();

    @Source("org/exoplatform/ide/editor/java/client/images/public-field.png")
    ImageResource publicField();

    @Source("org/exoplatform/ide/editor/java/client/images/blank.png")
    ImageResource blankImage();

    @Source("org/exoplatform/ide/editor/java/client/images/default-method.png")
    ImageResource defaultMethod();

    @Source("org/exoplatform/ide/editor/java/client/images/private-method.png")
    ImageResource privateMethod();

    @Source("org/exoplatform/ide/editor/java/client/images/protected-method.png")
    ImageResource protectedMethod();

    @Source("org/exoplatform/ide/editor/java/client/images/public-method.png")
    ImageResource publicMethod();

    @Source("org/exoplatform/ide/editor/java/client/images/package.png")
    ImageResource packageItem();

    @Source("org/exoplatform/ide/editor/java/client/images/import.png")
    ImageResource importItem();

    @Source("org/exoplatform/ide/editor/java/client/images/imports.png")
    ImageResource imports();

    @Source("org/exoplatform/ide/editor/java/client/images/local.png")
    ImageResource variable();

    @Source("org/exoplatform/ide/editor/java/client/images/row-selected.png")
    ImageResource itemSelected();

    @Source("org/exoplatform/ide/editor/java/client/images/jsp-tag.png")
    ImageResource jspTagItem();

    @Source("org/exoplatform/ide/editor/java/client/images/class-private.png")
    ImageResource classPrivateItem();

    @Source("org/exoplatform/ide/editor/java/client/images/class-protected.png")
    ImageResource classProtectedItem();

    @Source("org/exoplatform/ide/editor/java/client/images/class-default.png")
    ImageResource classDefaultItem();

    @Source("org/exoplatform/ide/editor/java/client/images/clock.png")
    ImageResource clockItem();

    @Source("org/exoplatform/ide/editor/java/client/images/groovy-tag.png")
    ImageResource groovyTagItem();

    @Source("org/exoplatform/ide/editor/java/client/images/java.png")
    ImageResource java();

    @Source("org/exoplatform/ide/editor/java/client/images/java-disabled.png")
    ImageResource javaDisabled();

    @Source("org/exoplatform/ide/editor/java/client/images/outline.png")
    ImageResource outline();

    @Source("org/exoplatform/ide/editor/java/client/images/loader.gif")
    ImageResource loader();

    @Source("org/exoplatform/ide/editor/java/client/images/template.png")
    ImageResource template();

    @Source("org/exoplatform/ide/editor/java/client/images/package_Disabled.png")
    ImageResource packageDisabled();

    @Source("org/exoplatform/ide/editor/java/client/images/breakpoint-current.gif")
    ImageResource breakpointCurrent();

    @Source("org/exoplatform/ide/editor/java/client/images/breakpoint.gif")
    ImageResource breakpoint();
}
