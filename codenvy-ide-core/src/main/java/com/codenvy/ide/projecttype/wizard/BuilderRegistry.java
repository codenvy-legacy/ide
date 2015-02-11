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
package com.codenvy.ide.projecttype.wizard;

import com.codenvy.api.builder.dto.BuilderDescriptor;
import com.codenvy.api.builder.dto.BuilderEnvironment;
import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.json.JsonHelper;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;

/**
 * Helps to get name of the builder's default environment by builder name.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
class BuilderRegistry {
    private final StringMap<String> environments;

    @Inject
    BuilderRegistry(BuilderServiceClient builderServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        environments = Collections.createStringMap();

        final Unmarshallable<Array<BuilderDescriptor>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(BuilderDescriptor.class);
        builderServiceClient.getRegisteredServers(new AsyncRequestCallback<Array<BuilderDescriptor>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<BuilderDescriptor> result) {
                for (BuilderDescriptor builderDescriptor : result.asIterable()) {
                    for (BuilderEnvironment environment : builderDescriptor.getEnvironments().values()) {
                        if (environment.getIsDefault()) {
                            environments.put(builderDescriptor.getName(), environment.getDisplayName());
                            break;
                        }
                    }
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(getClass(), JsonHelper.parseJsonMessage(exception.getMessage()));
            }
        });
    }

    /** Returns display name of the default environment for the given builder. */
    String getDefaultEnvironmentName(@Nonnull String builderName) {
        return environments.get(builderName);
    }
}
