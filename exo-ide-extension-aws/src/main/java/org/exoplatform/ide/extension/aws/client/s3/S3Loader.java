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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.commons.loader.Loader;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3Loader.java Oct 4, 2012 vetal $
 */
public class S3Loader extends Loader {

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader#show() */
    @Override
    public void show() {
        DOM.getElementById("gwt-debug-s3loader").setAttribute("style", "visibility: visible;");
        RootPanel.get().getElement().getStyle().setCursor(Cursor.WAIT);
    }

    /** @see org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader#hide() */
    @Override
    public void hide() {
        DOM.getElementById("gwt-debug-s3loader").setAttribute("style", "visibility: hidden;");
        RootPanel.get().getElement().getStyle().setCursor(Cursor.DEFAULT);
    }

}
