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
import com.google.gwt.user.cellview.client.Column;

import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.extension.aws.shared.s3.S3Bucket;

import java.util.List;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: S3BucketsGrid.java Oct 3, 2012 vetal $
 */
public class S3BucketsGrid extends ListGrid<S3Bucket> {
    private static final String ID = "ideS3BucketsGrid";

    public S3BucketsGrid() {
        setID(ID);
        initColumns();
    }

    /** Initialize columns. */
    private void initColumns() {
        Column<S3Bucket, String> keyCol = new Column<S3Bucket, String>(new TextCell()) {

            @Override
            public String getValue(S3Bucket S3Bucket) {
                return S3Bucket.getName();
            }
        };
        getCellTable().addColumn(keyCol);
        getCellTable().setColumnWidth(keyCol, "100%");
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.ListGrid#setValue(java.util.List) */
    @Override
    public void setValue(List<S3Bucket> value) {
        super.setValue(value);
        if (value != null && value.size() > 0) {
            selectItem(value.get(0));
        }
        getCellTable().redraw();
    }


}
