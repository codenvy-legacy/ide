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
package org.exoplatform.ide.extension.aws.client.beanstalk.manage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.AwsAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.ApplicationVersionListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.EnvironmentsInfoListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.EnvironmentsLogListUnmarshaller;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentInfoChangedEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentInfoChangedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentRequestStatusHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentStatusChecker;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.EditConfigurationEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.configuration.UpdateEnvironmentStartedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.launch.LaunchEnvironmentStartedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.rebuild.RebuildEnvironmentStartedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.restart.RestartAppServerEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate.TerminateEnvironmentEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.environments.terminate.TerminateEnvironmentStartedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.update.ApplicationUpdatedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.update.UpdateApplicationEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.HasVersionActions;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.VersionCreatedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.VersionDeletedHandler;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionEvent;
import org.exoplatform.ide.extension.aws.client.beanstalk.versions.deploy.DeployVersionStartedHandler;
import org.exoplatform.ide.extension.aws.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.InstanceLog;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 11:34:35 AM anya $
 */
public class ManageApplicationPresenter implements ProjectOpenedHandler, ProjectClosedHandler,
        VfsChangedHandler, ManageApplicationHandler, ViewClosedHandler, EnvironmentInfoChangedHandler {

    interface Display extends IsView {
        // GeneralInfo

        HasValue<String> getApplicationNameField();

        HasValue<String> getDescriptionField();

        HasValue<String> getCreateDateField();

        HasValue<String> getUpdatedDateField();

        HasClickHandlers getDeleteButton();

        HasClickHandlers getUpdateDescriptionButton();

        HasClickHandlers getCloseButton();

        HasClickHandlers getCreateVersionButton();

        HasClickHandlers getLaunchEnvironmentButton();

        // Versions

        void selectVersionsTab();

        ListGridItem<ApplicationVersionInfo> getVersionsGrid();

        HasVersionActions getVersionActions();

        // Environments

        void selectEnvironmentTab();

        HasClickHandlers getConfigurationButton();

        HasClickHandlers getRestartButton();

        HasClickHandlers getRebuildButton();

        HasClickHandlers getTerminateButton();

        HasClickHandlers getGetLogsButton();

        ListGridItem<EnvironmentInfo> getEnvironmentGrid();

        /**
         * Change the enable state of the all buttons on Environment tab.
         *
         * @param isEnable
         *         enabled or not
         */
        void setAllEnvironmentButtonsEnableState(boolean isEnable);
    }

    /** Current VFS. */
    protected VirtualFileSystemInfo vfs;

    /** Project which is currently opened. */
    protected ProjectModel project;

    private Display display;

    private ApplicationInfo applicationInfo;

    /** Time of last received event. */
    protected long lastReceivedEventTime;

    /** Environment which is currently selected. */
    private EnvironmentInfo selectedEnvironment;

    // General

    private ApplicationUpdatedHandler applicationUpdatedHandler = new ApplicationUpdatedHandler() {

        @Override
        public void onApplicationUpdated(ApplicationInfo application) {
            applicationInfo = application;
            if (display != null) {
                display.getDescriptionField().setValue(application.getDescription());
            }
        }
    };

    private VersionCreatedHandler versionCreatedHandler = new VersionCreatedHandler() {

        @Override
        public void onVersionCreate(ApplicationVersionInfo version) {
            getVersions();
            display.selectVersionsTab();
        }
    };

    private LaunchEnvironmentStartedHandler launchEnvironmentStartedHandler = new LaunchEnvironmentStartedHandler() {

        @Override
        public void onLaunchEnvironmentStarted(EnvironmentInfo environmentInfo) {
            if (environmentInfo == null) {
                return;
            }
            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentLaunching(environmentInfo
                                                                                                                .getName()), Type.INFO));
            RequestStatusHandler environmentStatusHandler =
                    new EnvironmentRequestStatusHandler(
                            AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentLaunching(environmentInfo.getName()),
                            AWSExtension.LOCALIZATION_CONSTANT.launchEnvironmentSuccess(environmentInfo.getName()));
            new EnvironmentStatusChecker(vfs, project, environmentInfo, true, environmentStatusHandler).startChecking();
        }
    };

    // Environments

    private UpdateEnvironmentStartedHandler updateEnvironmentStartedHandler = new UpdateEnvironmentStartedHandler() {

        @Override
        public void onUpdateEnvironmentStarted(EnvironmentInfo environmentInfo) {
            if (environmentInfo == null) {
                return;
            }
            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentLaunching(environmentInfo
                                                                                                                .getName()), Type.INFO));
            RequestStatusHandler environmentStatusHandler =
                    new EnvironmentRequestStatusHandler(
                            AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentLaunching(environmentInfo.getName()),
                            AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentSuccess(environmentInfo.getName()));
            JobManager.get().showJobSeparated();
            new EnvironmentStatusChecker(vfs, project, environmentInfo, false, environmentStatusHandler).startChecking();
        }
    };

    private RebuildEnvironmentStartedHandler rebuildEnvironmentStartedHandler = new RebuildEnvironmentStartedHandler() {

        @Override
        public void onRebuildEnvironmentStarted(EnvironmentInfo environmentInfo) {
            if (environmentInfo == null) {
                return;
            }
            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.rebuildEnvironmentLaunching(environmentInfo
                                                                                                                 .getName()), Type.INFO));
            RequestStatusHandler environmentStatusHandler =
                    new EnvironmentRequestStatusHandler(
                            AWSExtension.LOCALIZATION_CONSTANT.rebuildEnvironmentLaunching(environmentInfo.getName()),
                            AWSExtension.LOCALIZATION_CONSTANT.rebuildEnvironmentSuccess(environmentInfo.getName()));
            new EnvironmentStatusChecker(vfs, project, environmentInfo, false, environmentStatusHandler).startChecking();
        }
    };

    private TerminateEnvironmentStartedHandler terminateEnvironmentStartedHandler =
            new TerminateEnvironmentStartedHandler() {

                @Override
                public void onTerminateEnvironmentStarted(EnvironmentInfo environmentInfo) {
                    if (environmentInfo == null) {
                        return;
                    }
                    IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT
                                                              .terminateEnvironmentLaunching(environmentInfo.getName()), Type.INFO));
                    RequestStatusHandler environmentStatusHandler =
                            new EnvironmentRequestStatusHandler(
                                    AWSExtension.LOCALIZATION_CONSTANT.terminateEnvironmentLaunching(environmentInfo.getName()),
                                    AWSExtension.LOCALIZATION_CONSTANT.terminateEnvironmentSuccess(environmentInfo.getName()));
                    new EnvironmentStatusChecker(vfs, project, environmentInfo, false, environmentStatusHandler)
                            .startChecking();
                }
            };

    // Versions

    private DeployVersionStartedHandler deployVersionStartedHandler = new DeployVersionStartedHandler() {

        @Override
        public void onDeployVersionStarted(EnvironmentInfo environmentInfo) {
            if (environmentInfo == null) {
                return;
            }
            IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentLaunching(environmentInfo
                                                                                                                .getName()), Type.INFO));
            RequestStatusHandler environmentStatusHandler =
                    new EnvironmentRequestStatusHandler(
                            AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentLaunching(environmentInfo.getName()),
                            AWSExtension.LOCALIZATION_CONSTANT.updateEnvironmentSuccess(environmentInfo.getName()));
            new EnvironmentStatusChecker(vfs, project, environmentInfo, false, environmentStatusHandler).startChecking();
        }
    };

    private VersionDeletedHandler versionDeletedHandler = new VersionDeletedHandler() {

        @Override
        public void onVersionDeleted(ApplicationVersionInfo version) {
            getVersions();
        }
    };

    public ManageApplicationPresenter() {
        IDE.getInstance().addControl(new ManageApplicationControl());

        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);
        IDE.addHandler(ProjectClosedEvent.TYPE, this);
        IDE.addHandler(ManageApplicationEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(EnvironmentInfoChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.manage.ManageApplicationHandler#onManageApplication(org.exoplatform.ide
     * .extension.aws.client.beanstalk.manage.ManageApplicationEvent) */
    @Override
    public void onManageApplication(ManageApplicationEvent event) {
        if (project == null || !AWSExtension.isAWSApplication(project)) {
            Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.notAWSApplictaionMessage());
            return;
        }

        getApplicationInfo();
    }

    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDeleteButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                askForDelete();
            }
        });

        display.getUpdateDescriptionButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new UpdateApplicationEvent(vfs.getId(), project.getId(), applicationInfo,
                                                         applicationUpdatedHandler));
            }
        });

        display.getVersionActions().addDeployHandler(new SelectionHandler<ApplicationVersionInfo>() {

            @Override
            public void onSelection(SelectionEvent<ApplicationVersionInfo> event) {
                IDE.fireEvent(new DeployVersionEvent(event.getSelectedItem().getApplicationName(), event.getSelectedItem()
                                                                                                        .getVersionLabel(),
                                                     deployVersionStartedHandler));
            }
        });

        display.getVersionActions().addDeleteHandler(new SelectionHandler<ApplicationVersionInfo>() {

            @Override
            public void onSelection(SelectionEvent<ApplicationVersionInfo> event) {
                IDE.fireEvent(new DeleteVersionEvent(vfs.getId(), project.getId(), event.getSelectedItem(),
                                                     versionDeletedHandler));
            }
        });

        display.getEnvironmentGrid().addSelectionHandler(new SelectionHandler<EnvironmentInfo>() {

            @Override
            public void onSelection(SelectionEvent<EnvironmentInfo> event) {
                selectedEnvironment = event.getSelectedItem();
                display.setAllEnvironmentButtonsEnableState(true);
            }
        });

        display.getConfigurationButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedEnvironment != null) {
                    IDE.fireEvent(new EditConfigurationEvent(selectedEnvironment, updateEnvironmentStartedHandler));
                }
            }
        });

        display.getRestartButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedEnvironment != null) {
                    IDE.fireEvent(new RestartAppServerEvent(selectedEnvironment));
                }
            }
        });

        display.getRebuildButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedEnvironment != null) {
                    IDE.fireEvent(new RebuildEnvironmentEvent(selectedEnvironment, rebuildEnvironmentStartedHandler));
                }
            }
        });

        display.getTerminateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedEnvironment != null) {
                    IDE.fireEvent(new TerminateEnvironmentEvent(selectedEnvironment, terminateEnvironmentStartedHandler));
                }
            }
        });

        display.getGetLogsButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                if (selectedEnvironment != null) {
                    getLogs(selectedEnvironment);
                }
            }
        });

        display.getCreateVersionButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new CreateVersionEvent(vfs.getId(), project, applicationInfo.getName(), versionCreatedHandler));
            }
        });

        display.getLaunchEnvironmentButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.fireEvent(new LaunchEnvironmentEvent(vfs.getId(), project.getId(), applicationInfo.getName(),
                                                         AWSExtension.INIT_VER_LABEL, launchEnvironmentStartedHandler));
            }
        });
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void askForDelete() {
        final String applicationName = applicationInfo.getName();
        Dialogs.getInstance().ask(AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(),
                                  AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationQuestion(applicationName),
                                  new BooleanValueReceivedHandler() {

                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              deleteApplication(applicationName);
                                          }
                                      }
                                  });
    }

    private void deleteApplication(final String applicationName) {
        try {
            BeanstalkClientService.getInstance().deleteApplication(vfs.getId(), project.getId(),
               new AwsAsyncRequestCallback<Object>(new LoggedInHandler() {

                   @Override
                   public void onLoggedIn() {
                       deleteApplication(applicationName);
                   }
               }, null) {
                   @Override
                   protected void processFail(Throwable exception) {
                       String message = AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationFailed(applicationName);
                       if (exception instanceof ServerException &&
                           ((ServerException)exception).getMessage() != null) {
                           message += "<br>" + ((ServerException)exception).getMessage();
                       }
                       IDE.fireEvent(new OutputEvent(message, Type.ERROR));
                   }

                   @Override
                   protected void onSuccess(Object result) {
                       IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.deleteApplicationSuccess(applicationName), Type.INFO));
                       if (display != null) {
                           IDE.getInstance().closeView(display.asView().getId());
                       }
                   }
               });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get the info about AWS Elastic Beanstalk application and open view. */
    private void getApplicationInfo() {
        AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();
        try {
            BeanstalkClientService.getInstance().getApplicationInfo(
                    vfs.getId(),
                    project.getId(),
                    new AwsAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
                                                                 new LoggedInHandler() {

                                                                     @Override
                                                                     public void onLoggedIn() {
                                                                         getApplicationInfo();
                                                                     }
                                                                 }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }

                        @Override
                        protected void onSuccess(ApplicationInfo result) {
                            applicationInfo = result;
                            openView();
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.getApplicationNameField().setValue(applicationInfo.getName());
        display.getDescriptionField().setValue(
                applicationInfo.getDescription() != null ? applicationInfo.getDescription() : "");
        display.getCreateDateField().setValue(new Date(applicationInfo.getCreated()).toString());
        display.getUpdatedDateField().setValue(new Date(applicationInfo.getUpdated()).toString());
        display.setAllEnvironmentButtonsEnableState(false);

        getVersions();
        getEnvironments();
    }

    /** Get application versions. */
    private void getVersions() {
        try {
            BeanstalkClientService.getInstance().getVersions(
                    vfs.getId(),
                    project.getId(),
                    new AwsAsyncRequestCallback<List<ApplicationVersionInfo>>(new ApplicationVersionListUnmarshaller(),
                          new LoggedInHandler() {

                              @Override
                              public void onLoggedIn() {
                                  getVersions();
                              }
                          }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                        }

                        @Override
                        protected void onSuccess(List<ApplicationVersionInfo> result) {
                            display.getVersionsGrid().setValue(result);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Get application environments. */
    private void getEnvironments() {
        try {
            BeanstalkClientService.getInstance().getEnvironments(
                    vfs.getId(),
                    project.getId(),
                    new AwsAsyncRequestCallback<List<EnvironmentInfo>>(new EnvironmentsInfoListUnmarshaller(),
                           new LoggedInHandler() {

                               @Override
                               public void onLoggedIn() {
                                   getEnvironments();
                               }
                           }, null) {

                        @Override
                        protected void processFail(Throwable exception) {
                        }

                        @Override
                        protected void onSuccess(List<EnvironmentInfo> result) {
                            display.getEnvironmentGrid().setValue(result);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Get the environment logs.
     *
     * @param environment
     *         {@link EnvironmentInfo}
     */
    private void getLogs(final EnvironmentInfo environment) {
        try {
            BeanstalkClientService.getInstance().getEnvironmentLogs(environment.getId(),
                new AwsAsyncRequestCallback<List<InstanceLog>>(
                        new EnvironmentsLogListUnmarshaller(), new LoggedInHandler() {

                    @Override
                    public void onLoggedIn() {
                        getLogs(environment);
                    }
                }, null) {

                    @Override
                    protected void processFail(Throwable exception) {
                        String message = AWSExtension.LOCALIZATION_CONSTANT
                                                     .logsEnvironmentFailed(
                                                             environment.getName());
                        if (exception instanceof ServerException &&
                            ((ServerException)exception).getMessage() != null) {
                            message += "<br>" + ((ServerException)exception).getMessage();
                        }
                        Dialogs.getInstance().showError(message);
                    }

                    @Override
                    protected void onSuccess(List<InstanceLog> result) {
                        if (result.size() == 0) {
                            Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.logsPreparing());
                            return;
                        }

                        StringBuffer message = new StringBuffer();
                        for (InstanceLog instanceLog : result) {
                            message.append(getUrl(instanceLog)).append("\n");
                        }
                        IDE.fireEvent(new OutputEvent(message.toString(), OutputMessage.Type.INFO));
                        Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.seeOutputForLinkToLog());
                    }
                });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Returns formatted URL.
     *
     * @param instanceLog
     *         {@link InstanceLog}
     * @return formatted URL
     */
    private String getUrl(InstanceLog instanceLog) {
        String logUrl = instanceLog.getLogUrl();
        if (!logUrl.startsWith("http")) {
            logUrl = "http://" + logUrl;
        }
        logUrl =
                "<a href=\"" + logUrl + "\" target=\"_blank\">"
                + AWSExtension.LOCALIZATION_CONSTANT.viewLogFromInstance(instanceLog.getInstanceId()) + "</a>";
        return logUrl;
    }

    /** @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent) */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        vfs = event.getVfsInfo();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent) */
    @Override
    public void onProjectClosed(ProjectClosedEvent event) {
        project = null;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent) */
    @Override
    public void onProjectOpened(ProjectOpenedEvent event) {
        project = event.getProject();
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentInfoChangedHandler#onEnvironmentInfoChanged(org.exoplatform.ide.extension.aws.client.beanstalk.environments.EnvironmentInfoChangedEvent) */
    @Override
    public void onEnvironmentInfoChanged(EnvironmentInfoChangedEvent event) {
        if (display != null) {
            getEnvironments();
        }
    }

}
