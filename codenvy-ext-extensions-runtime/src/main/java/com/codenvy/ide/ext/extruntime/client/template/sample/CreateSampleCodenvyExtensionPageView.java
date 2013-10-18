/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extruntime.client.template.sample;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link CreateSampleCodenvyExtensionPage}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateSampleCodenvyExtensionPageView.java Jul 8, 2013 4:07:39 PM azatsarynnyy $
 */
public interface CreateSampleCodenvyExtensionPageView extends View<CreateSampleCodenvyExtensionPageView.ActionDelegate> {
    /** Needs for delegate some function into {@link CreateSampleCodenvyExtensionPageView}. */
    public interface ActionDelegate {
        /** Performs any actions in response to user's introducing changes. */
        void onValueChanged();
    }

    /**
     * Returns group id.
     *
     * @return group id
     */
    @NotNull
    String getGroupId();

    /**
     * Set group id.
     *
     * @param groupId
     *         group id
     */
    void setGroupId(@NotNull String groupId);

    /**
     * Returns artifact id.
     *
     * @return artifact id
     */
    @NotNull
    String getArtifactId();

    /**
     * Set artifact id.
     *
     * @param artifactId
     *         artifact id
     */
    void setArtifactId(@NotNull String artifactId);

    /**
     * Returns artifact version.
     *
     * @return artifact version
     */
    @NotNull
    String getVersion();

    /**
     * Set artifact version.
     *
     * @param version
     *         version
     */
    void setVersion(@NotNull String version);
}