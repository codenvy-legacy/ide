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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Toolbar item that represent 'delimiter'
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DelimiterItem extends Composite {
    public DelimiterItem() {
        FlowPanel widget = new FlowPanel();
        widget.setStyleName(Toolbar.RESOURCES.toolbar().toolbarDelimiter());
        initWidget(widget);
    }
}
