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
package org.exoplatform.ide.editor.php.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.resources.client.ImageResource.RepeatStyle;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface PhpClientBundle extends ClientBundle {
    PhpClientBundle INSTANCE = GWT.create(PhpClientBundle.class);

    @Source("org/exoplatform/ide/editor/php/client/styles/php.css")
    PhpCss css();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/class.gif")
    ImageResource classItem();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/innerinterface_public.gif")
    ImageResource interfaceItem();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/constant-item.png")
    ImageResource constantItem();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/public-method.png")
    ImageResource publicMethod();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/private-method.png")
    ImageResource privateMethod();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/protected-method.png")
    ImageResource protectedMethod();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/private-field.png")
    ImageResource privateField();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/protected-field.png")
    ImageResource protectedField();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/public-field.png")
    ImageResource publicField();

    @Source("org/exoplatform/ide/editor/php/client/images/blank.png")
    ImageResource blankImage();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/local.png")
    ImageResource variable();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/row-selected.png")
    @ImageOptions(repeatStyle = RepeatStyle.Horizontal)
    ImageResource itemSelected();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/php-tag.png")
    ImageResource phpTag();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/namespace-item.png")
    ImageResource namespace();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/class-constant-item.png")
    ImageResource classConstant();

    @Source("org/exoplatform/ide/editor/php/client/images/codeassistant/function-item.png")
    ImageResource function();

    @Source("org/exoplatform/ide/editor/php/client/images/php.png")
    ImageResource php();

    @Source("org/exoplatform/ide/editor/php/client/images/php-disabled.png")
    ImageResource phpDisabled();

}
