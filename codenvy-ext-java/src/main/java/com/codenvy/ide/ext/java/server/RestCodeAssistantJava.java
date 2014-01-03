/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
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
import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.builder.internal.BuilderException;
import com.codenvy.api.core.rest.HttpJsonHelper;
import com.codenvy.api.core.rest.RemoteException;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.core.util.Pair;
import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemRegistry;
import com.codenvy.api.vfs.server.dto.DtoServerImpls;
import com.codenvy.api.vfs.server.exceptions.InvalidArgumentException;
import com.codenvy.api.vfs.server.exceptions.VirtualFileSystemException;
import com.codenvy.api.vfs.shared.ItemType;
import com.codenvy.api.vfs.shared.PropertyFilter;
import com.codenvy.api.vfs.shared.dto.Item;
import com.codenvy.api.vfs.shared.dto.ItemList;
import com.codenvy.api.vfs.shared.dto.Project;
import com.codenvy.api.vfs.shared.dto.Property;
import com.codenvy.builder.maven.dto.MavenDependency;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.java.shared.BuildStatusBean;
import com.codenvy.ide.ext.java.shared.JavaType;
import com.codenvy.ide.ext.java.shared.TypeInfo;
import com.codenvy.ide.ext.java.shared.TypesList;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Service provide Autocomplete of source code is also known as code completion feature. In a source code editor autocomplete is
 * greatly simplified by the regular structure of the programming languages. At current moment implemented the search class FQN,
 * by Simple Class Name and a prefix (the lead characters in the name of the package or class).
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RestCodeAssistantJava Mar 30, 2011 10:40:38 AM evgen $
 */
@Path("{ws-name}/code-assistant/java")
public class RestCodeAssistantJava {

    @PathParam("ws-name")
    private String                     wsName;
    @Inject
    private JavaCodeAssistant          codeAssistant;
    @Inject
    private VirtualFileSystemRegistry  vfsRegistry;
    @Inject
    private CodeAssistantStorageClient storageClient;

    /** Logger. */
    private static final Logger LOG = LoggerFactory.getLogger(RestCodeAssistantJava.class);

    /**
     * Returns the Class object associated with the class or interface with the given string name.
     *
     * @param fqn
     *         the Full Qualified Name
     * @return {@link TypeInfo}
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    @GET
    @Path("/class-description")
    @Produces(MediaType.APPLICATION_JSON)
    public TypeInfo getClassByFQN(@QueryParam("fqn") String fqn, @QueryParam("projectid") String projectId,
                                  @QueryParam("vfsid") String vfsId) throws CodeAssistantException, VirtualFileSystemException {
        TypeInfo info = codeAssistant.getClassByFQN(fqn, projectId, vfsId);

        if (info != null)
            return info;

        if (LOG.isDebugEnabled())
            LOG.error("Class info for " + fqn + " not found");
        return null;
    }

    /** Returns the class objects associated with the class or interface with the given simple name prefix. */
    @GET
    @Path("/classes-by-prefix")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TypeInfo> getTypesByNamePrefix(@QueryParam("prefix") String namePrefix,
                                               @QueryParam("projectid") String projectId, @QueryParam("vfsid") String vfsId)
            throws CodeAssistantException,
                   VirtualFileSystemException {
        List<TypeInfo> infos = codeAssistant.getTypeInfoByNamePrefix(namePrefix, projectId, vfsId);

        if (infos != null)
            return infos;

        if (LOG.isDebugEnabled())
            LOG.error("Class with name prefix '" + namePrefix + "' not found");
        return null;
    }

    /**
     * Returns set of FQNs matched to prefix (means FQN begin on {prefix} or Class simple name) Example : if prefix = "java.util.c"
     * set must content: { java.util.Comparator<T> java.util.Calendar java.util.Collection<E> java.util.Collections
     * java.util.ConcurrentModificationException java.util.Currency java.util.concurrent java.util.concurrent.atomic
     * java.util.concurrent.locks }
     *
     * @param prefix
     *         the string for matching FQNs
     * @param where
     *         the string that indicate where find (must be "className" or "fqn")
     * @throws VirtualFileSystemException
     */
    @GET
    @Path("/find-by-prefix/{prefix}")
    @Produces(MediaType.APPLICATION_JSON)
    public TypesList findFQNsByPrefix(@PathParam("prefix") String prefix, @QueryParam("where") String where,
                                      @QueryParam("projectid") String projectId, @QueryParam("vfsid") String vfsId)
            throws CodeAssistantException,
                   VirtualFileSystemException {
        if (projectId == null)
            throw new InvalidArgumentException("'projectid' parameter is null.");
        TypesList typesList = DtoFactory.getInstance().createDto(TypesList.class);
        if ("className".equalsIgnoreCase(where)) {
            typesList.setTypes(codeAssistant.getTypesByNamePrefix(prefix, projectId, vfsId));
            return typesList;
        }
        typesList.setTypes(codeAssistant.getTypesByFqnPrefix(prefix, projectId, vfsId));
        return typesList;

    }

