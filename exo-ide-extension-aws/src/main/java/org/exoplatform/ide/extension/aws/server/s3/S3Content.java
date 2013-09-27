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
package org.exoplatform.ide.extension.aws.server.s3;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public final class S3Content {
    private final InputStream stream;
    private final String      contentType;
    private final Date        lastModificationDate;
    private final long        length;

    public S3Content(InputStream stream, String contentType, Date lastModificationDate, long length) {
        this.stream = stream;
        this.contentType = contentType;
        this.lastModificationDate = lastModificationDate;
        this.length = length;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getContentType() {
        return contentType;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }

    public long getLength() {
        return length;
    }
}
