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
