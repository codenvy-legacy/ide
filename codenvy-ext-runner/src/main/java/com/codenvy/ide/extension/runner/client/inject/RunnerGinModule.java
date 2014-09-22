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
package com.codenvy.ide.extension.runner.client.inject;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleToolbar;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleView;
import com.codenvy.ide.extension.runner.client.console.RunnerConsoleViewImpl;
import com.codenvy.ide.extension.runner.client.manage.ram.RamManagePresenter;
import com.codenvy.ide.extension.runner.client.manage.ram.RamManagerView;
import com.codenvy.ide.extension.runner.client.manage.ram.RamManagerViewImpl;
import com.codenvy.ide.extension.runner.client.run.CustomRunView;
import com.codenvy.ide.extension.runner.client.run.CustomRunViewImpl;
import com.codenvy.ide.extension.runner.client.run.customimages.EditImagesView;
import com.codenvy.ide.extension.runner.client.run.customimages.EditImagesViewImpl;
import com.codenvy.ide.extension.runner.client.run.customimages.ImageActionFactory;
import com.codenvy.ide.extension.runner.client.run.customimages.ImageActionManager;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/** @author Artem Zatsarynnyy */
@ExtensionGinModule
public class RunnerGinModule extends AbstractGinModule {
    /** {@inheritDoc} */
    @Override
    protected void configure() {
        bind(CustomRunView.class).to(CustomRunViewImpl.class).in(Singleton.class);

        bind(RunnerConsoleView.class).to(RunnerConsoleViewImpl.class).in(Singleton.class);
        bind(ToolbarPresenter.class).annotatedWith(RunnerConsoleToolbar.class).to(ToolbarPresenter.class).in(Singleton.class);

        bind(RamManagerView.class).to(RamManagerViewImpl.class).in(Singleton.class);
        GinMultibinder<PreferencesPagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencesPagePresenter.class);
        prefBinder.addBinding().to(RamManagePresenter.class);

        bind(ImageActionManager.class).asEagerSingleton();
        install(new GinFactoryModuleBuilder().build(ImageActionFactory.class));
        bind(EditImagesView.class).to(EditImagesViewImpl.class).in(Singleton.class);
    }

    /** Provides project-relative path of the folder for user-defined recipes for runner. */
    @Provides
    @Named("recipesFolderPath")
    @Singleton
    protected String provideRecipesFolderRelPath() {
        return ".codenvy/recipes";
    }
}
