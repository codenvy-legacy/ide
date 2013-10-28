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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.project.ConvertToProjectEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;
import org.exoplatform.ide.extension.samples.client.marshal.OrganizationsUnmarshaller;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GenerateGitHubKeyEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GitHubKeyGeneratedEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.GitHubKeyGeneratedHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.clone.CloneRepositoryCompleteEvent;
import org.exoplatform.ide.git.client.clone.GitURLParser;
import org.exoplatform.ide.git.client.github.GitHubClientService;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RepoInfoUnmarshallerWS;
import org.exoplatform.ide.git.client.marshaller.StringUnmarshaller;
import org.exoplatform.ide.git.shared.GitHubRepository;
import org.exoplatform.ide.git.shared.GitHubRepositoryList;
import org.exoplatform.ide.git.shared.GitHubUser;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FolderUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for importing user's GitHub project to IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubPresenter.java Dec 7, 2011 3:37:11 PM vereshchaka $
 */
public class ImportFromGithubPresenter implements ImportFromGithubHandler, ViewClosedHandler, GitHubKeyGeneratedHandler,
                                      UserInfoReceivedHandler, VfsChangedHandler, ScrollEndHandler {
    public interface Display extends IsView {
        /**
         * Returns project's name field.
         * 
         * @return {@link HasValue} project's name field
         */
        HasValue<String> getProjectNameField();

        /**
         * Returns next button's click handler.
         * 
         * @return {@link HasClickHandlers} button's click handler
         */
        HasClickHandlers getFinishButton();

        /**
         * Returns cancel button's click handler.
         * 
         * @return {@link HasClickHandlers} button's click handler
         */
        HasClickHandlers getCancelButton();

        /**
         * Returns repositories list grid.
         * 
         * @return {@link ListGridItem} repositories list grid
         */
        ListGridItem<ProjectData> getRepositoriesGrid();
        
        /**
         * Add repositories to the grid.
         * 
         * @param values repositories to be added
         */
        void addRepositories(List<ProjectData> values);

        /**
         * Set the enabled state of the next button.
         * 
         * @param enabled enabled state of the next button
         */
        void setFinishButtonEnabled(boolean enabled);

        /**
         * Returns user's name field.
         * 
         * @return {@link HasValue} user's name field
         */
        TextFieldItem getAccountNameBox();

        /**
         * Sets up data in user's name field.
         */
        void setAccountNameValues(String[] values);
        
        /**
         * Add account name to the list of proposed.
         * 
         * @param value
         */
        void addAccountNameValue(String value);

        /**
         * Clears repositories grid.
         */
        void refreshRepositoriesGrid();
        
        /**
         * Set handler for scroll eto the end event.
         * 
         * @param handler
         */
        void setScrollEndHandler(ScrollEndHandler handler);
        
        /**
         * Set the error state of account field.
         * 
         * @param isError
         */
        void changeAccountFieldState(boolean isError);
        
        /**
         * Set error message to display.
         * 
         * @param error
         */
        void setErrorMessage(String error);
    }

    /**
     * Current user information.
     */
    private UserInfo                            userInfo;
    
    /**
     * Authorized to GitHub user's information.
     */
    private GitHubUser                          githubUser;

    /**
     * All loaded repositories for each account.
     */
    private Map<String, GitHubRepositoryList> repositories = new HashMap<String, GitHubRepositoryList>();
    
    /**
     * List of organizations, where authorized user is a member.
     */
    private List<String>                      organizations = new ArrayList<String>();

    /** Presenter's display. */
    private Display                             display;

    /** Map of read-only URLs. Key is ssh Git URL - value is read-only Git URL. */
    private HashMap<String, String>             readonlyUrls = new HashMap<String, String>();

    private ProjectData                         data;

    private VirtualFileSystemInfo               vfs;
    
    /**
     * Flag for user's typing operation.
     */
    private boolean                           isTyping           = false;
    
    /**
     * Timer is used for delay typing.
     */
    private Timer                             typingAccountTimer = new Timer() {
                                                                     @Override
                                                                     public void run() {
                                                                         isTyping = false;
                                                                     }
                                                                 };

    public ImportFromGithubPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ImportFromGithubEvent.TYPE, this);
        IDE.addHandler(UserInfoReceivedEvent.TYPE, this);
        IDE.addHandler(VfsChangedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    private void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
        
        
        display.getRepositoriesGrid().addSelectionHandler(new SelectionHandler<ProjectData>() {

            @Override
            public void onSelection(SelectionEvent<ProjectData> event) {
                if (event.getSelectedItem() != null) {
                    data = event.getSelectedItem();
                    display.getProjectNameField().setValue(event.getSelectedItem().getName());
                    display.setFinishButtonEnabled(true);
                }
            }
        });
        
        display.getFinishButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (data != null && !display.getProjectNameField().getValue().isEmpty()) {
                    if (!display.getProjectNameField().getValue().matches("(^[-.a-zA-Z0-9])([-._a-zA-Z0-9])*$")) {
                        if (display.getProjectNameField().getValue().startsWith("_")) {
                            Dialogs.getInstance()
                                   .showInfo(GitExtension.MESSAGES.noIncorrectProjectNameTitle(),
                                             GitExtension.MESSAGES.projectNameStartWith_Message());
                        } else {
                            Dialogs.getInstance()
                                   .showInfo(GitExtension.MESSAGES.noIncorrectProjectNameTitle(),
                                             GitExtension.MESSAGES.noIncorrectProjectNameMessage());
                        }
                    } else {
                        createFolder();
                    }
                }
            }
        });
        

        display.getAccountNameBox().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.changeAccountFieldState(false);
                
                //Ignore the event, when user is typing:
                if (isTyping){
                    return;
                }
                
                if (event.getValue() != null & event.getValue().length() > 0) {
                    loadRepositories(event.getValue());
                }
            }
        });
        
        display.getAccountNameBox().addKeyUpHandler(new KeyUpHandler() {
            
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13) {
                    isTyping = false;
                    typingAccountTimer.cancel();
                } else {
                    isTyping = true;
                    typingAccountTimer.cancel();
                    typingAccountTimer.schedule(2000);
                }
            }
        });
        
        display.setScrollEndHandler(this);
        
    }

    /** Open view. */
    private void openView() {
        if (display == null) {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView(d.asView());
            display = d;
            bindDisplay();
            return;
        }
    }

    /**
     * Load repositories.
     * 
     * @param account
     */
    private void loadRepositories(String account) {
        display.setFinishButtonEnabled(false);
        display.getProjectNameField().setValue("");
        if (repositories.containsKey(account)) {
            refreshProjectList(account);
        } else {
            if (organizations.contains(account)) {
                loadOrganizationRepositories(account);
            } else if (githubUser.getLogin().equals(account)) {
                loadCurrentUserRepositories();
            } else {
                loadUnknownAccountRepositories(account);
            }
        }
    }
    
    /**
     * Load repositories of the authorized user.
     */
    private void loadCurrentUserRepositories() {
        AutoBean<GitHubRepositoryList> autoBean = GitExtension.AUTO_BEAN_FACTORY.gitHubRepositoryList();
        AutoBeanUnmarshaller<GitHubRepositoryList> unmarshaller = new AutoBeanUnmarshaller<GitHubRepositoryList>(autoBean);
        try {
            GitHubClientService.getInstance().getRepositoriesList(new AsyncRequestCallback<GitHubRepositoryList>(unmarshaller) {

                @Override
                protected void onSuccess(GitHubRepositoryList result) {
                    repositories.put(githubUser.getLogin(), result);
                    refreshProjectList(githubUser.getLogin());
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
     * Load repositories by organization name.
     * 
     * @param organization
     */
    private void loadOrganizationRepositories(final String organization) {
        AutoBean<GitHubRepositoryList> autoBean = GitExtension.AUTO_BEAN_FACTORY.gitHubRepositoryList();
        AutoBeanUnmarshaller<GitHubRepositoryList> unmarshaller = new AutoBeanUnmarshaller<GitHubRepositoryList>(autoBean);
        try {
            GitHubClientService.getInstance().getRepositoriesByOrganization(organization,
                                                                            new AsyncRequestCallback<GitHubRepositoryList>(unmarshaller) {

                                                                                @Override
                                                                                protected void onSuccess(GitHubRepositoryList result) {
                                                                                    repositories.put(organization, result);
                                                                                    refreshProjectList(organization);
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
     * Load repositories by account : user name, organization or may not be found.
     * 
     * @param account
     */
    private void loadUnknownAccountRepositories(final String account) {
        AutoBean<GitHubRepositoryList> autoBean = GitExtension.AUTO_BEAN_FACTORY.gitHubRepositoryList();
        AutoBeanUnmarshaller<GitHubRepositoryList> unmarshaller = new AutoBeanUnmarshaller<GitHubRepositoryList>(autoBean);
        try {
            GitHubClientService.getInstance().getRepositoriesByAccount(account,
                                                                       new AsyncRequestCallback<GitHubRepositoryList>(unmarshaller) {

                                                                           @Override
                                                                           protected void onSuccess(GitHubRepositoryList result) {
                                                                               display.addAccountNameValue(account);
                                                                               repositories.put(account, result);
                                                                               refreshProjectList(account);
                                                                           }

                                                                           @Override
                                                                           protected void onFailure(Throwable exception) {
                                                                               if (exception instanceof ServerException
                                                                                   && ((ServerException)exception).getHTTPStatus() == HTTPStatus.NOT_FOUND) {
                                                                                   display.changeAccountFieldState(true);
                                                                                   display.setErrorMessage(SamplesExtension.LOCALIZATION_CONSTANT.importFromGitHubErrorLabel(account));
                                                                                   display.getRepositoriesGrid().setValue(new ArrayList<ProjectData>(), true);
                                                                               } else {
                                                                                   IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                               }
                                                                           }
                                                                       });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
    
    /**
     * Get next page of repositories.
     * 
     * @param account name of the GitHub account
     * @param repositoryList
     */
    private void getNextPage(final String account, final GitHubRepositoryList repositoryList) {
        if (repositoryList.getNextPage() != null) {
            AutoBean<GitHubRepositoryList> autoBean = GitExtension.AUTO_BEAN_FACTORY.gitHubRepositoryList();
            AutoBeanUnmarshaller<GitHubRepositoryList> unmarshaller = new AutoBeanUnmarshaller<GitHubRepositoryList>(autoBean);
            try {
                GitHubClientService.getInstance().getPage(repositoryList.getNextPage(),
                                                          new AsyncRequestCallback<GitHubRepositoryList>(unmarshaller) {

                                                              @Override
                                                              protected void onSuccess(GitHubRepositoryList result) {
                                                                  ArrayList<GitHubRepository> repositoriesToAdd =
                                                                                                                  new ArrayList<GitHubRepository>(
                                                                                                                                                  result.getRepositories());
                                                                  ArrayList<GitHubRepository> temp =
                                                                                                     new ArrayList<GitHubRepository>(
                                                                                                                                     repositoryList.getRepositories());
                                                                  temp.addAll(result.getRepositories());
                                                                  result.setRepositories(temp);
                                                                  repositories.put(account, result);
                                                                  addProjectList(repositoriesToAdd);
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
    }
    
    /**
     * @param account
     */
    private void refreshProjectList(String account) {
        List<ProjectData> projectDataList = new ArrayList<ProjectData>();
        readonlyUrls.clear();
        for (GitHubRepository repo : repositories.get(account).getRepositories()) {
            projectDataList.add(new ProjectData(repo.getName(), repo.getDescription(), null,
                                                null, repo.getSshUrl(), repo.getGitUrl()));
            readonlyUrls.put(repo.getSshUrl(), repo.getGitUrl());
        }
        display.getRepositoriesGrid().setValue(projectDataList);

        if (projectDataList.size() != 0) {
            display.getRepositoriesGrid().selectItem(projectDataList.get(0));
            data = projectDataList.get(0);
        }
    }
    
    /**
     * Add more repositories to the list.
     * 
     * @param repositories
     */
    private void addProjectList(ArrayList<GitHubRepository> repositories) {
        List<ProjectData> projectDataList = new ArrayList<ProjectData>();
        for (GitHubRepository repo : repositories) {
            projectDataList.add(new ProjectData(repo.getName(), repo.getDescription(), null,
                                                null, repo.getSshUrl(), repo.getGitUrl()));
            readonlyUrls.put(repo.getSshUrl(), repo.getGitUrl());
        }
        display.addRepositories(projectDataList);
    }
    
    

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            organizations.clear();
        }
    }

    /**
     * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubHandler#onImportFromGithub(org.exoplatform.ide
     *      .extension.samples.client.githubimport.ImportFromGithubEvent)
     */
    @Override
    public void onImportFromGithub(ImportFromGithubEvent event) {
        if (userInfo != null) {
            getToken(userInfo.getName());
            return;
        } else {
            Dialogs.getInstance().showError(SamplesExtension.LOCALIZATION_CONSTANT.userNotFound());
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client
     *      .framework.userinfo.event.UserInfoReceivedEvent)
     */
    @Override
    public void onUserInfoReceived(UserInfoReceivedEvent event) {
        this.userInfo = event.getUserInfo();
    }

    private void getToken(String user) {
        try {
            GitHubClientService.getInstance()
                               .getUserToken(user,
                                             new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder())) {

                                                 @Override
                                                 protected void onSuccess(StringBuilder result) {
                                                     generateGitHubKey();
                                                 }

                                                 @Override
                                                 protected void onFailure(Throwable exception) {
                                                     generateGitHubKey();
                                                 }
                                             });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    public void generateGitHubKey() {
        IDE.addHandler(GitHubKeyGeneratedEvent.TYPE, this);
        IDE.fireEvent(new GenerateGitHubKeyEvent());
    }

    @Override
    public void onGithubKeyGenerated(GitHubKeyGeneratedEvent event) {
        IDE.removeHandler(GitHubKeyGeneratedEvent.TYPE, this);
        getUserInfo();
    }

    /**
     * Get the list of authorized user's organizations.
     */
    private void getUserOrganizations() {
        organizations.clear();
        try {
            GitHubClientService.getInstance()
                               .getOrganizations(new AsyncRequestCallback<List<String>>(
                                                                                        new OrganizationsUnmarshaller(
                                                                                                                      new ArrayList<String>())) {

                                                     @Override
                                                     protected void onSuccess(List<String> result) {
                                                         organizations.addAll(result);
                                                         result.add(0, githubUser.getLogin());
                                                         openView();
                                                         display.setAccountNameValues(result.toArray(new String[result.size()]));
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
     * Get the authorized GitHub user's information.
     */
    private void getUserInfo() {
        try {
            AutoBean<GitHubUser> autoBean = GitExtension.AUTO_BEAN_FACTORY.gitHubUser();
            AutoBeanUnmarshaller<GitHubUser> unmarshaller = new AutoBeanUnmarshaller<GitHubUser>(autoBean);
            GitHubClientService.getInstance().getUserInfo(new AsyncRequestCallback<GitHubUser>(unmarshaller) {

                @Override
                protected void onSuccess(GitHubUser result) {
                    githubUser = result;
                    getUserOrganizations();
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
    
    private void revertProjectImport(FolderModel path) {
        if (path.getLinks().isEmpty()) {
            try {
                VirtualFileSystem.getInstance()
                                 .getItemById(path.getId(),
                                              new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(path))) {

                                                  @Override
                                                  protected void onSuccess(ItemWrapper result) {
                                                      deleteFolder((FolderModel)result.getItem());
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
            deleteFolder(path);
        }
    }

    private void deleteFolder(FolderModel folder) {
        try {
            VirtualFileSystem.getInstance().delete(folder,
                                                   new AsyncRequestCallback<String>() {
                                                       @Override
                                                       protected void onSuccess(String result) {
                                                           // Do nothing
                                                       }

                                                       @Override
                                                       protected void onFailure(Throwable exception) {
                                                           IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                  "Exception during folder removing"));
                                                       }
                                                   });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during removing of directory project"));
        }
    }

    private void createFolder() {
        FolderModel parent = (FolderModel)vfs.getRoot();
        FolderModel model = new FolderModel();
        model.setName(display.getProjectNameField().getValue());
        model.setParent(parent);
        try {
            VirtualFileSystem.getInstance().createFolder(parent,
                                                         new AsyncRequestCallback<FolderModel>(new FolderUnmarshaller(model)) {
                                                             @Override
                                                             protected void onSuccess(FolderModel result) {
                                                                 cloneFolder(data, result);
                                                             }

                                                             @Override
                                                             protected void onFailure(Throwable exception) {
                                                                 IDE.fireEvent(new ExceptionThrownEvent(exception,
                                                                                                        "Exception during creating project"));
                                                             }
                                                         });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e, "Exception during creating project"));
        }
    }

    /** Get the necessary parameters values and call the clone repository method (over WebSocket or HTTP). */
    private void cloneFolder(final ProjectData repo, final FolderModel folder) {

        String remoteUri = repo.getRepositoryUrl();

        if (!remoteUri.endsWith(".git")) {
            remoteUri += ".git";
        }
        JobManager.get().showJobSeparated();

        try {
            GitClientService.getInstance().cloneRepositoryWS(vfs.getId(), folder, remoteUri, null,
                                                             new RequestCallback<RepoInfo>(new RepoInfoUnmarshallerWS(new RepoInfo())) {
                                                                 @Override
                                                                 protected void onSuccess(RepoInfo result) {
                                                                     onRepositoryCloned(result, folder);
                                                                 }

                                                                 @Override
                                                                 protected void onFailure(Throwable exception) {
                                                                     revertProjectImport(folder);
                                                                     handleError(exception, repo.getRepositoryUrl());
                                                                 }
                                                             });
            IDE.getInstance().closeView(display.asView().getId());
        } catch (WebSocketException e) {
            cloneFolderREST(folder, remoteUri);
        }
    }

    private void cloneFolderREST(final FolderModel folder, final String remoteUri) {
        try {
            GitClientService.getInstance().cloneRepository(vfs.getId(), folder, remoteUri, null,
                                                           new AsyncRequestCallback<RepoInfo>(new RepoInfoUnmarshaller(new RepoInfo())) {
                                                               @Override
                                                               protected void onSuccess(RepoInfo result) {
                                                                   onRepositoryCloned(result, folder);
                                                               }

                                                               @Override
                                                               protected void onFailure(Throwable exception) {
                                                                   revertProjectImport(folder);
                                                                   handleError(exception, remoteUri);
                                                               }
                                                           });
        } catch (RequestException e) {
            handleError(e, remoteUri);
        } finally {
            IDE.getInstance().closeView(display.asView().getId());
        }
    }

    /**
     * Perform actions when repository was successfully cloned.
     * 
     * @param gitRepositoryInfo {@link RepoInfo} repository info
     * @param folder {@link FolderModel} in which repository was cloned
     */
    private void onRepositoryCloned(final RepoInfo gitRepositoryInfo, final FolderModel folder) {
        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(gitRepositoryInfo.getRemoteUri()), OutputMessage.Type.GIT));
        IDE.fireEvent(new ConvertToProjectEvent(folder.getId(), vfs.getId(), null));

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                String[] userRepo = GitURLParser.parseGitHubUrl(gitRepositoryInfo.getRemoteUri());
                if (userRepo != null) {
                    IDE.fireEvent(new CloneRepositoryCompleteEvent(userRepo[0], userRepo[1]));
                }
            }
        });
    }

    @Override
    public void onVfsChanged(VfsChangedEvent event) {
        this.vfs = event.getVfsInfo();
    }

    private void handleError(Throwable t, String repoUrl) {
        String errorMessage =
                              (t.getMessage() != null && t.getMessage().length() > 0) ? t.getMessage()
                                  : GitExtension.MESSAGES.cloneFailed(repoUrl);
        IDE.fireEvent(new OutputEvent(errorMessage, OutputMessage.Type.GIT));
    }

    /**
     * @see org.exoplatform.ide.extension.samples.client.githubimport.ScrollEndHandler#onScrollEnd()
     */
    @Override
    public void onScrollEnd() {
        String account = display.getAccountNameBox().getValue();
        GitHubRepositoryList repositoryList = repositories.get(account);
        if (repositoryList.getNextPage() != null && repositoryList.getNextPage().length() > 0) {
            getNextPage(account, repositoryList);
        }
    }
}
