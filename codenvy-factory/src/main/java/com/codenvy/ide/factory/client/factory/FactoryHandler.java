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
package com.codenvy.ide.factory.client.factory;


import com.codenvy.api.factory.AdvancedFactoryUrl;
import com.codenvy.api.factory.SimpleFactoryUrl;
import com.codenvy.ide.factory.client.FactoryClientService;
import com.codenvy.ide.factory.client.FactoryExtension;
import com.codenvy.ide.factory.client.marshaller.ChildrenUnmarshallerWS;
import com.codenvy.ide.factory.shared.AdvancedFactorySpec;
import com.codenvy.ide.factory.shared.CopySpec10;
import com.codenvy.ide.factory.client.marshaller.AdvancedFactoryUrlUnmarshaller;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.IDELoader;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.PaaS;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.JsPopUpOAuthWindow;
import org.exoplatform.ide.client.framework.util.StringUnmarshaller;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.events.MessageHandler;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.marshaller.GitUrlInfoUnmarshaller;
import org.exoplatform.ide.git.client.ssh.SSHKeyProcessor;
import org.exoplatform.ide.git.client.ssh.SSHKeyProcessorEvent;
import org.exoplatform.ide.git.shared.GitUrlVendorInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handle user factory URL to start clone git repository with specified
 * properties or simply copy project from temporary workspace into permanent.
 */
