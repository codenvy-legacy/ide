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
package org.eclipse.che.ide.api.wizard;

import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import com.google.inject.Inject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base implementation of a {@link Wizard}.
 *
 * @author Andrey Plotnikov
 * @author Artem Zatsarynnyy
 */
public abstract class AbstractWizard<T> implements Wizard<T> {
    protected final T                    dataObject;
    protected final Map<String, String>  context;
    protected final Array<WizardPage<T>> wizardPages;
    private         UpdateDelegate       delegate;
    private         int                  currentPageIndex;

    /**
     * Creates new wizard with the specified {@code dataObject} which will be passed into every added page.
     * <p/>
     * So multiple pages have the same {@code dataObject}, and any change to the
     * {@code dataObject} made by one page is available to the other pages.
     *
     * @param dataObject
     *         data-object for wizard
     */
    @Inject
    public AbstractWizard(T dataObject) {
        this.dataObject = dataObject;
        context = new HashMap<>();
        wizardPages = Collections.createArray();
    }

    public Map<String, String> getContext() {
        return context;
    }

    /** Returns wizard's data-object. */
    public T getDataObject() {
        return dataObject;
    }

    /**
     * Add page to wizard.
     *
     * @param page
     *         page to add
     */
    public void addPage(@Nonnull WizardPage<T> page) {
        page.setUpdateDelegate(delegate);
        page.setContext(context);
        page.init(dataObject);
        wizardPages.add(page);
    }

    /**
     * Add page to wizard at the specified position.
     *
     * @param page
     *         page to be stored at the specified position
     * @param index
     *         position where the page should be inserted
     * @param replace
     *         {@code true} if the existed page should be replaced by the given one,
     *         {@code false} if a page should be inserted at the specified position
     */
    public void addPage(@Nonnull WizardPage<T> page, int index, boolean replace) {
        if (index >= wizardPages.size()) {
            addPage(page);
            return;
        }

        if (replace) {
            setPage(page, index);
        } else {
            Array<WizardPage<T>> before = wizardPages.slice(0, index);
            WizardPage<T> currentPage = wizardPages.get(index);
            Array<WizardPage<T>> after = wizardPages.slice(index + 1, wizardPages.size());

            wizardPages.clear();
            wizardPages.addAll(before);
            addPage(page);
            wizardPages.add(currentPage);
            wizardPages.addAll(after);
        }
    }

    private void setPage(@Nonnull WizardPage<T> page, int index) {
        page.setUpdateDelegate(delegate);
        page.setContext(context);
        page.init(dataObject);
        wizardPages.set(index, page);
    }

    @Override
    public void setUpdateDelegate(@Nonnull UpdateDelegate delegate) {
        this.delegate = delegate;
        for (WizardPage<T> page : wizardPages.asIterable()) {
            page.setUpdateDelegate(delegate);
        }
    }

    @Nullable
    @Override
    public WizardPage<T> navigateToFirst() {
        resetNavigationState();
        return navigateToNext();
    }

    /** Reset wizard's navigation state. */
    private void resetNavigationState() {
        currentPageIndex = -1;
    }

    @Nullable
    @Override
    public WizardPage<T> navigateToNext() {
        return getNextPage();
    }

    /** Returns next page that may be shown. */
    @Nullable
    private WizardPage<T> getNextPage() {
        while (++currentPageIndex < wizardPages.size()) {
            WizardPage<T> page = wizardPages.get(currentPageIndex);
            if (!page.canSkip()) {
                return page;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public WizardPage<T> navigateToPrevious() {
        while (--currentPageIndex >= 0) {
            final WizardPage<T> page = wizardPages.get(currentPageIndex);
            if (!page.canSkip()) {
                return page;
            }
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        for (int i = currentPageIndex + 1; i < wizardPages.size(); i++) {
            WizardPage<T> page = wizardPages.get(i);
            if (!page.canSkip()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasPrevious() {
        for (int i = currentPageIndex - 1; i >= 0; i--) {
            WizardPage<T> page = wizardPages.get(i);
            if (!page.canSkip()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canComplete() {
        for (WizardPage<T> page : wizardPages.asIterable()) {
            if (!page.isCompleted()) {
                return false;
            }
        }
        return true;
    }
}
