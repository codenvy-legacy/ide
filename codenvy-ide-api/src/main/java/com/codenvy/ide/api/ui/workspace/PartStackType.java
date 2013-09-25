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
package com.codenvy.ide.api.ui.workspace;

/** Defines Part's position on the Screen */
public enum PartStackType {
    /**
     * Contains navigation parts. Designed to navigate
     * by project, types, classes and any other entities.
     * Usually placed on the LEFT side of the IDE.
     */
    NAVIGATION,
    /**
     * Contains informative parts. Designed to display
     * the state of the application, project or processes.
     * Usually placed on the BOTTOM side of the IDE.
     */
    INFORMATION,
    /**
     * Contains editing parts. Designed to provide an
     * ability to edit any resources or settings.
     * Usually placed in the CENTRAL part of the IDE.
     */
    EDITING,
    /**
     * Contains tooling parts. Designed to provide handy
     * features and utilities, access to other services
     * or any other features that are out of other PartType
     * scopes.
     * Usually placed on the RIGHT side of the IDE.
     */
    TOOLING
}
