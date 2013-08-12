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
package com.codenvy.ide.toolbar;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface ToolbarResources extends ClientBundle {

    public interface Css extends CssResource {

        String iconButtonPanel();

        String iconButtonIcon();

        String toolbarPanel();

        String iconButtonPanelDown();

        String iconButtonPanelSelectedOver();

        String iconButtonPanelSelectedDown();

        String toolbarDelimiter();

        String iconButtonPanelSelected();

        String iconButtonPanelOver();

        String disabled();

        String popupButtonPanel();

        String popupButtonPanelDown();

        String popupButtonIcon();

        String popupButtonPanelOver();

        String caret();
    }

    @Source({"toolbar.css", "com/codenvy/ide/api/ui/style.css"})
    Css toolbar();
}
