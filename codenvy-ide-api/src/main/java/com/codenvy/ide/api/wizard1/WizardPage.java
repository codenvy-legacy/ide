/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.wizard1;

import com.codenvy.ide.api.mvp.Presenter;

import javax.annotation.Nonnull;

/**
 * The main responsibility of a {@link WizardPage} subclass is collecting data.
 *
 * @param <T>
 *         the type of the data object
 * @author Artem Zatsarynnyy
 */
public interface WizardPage<T> extends Presenter {
    /** Initializes this page using the passed                                                    data object. */
    void init(T data);

    /**
     * Sets update control delegate.
     *
     * @param delegate
     */
    void setUpdateDelegate(@Nonnull Wizard.UpdateDelegate delegate);

    /**
     * Returns whether this page is complete or not.
     * This information is typically used by the wizard to decide when it is okay to finish.
     *
     * @return <code>true</code> if this page is complete, and <code>false</code> otherwise
     */
    boolean isCompleted();

    /**
     * Returns whether this page can be skipped.
     *
     * @return <code>true</code> if this page can be skipped, and <code>false</code> otherwise
     */
    boolean canSkip();
}
