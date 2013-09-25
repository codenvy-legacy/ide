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
package com.codenvy.ide.api.ui.action;

/**
 * Defines possible positions of an action relative to another action.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public enum Anchor {
    /**
     * Anchor type that specifies the action to be the first in the list at the
     * moment of addition.
     */
    FIRST,
    /**
     * Anchor type that specifies the action to be the last in the list at the
     * moment of addition.
     */
    LAST,
    /**
     * Anchor type that specifies the action to be placed before the relative
     * action.
     */
    BEFORE,
    /**
     * Anchor type that specifies the action to be placed after the relative
     * action.
     */
    AFTER
}