public class FactoryHandler
        implements VfsChangedHandler, StartWithInitParamsHandler, ProjectOpenedHandler, AdvancedFactorySpec, CopySpec10 {

    /** Instance of virtual file system. */
    private VirtualFileSystemInfo vfs;

    /** Instance of {@link SimpleFactoryUrl} factory url. */
    private SimpleFactoryUrl factoryUrl;

    /** Construct factory handler. */
    public FactoryHandler() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(StartWithInitParamsEvent.TYPE, this);
    }

    /** {@inheritDoc} */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    /** {@inheritDoc} */
    @Override
    public void onStartWithInitParams(StartWithInitParamsEvent event) {
        if (getParamValue(DOWNLOAD_URL, event.getParameterMap()) != null &&
            getParamValue(PROJECT_ID, event.getParameterMap()) != null) {
            handleCopyProjects(event.getParameterMap());
        } else if (getParamValue(ID, event.getParameterMap()) != null) {
            handleAdvancedFactory(event.getParameterMap());
        } else {
            handleSimpleFactory(event.getParameterMap());
        }
    }

    /**
     * Get parameter value from map, which contains query params given by user.
     *
     * @param paramName
     *         name of the query parameter
     * @param parameterMap
     *         map, which contains all query parameters given by user
     * @return value of given parameter or null if parameter is not found.
     */
    private String getParamValue(String paramName, Map<String, List<String>> parameterMap) {
        if (parameterMap.get(paramName) != null && !parameterMap.get(paramName).isEmpty()) {
            return parameterMap.get(paramName).get(0);
        }
        return null;
    }

    /**
     * Handle user factory url, which contains two parameter: download link and projects ids
     * to start copying projects from temporary workspace into permanent.
     *
     * @param parameterMap
     *         map, which contains all query parameters given by user
     */
    private void handleCopyProjects(Map<String, List<String>> parameterMap) {
        final String downloadUrl = getParamValue(DOWNLOAD_URL, parameterMap);
        final List<String> projects = parameterMap.get(PROJECT_ID);
        
        try {
            FactoryClientService.getInstance()
            .copyProjects(downloadUrl, projects,
                          new RequestCallback<List<Item>>(new ChildrenUnmarshallerWS(new ArrayList<Item>())) {
                @Override
                protected void onSuccess(List<Item> result) {
                    if (result.size() == 1) {
                        IDE.fireEvent(new OpenProjectEvent((ProjectModel)result.get(0)));
                    } else {
                        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                            @Override
                            public void execute() {
                                IDE.fireEvent(new IDELoadCompleteEvent());
                            }
                        });
                    }
                }
                
                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (WebSocketException e) {
            handleError(e);
        }
    }

    /**
     * @param parameterMap
     */
    private void handleSimpleFactory(Map<String, List<String>> parameterMap) {
        Map<String, String> projectAttributes = new HashMap<String, String>();

        projectAttributes.put(PROJECT_TYPE, getParamValue(PROJECT_TYPE, parameterMap));
        projectAttributes.put(PROJECT_NAME, getParamValue(PROJECT_NAME, parameterMap));

        factoryUrl = new SimpleFactoryUrl(getParamValue(FACTORY_VERSION, parameterMap),
                                          getParamValue(VCS_TYPE, parameterMap),
                                          getParamValue(VCS_URL, parameterMap),
                                          getParamValue(COMMIT_ID, parameterMap),
                                          getParamValue(ACTION, parameterMap),
                                          getParamValue(OPEN_FILE, parameterMap),
                                          Boolean.parseBoolean(getParamValue(VCS_INFO, parameterMap)),
                                          getParamValue(ORG_ID, parameterMap),
                                          getParamValue(AFFILIATE_ID, parameterMap),
                                          getParamValue(VCS_BRANCH, parameterMap),
                                          projectAttributes,
                                          AdvancedFactoryUrlUnmarshaller.getVariables(getParamValue(VARIABLES, parameterMap)));

        //For back compatibility we check if user pass through factory url old version of commit id parameter.
        if (factoryUrl.getCommitid() == null && getParamValue("idcommit", parameterMap) != null) {
            factoryUrl.setCommitid(getParamValue("idcommit", parameterMap));
        }

        if (!factoryUrl.getProjectattributes().containsKey(PROJECT_NAME)) {
            factoryUrl.getProjectattributes().put(PROJECT_NAME, "Untitled_project");
        }

        prepareCloning();
    }

    /**
     * Handle user factory URL. Send map which contains factory parameter
     * to factory service which return instance of {@link AdvancedFactoryUrl}.
     * It's no needed to parse parameters map to inspect what version of factory
     * we received from user.
     *
     * @param parameterMap
     *         map, which contains all query parameters given by user
     */
    private void handleAdvancedFactory(Map<String, List<String>> parameterMap) {
        final AdvancedFactoryUrlUnmarshaller unmarshaller = new AdvancedFactoryUrlUnmarshaller(new AdvancedFactoryUrl());

        try {
            FactoryClientService.getInstance().getFactory(getParamValue(ID, parameterMap),
                    new AsyncRequestCallback<AdvancedFactoryUrl>(unmarshaller) {
                        @Override
                        protected void onSuccess(AdvancedFactoryUrl advancedFactoryUrl) {
                            IDE.fireEvent(new FactoryReceivedEvent(unmarshaller.getFactoryObject()));
                            factoryUrl = advancedFactoryUrl;
                            prepareCloning();
                        }

                        @Override
                        protected void onFailure(Throwable e) {
                            handleError(e);
                        }
                    });
        } catch (RequestException e) {
            handleError(e);
        }
    }


    /**
     * Check if in our project's root already exist such project
     * with the same name as we received in factory clone request.
     */
    private void prepareCloning() {
        try {
            VirtualFileSystem.getInstance()
                             .getChildren(vfs.getRoot(),
                                          ItemType.PROJECT,
                                          new AsyncRequestCallback<List<Item>>(
                                                  new ChildrenUnmarshaller(new ArrayList<Item>())) {
                                              @Override
                                              protected void onSuccess(List<Item> result) {
                                                  boolean projectDirExist = false;
                                                  for (Item item : result) {
                                                      if (item.getName().equals(factoryUrl.getProjectattributes()
                                                                                          .get(PROJECT_NAME))) {
                                                          projectDirExist = true;
                                                      }

                                                      if (item.hasProperty("codenow") &&
                                                          item.getPropertyValue("codenow")
                                                              .equals(factoryUrl.getVcsurl())) {
                                                          IDE.fireEvent(new OpenProjectEvent((ProjectModel)item));
                                                          return;
                                                      }
                                                  }
                                                  createFolder(projectDirExist);
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  createFolder(false);
                                              }
                                          });
        } catch (RequestException e) {
            handleError(e);
        }
    }

    /**
     * Create directory to which clone will be proceed. If directory
     * already exist in projects root then we create new with different name.
     *
     * @param projectDirExist
     *         create new directory or not
     */
    public void createFolder(boolean projectDirExist) {
        FolderModel folder = new FolderModel();
        folder.setName(factoryUrl.getProjectattributes().get(PROJECT_NAME) + ((projectDirExist) ? Random.nextInt() : ""));
        try {
            VirtualFileSystem
                    .getInstance()
                    .createFolder(vfs.getRoot(),
                                  new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder)) {
                                      @Override
                                      protected void onSuccess(final FolderModel projectFolder) {
                                          IDE.fireEvent(
                                                  new SSHKeyProcessorEvent(factoryUrl.getVcsurl(), false, new SSHKeyProcessor.Callback() {
                                                      @Override
                                                      public void onSuccess() {
                                                          cloneRepository(projectFolder);
                                                      }
                                                  }));
                                      }

                                      @Override
                                      protected void onFailure(Throwable e) {
                                          handleError(e);
                                      }
                                  });
        } catch (RequestException e) {
            handleError(e);
        }
    }

    /**
     * Open native js popup window with wso2 authorization page.
     *
     * @param callback
     *         callback for authorization status.
     */
    private void openOauthPopupWindow(final JsPopUpOAuthWindow.Callback callback, final String vcsUrl) {
        GitUrlInfoUnmarshaller unmarshaller = new GitUrlInfoUnmarshaller(new GitUrlVendorInfo());

        try {
            GitClientService.getInstance().getUrlVendorInfo(vcsUrl, new AsyncRequestCallback<GitUrlVendorInfo>(unmarshaller) {
                @Override
                protected void onSuccess(final GitUrlVendorInfo info) {
                    if (info.getVendorName() != null && !(info.getVendorName().equals("bitbucket") && !info.isGivenUrlSSH())) {
                        Dialogs.getInstance().ask(GitExtension.MESSAGES.authorizeTitle(),
                                                  GitExtension.MESSAGES.authorizeBody(info.getVendorBaseHost()),
                                                  new BooleanValueReceivedHandler() {
                                                      @Override
                                                      public void booleanValueReceived(Boolean value) {
                                                          if (value != null && value) {
                                                              new JsPopUpOAuthWindow().withOauthProvider(info.getVendorName())
                                                                                      .withCallback(callback)
                                                                                      .withScopes(info.getOAuthScopes())
                                                                                      .login();
                                                          }
                                                      }
                                                  }, true);
                    } else {
                        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.authorizeNotSupported()));
                    }
                }

                @Override
                protected void onFailure(Throwable e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Clone of the repository by sending request over WebSocket or HTTP.
     *
     * @param projectFolder
     *         folder to which clone should be proceed
     */
    private void cloneRepository(final FolderModel projectFolder) {
        IDELoader.getInstance().setMessage(FactoryExtension.LOCALIZATION_CONSTANTS.cloningProcess());
        IDELoader.getInstance().show();

        final JsPopUpOAuthWindow.Callback authCallback = new JsPopUpOAuthWindow.Callback() {
            @Override
            public void oAuthFinished(int authenticationStatus) {
                if (authenticationStatus == 2) {
                    IDE.fireEvent(new SSHKeyProcessorEvent(factoryUrl.getVcsurl(), true, new SSHKeyProcessor.Callback() {
                        @Override
                        public void onSuccess() {
                            cloneRepository(projectFolder);
                        }
                    }));
                }
            }
        };

        final MessageBus eventBus = IDE.messageBus();
        try {
            eventBus.subscribe("factory-events", webSocketEventHandler);
            FactoryClientService.getInstance()
                                .cloneProjectWS(vfs.getId(),
                                                projectFolder.getId(),
                                                factoryUrl,
                                                new RequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                                                    @Override
                                                    protected void onSuccess(StringBuilder clonedItem) {
                                                        IDELoader.getInstance().hide();
                                                        if (eventBus.isHandlerSubscribed(webSocketEventHandler, "factory-events")) {
                                                            eventBus.unsubscribe("factory-events", webSocketEventHandler);
                                                        }

                                                        onCloneSuccess(clonedItem.toString());
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable e) {
                                                        IDELoader.getInstance().hide();
                                                        if (eventBus.isHandlerSubscribed(webSocketEventHandler, "factory-events")) {
                                                            eventBus.unsubscribe("factory-events", webSocketEventHandler);
                                                        }

                                                        if (GitExtension.needAuth(e.getMessage())) {
                                                            openOauthPopupWindow(authCallback, factoryUrl.getVcsurl());
                                                        } else {
                                                            handleError(e);
                                                        }
                                                    }
                                                });
        } catch (WebSocketException e) {
            IDELoader.getInstance().hide();
            if (eventBus.isHandlerSubscribed(webSocketEventHandler, "factory-events")) {
                eventBus.unsubscribe("factory-events", webSocketEventHandler);
            }
            IDE.fireEvent(new SSHKeyProcessorEvent(factoryUrl.getVcsurl(), false, new SSHKeyProcessor.Callback() {
                @Override
                public void onSuccess() {
                    cloneRepositoryREST(projectFolder);
                }
            }));
        }
    }

    /** Handler to display Git error messages into console through websocket. */
    private MessageHandler webSocketEventHandler = new MessageHandler() {
        @Override
        public void onMessage(String message) {
            IDE.fireEvent(new OutputEvent(message, Type.GIT));
        }
    };

    /**
     * Clone of the repository by sending request over REST.
     *
     * @param projectFolder
     *         folder to which clone should be proceed
     */
    private void cloneRepositoryREST(final FolderModel projectFolder) {
        IDELoader.getInstance().setMessage(FactoryExtension.LOCALIZATION_CONSTANTS.cloningProcess());
        IDELoader.getInstance().show();

        final JsPopUpOAuthWindow.Callback authCallback = new JsPopUpOAuthWindow.Callback() {
            @Override
            public void oAuthFinished(int authenticationStatus) {
                if (authenticationStatus == 2) {
                    IDE.fireEvent(new SSHKeyProcessorEvent(factoryUrl.getVcsurl(), true, new SSHKeyProcessor.Callback() {
                        @Override
                        public void onSuccess() {
                            cloneRepositoryREST(projectFolder);
                        }
                    }));
                }
            }
        };

        try {
            FactoryClientService.getInstance()
                                .cloneProject(vfs.getId(),
                                              projectFolder.getId(),
                                              factoryUrl,
                                              new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {
                                                  @Override
                                                  protected void onSuccess(StringBuilder clonedItem) {
                                                      IDELoader.getInstance().hide();
                                                      onCloneSuccess(clonedItem.toString());
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable e) {
                                                      IDELoader.getInstance().hide();

                                                      if (GitExtension.needAuth(e.getMessage())) {
                                                          openOauthPopupWindow(authCallback, factoryUrl.getVcsurl());
                                                      } else {
                                                          handleError(e);
                                                      }
                                                  }
                                              });
        } catch (RequestException e) {
            IDELoader.getInstance().hide();
            handleError(e);
        }
    }

    /**
     * Get target list in order to specified project type.
     * //TODO Need to decide how to work in future with enum of ProjectType
     *
     * @param currentType
     *         type of project for which we should collect target list
     * @return {@link Property} instance with key "exoide:target" and value consist from PaaS target list
     */
    @SuppressWarnings("deprecation")
    private Property getTarget(Property currentType) {
        List<String> target = new ArrayList<String>();
        org.exoplatform.ide.client.framework.project.ProjectType currentProjType =
                org.exoplatform.ide.client.framework.project.ProjectType.fromValue(currentType.getValue().get(0));
        List<PaaS> paases = IDE.getInstance().getPaaSes();
        for (PaaS paas : paases) {
            if (paas.getSupportedProjectTypes().contains(currentProjType)) {
                target.add(paas.getId());
            }
        }
        return new PropertyImpl("exoide:target", target);
    }

    /**
     * Perform actions when repository was successfully cloned.
     * Find user links in project instance. If it's empty we
     * retrieve this project from virtual file system and get user
     * links for future operations.
     *
     * @param clonedItem
     *         {@link ProjectModel} instance of cloned directory.
     */
    private void onCloneSuccess(final String clonedItem) {
        final JSONObject clonedJsonItem = JSONParser.parseLenient(clonedItem).isObject();
        final String itemType = clonedJsonItem.get("itemType").isString().stringValue();

        IDE.fireEvent(new OutputEvent(FactoryExtension.LOCALIZATION_CONSTANTS.cloneSuccessful(factoryUrl.getVcsurl())));

        if (ItemType.PROJECT.toString().equalsIgnoreCase(itemType)) {
            writeTarget(clonedJsonItem.get("id").isString().stringValue());
        } else {
            List<Property> properties = new ArrayList<Property>();
            String id = clonedJsonItem.get("id").isString().stringValue();
            properties.add(new PropertyImpl("codenow", factoryUrl.getVcsurl()));
            IDE.fireEvent(new ConvertToProjectEvent(id, vfs.getId(), factoryUrl.getProjectattributes().get(PROJECT_TYPE), properties));
        }
    }

    /**
     * Write PaaS target to properties of cloned project.
     *
     * @param folderId
     *         id of project folder
     */
    private void writeTarget(String folderId) {
        try {
            ProjectModel project = new ProjectModel();
            ItemWrapper item = new ItemWrapper(project);
            ItemUnmarshaller unmarshaller = new ItemUnmarshaller(item);
            VirtualFileSystem.getInstance().getItemById(folderId, new AsyncRequestCallback<ItemWrapper>(unmarshaller) {
                @Override
                protected void onSuccess(ItemWrapper result) {
                    boolean targetSet = false;
                    Property pt = result.getItem().getProperty("vfs:projectType");
                    Property target = getTarget(pt);
                    for (Property p : result.getItem().getProperties()) {
                        if (p.getName().equals(target.getName())) {
                            p.setValue(target.getValue());
                            targetSet = true;
                        }
                    }
                    if (!targetSet) {
                        result.getItem().getProperties().add(target);
                    }
                    writeUserPropertiesToProject(result.getItem());
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
     * Check if project has links, otherwise we try to fetch project object and create links.
     *
     * @param item
     *         item to update
     */
    private void writeUserPropertiesToProject(final Item item) {
        if (item.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(item.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(item))) {
                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      item.setLinks(result.getItem().getLinks());
                                                      updateProjectProperties(item);
                                                  }

                                                  @Override
                                                  protected void onFailure(Throwable exception) {
                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                  }
                                              });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            }
        } else {
            updateProjectProperties(item);
        }
    }

    /**
     * Update item with new properties.
     *
     * @param item
     *         item to update
     */
    private void updateProjectProperties(Item item) {
        IDE.addHandler(ProjectOpenedEvent.TYPE, this);

        try {
            ItemWrapper itemWrapper = new ItemWrapper(item);
            ItemUnmarshaller unmarshaller = new ItemUnmarshaller(itemWrapper);
            VirtualFileSystem.getInstance().updateItem(item, null, new AsyncRequestCallback<ItemWrapper>(unmarshaller) {
                @Override
                protected void onSuccess(ItemWrapper result) {
                    IDE.fireEvent(new OpenProjectEvent((ProjectModel)result.getItem()));
                }

                @Override
                protected void onFailure(Throwable e) {
                    IDE.fireEvent(new ExceptionThrownEvent(e));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        } finally {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    IDE.fireEvent(new RefreshBrowserEvent());
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(final ProjectOpenedEvent event) {
        if (factoryUrl.getOpenfile() != null && !factoryUrl.getOpenfile().isEmpty()) {
            final String path = event.getProject().getPath() + '/' + factoryUrl.getOpenfile();
            try {
                VirtualFileSystem.getInstance()
                                 .getItemByPath(path,
                                                new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper())) {
                                                    @Override
                                                    protected void onSuccess(final ItemWrapper result) {
                                                        if (result.getItem() instanceof File) {
                                                            FileModel file = (FileModel)result.getItem();
                                                            file.setProject(event.getProject());
                                                            IDE.fireEvent(new OpenFileEvent(file));
                                                        }
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable e) {
                                                        IDE.fireEvent(new OutputEvent(FactoryExtension.LOCALIZATION_CONSTANTS
                                                                                                      .fileToOpenIsNotFound(path),
                                                                                      Type.WARNING));
                                                    }
                                                });
            } catch (RequestException e) {
                IDE.fireEvent(new ExceptionThrownEvent(e));
            } finally {
                IDE.removeHandler(ProjectOpenedEvent.TYPE, this);
            }
        }
    }

    /**
     * Handle error from cloning operations and show them to output console.
     *
     * @param e
     *         instance of {@link Throwable}
     */
    private void handleError(Throwable e) {
        String message = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage()
                                                                               : GitExtension.MESSAGES.cloneFailed(
                                                                                       factoryUrl.getVcsurl());
        IDE.fireEvent(new OutputEvent(message, Type.GIT));
    }

}
