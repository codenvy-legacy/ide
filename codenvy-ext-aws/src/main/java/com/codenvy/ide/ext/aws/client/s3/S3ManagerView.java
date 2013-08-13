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
package com.codenvy.ide.ext.aws.client.s3;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.ext.aws.shared.s3.S3Bucket;
import com.codenvy.ide.ext.aws.shared.s3.S3Object;
import com.codenvy.ide.ext.aws.shared.s3.S3ObjectsList;
import com.codenvy.ide.json.JsonArray;

/**
 * The view of {@link S3ManagerPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3ManagerView extends View<S3ManagerView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    public interface ActionDelegate {
        /**
         * Perform delete object from S3 Bucket.
         *
         * @param bucketId
         *         S3 Bucket id.
         * @param objectId
         *         S3 Object Id.
         */
        public void onDeleteObjectClicked(String bucketId, String objectId);

        /**
         * Perform upload object to selected S3 Bucket.
         *
         * @param bucketId
         *         S3 Bucket id.
         */
        public void onUploadObjectClicked(String bucketId);

        /**
         * Perform download object from specified S3 Bucket.
         *
         * @param bucketId
         *         S3 Bucket id.
         * @param objectId
         *         S3 Object id.
         */
        public void onDownloadObjectClicked(String bucketId, String objectId);

        /** Perform upload current opened project to AWS. */
        public void onUploadProjectClicked();

        /**
         * Perform refresh selected S3 Bucket.
         *
         * @param bucketId
         *         S3 Bucket id.
         */
        public void onRefreshObjectsClicked(final String bucketId);

        /**
         * Perform delete selected S3 Bucket.
         *
         * @param bucketId
         *         S3 Bucket id.
         */
        public void onDeleteBucketClicked(String bucketId);

        /** Perform create bucket action. */
        public void onCreateBucketClicked();

        /** Perform close window action. */
        public void onCloseButtonClicked();
    }

    /**
     * Set array of S3 Buckets into Buckets grid.
     *
     * @param s3Buckets
     *         Array of S3 Buckets.
     */
    public void setS3Buckets(JsonArray<S3Bucket> s3Buckets);

    /**
     * Set array of S3 Object from selected S3 Bucket.
     *
     * @param s3ObjectsList
     *         Array of S3 Objects.
     */
    public void setS3ObjectsList(S3ObjectsList s3ObjectsList);

    /**
     * Get Id for the selected S3 Bucket.
     *
     * @return S3 Bucket id.
     */
    public String getSelectedBucketId();

    /** Set Id for the selected S3 Bucket. */
    public void setBucketId();

    /**
     * Get selected S3 object from S3 Bucket.
     *
     * @return S3 Object.
     */
    public S3Object getSelectedObject();

    /**
     * Enable upload project button.
     *
     * @param enabled
     *         true if enable, otherwise false.
     */
    public void setUploadProjectButtonEnabled(boolean enabled);

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    boolean isShown();

    /** Shows current dialog. */
    void showDialog();

    /** Close current dialog. */
    void close();
}
