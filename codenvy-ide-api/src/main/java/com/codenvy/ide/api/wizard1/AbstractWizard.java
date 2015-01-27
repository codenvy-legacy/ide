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

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractWizard<T> implements Wizard<T> {
    protected final T                    data;
    protected       Array<WizardPage<T>> wizardPages;
    private         UpdateDelegate       delegate;
    private         int                  index;

    @Inject
    public AbstractWizard(T data) {
        this.data = data;
        wizardPages = Collections.createArray();
    }

    public T getData() {
        return data;
    }

    /**
     * Add page to wizard.
     *
     * @param pageProvider
     *         page that need to add
     */
    public void addPage(@Nonnull Provider<? extends WizardPage<T>> pageProvider) {
        final WizardPage<T> page = pageProvider.get();
        page.setUpdateDelegate(delegate);
        wizardPages.add(page);
    }

    /**
     * Add page to a wizard in place with index
     *
     * @param pageProvider
     *         page that needs to be added
     * @param index
     *         place where the page needs to be inserted
     * @param replace
     *         <code>true</code> if one needs to replace a page with a given index, and <code>false</code> if a page needs to be inserted
     *         at a given position
     */
    public void addPage(@Nonnull Provider<? extends WizardPage<T>> pageProvider, int index, boolean replace) {
        if (index >= wizardPages.size()) {
            addPage(pageProvider);
            return;
        }

        if (replace) {
            setPage(pageProvider, index);
        } else {
            Array<WizardPage<T>> before = wizardPages.slice(0, index);
            WizardPage<T> currentPage = wizardPages.get(index);
            Array<WizardPage<T>> after = wizardPages.slice(index + 1, wizardPages.size());

            wizardPages.clear();
            wizardPages.addAll(before);
            addPage(pageProvider);
            wizardPages.add(currentPage);
            wizardPages.addAll(after);
        }
    }

    private void setPage(@Nonnull Provider<? extends WizardPage<T>> pageProvider, int index) {
        final WizardPage<T> page = pageProvider.get();
        page.setUpdateDelegate(delegate);
        wizardPages.set(index, page);
    }

    @Override
    public void setUpdateDelegate(@Nonnull UpdateDelegate delegate) {
        this.delegate = delegate;
        for (WizardPage<T> page : wizardPages.asIterable()) {
            page.setUpdateDelegate(delegate);
        }
    }

    @Override
    public WizardPage<T> flipToFirst() {
        clear();
        return flipToNext();
    }

    /** Clear wizard values. */
    private void clear() {
        index = -1;
    }

    @Override
    public WizardPage<T> flipToNext() {
        final WizardPage<T> nextPage = getNextEnablePage();
        if (nextPage != null) {
            nextPage.init(data);
        }
        return nextPage;
    }

    /** Returns next enabled page that need to be shown. */
    @Nullable
    private WizardPage<T> getNextEnablePage() {
        while (++index < wizardPages.size()) {
            WizardPage<T> page = wizardPages.get(index);
            if (!page.canSkip()) {
                return page;
            }
        }
        return null;
    }

    @Override
    public WizardPage<T> flipToPrevious() {
        while (--index >= 0) {
            final WizardPage<T> page = wizardPages.get(index);
            if (!page.canSkip()) {
                page.init(data);
                return page;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        for (int i = index + 1; i < wizardPages.size(); i++) {
            WizardPage<T> page = wizardPages.get(i);
            if (!page.canSkip()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPrevious() {
        for (int i = index - 1; i >= 0; i--) {
            WizardPage<T> page = wizardPages.get(i);
            if (!page.canSkip()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canFinish() {
        for (WizardPage<T> page : wizardPages.asIterable()) {
            if (!page.isCompleted()) {
                return false;
            }
        }
        return true;
    }
}
