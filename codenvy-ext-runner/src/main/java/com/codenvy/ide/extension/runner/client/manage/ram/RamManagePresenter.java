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
package com.codenvy.ide.extension.runner.client.manage.ram;

import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentUser;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.extension.runner.client.RunnerExtension;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;
import java.util.Map;

/**
 * @author Vitaly Parfonov
 */
public class RamManagePresenter extends AbstractPreferencesPagePresenter implements RamManagerView.ActionDelegate {

    private RunnerLocalizationConstant localizationConstant;
    private RunnerResources            resources;
    private UserProfileServiceClient   userProfileService;
    private RamManagerView             view;
    private DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private AppContext                 appContext;
    private boolean dirty = false;

    /**
     * Create preference page.
     */
    @Inject
    public RamManagePresenter(RunnerLocalizationConstant localizationConstant,
                              RunnerResources resources,
                              UserProfileServiceClient userProfileService,
                              RamManagerView view,
                              DtoUnmarshallerFactory dtoUnmarshallerFactory,
                              AppContext appContext) {
        super(localizationConstant.titlesRamManager(), null);
        this.localizationConstant = localizationConstant;
        this.resources = resources;
        this.userProfileService = userProfileService;
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.appContext = appContext;
        this.view.setDelegate(this);
    }

    @Override
    public void doApply() {
        save();
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void setDirty(boolean b) {
        this.dirty = b;
        delegate.onDirtyChanged();
    }

    @Override
    public void validateRamSize(String value) {
        try {
            int ram = Integer.parseInt(value);
            if (ram > 0 && ram % 128 == 0) {
                setDirty(true);
            } else {
                view.showWarnMessage(localizationConstant.ramSizeMustBeMultipleOf("128"));
                setDirty(false);
            }
            delegate.onDirtyChanged();
        } catch (NumberFormatException e) {
            Log.error(RamManagePresenter.class, e.getMessage());
            view.showWarnMessage(localizationConstant.enteredValueNotCorrect());
        }
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        userProfileService.getCurrentProfile(null, new AsyncRequestCallback<ProfileDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(
                ProfileDescriptor.class)) {
            @Override
            protected void onSuccess(ProfileDescriptor result) {
                Map<String, String> preferences = result.getPreferences();

                if (preferences.containsKey(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT)) {
                    String ram = preferences.get(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT);
                    view.showRam(ram);
                }

            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(RamManagePresenter.class, exception);
            }
        });
    }


    private void save() {
        userProfileService.getCurrentProfile(null, new AsyncRequestCallback<ProfileDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(
                ProfileDescriptor.class)) {
            @Override
            protected void onSuccess(ProfileDescriptor result) {
                Map<String, String> preferences = result.getPreferences();
                preferences.put(RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT, view.getRam());

                userProfileService
                        .updatePreferences(preferences, new AsyncRequestCallback<ProfileDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(
                                ProfileDescriptor.class)) {
                            @Override
                            protected void onSuccess(ProfileDescriptor result) {
                                CurrentUser currentUser = appContext.getCurrentUser() == null ? new CurrentUser()
                                                                                              : appContext.getCurrentUser();
                                currentUser.setProfile(result);
                                appContext.setCurrentUser(currentUser);
                            }

                            @Override
                            protected void onFailure(Throwable exception) {

                            }
                        });


            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(RamManagePresenter.class, exception);
            }
        });

    }

}
