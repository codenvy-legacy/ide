/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.java.server;

import com.codenvy.api.builder.BuildStatus;
import com.codenvy.api.builder.BuilderException;
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.core.rest.HttpJsonHelper;
import com.codenvy.api.core.rest.RemoteException;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.project.server.AbstractVirtualFileEntry;
import com.codenvy.api.project.server.ProjectManager;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.internal.codeassist.impl.AssistOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.matching.JavaSearchNameEnvironment;

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
import org.everrest.core.impl.provider.json.JsonException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.File;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rest service for {@link com.codenvy.ide.ext.java.worker.WorkerNameEnvironment}
 *
 * @author Evgen Vidolob
 */
@javax.ws.rs.Path("java-name-environment/{ws-id}")
public class RestNameEnvironment {
    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(RestNameEnvironment.class);

    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static Map<String, String> options = new HashMap<>();

    static {
        options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
        options.put(JavaCore.CORE_ENCODING, "UTF-8");
        options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
        options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_7);
        options.put(AssistOptions.OPTION_PerformVisibilityCheck, AssistOptions.ENABLED);
        options.put(CompilerOptions.OPTION_ReportUnusedLocal, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_TaskTags, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_ReportUnusedPrivateMember, CompilerOptions.WARNING);
        options.put(CompilerOptions.OPTION_SuppressWarnings, CompilerOptions.DISABLED);
        options.put(JavaCore.COMPILER_TASK_TAGS, "TODO,FIXME,XXX");
        options.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
        options.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        options.put(CompilerOptions.OPTION_Process_Annotations, JavaCore.DISABLED);
    }

    @Inject
    ProjectManager projectManager;
    @PathParam("ws-id")
    @Inject
    private String wsId;

    private static void removeRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                if (exc == null) {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                } else {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
    }

