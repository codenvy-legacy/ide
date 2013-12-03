package com.codenvy.api.bootstrap.servlet;

import com.codenvy.api.builder.BuilderAdminService;
import com.codenvy.api.builder.BuilderService;
import com.codenvy.api.builder.internal.SlaveBuilderService;
import com.codenvy.api.core.rest.ApiExceptionMapper;
import com.codenvy.api.runner.RunnerAdminService;
import com.codenvy.api.runner.RunnerService;
import com.codenvy.api.runner.internal.SlaveRunnerService;
import com.codenvy.api.vfs.server.ContentStreamWriter;
import com.codenvy.api.vfs.server.RequestContextResolver;
import com.codenvy.api.vfs.server.VirtualFileSystemFactory;
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

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class ApiDeployApplication extends Application {
    private final Set<Class<?>> classes;
    private final Set<Object>   singletons;

    public ApiDeployApplication() {
        classes = new HashSet<>();
        singletons = new HashSet<>();
        classes.add(WorkspaceService.class);
        classes.add(RequestContextResolver.class);
        classes.add(VirtualFileSystemFactory.class);

        classes.add(SlaveBuilderService.class);
        classes.add(BuilderService.class);
        classes.add(BuilderAdminService.class);
        classes.add(SlaveRunnerService.class);
        classes.add(RunnerService.class);
        classes.add(RunnerAdminService.class);
        singletons.add(new ApiExceptionMapper());
        singletons.add(new ContentStreamWriter());
        singletons.add(new ConstraintExceptionMapper());
        singletons.add(new InvalidArgumentExceptionMapper());
        singletons.add(new LockExceptionMapper());
        singletons.add(new ItemNotFoundExceptionMapper());
        singletons.add(new ItemAlreadyExistExceptionMapper());
        singletons.add(new NotSupportedExceptionMapper());
        singletons.add(new PermissionDeniedExceptionMapper());
        singletons.add(new LocalPathResolveExceptionMapper());
        singletons.add(new GitUrlResolveExceptionMapper());
        singletons.add(new VirtualFileSystemRuntimeExceptionMapper());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
