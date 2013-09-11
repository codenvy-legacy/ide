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
package com.google.collide.server;

import com.codenvy.ide.dtogen.server.ServerErrorImpl;
import com.codenvy.ide.dtogen.shared.ServerError;


/**
 * Wraps any exception in collaboration editor at server side.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
@SuppressWarnings("serial")
public final class CollaborationEditorException extends RuntimeException {
    private final ServerError error;

    public CollaborationEditorException(Throwable cause) {
        super(cause);
        ServerErrorImpl myError = ServerErrorImpl.make();
        myError.setDetails(cause.getMessage());
        myError.setFailureReason(ServerError.FailureReason.UNKNOWN);
        this.error = myError;
    }

    public CollaborationEditorException(ServerError error) {
        this.error = error;
    }

    public ServerError getError() {
        return error;
    }
}
