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
package org.exoplatform.ide.codeassistant.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * REST interface for {@link UpdateStorageService}
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Path("storage")
public class UpdateStorage {
    private static final Logger LOG = LoggerFactory.getLogger(UpdateStorage.class);

    @Inject
    private UpdateStorageService storageService;

    @Path("update/type")
    @POST
    @Consumes("application/json")
    public Response updateTypeDependecys(@Context UriInfo uriInfo, Dependencys dependencys) throws IOException {
        try {
            InputStream zip = doDownload(dependencys.getZipUrl());
            UpdateStorageTask task = storageService.updateTypeIndex(dependencys.getDependencies(), zip);
            final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(task.getId());
            return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
        } catch (MalformedURLException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    @Path("update/dock")
    @POST
    @Consumes("application/json")
    public Response updateJavaDock(@Context UriInfo uriInfo, Dependencys dependencys) throws IOException {
        try {
            InputStream zip = doDownload(dependencys.getZipUrl());
            UpdateStorageTask task = storageService.updateDockIndex(dependencys.getDependencies(), zip);
            final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(task.getId());
            return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
        } catch (MalformedURLException e) {
            return Response.status(404).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("status/{buildid}")
    public Response status(@PathParam("buildid") String buildID, @Context UriInfo uriInfo) {
        UpdateStorageTask task = storageService.getTask(buildID);
        if (task != null) {
            if (task.isDone()) {
                UpdateStorageResult result = task.getResult();
                if (task.getResult().getExitCode() == 0) {
                    return Response
                            .status(200)
                            .entity(
                                    "{\"status\":\"SUCCESSFUL\",\"downloadUrl\":\"\",\"time\":\""
                                    + Long.toString(System.currentTimeMillis()) + "\"}").type(MediaType.APPLICATION_JSON).build();
                } else {
                    return Response
                            .status(200)
                            .entity(
                                    "{\"status\":\"FAILED\",\"exitCode\":" + result.getExitCode() + ",\"error\":\""
                                    + result.getErroMessage() + "\"}").type(MediaType.APPLICATION_JSON).build();
                }
            } else {
                return Response.status(200).entity("{\"status\":\"IN_PROGRESS\"}").type(MediaType.APPLICATION_JSON).build();
            }
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
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
