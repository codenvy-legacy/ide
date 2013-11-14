package com.codenvy.ide.tutorial.dto.actions;

import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.tutorial.dto.TutorialDtoLocalizationConstant;
import com.codenvy.ide.tutorial.dto.TutorialDtoResources;
import com.codenvy.ide.tutorial.dto.createGist.CreateGistPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Action for creating Gist on GitHub. */
@Singleton
public class CreateGistAction extends Action {

    private CreateGistPresenter createGistPresenter;

    @Inject
    public CreateGistAction(TutorialDtoResources resources,
                            CreateGistPresenter createGistPresenter,
                            TutorialDtoLocalizationConstant localizationConstants) {
        super(localizationConstants.createGistActionText(), localizationConstants.createGistActionDescription(),
              resources.github());
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
