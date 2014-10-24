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
package com.codenvy.ide.ui.dialogs.input;

import javax.annotation.Nullable;

/**
 * Validator for {@link InputDialog}.
 *
 * @author Artem Zatsarynnyy
 */
public interface InputValidator {
    /**
     * Validate the {@code value}.
     *
     * @param value
     *         value to validate
     * @return {@link Violation} instance if {@code value} isn't valid or {@code null} otherwise
     */
    @Nullable
    Violation validate(String value);

    /** Describes a violation of validation constraint. */
    interface Violation {
        /** Returns error message for violation of validation constraints. */
        @Nullable
        String getMessage();
    }
}
