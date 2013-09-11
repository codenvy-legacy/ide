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
package org.exoplatform.ide.vfs.server;

import java.io.InputStream;
import java.util.Date;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ContentStream {
    private final String fileName;

    private final InputStream stream;

    private final String mimeType;

    private final long length;

    private final Date lastModificationDate;

    public ContentStream(String fileName, InputStream stream, String mimeType, long length, Date lastModificationDate) {
        this.fileName = fileName;
        this.stream = stream;
        this.mimeType = mimeType;
        this.length = length;
        this.lastModificationDate = lastModificationDate;
    }

    public ContentStream(String fileName, InputStream stream, String mimeType, Date lastModificationDate) {
        this(fileName, stream, mimeType, -1, lastModificationDate);
    }

    public ContentStream(String fileName, InputStream stream, String mimeType) {
        this(fileName, stream, mimeType, -1, new Date());
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getStream() {
        return stream;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getLength() {
        return length;
    }

    public Date getLastModificationDate() {
        return lastModificationDate;
    }
}
