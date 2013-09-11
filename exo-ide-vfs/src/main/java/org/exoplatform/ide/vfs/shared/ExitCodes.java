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
package org.exoplatform.ide.vfs.shared;

/**
 * Provide set of exit codes of Virtual Files System operation. Such codes can be used as a supplement to the HTTP status of the
 * client to help define more precisely the type of error.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ExitCodes {
    public static final int SUCCESS = 0;

    /** If operation fails cause to any constraints. */
    public static final int CONSTRAINT = 100;

    /** If any parameter of request is not acceptable. */
    public static final int INVALID_ARGUMENT = 101;

    /** Name conflict. */
    public static final int ITEM_EXISTS = 102;

    /** Item with specified path or ID does not exist. */
    public static final int ITEM_NOT_FOUND = 103;

    /** Lock conflict. */
    public static final int LOCK_CONFLICT = 104;

    /** Requested action is not supported. */
    public static final int UNSUPPORTED = 105;

    /** Performed action is not allowed for caller. */
    public static final int NOT_PERMITTED = 106;

    public static final int INTERNAL_ERROR = 200;

    private ExitCodes() {
    }
}
