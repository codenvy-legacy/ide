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
package com.codenvy.ide.ext.extruntime.client.wizard;

import com.codenvy.ide.api.template.CreateProjectProvider;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.wizard.AbstractWizardPagePresenter;
import com.codenvy.ide.api.ui.wizard.WizardPagePresenter;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeLocalizationConstant;
import com.codenvy.ide.ext.extruntime.client.ExtRuntimeResources;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter for creating Codenvy extension project from 'Create project wizard'.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionPagePresenter.java Jul 8, 2013 4:24:17 PM azatsarynnyy $
 */
@Singleton
public class ExtensionPagePresenter extends AbstractWizardPagePresenter
                                                                       implements ExtensionPageView.ActionDelegate {
    private static final String            DEFAULT_VERSION = "1.0-SNAPSHOT";
    private ExtensionPageView              view;
    private String                         groupId;
    private String                         artifactId;
    private String                         artifactVersion;
    private ExtRuntimeLocalizationConstant constant;
    private TemplateAgent                  templateAgent;
    private CreateProjectProvider          createProjectProvider;

    /**
     * Create presenter.
     * 
     * @param view
     * @param resources
     * @param constant
     * @param templateAgent
     */
    @Inject
    protected ExtensionPagePresenter(ExtensionPageView view,
                                     ExtRuntimeResources resources, ExtRuntimeLocalizationConstant constant,
                                     TemplateAgent templateAgent) {
        super("Define the properties of a new Maven module", resources.codenvyExtensionTemplate());
        this.view = view;
        this.view.setDelegate(this);
        this.constant = constant;
        this.templateAgent = templateAgent;
    }

    /** {@inheritDoc} */
    @Override
    public void onGroupIdChanged() {
        groupId = view.getGroupId();
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onArtifactIdChanged() {
        artifactId = view.getArtifactId();
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void onVersionChanged() {
        artifactVersion = view.getVersion();
        delegate.updateControls();
    }

    /** Validate entered information on view. */
    public boolean validate() {
        return view.getGroupId() != null && !view.getGroupId().isEmpty()
               && view.getArtifactId() != null && !view.getArtifactId().isEmpty()
               && view.getVersion() != null && !view.getVersion().isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public WizardPagePresenter flipToNext() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFinish() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasNext() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCompleted() {
        return validate();
    }

    /** {@inheritDoc} */
    @Override
    public String getNotice() {
        if (view.getGroupId().isEmpty()) {
            return "Please, specify groupId.";
        } else if (view.getArtifactId().isEmpty()) {
            return "Please, specify artifactId.";
        } else if (view.getVersion().isEmpty()) {
            return "Please, specify version.";
        }

        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        createProjectProvider = templateAgent.getSelectedTemplate().getCreateProjectProvider();
        final String projectName = createProjectProvider.getProjectName();

        view.setGroupId(projectName);
        groupId = view.getGroupId();
        view.setArtifactId(projectName);
        artifactId = view.getArtifactId();
        view.setVersion(DEFAULT_VERSION);
        artifactVersion = view.getVersion();

        container.setWidget(view);
        delegate.updateControls();
    }

    /** {@inheritDoc} */
    @Override
    public void doFinish() {
        createProjectProvider.create(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                // nothing to do
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ExtensionPagePresenter.class, caught);
            }
        });
    }

}
