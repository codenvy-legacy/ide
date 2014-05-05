/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.VirtualFile;
import com.codenvy.api.vfs.server.VirtualFileFilter;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.inject.DynaModule;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/** @author andrew00x */
@DynaModule
public class SearcherVirtualFileFilterModule extends AbstractModule {
    @Override
    protected void configure() {
        final Multibinder<VirtualFileFilter> multibinder =
                Multibinder.newSetBinder(binder(), VirtualFileFilter.class, Names.named("vfs.index_filter"));

        multibinder.addBinding().toInstance(new VirtualFileFilter() {
            @Override
            public boolean accept(VirtualFile virtualFile) throws VirtualFileSystemException {
                return !virtualFile.getPath().endsWith("/.codenvy/misc.xml");
            }
        });

        bind(EventSubscriberRegister.class).asEagerSingleton();

        Multibinder<com.codenvy.api.core.notification.EventSubscriber> subscriptionServiceBinder =
                Multibinder.newSetBinder(binder(), com.codenvy.api.core.notification.EventSubscriber.class);

        subscriptionServiceBinder.addBinding().to(SynchronizerVFSWorkspace.RemoverVFSRoot.class);
        subscriptionServiceBinder.addBinding().to(SynchronizerVFSWorkspace.CreatorVFSRoot.class);

        subscriptionServiceBinder.addBinding().to(_IdeOldCacheUpdater_.class);
    }
}
