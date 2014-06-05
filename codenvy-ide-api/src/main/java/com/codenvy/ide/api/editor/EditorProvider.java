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
package com.codenvy.ide.api.editor;

import javax.validation.constraints.NotNull;

/**
 * Provider interface for creating new instance of {@link EditorPartPresenter}.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
public interface EditorProvider {
    /**
     * Every call this method should return new instance.
     *
     * @return new instance of {@link EditorPartPresenter}
     */
    @NotNull
    EditorPartPresenter getEditor();
}