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
     * Validate {@code value}.
     *
     * @param value
     *         value to validate
     * @return message about constraint violations or {@code null}
     */
    @Nullable
    ConstraintViolation validate(String value);

    interface ConstraintViolation {
        String getMessage();
    }
}
