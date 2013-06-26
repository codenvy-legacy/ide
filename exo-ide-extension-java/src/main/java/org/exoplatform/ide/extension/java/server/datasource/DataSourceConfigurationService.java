/*
 * Copyright (C) 2013 eXo Platform SAS.
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

package org.exoplatform.ide.extension.java.server.datasource;

import com.codenvy.commons.json.JsonHelper;

import org.exoplatform.ide.extension.java.shared.DataSourceOptions;
import org.exoplatform.ide.extension.java.shared.MavenDependency;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.List;

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
@Path("{ws-name}/data-source/java")
public class DataSourceConfigurationService {
    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @GET
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    public DataSourceOptions newConfiguration(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        return cfg.newConfiguration();
    }

    @POST
    @Path("create")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public DataSourceOptions newConfiguration(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId,
                                              String dependency) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        cfg.addProjectDependency(JsonHelper.fromJson(dependency, MavenDependency.class, null));
        return cfg.newConfiguration();
    }

    @POST
    @Path("configure")
    @Consumes(MediaType.APPLICATION_JSON)
    public void saveAll(@QueryParam("vfsid") String vfsId,
                        @QueryParam("projectid") String projectId,
                        List<DataSourceOptions> dataSourceOptions) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        cfg.configureDataSources(dataSourceOptions);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<DataSourceOptions> getAll(@QueryParam("vfsid") String vfsId,
                                          @QueryParam("projectid") String projectId) throws Exception {
        final DataSourceConfiguration cfg =
                new TomcatDataSourceConfiguration(vfsRegistry.getProvider(vfsId).newInstance(null, null), projectId);
        return cfg.getAllDataSources();
    }
}
