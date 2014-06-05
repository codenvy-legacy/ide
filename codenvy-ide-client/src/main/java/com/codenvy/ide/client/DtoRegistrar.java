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
package com.codenvy.ide.client;

import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.dto.DtoFactoryVisitor;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Class responsible for register DTO providers. It uses {@link DtoFactoryVisitorRegistry} to acquire
 * {@link DtoFactoryVisitor}s.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DtoRegistrar {
    private final DtoFactory                dtoFactory;
    private final DtoFactoryVisitorRegistry dtoFactoryVisitorRegistry;

    @Inject
    public DtoRegistrar(DtoFactory dtoFactory, DtoFactoryVisitorRegistry dtoFactoryVisitorRegistry) {
        this.dtoFactory = dtoFactory;
        this.dtoFactoryVisitorRegistry = dtoFactoryVisitorRegistry;
    }

    public void registerDtoProviders() {
        dtoFactoryVisitorRegistry.getDtoFactoryVisitors().iterate(
                new StringMap.IterationCallback<Provider>() {
                    @Override
                    public void onIteration(String dtoFactoryVisitorFqn, Provider dtoFactoryVisitorProvider) {
                        ((DtoFactoryVisitor)dtoFactoryVisitorProvider.get()).accept(dtoFactory);
                    }
                });
    }
}
