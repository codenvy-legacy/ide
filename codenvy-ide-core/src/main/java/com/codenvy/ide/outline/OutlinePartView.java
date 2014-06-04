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
package com.codenvy.ide.outline;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.parts.base.BaseActionDelegate;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface OutlinePartView extends View<OutlinePartView.ActionDelegate> {
    AcceptsOneWidget getContainer();

    void showNoOutline();

    void setTitle(String title);

    void clear();

    public interface ActionDelegate extends BaseActionDelegate {

    }
}
