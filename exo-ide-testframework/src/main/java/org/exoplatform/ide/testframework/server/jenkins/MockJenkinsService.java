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
package org.exoplatform.ide.testframework.server.jenkins;

import org.exoplatform.ide.testframework.server.FSLocation;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Mockup for Jenkins Service.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: MockJenkinsService.java Aug 18, 2011 5:41:57 PM vereshchaka $
 */
@Path("{ws-name}/jenkins")
public class MockJenkinsService {
    private static final int NUMBER_OF_BUILDS = 3;

    private static int buildCounter;

    private static String projectName;

    static JobStatus jobStatus;

    public MockJenkinsService() {

    }

    @Path("job/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Job createJob( //
                          @QueryParam("name") String name, //
                          @QueryParam("git") String git, //
                          @QueryParam("user") String user, //
                          @QueryParam("email") String email, //
                          @QueryParam("workdir") FSLocation workDir, //
                          @Context UriInfo uriInfo //
                        ) throws IOException, JenkinsException {
        jobStatus = new JobStatus();
        Job created = new Job();
        created.setBuildUrl(uriInfo.getBaseUri().toString() + "/ide/jenkins/job/build");
        created.setName(name);
        created.setStatusUrl(uriInfo.getBaseUri().toString() + "/ide/jenkins/job/status");
        return created;
    }

    @Path("job/build")
    @POST
    public void build( //
                       @QueryParam("name") String jobName, //
                       @QueryParam("workdir") FSLocation workDir, //
                       @Context UriInfo uriInfo //
                     ) throws IOException, JenkinsException {
        jobStatus = new JobStatus();
        buildCounter = 0;
        if (workDir != null) {
            projectName = getProjectName(workDir.getURL());
        } else {
            projectName = "NewProject";
        }
    }

    @Path("job/status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JobStatus jobStatus( //
                                @QueryParam("name") String jobName, //
                                @QueryParam("workdir") FSLocation workDir, //
                                @Context UriInfo uriInfo //
                              ) throws IOException, JenkinsException {
        try {
            jobStatus = new JobStatus();
            if (buildCounter++ < NUMBER_OF_BUILDS) {
                jobStatus.setLastBuildResult(null);
                jobStatus.setStatus(JobStatus.Status.QUEUE);
                jobStatus.setName(jobName);
                jobStatus.setArtifactUrl(null);
                return jobStatus;
            }

            jobStatus.setLastBuildResult("SUCCESS");
            jobStatus.setStatus(JobStatus.Status.END);
            jobStatus.setName(jobName);
            jobStatus.setArtifactUrl("https://exoplatform.ci.cloudbees.com/job/" + jobName + "/1/artifact/target/"
                                     + projectName + ".war");
            return jobStatus;
        } catch (NullPointerException e) {
        }
        return null;
    }

    @Path("job/console-output")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public InputStream consoleOutput( //
                                      @QueryParam("name") String jobName, //
                                      @QueryParam("workdir") FSLocation workDir, //
                                      @Context UriInfo uriInfo //
                                    ) throws IOException, JenkinsException {
        if ((jobName == null || jobName.isEmpty()) && workDir != null) {
            jobName = getJobByWorkDir(workDir);
            if (jobName == null || jobName.isEmpty())
                throw new IllegalArgumentException("Job name required. ");
        }

        if (jobStatus.getStatus() != JobStatus.Status.END)
            return null; // Do not show output if job in queue for build or building now.
        InputStream inputStream = new ByteArrayInputStream("Finished: SUCCESS".getBytes("UTF-8"));
        return inputStream;
    }

    private String getJobByWorkDir(FSLocation workDir) {
        // TODO
        return "job";
    }

    private String getProjectName(String workDir) {
        String name = workDir;
        if (name.endsWith("/")) {
            name = name.substring(0, name.length() - 1);
        }
        name = name.substring(name.lastIndexOf("/") + 1, name.length());
        return name;
    }

}