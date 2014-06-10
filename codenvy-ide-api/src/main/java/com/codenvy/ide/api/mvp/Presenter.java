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
package com.codenvy.ide.api.mvp;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Common interface for Presenters that are responsible for driving the UI
 * <p/>
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 *         Jul 24, 2012
 */
public interface Presenter {
    /**
     * Allows presenter to expose it's view to the container.
     *
     * @param container
     */
    void go(final AcceptsOneWidget container);
}
