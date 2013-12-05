package com.codenvy.ide.extension.demo.actions;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.extension.demo.GistExtensionLocalizationConstant;
import com.codenvy.ide.extension.demo.GistExtensionResources;
import com.codenvy.ide.extension.demo.createGist.CreateGistPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Action for creating Gist on GitHub. */
@Singleton
public class CreateGistAction extends Action {
    private CreateGistPresenter createGistPresenter;

    @Inject
    public CreateGistAction(GistExtensionResources resources,
                            CreateGistPresenter createGistPresenter,
                            GistExtensionLocalizationConstant localizationConstants) {
        super(localizationConstants.createGistActionText(), localizationConstants.createGistActionDescription(), resources.github());
        this.createGistPresenter = createGistPresenter;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        createGistPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        e.getPresentation().setEnabledAndVisible(true);
    }
}