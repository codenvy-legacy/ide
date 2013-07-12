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
package com.codenvy.ide.ext.aws.client.s3;

import com.codenvy.ide.ext.aws.client.AWSLocalizationConstant;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The implementation for the {@link S3ManagerView}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class S3ManagerViewImpl extends DialogBox implements S3ManagerView {
    interface S3ManagerViewImplUiBinder extends UiBinder<Widget, S3ManagerViewImpl> {
    }

    private static S3ManagerViewImplUiBinder uiBinder = GWT.create(S3ManagerViewImplUiBinder.class);

    @UiField
    MenuItem deleteBucketAction;

    @UiField
    MenuItem createBucketAction;

    @UiField
    MenuItem deleteObjectAction;

    @UiField
    MenuItem refreshObjectsAction;

    @UiField
    MenuItem downloadObjectAction;

    @UiField
    MenuItem uploadProjectAction;

    @UiField(provided = true)
    CellTable<S3Bucket> s3BucketCellTable = new CellTable<S3Bucket>();

    @UiField(provided = true)
    CellTable<S3Object> s3ObjectCellTable = new CellTable<S3Object>();

    @UiField
    Button btnUpload;

    @UiField
    Button btnRefreshObjects;

    @UiField
    Button btnCreate;

    @UiField
    Label bucketId;

    @UiField
    Button btnClose;

    @UiField(provided = true)
    AWSLocalizationConstant constant;

    private ActionDelegate delegate;

    private boolean isShown;

    private S3Bucket selectedS3Bucket;

    private S3Object selectedS3Object;

    /**
     * Create view.
     *
     * @param constant
     */
    @Inject
    protected S3ManagerViewImpl(AWSLocalizationConstant constant) {
        this.constant = constant;

        initBucketsTable();
        initObjectsTable();

        Widget widget = uiBinder.createAndBindUi(this);

        deleteBucketAction.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                delegate.onDeleteBucketClicked(selectedS3Bucket.getName());
            }
        });

        createBucketAction.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                delegate.onCreateBucketClicked();
            }
        });

        deleteObjectAction.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                delegate.onDeleteObjectClicked(selectedS3Bucket.getName(), selectedS3Object.getS3Key());
            }
        });

        refreshObjectsAction.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                delegate.onRefreshObjectsClicked(selectedS3Bucket.getName());
            }
        });

        downloadObjectAction.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                delegate.onDownloadObjectClicked(selectedS3Bucket.getName(), selectedS3Object.getS3Key());
            }
        });

        uploadProjectAction.setScheduledCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                delegate.onUploadObjectClicked(selectedS3Bucket.getName());
            }
        });

        this.setText(constant.s3ManagementControlTitle());
        this.setWidget(widget);
    }

    /** Init S3 Buckets table. */
    private void initBucketsTable() {
        s3BucketCellTable.setWidth("100%", true);
        s3BucketCellTable.setAutoHeaderRefreshDisabled(true);
        s3BucketCellTable.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyBucketsPanel = new HTMLPanel("No buckets.");
        s3BucketCellTable.setLoadingIndicator(emptyBucketsPanel);

        final SelectionModel<S3Bucket> selectionModel = new SingleSelectionModel<S3Bucket>();

        s3BucketCellTable.setSelectionModel(selectionModel);

        Column<S3Bucket, String> bucketNameColumn = new TextColumn<S3Bucket>() {
            @Override
            public String getValue(S3Bucket object) {
                return object.getName();
            }
        };

        s3BucketCellTable.addColumn(bucketNameColumn);
        s3BucketCellTable.setColumnWidth(bucketNameColumn, "100%");

        s3BucketCellTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedS3Bucket = ((SingleSelectionModel<S3Bucket>)selectionModel).getSelectedObject();
                delegate.onRefreshObjectsClicked(selectedS3Bucket.getName());
                bucketId.setText(selectedS3Bucket.getName());
            }
        });

        bucketNameColumn.setFieldUpdater(new FieldUpdater<S3Bucket, String>() {
            @Override
            public void update(int index, S3Bucket object, String value) {
                delegate.onRefreshObjectsClicked(object.getName());
            }
        });
    }

    /** Init S3 Objects table. */
    private void initObjectsTable() {
        s3ObjectCellTable.setWidth("100%", true);
        s3ObjectCellTable.setAutoHeaderRefreshDisabled(true);
        s3ObjectCellTable.setAutoFooterRefreshDisabled(true);

        HTMLPanel emptyObjectsPanel = new HTMLPanel("No objects.");
        s3ObjectCellTable.setLoadingIndicator(emptyObjectsPanel);

        final SelectionModel<S3Object> selectionModel = new SingleSelectionModel<S3Object>();

        s3ObjectCellTable.setSelectionModel(selectionModel);

        Column<S3Object, String> objectNameColumn = new TextColumn<S3Object>() {
            @Override
            public String getValue(S3Object object) {
                return object.getS3Key();
            }
        };

        Column<S3Object, String> eTagColumn = new TextColumn<S3Object>() {
            @Override
            public String getValue(S3Object object) {
                return object.getETag();
            }
        };

        Column<S3Object, String> ownerColumn = new TextColumn<S3Object>() {
            @Override
            public String getValue(S3Object object) {
                return object.getOwner().getName();
            }
        };

        Column<S3Object, String> sizeColumn = new TextColumn<S3Object>() {
            @Override
            public String getValue(S3Object object) {
                return String.valueOf((long)object.getSize());
            }
        };

        Column<S3Object, String> storageClassColumn = new TextColumn<S3Object>() {
            @Override
            public String getValue(S3Object object) {
                return object.getStorageClass();
            }
        };

        Column<S3Object, String> lastModifiedColumn = new TextColumn<S3Object>() {
            @Override
            public String getValue(S3Object object) {
                return new Date((long)object.getUpdated()).toString();
            }
        };

        s3ObjectCellTable.addColumn(objectNameColumn, "Key");
        s3ObjectCellTable.addColumn(eTagColumn, "ETag");
        s3ObjectCellTable.addColumn(ownerColumn, "Owner");
        s3ObjectCellTable.addColumn(sizeColumn, "Size");
        s3ObjectCellTable.addColumn(storageClassColumn, "Storage Class");
        s3ObjectCellTable.addColumn(lastModifiedColumn, "Last Modified");

        s3ObjectCellTable.getSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                selectedS3Object = ((SingleSelectionModel<S3Object>)selectionModel).getSelectedObject();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void setS3Buckets(JsonArray<S3Bucket> s3Buckets) {
        List<S3Bucket> buckets = new ArrayList<S3Bucket>(s3Buckets.size());

        for (int i = 0; i < s3Buckets.size(); i++) {
            buckets.add(s3Buckets.get(i));
        }

        s3BucketCellTable.setRowData(buckets);
    }

    /** {@inheritDoc} */
    @Override
    public void setS3ObjectsList(S3ObjectsList s3ObjectsList) {
        List<S3Object> objects = new ArrayList<S3Object>();
        for (int i = 0; i < s3ObjectsList.getObjects().size(); i++) {
            objects.add(s3ObjectsList.getObjects().get(i));
        }

        s3ObjectCellTable.setRowData(objects);
    }

    /** {@inheritDoc} */
    @Override
    public String getSelectedBucketId() {
        return selectedS3Bucket.getName();
    }

    /** {@inheritDoc} */
    @Override
    public void setBucketId() {
        bucketId.setText(selectedS3Bucket.getName());
    }

    /** {@inheritDoc} */
    @Override
    public S3Object getSelectedObject() {
        return selectedS3Object;
    }

    /** {@inheritDoc} */
    @Override
    public void setUploadProjectButtonEnabled(boolean enabled) {
        uploadProjectAction.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnCreate")
    public void onCreateButtonClicked(ClickEvent event) {
        delegate.onCreateBucketClicked();
    }

    @UiHandler("btnRefreshObjects")
    public void onRefreshButtonClicked(ClickEvent event) {
        delegate.onRefreshObjectsClicked(selectedS3Bucket.getName());
    }

    @UiHandler("btnUpload")
    public void onUploadButtonClicked(ClickEvent event) {
        delegate.onUploadObjectClicked(selectedS3Bucket.getName());
    }

    @UiHandler("btnClose")
    public void onCloseButtonClicked(ClickEvent event) {
        delegate.onCloseButtonClicked();
    }
}
