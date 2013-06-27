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
package com.codenvy.ide.ext.git.client.merge;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.BranchListUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.Merge;
import com.codenvy.ide.ext.git.client.marshaller.MergeUnmarshaller;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.Reference;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

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
    public static final String LOCAL_BRANCES_TITLE  = "Local Branches";
    public static final String REMOTE_BRANCES_TITLE = "Remote Branches";
    private MergeView               view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private EventBus                eventBus;
    private ConsolePart             console;
    private GitLocalizationConstant constant;
    private Reference               selectedReference;
    private String                  projectId;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param eventBus
     * @param console
     * @param constant
     */
    @Inject
    public MergePresenter(MergeView view, GitClientService service, ResourceProvider resourceProvider, EventBus eventBus,
                          ConsolePart console, GitLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();
        selectedReference = null;
        view.setEnableMergeButton(false);

        try {
            BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(JsonCollections.<Branch>createArray());
            service.branchList(resourceProvider.getVfsId(), projectId, LIST_LOCAL,
                               new AsyncRequestCallback<JsonArray<Branch>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Branch> result) {
                                       if (result == null || result.isEmpty())
                                           return;

                                       JsonArray<Reference> references = JsonCollections.createArray();
                                       for (int i = 0; i < result.size(); i++) {
                                           Branch branch = result.get(i);
                                           if (!branch.isActive()) {
                                               Reference reference = new Reference(branch.getName(), branch.getDisplayName(), LOCAL_BRANCH);
                                               references.add(reference);
                                           }
                                       }
                                       view.setLocalBranches(references);
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                       console.print(exception.getMessage());
                                   }
                               });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }

        try {
            BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(JsonCollections.<Branch>createArray());
            service.branchList(resourceProvider.getVfsId(), projectId, LIST_REMOTE,
                               new AsyncRequestCallback<JsonArray<Branch>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Branch> result) {
                                       if (result == null || result.isEmpty())
                                           return;

                                       JsonArray<Reference> references = JsonCollections.createArray();
                                       for (int i = 0; i < result.size(); i++) {
                                           Branch branch = result.get(i);
                                           if (!branch.isActive()) {
                                               Reference reference =
                                                       new Reference(branch.getName(), branch.getDisplayName(), REMOTE_BRANCH);
                                               references.add(reference);
                                           }
                                       }
                                       view.setRemoteBranches(references);
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                       console.print(exception.getMessage());
                                   }
                               });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
        MergeUnmarshaller unmarshaller = new MergeUnmarshaller(new Merge(), constant);

        try {
            service.merge(resourceProvider.getVfsId(), projectId, selectedReference.getDisplayName(),
                          new AsyncRequestCallback<MergeResult>(unmarshaller) {
                              @Override
                              protected void onSuccess(MergeResult result) {
                                  console.print(formMergeMessage(result));
                                  view.close();
                                  // TODO
                                  // IDE.fireEvent(new RefreshBrowserEvent());
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  eventBus.fireEvent(new ExceptionThrownEvent(exception));
                                  console.print(exception.getMessage());
                              }
                          });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Form the result message of the merge operation.
     *
     * @param mergeResult
     *         result of merge operation
     * @return {@link String} merge result message
     */
    private String formMergeMessage(MergeResult mergeResult) {
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
        boolean isEnabled = !selectedReference.getDisplayName().equals(LOCAL_BRANCES_TITLE) &
                            !selectedReference.getDisplayName().equals(REMOTE_BRANCES_TITLE);
        view.setEnableMergeButton(isEnabled);
    }
}