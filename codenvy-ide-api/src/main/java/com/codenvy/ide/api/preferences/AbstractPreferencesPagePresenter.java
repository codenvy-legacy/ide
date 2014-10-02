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
package com.codenvy.ide.api.preferences;

import com.google.gwt.resources.client.ImageResource;

/**
 * Abstract base implementation for all preference page implementations.
 * It's simpler to get started using Preferences.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public abstract class AbstractPreferencesPagePresenter implements PreferencesPagePresenter {
    protected DirtyStateListener delegate;

    private static String defaultCategory = "IDE Settings";

    private String title;

    private String category;

    private ImageResource icon;

    /**
     * Create preference page.
     *
     * @param title
     * @param category
     * @param icon
     */
    public AbstractPreferencesPagePresenter(String title, String category, ImageResource icon) {
        this.title = title;
        this.category = category;
        this.icon = icon;
    }

    /**
     * Create preference page with a default category for grouping elements.
     *
     * @param title
     * @param icon
     */
    public AbstractPreferencesPagePresenter(String title, ImageResource icon) {
        this(title, defaultCategory, icon);
    }

    /** {@inheritDoc} */
    @Override
    public void setUpdateDelegate(DirtyStateListener delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public String getCategory() {
        return category;
    }
}