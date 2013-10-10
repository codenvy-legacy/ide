/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 * [2012] - [2013] Codenvy, S.A. 
 * All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.tutorials.client.template;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPage;
import com.codenvy.ide.ext.tutorials.client.TutorialsClientService;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Abstract implementation of wizard page for creating project from template wizard page.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public abstract class AbstractCreateProjectPage extends AbstractWizardPage {
    protected TutorialsClientService service;
    protected ResourceProvider       resourceProvider;

    /**
     * Create page.
     *
     * @param service
     *         service that provides create this kind of project
     * @param resourceProvider
     */
    public AbstractCreateProjectPage(TutorialsClientService service, ResourceProvider resourceProvider) {
        super(null, null);
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSkip() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void focusComponent() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void removeOptions() {
        // do nothing
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        // do nothing
    }
}