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
package org.exoplatform.ide.editor.html.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface HtmlClientBundle extends ClientBundle {

    @Source("org/exoplatform/ide/editor/html/client/styles/html.css")
    HtmlCss css();

    @Source("org/exoplatform/ide/editor/html/client/images/attribute.png")
    ImageResource attribute();

    @Source("org/exoplatform/ide/editor/html/client/images/property-item.png")
    ImageResource property();

    @Source("org/exoplatform/ide/editor/html/client/images/tag.png")
    ImageResource tag();

    @Source("org/exoplatform/ide/editor/html/client/images/template.png")
    ImageResource template();

    @Source("org/exoplatform/ide/editor/html/client/images/row-selected.png")
    ImageResource itemSelected();

    @Source("org/exoplatform/ide/editor/html/client/images/cdata-item.png")
    ImageResource cdata();
}