//    private FSMountPoint getMountPoint() throws VirtualFileSystemException {
//        MountPoint mountPoint = vfsRegistry.getProvider(wsId).getMountPoint(true);
//        if (mountPoint instanceof FSMountPoint) {
//            return (FSMountPoint)mountPoint;
//        } else throw new IllegalStateException("This service works only with FSMountPoint class");
//    }

    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("findTypeCompound")
    public String findTypeCompound(@QueryParam("compoundTypeName") String compoundTypeName, @QueryParam("projectpath") String projectPath)
            throws VirtualFileSystemException {
        com.codenvy.api.project.server.Project project = projectManager.getProject(wsId, projectPath);
        JavaProject javaProject = new JavaProject(project, TEMP_DIR, projectManager, wsId);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);

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
                return TypeBindingConvetror.toJsonBinaryType(binding);
            }

            return processAnswer(answer, javaProject, environment);
        } catch (JavaModelException e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Can't parse class: ", e);
            }
            throw new WebApplicationException();
        }
    }

    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("findType")
    public String findType(@QueryParam("typename") String typeName, @QueryParam("packagename") String packageName,
                           @QueryParam("projectpath") String projectPath)
            throws VirtualFileSystemException {
        com.codenvy.api.project.server.Project project = projectManager.getProject(wsId, projectPath);
        JavaProject javaProject = new JavaProject(project, TEMP_DIR, projectManager, wsId);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);

        NameEnvironmentAnswer answer = environment.findType(typeName.toCharArray(), getCharArrayFrom(packageName));
        try {
            return processAnswer(answer, javaProject, environment);
        } catch (JavaModelException e) {
            if (LOG.isDebugEnabled()) {
                LOG.error("Can't parse class: ", e);
            }
            throw new WebApplicationException();
        }
    }

    @GET
    @javax.ws.rs.Path("package")
    @Produces("text/plain")
    public String isPackage(@QueryParam("packagename") String packageName, @QueryParam("parent") String parentPackageName,
                            @QueryParam("projectpath") String projectPath)
            throws VirtualFileSystemException {
        com.codenvy.api.project.server.Project project = projectManager.getProject(wsId, projectPath);
        JavaProject javaProject = new JavaProject(project, TEMP_DIR, projectManager, wsId);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);
        return String.valueOf(environment.isPackage(getCharArrayFrom(parentPackageName), packageName.toCharArray()));
    }

    /** Get list of all package names in project */
    @GET
    @javax.ws.rs.Path("/update-dependencies")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateDependency(@QueryParam("projectpath") String projectPath,
                                 @Context UriInfo uriInfo)
            throws CodeAssistantException, VirtualFileSystemException, IOException, JsonException, BuilderException {

        com.codenvy.api.project.server.Project project = projectManager.getProject(wsId, projectPath);


        if (project == null) {
            LOG.warn("Project doesn't exist in workspace: " + wsId + ", path: " + projectPath);
            throw new CodeAssistantException(500, "Project doesn't exist");
        }

        if (!hasPom(project)) {
            LOG.warn("Doesn't have pom.xml file");
            throw new CodeAssistantException(500, "Doesn't have pom.xml file");
        }

        URI uri = uriInfo.getBaseUri();
        String url = uri.getScheme() + "://" + uri.getHost();
        int port = uri.getPort();
        if (port > 0 && port != 80) {
            url += ":" + port;
        }
        url += "/api/builder/" + wsId + "/dependencies";
        try {

            BuildTaskDescriptor buildStatus = getDependencies(url, projectPath, "copy");

            if (buildStatus.getStatus() == BuildStatus.FAILED) {
                buildFailed(buildStatus);
            }
            File projectDepDir = new File(TEMP_DIR, project.getBaseFolder().getVirtualFile().getId());
            if (projectDepDir.exists()) {
                removeRecursive(projectDepDir.toPath());
            }

            projectDepDir.mkdirs();
            projectDepDir.deleteOnExit();
            Link downloadLink = findLink("download result", buildStatus.getLinks());
            if (downloadLink != null) {
                InputStream stream = doDownload(downloadLink.getHref());
                ZipUtils.unzip(stream, projectDepDir);
            }

        } catch (IOException e) {
            LOG.error("Error", e);
        }

    }


    private InputStream doDownload(String downloadURL) throws MalformedURLException, IOException {
        HttpURLConnection http = null;
        try {
            URL url = new URL(downloadURL);
            http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("GET");
            int responseCode = http.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Can't download zipped dependencys");
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

    private boolean hasPom(com.codenvy.api.project.server.Project project) throws VirtualFileSystemException {

        List<AbstractVirtualFileEntry> items = project.getBaseFolder().getChildren();
        for (int i = 0; i < items.size(); i++) {
            AbstractVirtualFileEntry f = items.get(i);
            if ("pom.xml".equals(f.getName()))
                return true;
        }
        return false;
    }

    private void buildFailed(@Nullable BuildTaskDescriptor buildStatus) throws BuilderException {
        if (buildStatus != null) {
            Link logLink = findLink("view build log", buildStatus.getLinks());
            LOG.error("Build failed see more detail here: " + logLink.getHref());
            throw new BuilderException(
                    "Build failed see more detail here: <a href=\"" + logLink.getHref() + "\" target=\"_blank\">" + logLink.getHref() +
                    "</a>");
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
    private BuildTaskDescriptor waitTaskFinish(@NotNull BuildTaskDescriptor buildDescription) throws IOException, RemoteException {
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
    private BuildTaskDescriptor getDependencies(@NotNull String url, @NotNull String projectName, @NotNull String analyzeType) {
        BuildTaskDescriptor buildStatus = null;
        try {
            Pair<String, String> projectParam = Pair.of("project", projectName);
            Pair<String, String> typeParam = Pair.of("type", analyzeType);
            buildStatus = HttpJsonHelper.request(BuildTaskDescriptor.class, url, "POST", null, projectParam, typeParam);
            buildStatus = waitTaskFinish(buildStatus);
        } catch (RemoteException | IOException e) {
            LOG.error("Error", e);
        }
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
            if(binding == null) return null;
            return TypeBindingConvetror.toJsonBinaryType(binding);
        }
        return null;
    }

    private CompilationUnit getCompilationUnit(IJavaProject project, INameEnvironmentWithProgress environment,
                                               ICompilationUnit compilationUnit) throws JavaModelException {
        int flags = 0;
        flags |= org.eclipse.jdt.core.ICompilationUnit.ENABLE_STATEMENTS_RECOVERY;
        flags |= org.eclipse.jdt.core.ICompilationUnit.IGNORE_METHOD_BODIES;
        flags |= org.eclipse.jdt.core.ICompilationUnit.ENABLE_BINDINGS_RECOVERY;
        HashMap<String, String> opts = new HashMap<>(options);
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
