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

import org.exoplatform.ide.vfs.shared.ExitCodes;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Factory of WebApplicationException that contains error message in HTML format.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class HtmlErrorFormatter {
    public static void sendErrorAsHTML(Exception e) {
        // GWT framework (used on client side) requires result in HTML format if use HTML forms.
        if (e instanceof ItemAlreadyExistException) {
            throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.ITEM_EXISTS),
                                                          MediaType.TEXT_HTML).build());
        } else if (e instanceof ItemNotFoundException) {
            throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.ITEM_NOT_FOUND),
                                                          MediaType.TEXT_HTML).build());
        } else if (e instanceof InvalidArgumentException) {
            throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.INVALID_ARGUMENT),
                                                          MediaType.TEXT_HTML).build());
        } else if (e instanceof ConstraintException) {
            throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.CONSTRAINT),
                                                          MediaType.TEXT_HTML).build());
        } else if (e instanceof PermissionDeniedException) {
            throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.NOT_PERMITTED),
                                                          MediaType.TEXT_HTML).build());
        } else if (e instanceof LockException) {
            throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.LOCK_CONFLICT),
                                                          MediaType.TEXT_HTML).build());
        }
        throw new WebApplicationException(Response.ok(formatAsHtml(e.getMessage(), ExitCodes.INTERNAL_ERROR),
                                                      MediaType.TEXT_HTML).build());
    }

    private static String formatAsHtml(String message, int exitCode) {
        return String.format("<pre>Code: %d Text: %s</pre>", exitCode, message);
    }

    private HtmlErrorFormatter() {
    }
}
