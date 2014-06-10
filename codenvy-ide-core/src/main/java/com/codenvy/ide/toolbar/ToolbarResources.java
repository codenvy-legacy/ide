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

        String toolbarMenuPanel();

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
