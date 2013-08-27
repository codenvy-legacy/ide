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

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.shared.s3.S3Object;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: ObjectGrid.java Sep 18, 2012 vetal $
 */
public class S3ObjectsGrid extends ListGrid<S3Object> {
    private static final String ID = "ideS3ObjectGrid";

    //TODO
    private Label label;

    public S3ObjectsGrid() {
        setID(ID);
        initColumns();
        label = new Label();
        getCellTable().setEmptyTableWidget(label);
    }

    /** Initialize columns. */
    private void initColumns() {
        Column<S3Object, String> keyCol = new Column<S3Object, String>(new TextCell()) {

            @Override
            public String getValue(S3Object s3object) {
                return s3object.getS3Key();
            }
        };

        Column<S3Object, String> eTagCol = new Column<S3Object, String>(new TextCell()) {
            @Override
            public String getValue(S3Object object) {
                return object.getETag();
            }
        };

        Column<S3Object, String> ownerCol = new Column<S3Object, String>(new TextCell()) {
            @Override
            public String getValue(S3Object object) {
                return object.getOwner().getName();
            }
        };

        Column<S3Object, String> sizeCol = new Column<S3Object, String>(new TextCell()) {
            @Override
            public String getValue(S3Object object) {
                return String.valueOf(object.getSize());
            }
        };

        Column<S3Object, String> storageCol = new Column<S3Object, String>(new TextCell()) {
            @Override
            public String getValue(S3Object object) {
                return object.getStorageClass();
            }
        };

        Column<S3Object, String> lastModifiedCol = new Column<S3Object, String>(new TextCell()) {
            @Override
            public String getValue(S3Object object) {
                return new Date(object.getUpdated()).toString();
            }
        };

        getCellTable().addColumn(keyCol, "Key");
        getCellTable().setColumnWidth(keyCol, 20, Unit.PCT);
        getCellTable().addColumn(eTagCol, "ETag");
        getCellTable().setColumnWidth(eTagCol, 20, Unit.PCT);
        getCellTable().addColumn(ownerCol, "Owner");
        getCellTable().setColumnWidth(ownerCol, 10, Unit.PCT);
        getCellTable().addColumn(sizeCol, "Size");
        getCellTable().setColumnWidth(sizeCol, 10, Unit.PCT);
        getCellTable().addColumn(storageCol, "Storage Class");
        getCellTable().setColumnWidth(storageCol, 15, Unit.PCT);
        getCellTable().addColumn(lastModifiedCol, "Last Modified");
        getCellTable().setColumnWidth(lastModifiedCol, 25, Unit.PCT);
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<S3Object> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(value.get(0));
        }
        getCellTable().redraw();
    }

    public void setBucketId(String bucketId) {
        label.setText(AWSExtension.LOCALIZATION_CONSTANT.s3managementEmptyBucket(bucketId));
    }
}
