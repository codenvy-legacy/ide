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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.BranchListUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.MergeUnmarshaller;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.Reference;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;
import static com.codenvy.ide.ext.git.shared.MergeResult.MergeStatus.ALREADY_UP_TO_DATE;
import static com.codenvy.ide.ext.git.shared.Reference.RefType.LOCAL_BRANCH;
import static com.codenvy.ide.ext.git.shared.Reference.RefType.REMOTE_BRANCH;

/**
 * Presenter to perform merge reference with current HEAD commit.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 20, 2011 12:38:39 PM anya $
 */
@Singleton
public class MergePresenter implements MergeView.ActionDelegate {
    public static final String LOCAL_BRANCHES_TITLE  = "Local Branches";
    public static final String REMOTE_BRANCHES_TITLE = "Remote Branches";
    private MergeView               view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private EventBus                eventBus;
    private GitLocalizationConstant constant;
    private NotificationManager     notificationManager;
    private Reference               selectedReference;
    private String                  projectId;
    private String                  projectName;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param eventBus
     * @param constant
     * @param notificationManager
     */
    @Inject
    public MergePresenter(MergeView view, GitClientService service, ResourceProvider resourceProvider, EventBus eventBus,
                          GitLocalizationConstant constant, NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        Project project = resourceProvider.getActiveProject();
        projectId = project.getId();
        projectName = project.getName();
        selectedReference = null;
        view.setEnableMergeButton(false);

        try {
            BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller();
            service.branchList(resourceProvider.getVfsId(), projectId, LIST_LOCAL,
                               new AsyncRequestCallback<JsonArray<Branch>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Branch> result) {
                                       if (result == null || result.isEmpty())
                                           return;

                                       JsonArray<Reference> references = JsonCollections.createArray();
                                       for (int i = 0; i < result.size(); i++) {
                                           Branch branch = result.get(i);
                                           if (!branch.active()) {
                                               DtoClientImpls.ReferenceImpl reference = DtoClientImpls.ReferenceImpl.make();
                                               reference.setFullName(branch.getName());
                                               reference.setDisplayName(branch.getDisplayName());
                                               reference.setRefType(LOCAL_BRANCH);
                                               references.add(reference);
                                           }
                                       }
                                       view.setLocalBranches(references);
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                       Notification notification = new Notification(exception.getMessage(), ERROR);
                                       notificationManager.showNotification(notification);
                                   }
                               });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }

        try {
            BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller();
            service.branchList(resourceProvider.getVfsId(), projectId, LIST_REMOTE,
                               new AsyncRequestCallback<JsonArray<Branch>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Branch> result) {
                                       if (result == null || result.isEmpty())
                                           return;

                                       JsonArray<Reference> references = JsonCollections.createArray();
                                       for (int i = 0; i < result.size(); i++) {
                                           Branch branch = result.get(i);
                                           if (!branch.active()) {
                                               DtoClientImpls.ReferenceImpl reference = DtoClientImpls.ReferenceImpl.make();
                                               reference.setFullName(branch.getName());
                                               reference.setDisplayName(branch.getDisplayName());
                                               reference.setRefType(REMOTE_BRANCH);
                                               references.add(reference);
                                           }
                                       }
                                       view.setRemoteBranches(references);
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                       Notification notification = new Notification(exception.getMessage(), ERROR);
                                       notificationManager.showNotification(notification);
                                   }
                               });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }

        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onMergeClicked() {
        MergeUnmarshaller unmarshaller = new MergeUnmarshaller();

        try {
            service.merge(resourceProvider.getVfsId(), projectId, selectedReference.getDisplayName(),
                          new AsyncRequestCallback<MergeResult>(unmarshaller) {
                              @Override
                              protected void onSuccess(final MergeResult result) {
                                  resourceProvider.getProject(projectName, new AsyncCallback<Project>() {
                                      @Override
                                      public void onSuccess(Project project) {
                                          Notification notification = new Notification(formMergeMessage(result), INFO);
                                          notificationManager.showNotification(notification);
                                          view.close();
                                      }

                                      @Override
                                      public void onFailure(Throwable caught) {
                                          Log.error(MergePresenter.class, "can not get project " + projectName);
                                      }
                                  });
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                  Notification notification = new Notification(exception.getMessage(), ERROR);
                                  notificationManager.showNotification(notification);
                              }
                          });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Form the result message of the merge operation.
     *
     * @param mergeResult
     *         result of merge operation
     * @return {@link String} merge result message
     */
    @NotNull
    private String formMergeMessage(@NotNull MergeResult mergeResult) {
        if (mergeResult.getMergeStatus().equals(ALREADY_UP_TO_DATE)) {
            return mergeResult.getMergeStatus().getValue();
        }

        StringBuilder conflictMessage = new StringBuilder();
        JsonArray<String> conflicts = mergeResult.getConflicts();
        if (conflicts != null && !conflicts.isEmpty()) {
            for (int i = 0; i < conflicts.size(); i++) {
                String conflict = conflicts.get(i);
                conflictMessage.append("- ").append(conflict).append("<br>");
            }
        }
        StringBuilder commitsMessage = new StringBuilder();
        JsonArray<String> commits = mergeResult.getMergedCommits();
        if (commits != null && !commits.isEmpty()) {
            for (int i = 0; i < commits.size(); i++) {
                String commit = commits.get(i);
                commitsMessage.append("- ").append(commit).append("<br>");
            }
        }

        String message = "<b>" + mergeResult.getMergeStatus().getValue() + "</b><br/>";
        String conflictText = conflictMessage.toString();
        message += (!conflictText.isEmpty()) ? constant.mergedConflicts(conflictText) : "";
        String commitText = commitsMessage.toString();
        message += (!commitText.isEmpty()) ? constant.mergedCommits(commitText) : "";
        message += (mergeResult.getNewHead() != null) ? constant.mergedNewHead(mergeResult.getNewHead()) : "";
        return message;
    }

    /** {@inheritDoc} */
    @Override
    public void onReferenceSelected(@NotNull Reference reference) {
        selectedReference = reference;
        String displayName = selectedReference.getDisplayName();
        boolean isEnabled = !displayName.equals(LOCAL_BRANCHES_TITLE) && !displayName.equals(REMOTE_BRANCHES_TITLE);
        view.setEnableMergeButton(isEnabled);
    }
}