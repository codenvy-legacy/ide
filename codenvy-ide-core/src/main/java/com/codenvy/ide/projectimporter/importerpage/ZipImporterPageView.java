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
package com.codenvy.ide.projectimporter.importerpage;

import com.codenvy.ide.api.projectimporter.basepage.ImporterBasePageView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
@ImplementedBy(ZipImporterPageViewImpl.class)
public interface ZipImporterPageView extends IsWidget {
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having selected a skip first level. */
        void skipFirstLevelChanged(boolean isSkipFirstLevel);
    }

    /** Sets the delegate to receive events from this view. */
    void setDelegate(@Nonnull ActionDelegate delegate);

    /** Performs when user select skip first level. */
    boolean isSkipFirstLevelSelected();

    /** Reset the page. */
    void reset();

    /** @return panel for {@link ImporterBasePageView}*/
    @Nonnull
    AcceptsOneWidget getBasePagePanel();
}
