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
package org.exoplatform.ide.extension.jenkins.client.build;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.AutoBeanUnmarshallerWS;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.SubscriptionHandler;
import org.exoplatform.ide.extension.jenkins.client.JenkinsExtension;
import org.exoplatform.ide.extension.jenkins.client.JenkinsService;
import org.exoplatform.ide.extension.jenkins.client.JobResult;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler;
import org.exoplatform.ide.extension.jenkins.client.marshal.StringContentUnmarshaller;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.extension.jenkins.shared.JobStatusBean.Status;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class BuildApplicationPresenter extends GitPresenter implements BuildApplicationHandler,
                                                                       UserInfoReceivedHandler, ViewClosedHandler {

    public interface Display extends IsView {

        void output(String text);

        void clearOutput();

        void startAnimation();

        void stopAnimation();

        void setBlinkIcon(Image icon, boolean blinking);

    }

    private Display display;

    private boolean closed = true;

    private String jobName;

    private UserInfo userInfo;

    /** Delay in millisecond between job status request */
    private static final int delay = 10000;

    private Status prevStatus = null;

    private boolean buildInProgress = false;

    /** Project for build on Jenkins. */
    private ProjectModel project;

    private String jobStatusChannel;

    /**
     *
     */
    public BuildApplicationPresenter() {
        IDE.addHandler(BuildApplicationEvent.TYPE, this);
        IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationHandler#onBuildApplication(org.exoplatform.ide.extension
     * .jenkins.client.event.BuildApplicationEvent) */
    @Override
    public void onBuildApplication(BuildApplicationEvent event) {
        if (buildInProgress) {
            String message = "You can not start the build of two projects at the same time.<br>";
            message += "Building of project <b>" + project.getPath() + "</b> is performed.";

            Dialogs.getInstance().showError(message);
            return;
        }

        project = event.getProject();
        if (project == null && makeSelectionCheck()) {
//         project = ((ItemContext)selectedItems.get(0)).getProject();
            project = getSelectedProject();
        }

        checkIsGitRepository(project);
    }

    private void checkIsGitRepository(final ProjectModel project) {
        if (project.getProperty(GitExtension.GIT_REPOSITORY_PROP) == null) 
           initRepository(project);
        else
            createJob();
    }

    /** Perform check, that job already exists. If it doesn't exist, then create job. */
    private void beforeBuild() {
        jobName = (String)project.getPropertyValue("jenkins-job");
        if (jobName != null && !jobName.isEmpty()) {
            build(jobName);
        } else {
            createJob();
        }
    }

    /** Create new Jenkins job. */
    private void createJob() {
        // dummy check that user name is e-mail.
        // Jenkins create git tag on build. Marks user as author of tag.
        String mail = userInfo.getName().contains("@") ? userInfo.getName() : userInfo.getName() + "@exoplatform.local";
        String uName = userInfo.getName().split("@")[0];// Jenkins don't allows in job name '@' character
        try {
            AutoBean<Job> job = JenkinsExtension.AUTO_BEAN_FACTORY.create(Job.class);
            AutoBeanUnmarshaller<Job> marshaller = new AutoBeanUnmarshaller<Job>(job);
            JenkinsService.get().createJenkinsJob(
                    uName + "-" + getProjectName() + "-" + Random.nextInt(Integer.MAX_VALUE), uName, mail, vfs.getId(),
                    project.getId(), new AsyncRequestCallback<Job>(marshaller) {
                @Override
                protected void onSuccess(Job result) {
                    build(result.getName());
                    jobName = result.getName();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Get project name (last URL segment of workDir value)
     *
     * @return project name
     */
    private String getProjectName() {
        String projectName = project.getPath();
        if (projectName.endsWith("/")) {
            projectName = projectName.substring(0, projectName.length() - 1);
        }
        projectName = projectName.substring(projectName.lastIndexOf("/") + 1, projectName.length() - 1);
        return projectName;
    }

    /**
     * Start building application.
     *
     * @param jobName
     *         name of Jenkins job
     */
    private void build(final String jobName) {
        try {
            JenkinsService.get().buildJob(vfs.getId(), project.getId(), jobName, new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                    buildInProgress = true;
                    showBuildMessage("Building project <b>" + project.getPath() + "</b>");
                    display.startAnimation();
                    display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.grey()), true);
                    prevStatus = null;
                    startCheckingStatus(jobName);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Starts checking job status by subscribing on messages over WebSocket or scheduling checking task.
     *
     * @param jobName
     *         name of the job to check status
     */
    private void startCheckingStatus(String jobName) {
        try {
            jobStatusChannel = JenkinsExtension.JOB_STATUS_CHANNEL + jobName;
            IDE.messageBus().subscribe(jobStatusChannel, jobStatusHandler);
        } catch (Exception e) {
            refreshJobStatusTimer.schedule(delay);
        }
    }

    /**
     * Sets Building status: Queue
     *
     * @param status
     */
    private void setBuildStatusQueue(JobStatus status) {
        prevStatus = Status.QUEUE;
        showBuildMessage("Status: " + status.getStatus());
        display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.grey()), true);
    }

    /**
     * Sets Building status: Building
     *
     * @param status
     */
    private void setBuildStatusBuilding(JobStatus status) {
        prevStatus = Status.BUILD;
        showBuildMessage("Status: " + status.getStatus());
        display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.blue()), true);
    }

    /**
     * Sets Building status: Finished
     *
     * @param status
     */
    private void setBuildStatusFinished(JobStatus status) {
        buildInProgress = false;

        if (display != null) {
            if (closed) {
                IDE.getInstance().openView(display.asView());
                closed = false;
            } else {
                display.asView().activate();
            }
        }

        prevStatus = Status.END;

        String message =
                "Building project <b>" + project.getPath() + "</b> has been finished.\r\nResult: "
                + status.getLastBuildResult() == null ? "Unknown" : status.getLastBuildResult();

        showBuildMessage(message);
        display.stopAnimation();

        if (status.getLastBuildResult() == null) {
            display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.red()), false);
            return;
        }

        switch (JobResult.valueOf(status.getLastBuildResult())) {
            case SUCCESS:
                display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.blue()), false);
                break;

            case FAILURE:
                display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.red()), false);
                break;

            default:
                display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.yellow()), false);
                break;
        }
    }

    /**
     * Check for status and display necessary messages.
     *
     * @param status
     */
    private void updateJobStatus(JobStatus status) {
        if (status.getStatus() == Status.QUEUE && prevStatus != Status.QUEUE) {
            setBuildStatusQueue(status);
            return;
        }

        if (status.getStatus() == Status.BUILD && prevStatus != Status.BUILD) {
            setBuildStatusBuilding(status);
            return;
        }

        if (status.getStatus() == Status.END && prevStatus != Status.END) {
            setBuildStatusFinished(status);
            return;
        }
    }

    private Timer refreshJobStatusTimer = new Timer() {
        @Override
        public void run() {
            try {
                AutoBean<JobStatus> jobStatus = JenkinsExtension.AUTO_BEAN_FACTORY.create(JobStatus.class);
                AutoBeanUnmarshaller<JobStatus> unmarshaller = new AutoBeanUnmarshaller<JobStatus>(jobStatus);
                JenkinsService.get().jobStatus(vfs.getId(), project.getId(), jobName,
                                               new AsyncRequestCallback<JobStatus>(unmarshaller) {
                                                   @Override
                                                   protected void onSuccess(JobStatus status) {
                                                       updateJobStatus(status);

                                                       if (status.getStatus() == Status.END) {
                                                           onJobFinished(status);
                                                       } else {
                                                           schedule(delay);
                                                       }
                                                   }

                                                   protected void onFailure(Throwable exception) {
                                                       buildInProgress = false;
                                                       IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                   }

                                                   ;

                                               });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        }
    };

    /**
     * Performs actions when job status received.
     *
     * @param status
     *         build job status
     */
    private void onJobFinished(JobStatus status) {
        try {
            IDE.messageBus().unsubscribe(jobStatusChannel, jobStatusHandler);
        } catch (WebSocketException e) {
            // nothing to do
        }
        IDE.fireEvent(new ApplicationBuiltEvent(status));

        try {
            JenkinsService.get().getJenkinsOutput(vfs.getId(), project.getId(), jobName,
                                                  new AsyncRequestCallback<StringBuilder>(
                                                          new StringContentUnmarshaller(new StringBuilder())) {
                                                      @Override
                                                      protected void onSuccess(StringBuilder result) {
                                                          showBuildMessage(result.toString());
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                      }
                                                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client
     * .framework.userinfo.event.UserInfoReceivedEvent) */
    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        userInfo = event.getUserInfo();
    }

    /** Initialize of the Git-repository by sending request over WebSocket or HTTP. */
    private void initRepository(final ProjectModel project) {
        try {
            GitClientService.getInstance().initWS(vfs.getId(), project.getId(), project.getName(), false,
                                                  new RequestCallback<String>() {
                                                      @Override
                                                      protected void onSuccess(String result) {
                                                          onInitSuccess();
                                                      }

                                                      @Override
                                                      protected void onFailure(Throwable exception) {
                                                          handleError(exception);
                                                      }
                                                  });
        } catch (WebSocketException e) {
            initRepositoryREST(project);
        }
    }

    /** Initialize Git repository (sends request over HTTP). */
    private void initRepositoryREST(final ProjectModel project) {
        try {
            GitClientService.getInstance().init(vfs.getId(), project.getId(), project.getName(), false,
                                                new AsyncRequestCallback<String>() {
                                                    @Override
                                                    protected void onSuccess(String result) {
                                                        onInitSuccess();
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        handleError(exception);
                                                    }
                                                });
        } catch (RequestException e) {
            handleError(e);
        }
    }

    /** Performs actions when initialization of Git-repository successfully completed. */
    private void onInitSuccess() {
        showBuildMessage(GitExtension.MESSAGES.initSuccess());
        IDE.fireEvent(new RefreshBrowserEvent(project));                           
        createJob();
    }
    

    private void handleError(Throwable e) {
        String errorMessage =
                (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.initFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

    private void showBuildMessage(String message) {
        if (display != null) {
            if (closed) {
                IDE.getInstance().openView(display.asView());
                closed = false;
            }
        } else {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            closed = false;
        }

        display.output(message);
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            closed = true;
        }
    }

    /** Handler for processing Jenkins job status which is received over WebSocket connection. */
    private SubscriptionHandler<JobStatus> jobStatusHandler = new SubscriptionHandler<JobStatus>(
            new AutoBeanUnmarshallerWS<JobStatus>(JenkinsExtension.AUTO_BEAN_FACTORY.create(JobStatus.class))) {
        @Override
        protected void onSuccess(JobStatus buildStatus) {
            updateJobStatus(buildStatus);
            if (buildStatus.getStatus() == Status.END) {
                onJobFinished(buildStatus);
            }
        }

        @Override
        protected void onFailure(Throwable exception) {
            try {
                IDE.messageBus().unsubscribe(jobStatusChannel, this);
            } catch (WebSocketException e) {
                // nothing to do
            }
            buildInProgress = false;
            display.setBlinkIcon(new Image(JenkinsExtension.RESOURCES.red()), false);
            IDE.fireEvent(new ExceptionThrownEvent(exception));
        }
    };

}
