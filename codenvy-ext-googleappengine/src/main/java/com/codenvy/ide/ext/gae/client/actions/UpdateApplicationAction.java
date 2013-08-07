package com.codenvy.ide.ext.gae.client.actions;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.gae.client.GAEExtension;
import com.codenvy.ide.ext.gae.client.GAEResources;
import com.codenvy.ide.ext.gae.client.create.CreateApplicationPresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class UpdateApplicationAction extends Action {
    private CreateApplicationPresenter presenter;
    private ResourceProvider           resourceProvider;

    @Inject

    public UpdateApplicationAction(CreateApplicationPresenter presenter,
                                   ResourceProvider resourceProvider, GAEResources resources) {
        super("Update application...", "Update application on Google App Engine.", resources.updateApplication());

        this.presenter = presenter;
        this.resourceProvider = resourceProvider;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        @NotNull
        Project project = resourceProvider.getActiveProject();

        presenter.deploy(project);
    }

    @Override
    public void update(ActionEvent e) {
        Project project = resourceProvider.getActiveProject();

        e.getPresentation().setEnabled(GAEExtension.isAppEngineProject(project));
    }
}
