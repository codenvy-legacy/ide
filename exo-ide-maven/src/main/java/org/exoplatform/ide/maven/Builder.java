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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.everrest.core.impl.provider.json.ObjectValue;
import org.everrest.core.impl.provider.json.StringValue;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.*;
import java.net.URI;
import java.util.Date;

/**
 * REST interface for BuildService.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("builder/maven")
public class Builder {
    @Inject
    private BuildService tasks;

    public Builder(@QueryParam("async") boolean async) {
        if (async) {
            // Prevent running builder methods asynchronously in EverRest framework.
            // Builder uses BuildService that has own thread pool for running build jobs.
            throw new WebApplicationException(
                    Response.status(400).entity("Builder does not support asynchronous mode. ")
                            .type(MediaType.TEXT_PLAIN).build());
        }
    }


    @GET
    @Path("state")
    public Response getSizeOfBuilderQueue() {
        int size = tasks.getSize();
        String resp = "{\"current_queue_size\":\"" + size
                      + "\",\"clean_build_result_delay_millis\":\"" + tasks.getCleanBuildResultDelayMillis()
                      + "\",\"publish_repository\":\"" + tasks.getPublishRepository()
                      + "\",\"publish_repository_url\":\"" + tasks.getPublishRepositoryUrl()
                      + "\",\"max_size_of_build_queue\":\"" + tasks.getMaxSizeOfBuildQueue()
                      + "\",\"builder_repository\":\"" + tasks.getRepository()
                      + "\",\"builder_timeout\":\"" + tasks.getTimeoutMillis()
                      + "\",\"builder_workers_number\":\"" + tasks.getWorkerNumber() + "\"}";

        return Response.ok(resp).build();
    }

    @POST
    @Path("build")
    @Consumes("application/zip")
    public Response build(@Context UriInfo uriInfo, InputStream data)
            throws IOException {
        MavenBuildTask task = tasks.build(data);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(task.getId());
        return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("deploy")
    @Consumes("application/zip")
    public Response deploy(@Context UriInfo uriInfo, InputStream data)
            throws IOException {
        MavenBuildTask task = tasks.deploy(data);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(task.getId());
        return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("dependencies/list")
    @Consumes("application/zip")
    public Response dependenciesList(@Context UriInfo uriInfo, InputStream data)
            throws IOException {
        MavenBuildTask task = tasks.dependenciesList(data);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(task.getId());
        return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
    }

    @POST
    @Path("dependencies/copy")
    @Consumes("application/zip")
    public Response dependenciesCopy(@Context UriInfo uriInfo, @QueryParam("classifier") String classifier,
                                     InputStream data) throws IOException {
        MavenBuildTask task = tasks.dependenciesCopy(data, classifier);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(task.getId());
        return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
    }

    @GET
    @Path("status/{buildid}")
    public Response status(@PathParam("buildid") String buildID, @Context UriInfo uriInfo) {
        MavenBuildTask task = tasks.get(buildID);
        ObjectValue jsonObject = new ObjectValue();
        if (task != null) {
            if (task.isDone()) {
                try {
                    InvocationResultImpl result = task.getInvocationResult();
                    if (0 == result.getExitCode()) {

                        if (result.getResult() != null) {
                            result.getResult().getTime();
                            jsonObject.addElement("status", new StringValue("SUCCESSFUL"));
                            jsonObject.addElement("downloadUrl",
                                                  new StringValue(
                                                          uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "download")
                                                                 .build(buildID).toString()));
                            jsonObject.addElement("time", new StringValue(Long.toString(result.getResult().getTime())));
                            return Response
                                    .status(200)
                                    .entity(jsonObject.toString())
                                    .type(MediaType.APPLICATION_JSON).build();
                        } else {
                            jsonObject.addElement("status", new StringValue("SUCCESSFUL"));
                            jsonObject.addElement("downloadUrl", new StringValue(""));
                            jsonObject.addElement("time", new StringValue(Long.toString(System.currentTimeMillis())));
                            return Response
                                    .status(200)
                                    .entity(jsonObject.toString()).type(MediaType.APPLICATION_JSON)
                                    .build();
                        }
                    } else {
                        CommandLineException cle = result.getExecutionException();
                        if (cle != null) {
                            jsonObject.addElement("status", new StringValue("FAILED"));
                            jsonObject.addElement("error", new StringValue(cle.getMessage()));
                            return Response.status(200)
                                           .entity(jsonObject.toString())
                                           .type(MediaType.APPLICATION_JSON).build();
                        }
                        Reader reader = task.getLogger().getLogReader();
                        StringBuilder error = new StringBuilder();
                        // Show plain text as HTML.
                        error.append("<div>");
                        try {
                            BufferedReader bReader = new BufferedReader(reader);
                            String line;
                            while ((line = bReader.readLine()) != null) {
                                // Replace location to surefire-reports by link accessible through the web.
                                if (line.startsWith("[ERROR] Please refer to ") &&
                                    line.endsWith(" for the individual test results.")) {
                                    error.append("[ERROR] Please refer to ");
                                    error.append("<a href='");
                                    error.append(
                                            uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "getSurefireReports")
                                                   .build(buildID).toString());
                                    error.append("' target='_blank'>surefire reports</a>");
                                    error.append(" for the individual test results.<br/>");
                                } else {
                                    error.append(line);
                                    error.append("<br/>");
                                }
                            }
                        } finally {
                            reader.close();
                        }
                        error.append("</div>");
                        jsonObject.addElement("status", new StringValue("FAILED"));
                        jsonObject.addElement("exitCode", new StringValue(Integer.toString(result.getExitCode())));
                        jsonObject.addElement("error", new StringValue(error.toString()));
                        return Response.status(200)
                                       .entity(jsonObject.toString())
                                       .type(MediaType.APPLICATION_JSON).build();
                    }
                } catch (MavenInvocationException e) {
                    if (e.getMessage().contains("cancelled")) {
                        jsonObject.addElement("status", new StringValue("CANCELLED"));
                        return Response.status(200).entity(jsonObject.toString()).type(MediaType.APPLICATION_JSON).build();
                    }
                    throw new WebApplicationException(e);
                } catch (IOException e) {
                    throw new WebApplicationException(e);
                }
            }
            jsonObject.addElement("status", new StringValue("IN_PROGRESS"));
            return Response.status(200).entity(jsonObject.toString()).type(MediaType.APPLICATION_JSON).build();
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
    }

    @GET
    @Path("reports/{buildid}")
    public Response getSurefireReports(@PathParam("buildid") final String buildID, @Context final UriInfo uriInfo) {
        MavenBuildTask task = tasks.get(buildID);
        if (task != null) {
            if (task.isDone()) {
                File reports = new File(task.getProjectDirectory(), "target/surefire-reports");
                final String[] files = reports.list();
                if (files == null) {
                    return Response.status(200).entity("Report files are not available.").type(MediaType.TEXT_PLAIN)
                                   .build();
                }
                StreamingOutput body = new StreamingOutput() {
                    @Override
                    public void write(OutputStream output) throws IOException, WebApplicationException {
                        PrintWriter writer = new PrintWriter(output);
                        for (String name : files) {
                            writer.printf("<a href='%s'>%s</a><br/>", uriInfo.getBaseUriBuilder().path(Builder.this.getClass())
                                                                             .path(Builder.this.getClass(), "getReportFile").queryParam(
                                            "name", name).build(buildID), name);
                        }
                        writer.flush();
                    }
                };
                return Response.status(200).entity(body).type(MediaType.TEXT_HTML).build();
            }
            return Response.status(200).entity("{\"status\":\"IN_PROGRESS\"}").type(MediaType.APPLICATION_JSON).build();
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
    }

    @GET
    @Path("report/file/{buildid}")
    public Response getReportFile(@PathParam("buildid") String buildID, @QueryParam("name") String name) {
        MavenBuildTask task = tasks.get(buildID);
        if (task != null) {
            if (task.isDone()) {
                File report = new File(task.getProjectDirectory(), "target/surefire-reports/" + name);
                if (report.exists()) {
                    String mediaType =
                            report.getName().endsWith(".xml") ? MediaType.APPLICATION_XML : MediaType.TEXT_PLAIN;
                    return Response.status(200).entity(report).type(mediaType).build();
                }
                return Response.status(200)
                               .entity("Report file " + name + " is not available.").type(MediaType.TEXT_PLAIN).build();
            }
            return Response.status(200).entity("{\"status\":\"IN_PROGRESS\"}").type(MediaType.APPLICATION_JSON).build();
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
    }

    @GET
    @Path("cancel/{buildid}")
    public void cancel(@PathParam("buildid") String buildID) {
        MavenBuildTask task = tasks.cancel(buildID);
        if (task == null) {
            // Incorrect task ID.
            throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                      .type(MediaType.TEXT_PLAIN).build());
        }
    }

    @GET
    @Path("log/{buildid}")
    public Response log(@PathParam("buildid") String buildID) throws IOException {
        MavenBuildTask task = tasks.get(buildID);
        if (task != null) {
            return Response.ok(task.getLogger().getLogReader(), MediaType.TEXT_PLAIN).build();
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
    }

    @GET
    @Path("download/{buildid}")
    public Response download(@PathParam("buildid") String buildID, @Context UriInfo uriInfo) {
        MavenBuildTask task = tasks.get(buildID);
        if (task != null) {
            if (task.isDone()) {
                try {
                    InvocationResultImpl invocationResult = task.getInvocationResult();
                    if (0 == invocationResult.getExitCode()) {
                        Result result = invocationResult.getResult();
                        if (result != null) {
                            Response.ResponseBuilder builder = Response.ok(result.getStream(), result.getMediaType());
                            String fileName = result.getFileName();
                            if (fileName != null) {
                                builder.header("Content-Disposition", "attachment; filename=\"" + fileName + '"');
                            }
                            long time = result.getTime();
                            if (time > 0) {
                                builder.lastModified(new Date(time));
                            }
                            return builder.build();
                        }
                    }

                    // Job is failed - nothing for download.
                    throw new WebApplicationException(Response.status(404)
                                                              .entity("Job failed. There is nothing for download. ")
                                                              .type(MediaType.TEXT_PLAIN).build());
                } catch (MavenInvocationException e) {
                    throw new WebApplicationException(e);
                } catch (IOException e) {
                    throw new WebApplicationException(e);
                }
            }
            // Sent location to check status method.
            final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(buildID);
            return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN)
                           .build();
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
    }

    @GET
    @Path("result/{buildid}")
    public Response getResult(@PathParam("buildid") String buildID, @Context UriInfo uriInfo) {
        MavenBuildTask task = tasks.get(buildID);
        if (task != null) {
            if (task.isDone()) {
                try {
                    InvocationResultImpl invocationResult = task.getInvocationResult();
                    if (0 == invocationResult.getExitCode()) {
                        Result result = invocationResult.getResult();
                        if (result != null) {
                            Response.ResponseBuilder builder = Response.ok(result.getStream(), result.getMediaType());
                            long time = result.getTime();
                            if (time > 0) {
                                builder.lastModified(new Date(time));
                            }
                            return builder.build();
                        }
                    }

                    // Job is failed - nothing for download.
                    throw new WebApplicationException(Response.status(404)
                                                              .entity("Job failed. There is nothing for download. ")
                                                              .type(MediaType.TEXT_PLAIN).build());
                } catch (MavenInvocationException e) {
                    throw new WebApplicationException(e);
                } catch (IOException e) {
                    throw new WebApplicationException(e);
                }
            }
            // Sent location to check status method.
            final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(buildID);
            return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN)
                           .build();
        }
        // Incorrect task ID.
        throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
                                                  .type(MediaType.TEXT_PLAIN).build());
    }
}
