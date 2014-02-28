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
import com.codenvy.api.vfs.server.MountPoint;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.api.vfs.shared.dto.Project;
import com.codenvy.commons.lang.ZipUtils;
import com.codenvy.ide.ext.java.server.internal.core.JavaProject;
import com.codenvy.ide.ext.java.server.internal.core.search.matching.JavaSearchNameEnvironment;
import com.codenvy.vfs.impl.fs.FSMountPoint;
import com.codenvy.vfs.impl.fs.VirtualFileImpl;

import org.eclipse.jdt.internal.compiler.env.IBinaryType;
import org.eclipse.jdt.internal.compiler.env.NameEnvironmentAnswer;
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
import java.util.List;

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

    @PathParam("ws-id")
    @Inject
    private String wsId;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    private FSMountPoint getMountPoint() throws VirtualFileSystemException {
        MountPoint mountPoint = vfsRegistry.getProvider(wsId).getMountPoint(true);
        if (mountPoint instanceof FSMountPoint) {
            return (FSMountPoint)mountPoint;
        } else throw new IllegalStateException("This service works only with FSMountPoint class");
    }

    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("findTypeCompound")
    public String findTypeCompound(@QueryParam("compoundTypeName") String compoundTypeName, @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException {
        VirtualFileImpl project = getMountPoint().getVirtualFileById(projectId);
        JavaProject javaProject = new JavaProject(project, TEMP_DIR);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);

        NameEnvironmentAnswer answer = environment.findType(getCharArrayFrom(compoundTypeName));
        return processAnswer(answer);
    }


    @GET
    @Produces("application/json")
    @javax.ws.rs.Path("findType")
    public String findType(@QueryParam("typename") String typeName, @QueryParam("packagename") String packageName,
                           @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException {
        VirtualFileImpl project = getMountPoint().getVirtualFileById(projectId);
        JavaProject javaProject = new JavaProject(project, TEMP_DIR);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);

        NameEnvironmentAnswer answer = environment.findType(typeName.toCharArray(), getCharArrayFrom(packageName));
        return processAnswer(answer);
    }

    @GET
    @javax.ws.rs.Path("package")
    @Produces("text/plain")
    public String isPackage(@QueryParam("packagename") String packageName, @QueryParam("parent") String parentPackageName,
                            @QueryParam("projectid") String projectId)
            throws VirtualFileSystemException {
        VirtualFileImpl project = getMountPoint().getVirtualFileById(projectId);
        JavaProject javaProject = new JavaProject(project, TEMP_DIR);
        JavaSearchNameEnvironment environment = new JavaSearchNameEnvironment(javaProject, null);
        return String.valueOf(environment.isPackage(getCharArrayFrom(parentPackageName), packageName.toCharArray()));
    }

    /** Get list of all package names in project */
    @GET
    @javax.ws.rs.Path("/update-dependencies")
    @Produces(MediaType.APPLICATION_JSON)
    public void updateDependency(@QueryParam("vfsid") String vfsId,
                                 @QueryParam("projectid") String projectId,
                                 @Context UriInfo uriInfo)
            throws CodeAssistantException, VirtualFileSystemException, IOException, JsonException, BuilderException {

        final VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        Item item = vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);

        Project project;
        String projectPath;
        if (item.getItemType().equals(ItemType.PROJECT)) {
            project = (Project)item;
            projectPath= project.getPath();
        } else {
            LOG.warn("Item not a project ");
            throw new CodeAssistantException(500, "Item not a project");
        }

        if (!hasPom(vfs, projectId)){
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
            File projectDepDir = new File(TEMP_DIR, projectId);
            if(projectDepDir.exists()){
                removeRecursive(projectDepDir.toPath());
            }

            projectDepDir.mkdirs();
            projectDepDir.deleteOnExit();
            Link downloadLink = findLink("download result", buildStatus.getLinks());
            if(downloadLink != null){
                InputStream stream = doDownload(downloadLink.getHref());
                ZipUtils.unzip(stream, projectDepDir);
            }

        } catch (IOException e) {
            LOG.error("Error", e);
        }

    }

    private static void removeRecursive(Path path) throws IOException
    {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>()
        {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException
            {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
            {
                // try to delete the file anyway, even if its attributes
                // could not be read, since delete-only access is
                // theoretically possible
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
            {
                if (exc == null)
                {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
                else
                {
                    // directory iteration failed; propagate exception
                    throw exc;
                }
            }
        });
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

    private boolean hasPom(VirtualFileSystem vfs, String projectId) throws VirtualFileSystemException {
        ItemList children = vfs.getChildren(projectId, -1, 0, "file", false);
        List<Item> items = children.getItems();
        for (int i = 0; i < items.size(); i++) {
            Item f = items.get(i);
            if ("pom.xml".equals(f.getName()))
                return true;
        }
        return false;
    }

    private void buildFailed(@Nullable BuildTaskDescriptor buildStatus) throws BuilderException {
        if (buildStatus != null) {
            Link logLink = findLink("view build log", buildStatus.getLinks());
            LOG.error("Build failed see more detail here: " + logLink.getHref());
            throw new BuilderException("Build failed see more detail here: <a href=\"" + logLink.getHref() +"\" target=\"_blank\">" + logLink.getHref() + "</a>");
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


    private String processAnswer(NameEnvironmentAnswer answer) {
        if(answer == null) return null;
        if (answer.isBinaryType()) {
            IBinaryType binaryType = answer.getBinaryType();
            return JsonUtil.toJsonBinaryType(binaryType);
        }
//        else if (answer.isCompilationUnit()) {
//
//        }
        return null;
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
}
