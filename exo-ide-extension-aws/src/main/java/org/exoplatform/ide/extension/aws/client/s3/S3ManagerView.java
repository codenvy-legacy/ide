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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSClientBundle;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;
import org.exoplatform.ide.extension.aws.shared.s3.S3ObjectsList;

import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3ManagerView.java Sep 28, 2012 vetal $
 */
public class S3ManagerView extends ViewImpl implements S3Manager.Display {

    private static final String ID = "ideS3ManagerView";

    private static final int WIDTH = 1200;

    private static final int HEIGHT = 750;

    @UiField
    MenuItem deleteBucketAction;

    @UiField
    MenuItem createBucketAction;

    @UiField
    MenuItem deleteAction;

    @UiField
    MenuItem refreshAction;

    @UiField
    MenuItem uploadAction;

    @UiField
    MenuItem downloadAction;

    @UiField
    MenuItem uploadProjectAction;

    @UiField
    S3BucketsGrid s3BucketsGrid;

    @UiField
    S3ObjectsGrid s3ObjectsGrid;

    @UiField
    ImageButton uploadButton;

    @UiField
    ImageButton refreshButton;

    @UiField
    ImageButton createButton;

    @UiField
    ScrollPanel scrollPanel;

    @UiField
    Label bucketId;

    private static S3ManagerViewUiBinder uiBinder = GWT.create(S3ManagerViewUiBinder.class);

    interface S3ManagerViewUiBinder extends UiBinder<Widget, S3ManagerView> {
    }

    public S3ManagerView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.s3managementViewTitle(), new Image(AWSClientBundle.INSTANCE.s3()),
              WIDTH, HEIGHT, false);
        add(uiBinder.createAndBindUi(this));
    }


    @Override
    public void setS3Buckets(List<S3Bucket> bucketsList) {
        s3BucketsGrid.setValue(bucketsList);
    }

    @Override
    public HasSelectionHandlers<S3Bucket> getBuckets() {
        return s3BucketsGrid;
    }

    @Override
    public String getSelectedBucketId() {
        return s3BucketsGrid.getSelectedItems().get(0).getName();
    }

    @Override
    public void setS3ObjectsList(S3ObjectsList s3ObjectsList) {
        s3ObjectsGrid.setValue(s3ObjectsList.getObjects());
        s3ObjectsGrid.setBucketId(s3ObjectsList.getS3Bucket());
        scrollPanel.scrollToTop();
    }

    @Override
    public S3Object getSelectedObject() {
        return s3ObjectsGrid.getSelectedItems().get(0);
    }

    @Override
    public void setDeleteAction(ScheduledCommand command) {
        deleteAction.setScheduledCommand(command);
    }

    @Override
    public void setUploadAction(ScheduledCommand command) {
        uploadAction.setScheduledCommand(command);

    }

    @Override
    public void setEnableUploadProjectAction(boolean enabled) {
        uploadProjectAction.setEnabled(enabled);

    }

    @Override
    public void setEnableDeleteAction(boolean enabled) {
        deleteAction.setEnabled(enabled);

    }

    @Override
    public void setEnableUploadAction(boolean enabled) {
        uploadAction.setEnabled(enabled);
    }

    @Override
    public void setUploadOpenedProjectAction(ScheduledCommand command) {
        uploadProjectAction.setScheduledCommand(command);
    }

    @Override
    public void setDeleteBucketAction(ScheduledCommand command) {
        deleteBucketAction.setScheduledCommand(command);

    }

    @Override
    public void setCreateBucketAction(ScheduledCommand command) {
        createBucketAction.setScheduledCommand(command);
    }

    @Override
    public HasScrollHandlers getNextObject() {
        return scrollPanel;
    }

    @Override
    public int getVerticalScrollPosition() {
        return scrollPanel.getVerticalScrollPosition();
    }

    @Override
    public int getOffsetHeight() {
        return scrollPanel.getOffsetHeight();
    }

    @Override
    public int getWidgetgetOffsetHeight() {
        return scrollPanel.getWidget().getOffsetHeight();
    }

    @Override
    public void addS3ObjectsList(S3ObjectsList s3ObjectsList) {
        s3ObjectsGrid.addItems(s3ObjectsList.getObjects());
    }

    @Override
    public void setRefreshAction(ScheduledCommand command) {
        refreshAction.setScheduledCommand(command);
    }

    @Override
    public HasClickHandlers getRefreshButton() {
        return refreshButton;
    }

    @Override
    public HasClickHandlers getUploadButton() {
        return uploadButton;
    }

    @Override
    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    @Override
    public void setBucketId(String bucketId) {
        this.bucketId.setText(bucketId);
    }


    @Override
    public void setEnableDowloadAction(boolean enabled) {
        downloadAction.setEnabled(enabled);
    }


    @Override
    public void setDownloadAction(ScheduledCommand command) {
        downloadAction.setScheduledCommand(command);
    }


}
