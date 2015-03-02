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

import com.codenvy.api.factory.dto.Factory;
import com.codenvy.api.factory.gwt.client.FactoryServiceClient;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.core.Component;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Callback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class FactoryComponent implements Component {

    private final FactoryServiceClient   factoryService;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private final AppContext             appContext;

    @Inject
    public FactoryComponent(FactoryServiceClient factoryService, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            AppContext appContext) {
        this.factoryService = factoryService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.appContext = appContext;
    }

    @Override
    public void start(final Callback<Component, Exception> callback) {
        String factoryParams = null;
        boolean encoded = false;
        if (Config.getStartupParam("id") != null) {
            factoryParams = Config.getStartupParam("id");
            encoded = true;
        } else if (Config.getStartupParam("v") != null) {
            factoryParams = Config.getStartupParams();
        }

        if (factoryParams != null) {
            factoryService.getFactory(factoryParams, encoded,
                                      new AsyncRequestCallback<Factory>(dtoUnmarshallerFactory.newUnmarshaller(Factory.class)) {
                                          @Override
                                          protected void onSuccess(Factory factory) {
                                              appContext.setFactory(factory);
                                              callback.onSuccess(null);
                                          }

                                          @Override
                                          protected void onFailure(Throwable error) {
                                              Log.error(FactoryComponent.class, "Unable to load Factory", error);
                                              callback.onSuccess(FactoryComponent.this);
                                          }
                                      }
                                     );
        } else {
            callback.onSuccess(this);
        }
    }
}
