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
package org.exoplatform.ide.editor.javascript.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface JavaScriptClientBundle extends ClientBundle {

    @Source("org/exoplatform/ide/editor/javascript/client/styles/javascript.css")
    JavaScriptCss css();

    @Source("org/exoplatform/ide/editor/javascript/client/images/function-item.png")
    ImageResource functionItem();

    @Source("org/exoplatform/ide/editor/javascript/client/images/property-item.png")
    ImageResource propertyItem();

    @Source("org/exoplatform/ide/editor/javascript/client/images/row-selected.png")
    ImageResource itemSelected();

    @Source("org/exoplatform/ide/editor/javascript/client/images/var-item.png")
    ImageResource varItem();

    @Source("org/exoplatform/ide/editor/javascript/client/images/blank.png")
    ImageResource blankImage();

    @Source("org/exoplatform/ide/editor/javascript/client/images/class.gif")
    ImageResource classItem();

    @Source("org/exoplatform/ide/editor/javascript/client/images/template.png")
    ImageResource template();

    @Source("org/exoplatform/ide/editor/javascript/client/images/method-item.png")
    ImageResource methodItem();

    @Source("org/exoplatform/ide/editor/javascript/client/images/tag.png")
    ImageResource tag();

    @Source("org/exoplatform/ide/editor/javascript/public/images/javascript/javascript.gif")
    ImageResource javaScript();

    @Source("org/exoplatform/ide/editor/javascript/client/esprima/esprima.js")
    TextResource esprima();

    @Source("org/exoplatform/ide/editor/javascript/client/esprima/esprimaJsContentAssist.js")
    TextResource esprimaJsContentAssist();
}
