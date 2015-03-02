/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.bootstrap;

import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.app.CurrentUser;
import com.codenvy.ide.core.Component;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class ProfileComponent implements Component {
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final UserProfileServiceClient userProfileService;
    private final CurrentUser              currentUser;

    @Inject
    public ProfileComponent(DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            UserProfileServiceClient userProfileService, CurrentUser currentUser) {
        this.userProfileService = userProfileService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.currentUser = currentUser;
    }

    @Override
    public void start(final Callback<Component, Exception> callback) {
        AsyncRequestCallback<ProfileDescriptor> asyncRequestCallback = new AsyncRequestCallback<ProfileDescriptor>(
                dtoUnmarshallerFactory.newUnmarshaller(ProfileDescriptor.class)) {
            @Override
            protected void onSuccess(final ProfileDescriptor profile) {
                currentUser.setProfile(profile);
                callback.onSuccess(ProfileComponent.this);
            }

            @Override
            protected void onFailure(Throwable error) {
                Log.error(ProfileComponent.class, "Unable to get Profile", error);
                callback.onFailure(new Exception("Unable to get Profile", error));
            }
        };
        userProfileService.getCurrentProfile(asyncRequestCallback);
    }
}
