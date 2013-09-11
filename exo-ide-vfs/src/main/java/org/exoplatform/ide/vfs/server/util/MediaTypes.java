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
package org.exoplatform.ide.vfs.server.util;

import org.exoplatform.commons.utils.MimeTypeResolver;

/**
 * Resolves media type from file name extension.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public enum MediaTypes {
    INSTANCE;

    final MimeTypeResolver resolver;

    private MediaTypes() {
        resolver = new MimeTypeResolver();
        resolver.setDefaultMimeType("text/plain");
    }

    public String getMediaType(String filename) {
        return resolver.getMimeType(filename);
    }
}
