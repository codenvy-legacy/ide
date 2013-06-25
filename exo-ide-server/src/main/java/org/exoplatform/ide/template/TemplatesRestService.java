/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.template;

import com.codenvy.commons.lang.IoUtil;
import com.codenvy.ide.commons.server.ParsingResponseException;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.ProjectTemplate;
import org.exoplatform.ide.vfs.server.RequestContext;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.ProjectImpl;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import java.io.ByteArrayInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This REST service is used for getting and storing templates for file and projects.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplatesRestService.java Apr 4, 2011 3:21:46 PM vereshchaka $
 */
@Path("{ws-name}/templates")
@RolesAllowed("developer")
public class TemplatesRestService {

    private static final Pattern PATTERN_GROUP_ID = Pattern.compile(".*<groupId>groupId</groupId>.*");

    private static final Pattern PATTERN_ARTIFACT_ID = Pattern.compile(".*<artifactId>artifactId</artifactId>.*");

    private static final Pattern PATTERN_GROUP_ID_OF_PARENT = Pattern.compile(".*<groupId>parent-groupId</groupId>.*");

    private static final Pattern        PATTERN_ARTIFACT_ID_OF_PARENT = Pattern.compile(".*<artifactId>parent-artifactId</artifactId>.*");
    /** File name filter. Need to filter non "zip" files. */
    private static       FilenameFilter projectsZipFilter             = new FilenameFilter() {
        @Override
        public boolean accept(java.io.File dir, String name) {
            return name.endsWith(".zip");
        }
    };

    private static Log log = ExoLogger.getLogger(TemplatesRestService.class);

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    private EventListenerList listenerList;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/project/list")
    public List<ProjectTemplate> getProjectTemplateList() throws URISyntaxException, IOException,
                                                                 ParsingResponseException {
        return getProjectTemplates();
    }



    /**
     * Create new IDE project from predefined template
     *
     * @param vfsId
     *         id of VFS
     * @param name
     *         name of new project
     * @param parentId
     *         parent of the project
     * @param templateName
     *         name of the project template
     * @return created project
     * @throws VirtualFileSystemException
     * @throws IOException
     */
    @POST
    @Path("/project/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Project createProjectFromTemplate(@QueryParam("vfsid") String vfsId, //
                                             @QueryParam("name") String name, //
                                             @QueryParam("parentId") String parentId,//
                                             @QueryParam("templateName") String templateName,//
                                             @Context Providers providers, //
                                             @Context UriInfo uriInfo) throws VirtualFileSystemException, IOException {
        ContextResolver<RequestContext> contextResolver = providers.getContextResolver(RequestContext.class, null);
        RequestContext context = null;
        if (contextResolver != null) {
            context = contextResolver.getContext(RequestContext.class);
        }

        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(context, listenerList);
        Folder projectFolder = vfs.createFolder(parentId, name);
        InputStream templateStream = null;
        try {
            templateStream =
                    Thread.currentThread().getContextClassLoader().getResourceAsStream("projects/" + templateName + ".zip");
            if (templateStream == null)
                throw new InvalidArgumentException("Can't find " + templateName + ".zip");
            vfs.importZip(projectFolder.getId(), templateStream, true);

            //Goto change Maven groupId & artifactId IDE-1981
            try {
                String path2pom = projectFolder.getPath() + "/pom.xml";
                File pom =
                        (File)vfs.getItemByPath(path2pom, null, false, PropertyFilter.NONE_FILTER);
                String content = IoUtil.readStream(vfs.getContent(pom.getId()).getStream());
                String host = uriInfo.getAbsolutePath().getHost();
                String groupId = null;
                if (host.contains(".")) {
                    String[] split = host.split("\\.");
                    StringBuffer result = new StringBuffer();
                    int j = split.length - 1;
                    while (j > 0) {
                        result.append(split[j--]).append(".");
                    }
                    result.append(split[0]);
                    groupId = result.toString();
                } else {
                    groupId = host;
                }
                String newContent = PATTERN_GROUP_ID.matcher(content).replaceFirst("<groupId>" + groupId + "</groupId>");
                newContent = PATTERN_GROUP_ID.matcher(newContent).replaceFirst("<groupId>" + groupId + "</groupId>");
                newContent = PATTERN_ARTIFACT_ID.matcher(newContent).replaceFirst("<artifactId>" + name + "</artifactId>");
                vfs.updateContent(pom.getId(), MediaType.valueOf(pom.getMimeType()),
                                  new ByteArrayInputStream(newContent.getBytes()), null);

                //Goto change groupId & artifactId for child project for MultiModule project IDE-2025
                //TODO: need fix it remove hardcode
                pom =
                        (File)vfs.getItemByPath(projectFolder.getPath() + "/my-lib/pom.xml", null, false, PropertyFilter.NONE_FILTER);
                content = IoUtil.readStream(vfs.getContent(pom.getId()).getStream());
                newContent = PATTERN_GROUP_ID_OF_PARENT.matcher(content).replaceFirst("<groupId>" + groupId + "</groupId>");
                newContent = PATTERN_ARTIFACT_ID_OF_PARENT.matcher(newContent).replaceFirst("<artifactId>" + name + "</artifactId>");
                vfs.updateContent(pom.getId(), MediaType.valueOf(pom.getMimeType()),
                                  new ByteArrayInputStream(newContent.getBytes()), null);

                //TODO: need fix it remove hardcode
                pom =
                        (File)vfs.getItemByPath(projectFolder.getPath() + "/my-webapp/pom.xml", null, false, PropertyFilter.NONE_FILTER);
                content = IoUtil.readStream(vfs.getContent(pom.getId()).getStream());
                newContent = PATTERN_GROUP_ID_OF_PARENT.matcher(content).replaceFirst("<groupId>" + groupId + "</groupId>");
                newContent = PATTERN_GROUP_ID.matcher(newContent).replaceFirst("<groupId>" + groupId + "</groupId>");//change dependency
                newContent = PATTERN_ARTIFACT_ID_OF_PARENT.matcher(newContent).replaceFirst("<artifactId>" + name + "</artifactId>");
                vfs.updateContent(pom.getId(), MediaType.valueOf(pom.getMimeType()),
                                  new ByteArrayInputStream(newContent.getBytes()), null);

            } catch (ItemNotFoundException e) {
                //nothing todo not maven project
            }

        } catch (IOException e) {
            if (log.isDebugEnabled())
                log.error("Cant create project", e);
            throw e;
        } finally {
            if (templateStream != null)
                templateStream.close();
        }
        org.exoplatform.ide.vfs.shared.Item projectItem = vfs.getItem(projectFolder.getId(), false, PropertyFilter.ALL_FILTER);
        if (projectItem instanceof ProjectImpl) {
            return (Project)projectItem;
        } else
            throw new IllegalStateException("Something other than project was created on " + name);
    }



