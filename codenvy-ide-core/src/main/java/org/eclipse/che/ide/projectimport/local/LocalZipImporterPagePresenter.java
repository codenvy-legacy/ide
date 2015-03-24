/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.projectimport.local;

import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.shared.dto.ImportResponse;
import org.eclipse.che.api.vfs.gwt.client.VfsServiceClient;
import org.eclipse.che.api.vfs.shared.dto.Item;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.event.OpenProjectEvent;
import org.eclipse.che.ide.api.project.wizard.ImportProjectNotificationSubscriber;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.json.JsonHelper;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.util.NameUtils;
import org.eclipse.che.ide.util.loging.Log;

import javax.annotation.Nonnull;

/**
 * @author Roman Nikitenko
 */
public class LocalZipImporterPagePresenter implements LocalZipImporterPageView.ActionDelegate {

    private       CoreLocalizationConstant            locale;
    private       LocalZipImporterPageView            view;
    private       DtoFactory                          dtoFactory;
    private       String                              restContext;
    private       String                              workspaceId;
    private final EventBus                            eventBus;
    private final VfsServiceClient                    vfsServiceClient;
    private final ProjectServiceClient                projectServiceClient;
    private final DialogFactory                       dialogFactory;
    private final ImportProjectNotificationSubscriber importProjectNotificationSubscriber;

    @Inject
    public LocalZipImporterPagePresenter(LocalZipImporterPageView view,
                                         DtoFactory dtoFactory,
                                         CoreLocalizationConstant locale,
                                         @Named("restContext") String restContext,
                                         @Named("workspaceId") String workspaceId,
                                         EventBus eventBus,
                                         VfsServiceClient vfsServiceClient,
                                         ProjectServiceClient projectServiceClient,
                                         DialogFactory dialogFactory,
                                         ImportProjectNotificationSubscriber importProjectNotificationSubscriber) {
        this.view = view;
        this.locale = locale;
        this.dtoFactory = dtoFactory;
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.eventBus = eventBus;
        this.vfsServiceClient = vfsServiceClient;
        this.projectServiceClient = projectServiceClient;
        this.dialogFactory = dialogFactory;
        this.importProjectNotificationSubscriber = importProjectNotificationSubscriber;
        this.view.setDelegate(this);
    }

    public void show() {
        updateView();
        view.showDialog();
    }

    @Override
    public void projectNameChanged() {
        view.setEnabledImportButton(isCompleted());
    }

    @Override
    public void fileNameChanged() {
        String projectName = extractProjectName(view.getFileName());
        if (!projectName.isEmpty()) {
            view.setProjectName(projectName);
            projectNameChanged();
        }
    }

    @Override
    public void onSubmitComplete(String result) {
        try {
            showProcessing(false);

            result = extractFromHtmlFormat(result);
            if (result.isEmpty()) {
                importFailure(locale.importProjectMessageFailure());
                return;
            }

            ImportResponse importResponse = dtoFactory.createDtoFromJson(result, ImportResponse.class);
            if (importResponse.getProjectDescriptor() == null) {
                importFailure(JsonHelper.parseJsonMessage(result));
                return;
            }
            importSuccess(importResponse);
        } catch (Exception e) {
            importFailure(result);
        }
    }

    @Override
    public void onCancelClicked() {
        view.closeDialog();
    }

    @Override
    public void onImportClicked() {
        // check on VFS because need to check whether the folder with the same name already exists in the root of workspace
        final String projectName = view.getProjectName();
        vfsServiceClient.getItemByPath(projectName, new AsyncRequestCallback<Item>() {
            @Override
            protected void onSuccess(Item result) {
                view.setEnabledImportButton(false);
                dialogFactory.createMessageDialog("", locale.createProjectFromTemplateProjectExists(projectName), null).show();
            }

            @Override
            protected void onFailure(Throwable exception) {
                importProject();
            }
        });
    }

    private void importProject() {
        final String projectName = view.getProjectName();
        importProjectNotificationSubscriber.subscribe(projectName);

        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        view.setAction(restContext + "/project/" + workspaceId + "/upload/zipproject/" + projectName + "?force=false");
        view.submit();
        showProcessing(true);
    }

    private void importSuccess(ImportResponse importResponse) {
        view.closeDialog();
        importProjectNotificationSubscriber.onSuccess();

        String projectName = importResponse.getProjectDescriptor().getName();
        eventBus.fireEvent(new OpenProjectEvent(projectName));
    }

    private void importFailure(String error) {
        deleteProject(view.getProjectName());
        view.closeDialog();
        importProjectNotificationSubscriber.onFailure(error);
    }

    /** Updates view from data-object. */
    private void updateView() {
        view.setProjectName("");
        view.setProjectDescription("");
        view.setProjectVisibility(true);
        view.setSkipFirstLevel(true);
    }

    /** Shown the state that the request is processing. */
    private void showProcessing(boolean inProgress) {
        view.setLoaderVisibility(inProgress);
        view.setInputsEnableState(!inProgress);
    }

    private String extractProjectName(@Nonnull String zipName) {
        int indexStartProjectName = zipName.lastIndexOf("\\") + 1;
        int indexFinishProjectName = zipName.indexOf(".zip");
        if (indexFinishProjectName != (-1)) {
            return zipName.substring(indexStartProjectName, indexFinishProjectName);
        }
        return "";
    }

    private void deleteProject(final String name) {
        projectServiceClient.delete(name, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Log.info(LocalZipImporterPagePresenter.class, "Project " + name + " deleted.");
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(LocalZipImporterPagePresenter.class, exception);
            }
        });
    }

    private boolean isProjectNameCorrect() {
        if (NameUtils.checkProjectName(view.getProjectName())) {
            view.hideNameError();
            return true;
        }
        view.showNameError();
        return false;

    }

    private boolean isCompleted() {
        return view.getFileName().contains(".zip") && isProjectNameCorrect();
    }

    private String extractFromHtmlFormat(String text) {
        int beginIndex = -1;
        int lastIndex = -1;

        if (text.contains("<pre")) {
            beginIndex = text.indexOf(">") + 1;
            lastIndex = text.lastIndexOf("</pre");
        }
        return beginIndex != 0 && lastIndex != -1 ? text.substring(beginIndex, lastIndex) : text;
    }
}
