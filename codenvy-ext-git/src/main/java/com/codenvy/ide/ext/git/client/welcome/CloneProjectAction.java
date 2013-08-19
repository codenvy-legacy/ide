/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.git.client.welcome;

import com.codenvy.ide.api.parts.WelcomeItemAction;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.clone.CloneRepositoryPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The action what provides some actions when clone project item is clicked.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloneProjectAction implements WelcomeItemAction {
    private GitLocalizationConstant  constant;
    private GitResources             resources;
    private CloneRepositoryPresenter cloneRepositoryPresenter;

    /**
     * Create action.
     *
     * @param constant
     * @param resources
     * @param cloneRepositoryPresenter
     */
    @Inject
    public CloneProjectAction(GitLocalizationConstant constant, GitResources resources,
                              CloneRepositoryPresenter cloneRepositoryPresenter) {
        this.constant = constant;
        this.resources = resources;
        this.cloneRepositoryPresenter = cloneRepositoryPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return constant.welcomeCloneTitle();
    }

    /** {@inheritDoc} */
    @Override
    public String getCaption() {
        return constant.welcomeCloneText();
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        return resources.welcomeClone();
    }

    /** {@inheritDoc} */
    @Override
    public void execute() {
        cloneRepositoryPresenter.showDialog();
    }
}