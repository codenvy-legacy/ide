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
package com.codenvy.ide.ext.extruntime.client.wizard;

import com.codenvy.ide.api.mvp.View;

/**
 * The view of {@link ExtensionPagePresenter}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtensionPageView.java Jul 8, 2013 4:07:39 PM azatsarynnyy $
 */
public interface ExtensionPageView extends View<ExtensionPageView.ActionDelegate> {
    /** Needs for delegate some function into {@link ExtensionPageView}. */
    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having changed group id. */
        void onGroupIdChanged();

        /** Performs any actions appropriate in response to the user having changed artifact id. */
        void onArtifactIdChanged();

        /** Performs any actions appropriate in response to the user having changed artifact version. */
        void onVersionChanged();
    }

    /**
     * Returns group id.
     * 
     * @return group id
     */
    String getGroupId();

    /**
     * Set group id.
     * 
     * @param group id
     */
    void setGroupId(String groupId);

    /**
     * Returns artifact id.
     * 
     * @return artifact id
     */
    String getArtifactId();

    /**
     * Set artifact id.
     * 
     * @param artifact id
     */
    void setArtifactId(String artifactId);

    /**
     * Returns artifact version.
     * 
     * @return artifact version
     */
    String getVersion();

    /**
     * Set artifact version.
     * 
     * @param artifact version
     */
    void setVersion(String version);

}
