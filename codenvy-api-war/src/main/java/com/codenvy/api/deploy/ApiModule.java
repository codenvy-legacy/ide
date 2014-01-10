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
import com.codenvy.api.vfs.server.RequestValidator;
import com.codenvy.api.vfs.server.VirtualFileSystemFactory;
import com.codenvy.api.vfs.server.VirtualFileSystemProvider;
import com.codenvy.api.vfs.server.exceptions.ConstraintExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.ItemAlreadyExistExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.ItemNotFoundExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.LockExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.NotSupportedExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.PermissionDeniedExceptionMapper;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemRuntimeExceptionMapper;
import com.codenvy.api.vfs.server.observation.EventListenerList;
import com.codenvy.api.workspace.server.WorkspaceService;
import com.codenvy.commons.security.oauth.OAuthAuthenticationService;
import com.codenvy.commons.security.oauth.OAuthAuthenticatorProvider;
import com.codenvy.commons.security.oauth.OAuthAuthenticatorTokenProvider;
import com.codenvy.commons.security.oauth.OAuthTokenProvider;
import com.codenvy.ide.everrest.CodenvyAsynchronousJobPool;
import com.codenvy.ide.everrest.CodenvyAsynchronousJobService;
import com.codenvy.ide.ext.extensions.server.CreateProjectService;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.nativegit.NativeGitConnectionFactory;
import com.codenvy.ide.ext.git.server.rest.GitService;
import com.codenvy.ide.ext.github.server.rest.GitHubService;
import com.codenvy.ide.ext.java.jdi.server.DebuggerService;
import com.codenvy.ide.ext.java.server.CreateAntProjectService;
import com.codenvy.ide.ext.java.server.CreateMavenProjectService;
import com.codenvy.ide.ext.java.server.RestCodeAssistantJava;
import com.codenvy.ide.ext.ssh.server.DummySshKeyStore;
import com.codenvy.ide.ext.ssh.server.KeyService;
import com.codenvy.ide.ext.ssh.server.SshKeyStore;
import com.codenvy.ide.security.oauth.server.LabOAuthAuthenticatorProvider;
import com.codenvy.ide.server.UserService;
import com.codenvy.inject.DynaModule;
import com.codenvy.runner.sdk.SDKRunner;
import com.codenvy.runner.webapps.DeployToApplicationServerRunner;
import com.codenvy.vfs.impl.fs.EnvironmentContextLocalFSMountStrategy;
import com.codenvy.vfs.impl.fs.LocalFileSystemProvider;
import com.codenvy.vfs.impl.fs.exceptions.GitUrlResolveExceptionMapper;
import com.codenvy.vfs.impl.fs.exceptions.LocalPathResolveExceptionMapper;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Providers;

import org.everrest.core.impl.async.AsynchronousJobPool;

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
        bind(SDKRunner.class);
        bind(UserService.class);
        bind(CreateMavenProjectService.class);
        bind(CreateAntProjectService.class);
        bind(RestCodeAssistantJava.class);
        bind(DebuggerService.class);
        bind(AsynchronousJobPool.class).toInstance(new CodenvyAsynchronousJobPool(null)); // asynchronous job with default configuration
        bind(CodenvyAsynchronousJobService.class);
        bind(CreateProjectService.class);
        bind(GitService.class);
        bind(GitHubService.class);
        bind(GitConnectionFactory.class).to(NativeGitConnectionFactory.class);
        bind(KeyService.class);
        bind(OAuthAuthenticationService.class);
        bind(OAuthTokenProvider.class).to(OAuthAuthenticatorTokenProvider.class);
        bind(OAuthAuthenticatorProvider.class).to(LabOAuthAuthenticatorProvider.class);
        bind(SshKeyStore.class).to(DummySshKeyStore.class);
    }
}
