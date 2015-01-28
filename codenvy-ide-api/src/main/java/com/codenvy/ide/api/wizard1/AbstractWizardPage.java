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

import javax.annotation.Nonnull;

/**
 * Abstract base implementation of a {@link WizardPage}.
 *
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractWizardPage<T> implements WizardPage<T> {
    protected Wizard.UpdateDelegate updateDelegate;
    protected T                     data;

    /** Create wizard page. */
    public AbstractWizardPage() {
    }

    @Override
    public void init(T data) {
        this.data = data;
    }

    @Override
    public void setUpdateDelegate(@Nonnull Wizard.UpdateDelegate delegate) {
        this.updateDelegate = delegate;
    }

    @Override
    public boolean isCompleted() {
        return true;
    }

    @Override
    public boolean canSkip() {
        return false;
    }
}
