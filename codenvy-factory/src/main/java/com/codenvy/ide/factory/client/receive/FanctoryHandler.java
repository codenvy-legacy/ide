/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.factory.client.receive;

import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.factory.client.FactorySpec10;
import com.codenvy.ide.factory.client.copy.CopySpec10;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodeNowHandler.java Dec 6, 2012 vetal $
 */
public class FanctoryHandler implements VfsChangedHandler, StartWithInitParamsHandler {

    private final String          restServiceContext;
    private VirtualFileSystemInfo vfs;

    public FanctoryHandler() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);
        IDE.addHandler(StartWithInitParamsEvent.TYPE, this);
        restServiceContext = Utils.getWorkspaceName();
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework
     *      .application.event.VfsChangedEvent)
     */
    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    @Override
    public void onStartWithInitParams(StartWithInitParamsEvent event) {
        if (isFactoryValidParam(event.getParameterMap())) {
            handleFactory(event.getParameterMap());
        } else if (isCopyAllProjectsValidParam(event.getParameterMap())) {
            handleCopyProjects(event.getParameterMap());
        }
    }

    private void handleFactory(Map<String, List<String>> parameterMap) {
        String giturl = parameterMap.get(FactorySpec10.VCS_URL).get(0);

        String prjName;

        if (parameterMap.get(FactorySpec10.PROJECT_NAME) != null
            && !parameterMap.get(FactorySpec10.PROJECT_NAME).isEmpty()) {
            prjName = parameterMap.get(FactorySpec10.PROJECT_NAME).get(0);
        } else {
            prjName = giturl.substring(giturl.lastIndexOf('/') + 1, giturl.lastIndexOf(".git"));
        }

        String prjType;

        if (parameterMap.get(FactorySpec10.PROJECT_TYPE) != null
            && !parameterMap.get(FactorySpec10.PROJECT_TYPE).isEmpty()) {
            prjType = URL.decodeQueryString(parameterMap.get(FactorySpec10.PROJECT_TYPE).get(0));
        } else {
            prjType = giturl.substring(giturl.lastIndexOf('/') + 1, giturl.lastIndexOf(".git"));
        }

        String idCommit = parameterMap.get(FactorySpec10.COMMIT_ID).get(0);


        cloneProject(giturl, prjName, prjType, idCommit);
    }

    /** @param initParam */
    private boolean isFactoryValidParam(Map<String, List<String>> initParam) {
        if (initParam == null || initParam.isEmpty()) {
            return false;
        }
        if (!initParam.containsKey(FactorySpec10.VERSION_PARAMETER)
            || initParam.get(FactorySpec10.VERSION_PARAMETER).size() != 1
            || !initParam.get(FactorySpec10.VERSION_PARAMETER).get(0).equals(FactorySpec10.CURRENT_VERSION)) {
            return false;
        }
        if (!initParam.containsKey(FactorySpec10.VCS) || initParam.get(FactorySpec10.VCS).isEmpty()
            || !initParam.get(FactorySpec10.VCS).get(0).equalsIgnoreCase(FactorySpec10.DEFAULT_VCS)) {
            return false;
        }
        if (!initParam.containsKey(FactorySpec10.VCS_URL) || initParam.get(FactorySpec10.VCS_URL) == null
            || initParam.get(FactorySpec10.VCS_URL).isEmpty()) {
            return false;
        }
        return true;
    }

    private void handleCopyProjects(Map<String, List<String>> parameterMap) {
        final String downloadUrl = parameterMap.get(CopySpec10.DOWNLOAD_URL).get(0);
        final String projectId = parameterMap.get(CopySpec10.PROJECT_ID).get(0);
        try {
            String uri = "/copy/projects?" + CopySpec10.DOWNLOAD_URL + "=" + downloadUrl + "&" + CopySpec10.PROJECT_ID + "=" + projectId;
            RequestMessage message = RequestMessageBuilder.build(RequestBuilder.POST, restServiceContext + uri).getRequestMessage();
            IDE.messageBus().send(message, new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            IDE.fireEvent(new IDELoadCompleteEvent());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (WebSocketException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private boolean isCopyAllProjectsValidParam(Map<String, List<String>> parameterMap) {
        if (parameterMap == null || parameterMap.isEmpty()) {
            return false;
        }
        return parameterMap.get(CopySpec10.DOWNLOAD_URL) != null &&
               parameterMap.get(CopySpec10.PROJECT_ID) != null;
    }

    private void cloneProject(final String giturl, final String prjName, final String prjType, final String idCommit) {
        try {

            VirtualFileSystem.getInstance()
                             .getChildren(vfs.getRoot(), ItemType.PROJECT,
                                          new AsyncRequestCallback<List<Item>>(
                                                                               new ChildrenUnmarshaller(new ArrayList<Item>())) {

                                              @Override
                                              protected void onSuccess(List<Item> result) {
                                                  boolean itemExist = false;
                                                  for (Item item : result) {
                                                      if (item.getName().equals(prjName)) {
                                                          itemExist = true;
                                                      }
                                                      if (item.hasProperty("codenow")) {
                                                          String codenow = item.getPropertyValue("codenow");
                                                          if (codenow.equals(giturl)) {
                                                              IDE.fireEvent(new OpenProjectEvent((ProjectModel)item));
                                                              return;
                                                          }
                                                      }
                                                  }
                                                  if (itemExist) {
                                                      doClone(giturl, "origin",
                                                              prjName + "-" + Random.nextInt(Integer.MAX_VALUE), prjType, idCommit);
                                                  } else {
                                                      doClone(giturl, "origin", prjName, prjType, idCommit);
                                                  }
                                              }

                                              @Override
                                              protected void onFailure(Throwable exception) {
                                                  doClone(giturl, "origin", prjName, prjType, idCommit);
                                              }
                                          });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Going to cloning repository. Clone process flow 3 steps: - create new folder with name workDir - clone repository to this folder -
     * convert folder to project. This need because by default project with out file and folder not empty. It content ".project" item. Clone
     * is impossible to not empty folder
     * 
     * @param remoteUri - git url
     * @param remoteName - remote name (by default origin)
     * @param workDir - name of target folder
     */
    public void doClone(final String remoteUri, final String remoteName, final String workDir, final String prjType,
                        final String idCommit) {
        FolderModel folder = new FolderModel();
        folder.setName(workDir);
        try {
            VirtualFileSystem.getInstance().createFolder(vfs.getRoot(),
                                                         new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(folder)) {
                                                             @Override
                                                             protected void onSuccess(FolderModel result) {
                                                                 cloneRepository(remoteUri, remoteName, prjType, result, idCommit);
                                                             }

                                                             @Override
                                                             protected void onFailure(Throwable exception) {
                                                                 String errorMessage =
                                                                                       (exception.getMessage() != null &&
                                                                                       exception.getMessage().length() > 0)
                                                                                           ? exception.getMessage()
                                                                                           : GitExtension.MESSAGES
                                                                                                                  .cloneFailed(


                                                                                                                  remoteUri);
                                                                 IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                                             }
                                                         });
        } catch (RequestException e) {
            e.printStackTrace();
            String errorMessage =
                                  (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage()
                                      : GitExtension.MESSAGES
                                                             .cloneFailed(remoteUri);
            IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
        }
    }

    /**
     * Clone of the repository by sending request over WebSocket or HTTP.
     * 
     * @param remoteUri the location of the remote repository
     * @param remoteName remote name instead of "origin"
     * @param folder folder (root of GIT repository)
     */
    private void cloneRepository(final String remoteUri, final String remoteName, final String prjType, final FolderModel folder,
                                 final String idCommit) {
        try {
            String uri = "/factory/clone?vfsid=" + vfs.getId() + "&projectid=" + folder.getId() + "&remoteuri=" + remoteUri + "&idcommit=" +
                         idCommit;
            RequestMessage message =
                                     RequestMessageBuilder.build(RequestBuilder.POST, restServiceContext + uri).
                                                          getRequestMessage();
            IDE.messageBus().send(message, new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    onCloneSuccess(folder, prjType, remoteUri);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception, remoteUri);
                }
            });
        } catch (WebSocketException e) {
            cloneRepositoryREST(remoteUri, remoteName, prjType, folder, idCommit);
        }
    }

    /** Get the necessary parameters values and call the clone repository method (over HTTP). */
    private void cloneRepositoryREST(final String remoteUri, String remoteName, final String prjType, final FolderModel folder,
                                     final String idCommit) {

        try {
            String uri = "/factory/clone?vfsid=" + vfs.getId() + "&projectid=" + folder.getId() + "&remoteuri=" + remoteUri + "&idcommit=" +
                         idCommit;
            AsyncRequest.build(RequestBuilder.POST, uri).send(new AsyncRequestCallback<Object>() {
                @Override
                protected void onSuccess(Object result) {
                }

                @Override
                protected void onFailure(Throwable exception) {
                }
            });
        } catch (RequestException e) {
            handleError(e, remoteUri);
        }
    }

    /**
     * Perform actions when repository was successfully cloned.
     * 
     * @param folder {@link FolderModel} to clone
     */
    private void onCloneSuccess(FolderModel folder, String prjType, String remoteUri) {
        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(remoteUri), Type.GIT));
        // TODO: not good, comment temporary need found other way
        // for inviting collaborators
        // showInvitation(repoInfo.getRemoteUri());

        List<Property> properties = new ArrayList<Property>();
        properties.add(new PropertyImpl("codenow", remoteUri));
        try{
          IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId(), prjType, properties));

        } catch (Throwable e){
            Log.debug(getClass(), e);
        }
    }

    private void handleError(Throwable e, String remoteUri) {
        String errorMessage =
                              (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage()
                                  : GitExtension.MESSAGES
                                                         .cloneFailed(remoteUri);
        IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
    }

}
