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
package com.codenvy.api.deploy;

import com.codenvy.api.auth.oauth.OAuthTokenProvider;
import com.codenvy.api.builder.BuilderAdminService;
import com.codenvy.api.builder.BuilderSelectionStrategy;
import com.codenvy.api.builder.BuilderService;
import com.codenvy.api.builder.LastInUseBuilderSelectionStrategy;
import com.codenvy.api.builder.internal.SlaveBuilderService;
import com.codenvy.api.core.rest.ApiExceptionMapper;
import com.codenvy.api.project.server.ProjectService;
import com.codenvy.api.project.server.ProjectTypeDescriptionService;
import com.codenvy.api.runner.LastInUseRunnerSelectionStrategy;
import com.codenvy.api.runner.RunnerAdminService;
import com.codenvy.api.runner.RunnerSelectionStrategy;
import com.codenvy.api.runner.RunnerService;
import com.codenvy.api.runner.internal.SlaveRunnerService;
import com.codenvy.api.user.server.TokenValidator;
import com.codenvy.api.user.server.UserProfileService;
import com.codenvy.api.user.server.UserService;
import com.codenvy.api.vfs.server.ContentStreamWriter;
import com.codenvy.api.vfs.server.RequestValidator;
import com.codenvy.api.vfs.server.VirtualFileSystemFactory;
import com.codenvy.api.vfs.server.exceptions.ConstraintExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.ItemAlreadyExistExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.LockExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.NotSupportedExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeExceptionMapper;
import com.codenvy.api.vfs.server.search.SearcherProvider;
import com.codenvy.ide.env.TokenValidatorImpl;
import com.codenvy.ide.everrest.CodenvyAsynchronousJobPool;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitConnectionFactory;
import com.codenvy.ide.ext.git.server.rest.GitService;
import com.codenvy.ide.ext.github.server.oauth.GitHubOAuthAuthenticatorProvider;
import com.codenvy.ide.ext.github.server.rest.GitHubService;
import com.codenvy.ide.ext.ssh.server.KeyService;
import com.codenvy.ide.ext.ssh.server.SshKeyStore;
import com.codenvy.ide.ext.ssh.server.UserProfileSshKeyStore;
import com.codenvy.ide.security.oauth.server.LocalOAuthTokenProvider;
import com.codenvy.ide.security.oauth.server.OAuthAuthenticatorProvider;
import com.codenvy.inject.DynaModule;
import com.codenvy.runner.webapps.DeployToApplicationServerRunner;
import com.codenvy.vfs.impl.fs.CleanableSearcherProvider;
import com.codenvy.vfs.impl.fs.LocalFSMountStrategy;
import com.codenvy.vfs.impl.fs.LocalFileSystemRegistryPlugin;
import com.codenvy.vfs.impl.fs.WorkspaceHashLocalFSMountStrategy;
import com.codenvy.vfs.impl.fs.exceptions.GitUrlResolveExceptionMapper;
import com.codenvy.vfs.impl.fs.exceptions.LocalPathResolveExceptionMapper;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;

import org.everrest.core.impl.async.AsynchronousJobPool;
import org.everrest.core.impl.async.AsynchronousJobService;
import org.everrest.guice.PathKey;

/** @author andrew00x */
@DynaModule
public class ApiModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ProjectService.class);
        bind(ProjectTypeDescriptionService.class);
        bind(com.codenvy.api.project.server.ProjectImportersService.class);
        bind(LocalFileSystemRegistryPlugin.class);
        bind(com.codenvy.api.workspace.server.WorkspaceService.class);
        bind(LocalFSMountStrategy.class).to(WorkspaceHashLocalFSMountStrategy.class);
        bind(SearcherProvider.class).to(CleanableSearcherProvider.class);
        bind(RequestValidator.class).toProvider(Providers.<RequestValidator>of(null));
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
        bind(DeployToApplicationServerRunner.class);
        bind(UserService.class);
        bind(UserProfileService.class);
        bind(AsynchronousJobPool.class).to(CodenvyAsynchronousJobPool.class);
        bind(new PathKey<>(AsynchronousJobService.class, "/async/{ws-id}")).to(AsynchronousJobService.class);
        bind(GitService.class);
        bind(GitHubService.class);
        bind(GitConnectionFactory.class).to(NativeGitConnectionFactory.class);
        bind(KeyService.class);
        bind(SshKeyStore.class).to(UserProfileSshKeyStore.class);
        bind(OAuthTokenProvider.class).to(LocalOAuthTokenProvider.class);
        Multibinder<OAuthAuthenticatorProvider> oAuthAuthenticatorMultibinder =
                Multibinder.newSetBinder(binder(), OAuthAuthenticatorProvider.class);
        oAuthAuthenticatorMultibinder.addBinding().to(GitHubOAuthAuthenticatorProvider.class);
        bind(TokenValidator.class).to(TokenValidatorImpl.class);
    }
}
