package com.codenvy.ide.ext.gae.client.actions;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.gae.client.GAEResources;
import com.codenvy.ide.ext.gae.client.project.ProjectPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action for "Project/PaaS/Google App Engine Application..." to allow user to manage deployed application on Google
 * Services.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class ManageApplicationAction extends Action {
    private ProjectPresenter presenter;
    private ResourceProvider resourceProvider;

    /**
     * Constructor for action.
     */
    @Inject
    public ManageApplicationAction(ProjectPresenter presenter,
                                   ResourceProvider resourceProvider, GAEResources resources) {
        super("Google App Engine Application...", "Manage Google App Engine application.", resources.googleAppEngine());
        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        presenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        Project project = resourceProvider.getActiveProject();

        e.getPresentation().setVisible(project != null && project.hasProperty("gae-application"));
    }
}
