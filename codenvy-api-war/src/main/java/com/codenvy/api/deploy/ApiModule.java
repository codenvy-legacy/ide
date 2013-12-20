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
package com.codenvy.api.deploy;

import com.codenvy.api.builder.BuilderAdminService;
import com.codenvy.api.builder.BuilderSelectionStrategy;
import com.codenvy.api.builder.BuilderService;
import com.codenvy.api.builder.LastInUseBuilderSelectionStrategy;
import com.codenvy.api.builder.internal.SlaveBuilderService;
import com.codenvy.api.core.rest.ApiExceptionMapper;
import com.codenvy.api.runner.LastInUseRunnerSelectionStrategy;
import com.codenvy.api.runner.RunnerAdminService;
import com.codenvy.api.runner.RunnerSelectionStrategy;
import com.codenvy.api.runner.RunnerService;
import com.codenvy.api.runner.internal.SlaveRunnerService;
import com.codenvy.api.vfs.server.ContentStreamWriter;
import com.codenvy.api.vfs.server.RequestContextResolver;
import com.codenvy.api.vfs.server.RequestValidator;
import com.codenvy.api.vfs.server.VirtualFileSystemFactory;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.exceptions.ConstraintExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.GitUrlResolveExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.ItemAlreadyExistExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.LocalPathResolveExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.LockExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.NotSupportedExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeExceptionMapper;
import com.codenvy.api.workspace.server.WorkspaceService;
import com.codenvy.inject.DynaModule;
import com.codenvy.vfs.impl.fs.EnvironmentContextLocalFSMountStrategy;
import com.codenvy.vfs.impl.fs.LocalFileSystemProvider;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;

import javax.swing.event.EventListenerList;

/** @author andrew00x */
@DynaModule
public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(WorkspaceService.class);
        Multibinder<VirtualFileSystemProvider> vfsBindings = Multibinder.newSetBinder(binder(), VirtualFileSystemProvider.class);
        vfsBindings.addBinding().toInstance(new LocalFileSystemProvider("dev-monit", new EnvironmentContextLocalFSMountStrategy()));
        bind(EventListenerList.class).toInstance(new EventListenerList());
        bind(RequestValidator.class).toProvider(Providers.<RequestValidator>of(null));
        bind(RequestContextResolver.class);
        bind(ContentStreamWriter.class).toInstance(new ContentStreamWriter());
        bind(ConstraintExceptionMapper.class).toInstance(new ConstraintExceptionMapper());
        bind(InvalidArgumentExceptionMapper.class).toInstance(new InvalidArgumentExceptionMapper());
        bind(LockExceptionMapper.class).toInstance(new LockExceptionMapper());
        bind(ItemNotFoundExceptionMapper.class).toInstance(new ItemNotFoundExceptionMapper());
        bind(ItemAlreadyExistExceptionMapper.class).toInstance(new ItemAlreadyExistExceptionMapper());
        bind(NotSupportedExceptionMapper.class).toInstance(new NotSupportedExceptionMapper());
        bind(PermissionDeniedExceptionMapper.class).toInstance(new PermissionDeniedExceptionMapper());
        bind(LocalPathResolveExceptionMapper.class).toInstance(new LocalPathResolveExceptionMapper());
        bind(GitUrlResolveExceptionMapper.class).toInstance(new GitUrlResolveExceptionMapper());
        bind(VirtualFileSystemRuntimeExceptionMapper.class).toInstance(new VirtualFileSystemRuntimeExceptionMapper());
        bind(VirtualFileSystemFactory.class);
        bind(ApiExceptionMapper.class).toInstance(new ApiExceptionMapper());
        bind(BuilderSelectionStrategy.class).toInstance(new LastInUseBuilderSelectionStrategy());
        bind(BuilderService.class);
        bind(BuilderAdminService.class);
        bind(SlaveBuilderService.class);
        bind(RunnerSelectionStrategy.class).toInstance(new LastInUseRunnerSelectionStrategy());
        bind(RunnerService.class);
        bind(RunnerAdminService.class);
        bind(SlaveRunnerService.class);
    }
}