    /**
     * Find all classes or annotations or interfaces
     *
     * @param type
     *         the string that represent one of Java class type (i.e. CLASS, INTERFACE, ANNOTATION)
     * @param prefix
     *         optional parameter that matching first letter of type name
     * @return Returns set of FQNs matched to class type
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    @GET
    @Path("/find-by-type/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public TypesList findByType(@PathParam("type") String type, @QueryParam("prefix") String prefix,
                                @QueryParam("projectid") String projectId, @QueryParam("vfsid") String vfsId) throws CodeAssistantException,
                                                                                                                     VirtualFileSystemException {
        if (projectId == null)
            throw new InvalidArgumentException("'projectid' parameter is null.");
        TypesList typesList = DtoFactory.getInstance().createDto(TypesList.class);
        typesList.setTypes(codeAssistant.getByType(JavaType.valueOf(type.toUpperCase()), prefix, projectId, vfsId));
        return typesList;
    }

    @GET
    @Path("/class-doc")
    @Produces(MediaType.TEXT_HTML)
    public String getClassDoc(@QueryParam("fqn") String fqn, @QueryParam("projectid") String projectId,
                              @QueryParam("vfsid") String vfsId, @QueryParam("isclass") @DefaultValue("true") boolean isClass)
            throws CodeAssistantException, VirtualFileSystemException {

        if (projectId == null)
            throw new InvalidArgumentException("'projectid' parameter is null.");
        if (isClass) {
            String classJavaDoc = codeAssistant.getClassJavaDoc(fqn, projectId, vfsId);
            if (classJavaDoc == null)
                classJavaDoc = "JavaDoc not found";
            return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">" + classJavaDoc
                   + "</body></html>";
        } else {
            String memberJavaDoc = codeAssistant.getMemberJavaDoc(fqn, projectId, vfsId);
            if (memberJavaDoc == null)
                memberJavaDoc = "JavaDoc not found";
            return "<html><head></head><body style=\"font-family: monospace;font-size: 12px;\">" + memberJavaDoc
                   + "</body></html>";
        }
    }

    /**
     * Get list of package names
     *
     * @param vfsId
     * @param projectId
     * @param packagePrefix
     * @return
     * @throws CodeAssistantException
     * @throws VirtualFileSystemException
     */
    @GET
    @Path("/find-packages")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getPackages(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId,
                                    @QueryParam("package") String packagePrefix) throws CodeAssistantException, VirtualFileSystemException {
        if (projectId == null)
            throw new InvalidArgumentException("'projectid' parameter is null.");
        return codeAssistant.getPackagesByPrefix(packagePrefix, projectId, vfsId);
    }

    /** Get list of all package names in project */
    @GET
    @Path("/update-dependencies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> updateDependency(@QueryParam("vfsid") String vfsId,
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
            LOG.warn("Getting item not a project ");
            throw new CodeAssistantException(500, "Getting item not a project");
        }

        if (!hasPom(vfs, projectId)){
            LOG.warn("Don't has pom.xml in the child");
            throw new CodeAssistantException(500, "Don't has pom.xml in the child");
        }

