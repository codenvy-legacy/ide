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
package com.codenvy.ide.upload;

import com.codenvy.api.vfs.server.VirtualFileSystem;
import com.codenvy.api.vfs.server.VirtualFileSystemFactory;
import com.codenvy.api.vfs.server.VirtualFileSystemImpl;
import com.codenvy.api.vfs.shared.dto.Link;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Nikitenko.
 */
public class UploadFilePresenter implements UploadFileView.ActionDelegate {

    private UploadFileView view;
    private SelectionAgent selectionAgent;
    private String restContext;
    private String workspaceId;

    @Inject
    public UploadFilePresenter(UploadFileView view,
                               @Named("restContext") String restContext,
                               @Named("workspaceId") String workspaceId,
                               SelectionAgent selectionAgent) {
        this.restContext = restContext;
        this.workspaceId = workspaceId;
        this.selectionAgent = selectionAgent;
        this.view = view;
        this.view.setDelegate(this);
        this.view.setEnabledUploadButton(false);
        this.view.setEnabledMimeType(false);

    }

    /** Show dialog. */
    public void showDialog() {
        view.showDialog();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onSubmitComplete(@NotNull String result) {
        view.close();
    }

    @Override
    public void onUploadClicked() {
        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        String parentId = getParentId();
        view.setAction(restContext + "/project/" + workspaceId + "/uploadfile/" + parentId);
        view.submit();

    }

    @Override
    public void onFileNameChanged() {
        String fileName = view.getFileName();
        boolean enabled = !fileName.isEmpty();
        view.setEnabledUploadButton(enabled);
        view.setEnabledMimeType(enabled);
        view.setSupportedMimeTypes(getSupportedMimeTypes());
    }

    private List<String> getSupportedMimeTypes() {
        List<String> mimeTypeList = new ArrayList<String>();
//        for (FileType fileType : fileTypes) {
//            mimeTypeList.add(fileType.getMimeType());
//        }
        //temporary code
        mimeTypeList.add("application/xml");
        mimeTypeList.add("application/atom+xml");
        mimeTypeList.add("application/json");

        return mimeTypeList;
    }

    private String getParentId(){
        Selection<?> select = selectionAgent.getSelection();
        Resource resource = null;
        if (select != null && select.getFirstElement() instanceof Resource) {
            Selection<Resource> selection = (Selection<Resource>)select;
            resource = selection.getFirstElement();
        }
        return resource.getId();
    }
}
