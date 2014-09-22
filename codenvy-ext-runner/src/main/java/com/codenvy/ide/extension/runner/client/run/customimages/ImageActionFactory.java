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
package com.codenvy.ide.extension.runner.client.run.customimages;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.extension.runner.client.actions.RunImageAction;
import com.google.inject.assistedinject.Assisted;

import javax.annotation.Nonnull;

/**
 * Factory for creating {@link RunImageAction} instances.
 *
 * @author Artem Zatsarynnyy
 */
public interface ImageActionFactory {
    /**
     * Create an instance of a {@link RunImageAction} with a given title and description.
     *
     * @param title
     *         action's title
     * @param description
     *         action's description
     * @return new {@link com.codenvy.ide.extension.runner.client.actions.RunImageAction}
     */
    @Nonnull
    RunImageAction createAction(@Nonnull @Assisted("title") String title, @Nonnull @Assisted("description") String description,
                                ItemReference scriptFile);
}
