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
package org.exoplatform.ide.vfs.server.exceptions;

/**
 * If requested action requires optional capability that is not supported.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: NotSupportedException.java 73805 2011-09-21 08:30:20Z andrew00x $
 */
@SuppressWarnings("serial")
public class NotSupportedException extends VirtualFileSystemRuntimeException {
    /**
     * @param message
     *         message
     */
    public NotSupportedException(String message) {
        super(message);
    }
}
