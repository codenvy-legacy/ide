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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3ManagerView extends View<S3ManagerView.ActionDelegate> {
    public interface ActionDelegate {
        public void onDeleteObjectClicked(String bucketId, String objectId);

        public void onUploadObjectClicked(String bucketId);

        public void onDownloadObjectClicked(String bucketId, String objectId);

        public void onUploadProjectClicked();

        public void onRefreshObjectsClicked(final String bucketId);

        public void onDeleteBucketClicked(String bucketId);

        public void onCreateBucketClicked();

        public void onCloseButtonClicked();
    }

    public void setS3Buckets(JsonArray<S3Bucket> s3Buckets);

    public void setS3ObjectsList(S3ObjectsList s3ObjectsList);

    public void addS3ObjectsList(S3ObjectsList s3ObjectsList);

    public String getSelectedBucketId();

    public void setBucketId();

    public S3Object getSelectedObject();

    public void setUploadProjectButtonEnabled(boolean enabled);

    boolean isShown();

    void showDialog();

    void close();
}
