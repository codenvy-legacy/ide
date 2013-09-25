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
package com.codenvy.ide.ext.aws.client.s3.create;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.json.JsonArray;

/**
 * The view for {@link S3CreateBucketPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3CreateBucketView extends View<S3CreateBucketView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    public interface ActionDelegate {
        /** Perform action when create button clicked. */
        public void onCreateButtonClicked();

        /** Perform action when cancel button clicked. */
        public void onCancelButtonCLicked();

        /** Perform action when file name field changed. */
        public void onNameFieldChanged();
    }

    /**
     * Get S3 Bucket name which will be created.
     *
     * @return S3 Bucket name to create.
     */
    public String getBucketName();

    /**
     * Set S3 Bucket name.
     *
     * @param name
     *         name of S3 Bucket.
     */
    public void setBucketName(String name);

    /**
     * Enable or disable create button.
     *
     * @param enable
     *         true if enable.
     */
    public void setCreateButtonEnable(boolean enable);

    /** Set focus in name field. */
    public void setFocusNameField();

    /**
     * Set list of regions.
     *
     * @param regions
     *         list of regions.
     */
    public void setRegions(JsonArray<String> regions);

    /**
     * Get region name into which S3 Bucket will be created.
     *
     * @return region name.
     */
    public String getRegion();

    /**
     * Return shown state for current window.
     *
     * @return true if shown, otherwise false.
     */
    public boolean isShown();

    /** Shows current dialog. */
    public void showDialog();

    /** Close current dialog. */
    public void close();
}
