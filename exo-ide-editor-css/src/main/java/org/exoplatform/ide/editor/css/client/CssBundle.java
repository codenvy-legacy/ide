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
package org.exoplatform.ide.editor.css.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface CssBundle extends ClientBundle {

    @Source("org/exoplatform/ide/editor/css/client/styles/CSS.css")
    CssResources css();

    @Source("org/exoplatform/ide/editor/css/client/images/property-item.png")
    ImageResource propertyItem();

    @Source("org/exoplatform/ide/editor/css/client/images/row-selected.png")
    ImageResource itemSelected();

    @Source("org/exoplatform/ide/editor/css/client/images/tag.png")
    ImageResource tag();

    @Source("org/exoplatform/ide/editor/css/public/images/css/css.png")
    ImageResource cssImage();

    @Source("org/exoplatform/ide/editor/css/client/images/css-property.png")
    ImageResource cssProperty();

}
