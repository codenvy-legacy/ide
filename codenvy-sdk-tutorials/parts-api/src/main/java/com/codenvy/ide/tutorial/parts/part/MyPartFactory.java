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
package com.codenvy.ide.tutorial.parts.part;

import javax.validation.constraints.NotNull;

/**
 * The factory for creating a part.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface MyPartFactory {
    /**
     * Create an instance of a part with a given title.
     *
     * @param title
     *         title for part
     * @return {@link MyPartPresenter}
     */
    MyPartPresenter create(@NotNull String title);
}