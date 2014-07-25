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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.builder.BuildStatus;
import com.codenvy.api.builder.BuilderException;
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.core.rest.HttpJsonHelper;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.lang.Pair;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.commons.user.User;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.matching.JavaSearchNameEnvironment;
import com.google.inject.name.Named;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CodenvyCompilationUnitResolver;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.internal.compiler.lookup.SourceTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.core.INameEnvironmentWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rest service for WorkerNameEnvironment
 *
 * @author Evgen Vidolob
 */
@javax.ws.rs.Path("java-name-environment/{ws-id}")
public class RestNameEnvironment {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(RestNameEnvironment.class);

    @Inject
    private WorkspaceHashLocalFSMountStrategy fsMountStrategy;

    @Inject
    private JavaProjectService javaProjectService;

    @Inject
    @Named("project.temp")
    private String temp;

    @PathParam("ws-id")
    @Inject
    private String wsId;

    @Inject
    @Named("api.endpoint")
    private String apiUrl;
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @javax.ws.rs.Path("findTypeCompound")
    public String findTypeCompound(@QueryParam("compoundTypeName") String compoundTypeName, @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();
        try {
            NameEnvironmentAnswer answer = environment.findType(getCharArrayFrom(compoundTypeName));
            if (answer == null && compoundTypeName.contains("$")) {
                String innerName = compoundTypeName.substring(compoundTypeName.indexOf('$') + 1, compoundTypeName.length());
                compoundTypeName = compoundTypeName.substring(0, compoundTypeName.indexOf('$'));
                answer = environment.findType(getCharArrayFrom(compoundTypeName));
                if (!answer.isCompilationUnit()) return null;
                ICompilationUnit compilationUnit = answer.getCompilationUnit();
                CompilationUnit result = getCompilationUnit(javaProject, environment, compilationUnit);
                AbstractTypeDeclaration o = (AbstractTypeDeclaration)result.types().get(0);
                ITypeBinding typeBinding = o.resolveBinding();

                for (ITypeBinding binding : typeBinding.getDeclaredTypes()) {
                    if (binding.getBinaryName().endsWith(innerName)) {
                        typeBinding = binding;
                        break;
                    }
                }
                Map<TypeBinding, ?> bindings = (Map<TypeBinding, ?>)result.getProperty("compilerBindingsToASTBindings");
                SourceTypeBinding binding = null;
                for (Map.Entry<TypeBinding, ?> entry : bindings.entrySet()) {
                    if (entry.getValue().equals(typeBinding)) {
                        binding = (SourceTypeBinding)entry.getKey();
                        break;
                    }
                }
                return TypeBindingConvector.toJsonBinaryType(binding);
            }

            return processAnswer(answer, javaProject, environment);
        } catch (JavaModelException e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Can't parse class: ", e);
            }
            throw new WebApplicationException();
        }
    }

    private JavaProject getJavaProject(String projectPath) {
        return javaProjectService.getOrCreateJavaProject(wsId, projectPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @javax.ws.rs.Path("findType")
    public String findType(@QueryParam("typename") String typeName, @QueryParam("packagename") String packageName,
                           @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();

        NameEnvironmentAnswer answer = environment.findType(typeName.toCharArray(), getCharArrayFrom(packageName));
        try {
            return processAnswer(answer, javaProject, environment);
        } catch (JavaModelException e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Can't parse class: ", e);
            }
            throw new WebApplicationException(e);
        }
    }

    @GET
    @javax.ws.rs.Path("package")
    @Produces("text/plain")
    public String isPackage(@QueryParam("packagename") String packageName, @QueryParam("parent") String parentPackageName,
                            @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();
        return String.valueOf(environment.isPackage(getCharArrayFrom(parentPackageName), packageName.toCharArray()));
    }

    @GET
    @Path("findPackages")
    @Produces(MediaType.APPLICATION_JSON)
    public String findPackages(@QueryParam("packagename") String packageName, @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();
        JsonSearchRequester requestor = new JsonSearchRequester();
        environment.findPackages(packageName.toCharArray(), requestor);
        return requestor.toJsonString();
    }

    @GET
    @javax.ws.rs.Path("findConstructor")
    @Produces(MediaType.APPLICATION_JSON)
    public String findConstructorDeclarations(@QueryParam("prefix") String prefix,
                                              @QueryParam("camelcase") boolean camelCaseMatch,
                                              @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();
        JsonSearchRequester searchRequester = new JsonSearchRequester();
        environment.findConstructorDeclarations(prefix.toCharArray(), camelCaseMatch, searchRequester, null);
        return searchRequester.toJsonString();
    }

    @GET
    @javax.ws.rs.Path("findTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public String findTypes(@QueryParam("qualifiedname") String qualifiedName, @QueryParam("findmembers") boolean findMembers,
                            @QueryParam("camelcase") boolean camelCaseMatch,
                            @QueryParam("searchfor") int searchFor,
                            @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();
        JsonSearchRequester searchRequester = new JsonSearchRequester();
        environment.findTypes(qualifiedName.toCharArray(), findMembers, camelCaseMatch, searchFor, searchRequester);
        return searchRequester.toJsonString();
    }

    @GET
    @javax.ws.rs.Path("findExactTypes")
    @Produces(MediaType.APPLICATION_JSON)
    public String findExactTypes(@QueryParam("missingsimplename") String missingSimpleName, @QueryParam("findmembers") boolean findMembers,
                                 @QueryParam("searchfor") int searchFor,
                                 @QueryParam("projectpath") String projectPath) {
        JavaProject javaProject = getJavaProject(projectPath);
        JavaSearchNameEnvironment environment = javaProject.getNameEnvironment();
        JsonSearchRequester searchRequester = new JsonSearchRequester();
        environment.findExactTypes(missingSimpleName.toCharArray(), findMembers, searchFor, searchRequester);
        return searchRequester.toJsonString();
    }

    /** Get list of all package names in project */
    @GET
    @javax.ws.rs.Path("/update-dependencies")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateDependency(@QueryParam("projectpath") String projectPath, @Context UriInfo uriInfo) throws Exception {
//        com.codenvy.api.project.server.Project project = projectManager.getProject(wsId, projectPath);
        File workspace = fsMountStrategy.getMountPath(wsId);
        File project = new File(workspace, projectPath);
        if (!project.exists()) {
            LOG.warn("Project doesn't exist in workspace: " + wsId + ", path: " + projectPath);
            throw new CodeAssistantException(500, "Project doesn't exist");
        }

        if (!hasPom(project)) {
            LOG.warn("Project doesn't have pom.xml file");
            throw new CodeAssistantException(500, "Project doesn't have pom.xml file");
        }



        String url = apiUrl +  "/builder/" + wsId + "/dependencies";
        BuildTaskDescriptor buildStatus = getDependencies(url, projectPath, "copy");

        if (buildStatus.getStatus() == BuildStatus.FAILED) {
            buildFailed(buildStatus);
        }
        File projectDepDir = new File(temp, projectPath);
        if (projectDepDir.exists()) {
            IoUtil.deleteRecursive(projectDepDir);
        }

        projectDepDir.mkdirs();
        projectDepDir.deleteOnExit();
        Link downloadLink = findLink("download result", buildStatus.getLinks());
        if (downloadLink != null) {
            InputStream stream = doDownload(downloadLink.getHref());
            ZipUtils.unzip(stream, projectDepDir);
            javaProjectService.removeProject(wsId, projectPath);
        }
    }

    private InputStream doDownload(String downloadURL) throws IOException {
        HttpURLConnection http = null;
        try {
            URI uri = UriBuilder.fromUri(downloadURL).queryParam("token", getAuthenticationToken()).build();
            http = (HttpURLConnection)uri.toURL().openConnection();
            http.setRequestMethod("GET");
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Can't download zipped dependencies");
            }
            // Connection closed automatically when input stream closed.
            // If IOException or BuilderException occurs then connection closed immediately.
            return new HttpStream(http);
        } catch (MalformedURLException e) {
            throw e;
        } catch (IOException ioe) {
            if (http != null) {
                http.disconnect();
            }
            throw ioe;
        }

    }

    private static String getAuthenticationToken() {
        User user = EnvironmentContext.getCurrent().getUser();
        if (user != null) {
            return user.getToken();
        }
        return null;
    }

    private boolean hasPom(File project) {
        return new File(project, "pom.xml").exists();
//        return project.getBaseFolder().getChild("pom.xml") != null;
    }

    private void buildFailed(@Nullable BuildTaskDescriptor buildStatus) throws BuilderException {
        if (buildStatus != null) {
            Link logLink = findLink("view build log", buildStatus.getLinks());
            LOG.error("Build failed see more detail here: " + logLink.getHref());
            throw new BuilderException(
                    "Build failed see more detail here: <a href=\"" + logLink.getHref() + "\" target=\"_blank\">" + logLink.getHref() +
                    "</a>"
            );
        }
        throw new BuilderException("Build failed");
    }

    @Nullable
    private Link findLink(@NotNull String rel, List<Link> links) {
        for (Link link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }

    @NotNull
    private BuildTaskDescriptor waitTaskFinish(@NotNull BuildTaskDescriptor buildDescription) throws Exception {
        BuildTaskDescriptor request = buildDescription;
        final int sleepTime = 2000;

        Link statusLink = findLink("get status", buildDescription.getLinks());

        if (statusLink != null) {
            while (request.getStatus() == BuildStatus.IN_PROGRESS || request.getStatus() == BuildStatus.IN_QUEUE) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException ignored) {
                }
                request = HttpJsonHelper.request(BuildTaskDescriptor.class, statusLink);
            }
        }

        return request;
    }


    @NotNull
    private BuildTaskDescriptor getDependencies(@NotNull String url, @NotNull String projectName, @NotNull String analyzeType)
            throws Exception {
        Pair<String, String> projectParam = Pair.of("project", projectName);
        Pair<String, String> typeParam = Pair.of("type", analyzeType);
        BuildTaskDescriptor buildStatus = HttpJsonHelper.request(BuildTaskDescriptor.class, url, "POST", null, projectParam, typeParam);
        buildStatus = waitTaskFinish(buildStatus);
        return buildStatus;
    }


    private String processAnswer(NameEnvironmentAnswer answer, IJavaProject project, INameEnvironmentWithProgress environment)
            throws JavaModelException {
        if (answer == null) return null;
        if (answer.isBinaryType()) {
            IBinaryType binaryType = answer.getBinaryType();
            return BinaryTypeConvector.toJsonBinaryType(binaryType);
        } else if (answer.isCompilationUnit()) {
            ICompilationUnit compilationUnit = answer.getCompilationUnit();
            CompilationUnit result = getCompilationUnit(project, environment, compilationUnit);

            BindingASTVisitor visitor = new BindingASTVisitor();
            result.accept(visitor);
            Map<TypeBinding, ?> bindings = (Map<TypeBinding, ?>)result.getProperty("compilerBindingsToASTBindings");
            SourceTypeBinding binding = null;
            for (Map.Entry<TypeBinding, ?> entry : bindings.entrySet()) {
                if (entry.getValue().equals(visitor.typeBinding)) {
                    binding = (SourceTypeBinding)entry.getKey();
                    break;
                }
            }
            if (binding == null) return null;
            return TypeBindingConvector.toJsonBinaryType(binding);
        }
        return null;
    }

    private CompilationUnit getCompilationUnit(IJavaProject project, INameEnvironmentWithProgress environment,
                                               ICompilationUnit compilationUnit) throws JavaModelException {
        int flags = 0;
        flags |= org.eclipse.jdt.core.ICompilationUnit.ENABLE_STATEMENTS_RECOVERY;
        flags |= org.eclipse.jdt.core.ICompilationUnit.IGNORE_METHOD_BODIES;
        flags |= org.eclipse.jdt.core.ICompilationUnit.ENABLE_BINDINGS_RECOVERY;
        HashMap<String, String> opts = new HashMap<>(javaProjectService.getOptions());
        CompilationUnitDeclaration compilationUnitDeclaration =
                CodenvyCompilationUnitResolver.resolve(compilationUnit, project, environment, opts, flags, null);
        return CodenvyCompilationUnitResolver.convert(
                compilationUnitDeclaration,
                compilationUnit.getContents(),
                flags, opts);
    }

    private char[][] getCharArrayFrom(String list) {
        String[] strings = list.split(",");
        char[][] arr = new char[strings.length][];
        for (int i = 0; i < strings.length; i++) {
            String s = strings[i];
            arr[i] = s.toCharArray();
        }
        return arr;
    }

    /** Stream that automatically close HTTP connection when all data ends. */
    private static class HttpStream extends FilterInputStream {
        private final HttpURLConnection http;

        private boolean closed;

        private HttpStream(HttpURLConnection http) throws IOException {
            super(http.getInputStream());
            this.http = http;
        }

        @Override
        public int read() throws IOException {
            int r = super.read();
            if (r == -1) {
                close();
            }
            return r;
        }

        @Override
        public int read(byte[] b) throws IOException {
            int r = super.read(b);
            if (r == -1) {
                close();
            }
            return r;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int r = super.read(b, off, len);
            if (r == -1) {
                close();
            }
            return r;
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            try {
                super.close();
            } finally {
                http.disconnect();
                closed = true;
            }
        }
    }

    private static class BindingASTVisitor extends ASTVisitor {
        ITypeBinding typeBinding;

        public boolean visit(AnnotationTypeDeclaration annotationTypeDeclaration) {
            typeBinding = annotationTypeDeclaration.resolveBinding();
            return false;
        }

        public boolean visit(AnonymousClassDeclaration anonymousClassDeclaration) {
            typeBinding = anonymousClassDeclaration.resolveBinding();
            return false;
        }

        public boolean visit(TypeDeclaration typeDeclaration) {
            typeBinding = typeDeclaration.resolveBinding();
            return false;
        }

        public boolean visit(EnumDeclaration enumDeclaration) {
            typeBinding = enumDeclaration.resolveBinding();
            return false;
        }
    }
}
