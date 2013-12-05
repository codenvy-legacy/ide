/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.client;

import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.dto.DtoFactoryVisitor;
import com.codenvy.ide.collections.StringMap;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Class responsible for register DTO providers. It uses {@link DtoFactoryVisitorRegistry} to acquire
 * {@link DtoFactoryVisitor}s.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
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