    private List<ProjectTemplate> getProjectTemplates() throws URISyntaxException, IOException, ParsingResponseException {
        List<ProjectTemplate> projectTemplateList = new ArrayList<ProjectTemplate>();
        URL url = Thread.currentThread().getContextClassLoader().getResource("projects");
        if (url != null) {
            java.io.File projectsFolder = new java.io.File(url.toURI());
            java.io.File[] projects = projectsFolder.listFiles(projectsZipFilter);
            for (java.io.File f : projects) {
                ZipFile zip = null;
                InputStream prjDescrStream = null;
                try {
                    zip = new ZipFile(f);
                    ZipArchiveEntry entry = zip.getEntry(".project");
                    // if zip not contains ".project" file then search in next archive
                    if (entry == null)
                        continue;
                    JsonParser jp = new JsonParser();
                    prjDescrStream = zip.getInputStream(entry);
                    jp.parse(prjDescrStream);
                    Property[] array = (Property[])ObjectBuilder.createArray(PropertyImpl[].class, jp.getJsonObject());
                    List<Property> properties = Arrays.asList(array);
                    String name = f.getName();
                    name = name.substring(0, name.lastIndexOf(".zip"));
                    projectTemplateList.add(createTemplateFromMethaData(properties, name));
                } catch (JsonException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } finally {
                    if (zip != null)
                        zip.close();
                    if (prjDescrStream != null)
                        prjDescrStream.close();
                }

            }
        }

        return projectTemplateList;
    }

    private ProjectTemplate createTemplateFromMethaData(List<Property> properties, String templateName) {
        ProjectTemplate template = new ProjectTemplate();
        template.setDefault(true);
        template.setName(templateName);
        for (Property prop : properties) {
            String name = prop.getName();
            if ("vfs:projectType".equals(name)) {
                template.setType(prop.getValue().get(0));
            } else if ("exoide:projectDescription".equals(name)) {
                template.setDescription(prop.getValue().get(0));
            } else if ("exoide:target".equals(name)) {
                template.setDestination(prop.getValue());
            }
        }
        return template;
    }


    


   

}
