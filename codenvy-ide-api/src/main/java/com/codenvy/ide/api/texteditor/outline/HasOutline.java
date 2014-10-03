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
package com.codenvy.ide.api.texteditor.outline;

/**
 * Interface for components that expose an {@link OutlinePresenter}.
 */
public interface HasOutline {

    /**
     * Returns the outline presenter.<br>
     * If editor doesn't support Outline, returns <code>null</code>
     *
     * @return the outline presenter.
     */
    OutlinePresenter getOutline();
}