        URI uri = uriInfo.getBaseUri();
        String url = uri.getScheme() + "://" + uri.getHost();
        int port = uri.getPort();
        if (port != 0 && port != 80) {
            url += ":" + port;
        }
        url += "/api/rest" + "/" + wsName + "/"; //TODO: remove hardcode "api/rest"
        try {
            String jsonDependencies = null;
            List<MavenDependency> dependencies = null;
            List<String> dependencyString = new ArrayList<>();

            BuildTaskDescriptor buildStatus = getDependencies(url, projectPath, "list");

            if (buildStatus != null && buildStatus.getStatus() == BuildStatus.SUCCESSFUL) {
                Link downloadLink = findLink("download result", buildStatus.getLinks());

                HttpURLConnection conn = (HttpURLConnection)new URL(downloadLink.getHref()).openConnection();
                try (InputStream input = conn.getInputStream()) {
                    jsonDependencies = fromStream(input);
                    dependencies = DtoFactory.getInstance().createListDtoFromJson(jsonDependencies, MavenDependency.class);
                }

                List<Property> properties = project.getProperties();
                Property property = getProperty("exoide:classpath", properties);

                for (MavenDependency mavenDependency : dependencies) {
                    String value = mavenDependency.toString();
                    dependencyString.add(value);
                }

                if (property != null && property.getValue().equals(dependencyString))
                    return codeAssistant.getAllPackages(project, vfs);

                DtoServerImpls.PropertyImpl classpath = DtoServerImpls.PropertyImpl.make();
                classpath.setName("exoide:classpath");
                classpath.setValue(dependencyString);

                List<Property> newproperties = Arrays.<Property>asList(classpath);

                vfs.updateItem(projectId, newproperties, null);

            } else {
                buildFailed(buildStatus);
            }

            buildStatus = getDependencies(url, projectPath, "copy");

            if (buildStatus.getStatus() == BuildStatus.FAILED) {
                buildFailed(buildStatus);
            }

            Link downloadLink = findLink("download result", buildStatus.getLinks());

            if (dependencies == null || dependencies.isEmpty() || downloadLink == null)
                return Collections.emptyList();

            String statusUrl = storageClient.updateTypeIndex(jsonDependencies, downloadLink.getHref());
            try {
                waitStorageTaskFinish(statusUrl);
            } catch (Exception e)// Ignore exception in case add javadoc
            {
                LOG.debug("Adding sources artifact fail : " + statusUrl, e);
            }
        } catch (IOException e) {
            LOG.error("Error", e);
        }

        return codeAssistant.getAllPackages(project, vfs);
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

    @NotNull
    private String fromStream(@NotNull InputStream in) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            return out.toString();
        }
    }

    @NotNull
    private BuildTaskDescriptor getDependencies(@NotNull String url, @NotNull String projectName, @NotNull String analyzeType) {
        BuildTaskDescriptor buildStatus = null;
        try {
            Pair<String, String> projectParam = Pair.of("project", projectName);
            Pair<String, String> typeParam = Pair.of("type", analyzeType);
            buildStatus = HttpJsonHelper.request(BuildTaskDescriptor.class,
                                                 url + "builder/dependencies",
                                                 "POST",
                                                 null,
                                                 projectParam,
                                                 typeParam);
            buildStatus = waitTaskFinish(buildStatus);
        } catch (RemoteException | IOException e) {
            LOG.error("Error", e);
        }
        return buildStatus;
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

    @Nullable
    private Link findLink(@NotNull String rel, List<Link> links) {
        for (Link link : links) {
            if (link.getRel().equals(rel)) {
                return link;
            }
        }
        return null;
    }

    private void buildFailed(@Nullable BuildTaskDescriptor buildStatus) throws BuilderException {
        if (buildStatus != null) {
            Link logLink = findLink("view build log", buildStatus.getLinks());
            LOG.error("Build failed see more detail here: " + logLink.getHref());
            throw new BuilderException("Build failed see more detail here: " + logLink.getHref());
        }
        throw new BuilderException("Build failed");
    }

    @Nullable
    private Property getProperty(@NotNull String name, @NotNull List<Property> properties) {
        for (Property property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    @NotNull
    private BuildStatusBean waitStorageTaskFinish(@NotNull String buildId)
            throws IOException, BuilderException, VirtualFileSystemException, JsonException {
        String status;
        BuildStatusBean buildStatus;
        final int sleepTime = 2000;
        JsonParser parser = new JsonParser();
        boolean isDone = false;
        do {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
            status = storageClient.status(buildId);
            parser.parse(new ByteArrayInputStream(status.getBytes("UTF-8")));
            buildStatus = ObjectBuilder.createObject(BuildStatusBean.class, parser.getJsonObject());
            if (com.codenvy.ide.ext.java.shared.BuildStatus.Status.IN_PROGRESS != buildStatus.getStatus()) {
                isDone = true;
            }
        }
        while (!isDone);

        return buildStatus;
    }
}