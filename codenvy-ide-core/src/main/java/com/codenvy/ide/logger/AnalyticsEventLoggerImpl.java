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
package com.codenvy.ide.logger;

import com.codenvy.api.analytics.shared.dto.EventParameters;
import com.codenvy.api.user.gwt.client.UserServiceClient;
import com.codenvy.api.user.shared.dto.User;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.ExtensionDescription;
import com.codenvy.ide.extension.ExtensionRegistry;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.CONTENTTYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * API to track Analytics events.
 *
 * @author Anatoliy Bazko
 */
@Singleton
public class AnalyticsEventLoggerImpl implements AnalyticsEventLoggerExt {
    private static final String IDE_EVENT          = "ide-usage";
    private static final String API_ANALYTICS_PATH = "/analytics/log/";

    private static final String WS_PARAM           = "WS";
    private static final String USER_PARAM         = "USER";
    private static final String SOURCE_PARAM       = "SOURCE";
    private static final String ACTION_PARAM       = "ACTION";
    private static final String PROJECT_NAME_PARAM = "PROJECT";
    private static final String PROJECT_TYPE_PARAM = "TYPE";

    private static final String EMPTY_PARAM_VALUE = "";

    private final DtoFactory             dtoFactory;
    private final UserServiceClient      user;
    private final ResourceProvider       resourceProvider;
    private final MessageBus             messageBus;
    private final ExtensionRegistry      extensionRegistry;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;

    private String currentUser;

    @Inject
    public AnalyticsEventLoggerImpl(DtoFactory dtoFactory,
                                    ExtensionRegistry extensionRegistry,
                                    UserServiceClient user,
                                    ResourceProvider resourceProvider,
                                    MessageBus messageBus,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.dtoFactory = dtoFactory;
        this.user = user;
        this.resourceProvider = resourceProvider;
        this.messageBus = messageBus;
        this.extensionRegistry = extensionRegistry;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;

        saveCurrentUser();
    }

    @Override
    public void log(Class<?> extensionClass, String action, Map<String, String> additionalParams) {
        doLog(IDE_EVENT, action, getSource(extensionClass), additionalParams);
    }

    @Override
    public void log(Class<?> extensionClass, String action) {
        doLog(IDE_EVENT, action, getSource(extensionClass), Collections.<String, String>emptyMap());
    }

    @Override
    public void log(String action) {
        doLog(IDE_EVENT, action, null, Collections.<String, String>emptyMap());
    }

    @Override
    public void logEvent(String event, Map<String, String> additionalParams) {
        doLog(event, null, null, additionalParams);
    }

    private void doLog(String event,
                       @Nullable String action,
                       @Nullable String source,
                       Map<String, String> additionalParams) {

        validate(action, additionalParams);

        additionalParams = new HashMap<>(additionalParams);
        putReservedParameters(action, source, additionalParams);

        send(event, additionalParams);
    }

    private void putReservedParameters(@Nullable String action,
                                       @Nullable String source,
                                       Map<String, String> additionalParams) {
        Project project = resourceProvider.getActiveProject();
        if (project != null) {
            putIfNotNull(PROJECT_NAME_PARAM, project.getName(), additionalParams);
            putIfNotNull(PROJECT_TYPE_PARAM, project.getDescription().getProjectTypeId(), additionalParams);
        }

        putIfNotNull(USER_PARAM, currentUser, additionalParams);
        putIfNotNull(WS_PARAM, Config.getWorkspaceName(), additionalParams);
        putIfNotNull(ACTION_PARAM, action, additionalParams);
        putIfNotNull(SOURCE_PARAM, source, additionalParams);
    }

    private void putIfNotNull(String key,
                              @Nullable String value,
                              Map<String, String> additionalParams) {
        if (value != null) {
            additionalParams.put(key, value);
        }
    }

    private void validate(@Nullable String action, Map<String, String> additionalParams) throws IllegalArgumentException {
        if (additionalParams.size() > MAX_PARAMS_NUMBER) {
            throw new IllegalArgumentException("The number of parameters exceeded the limit in " + MAX_PARAMS_NUMBER);
        }

        for (Map.Entry<String, String> entry : additionalParams.entrySet()) {
            String param = entry.getKey();
            String value = entry.getValue();

            if (param.length() > MAX_PARAM_NAME_LENGTH) {
                throw new IllegalArgumentException(
                        "The length of parameter name " + param + " exceeded the length in "
                        + MAX_PARAM_NAME_LENGTH + " characters");

            } else if (value.length() > MAX_PARAM_VALUE_LENGTH) {
                throw new IllegalArgumentException(
                        "The length of parameter value " + value + " exceeded the length in "
                        + MAX_PARAM_VALUE_LENGTH + " characters");
            }
        }

        if (action != null && action.length() > MAX_PARAM_VALUE_LENGTH) {
            throw new IllegalArgumentException("The length of action name exceeded the length in "
                                               + MAX_PARAM_VALUE_LENGTH + " characters");

        }
    }

    private String getSource(Class<?> extensionClass) {
        ExtensionDescription description = extensionRegistry.getExtensionDescriptions().get(extensionClass.getName());
        return description != null ? description.getTitle() : EMPTY_PARAM_VALUE;
    }


    private void saveCurrentUser() {
        user.getCurrentUser(new AsyncRequestCallback<User>(dtoUnmarshallerFactory.newUnmarshaller(User.class)) {
            @Override
            protected void onSuccess(User result) {
                if (result != null) {
                    currentUser = result.getEmail();
                } else {
                    currentUser = EMPTY_PARAM_VALUE;
                }
            }

            @Override
            protected void onFailure(Throwable exception) {
                currentUser = EMPTY_PARAM_VALUE;
            }
        });
    }

    private void send(String event, Map<String, String> parameters) {
        EventParameters additionalParams = dtoFactory.createDto(EventParameters.class).withParams(parameters);
        final String json = dtoFactory.toJson(additionalParams);

        MessageBuilder builder = new MessageBuilder(POST, API_ANALYTICS_PATH + event);
        builder.data(json);
        builder.header(CONTENTTYPE, APPLICATION_JSON);
        Message message = builder.build();

        try {
            messageBus.send(message, new RequestCallback() {
                @Override
                protected void onSuccess(Object result) {
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(getClass(), exception.getMessage());
                    Log.info(getClass(), json);
                }
            });
        } catch (Exception e) {
            Log.error(getClass(), e.getMessage());
            Log.info(getClass(), json);
        }
    }
}
