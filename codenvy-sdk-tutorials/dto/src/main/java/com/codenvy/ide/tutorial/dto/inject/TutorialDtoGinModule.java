package com.codenvy.ide.tutorial.dto.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.tutorial.dto.createGist.CreateGistView;
import com.codenvy.ide.tutorial.dto.createGist.CreateGistViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

@ExtensionGinModule
public class TutorialDtoGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(CreateGistView.class).to(CreateGistViewImpl.class).in(Singleton.class);
    }
}