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
package org.exoplatform.ide.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Result of maven task.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class Result {
    /** Stream that contains result ot task, e.g. binary stream of maven artifact. */
    private final InputStream stream;

    /** File that contains result of task. */
    private final File file;

    /** Media type of result. This value is sent to the client. */
    private final String mediaType;

    /** Time when task was done. */
    private final long time;


    /**
     * This value may be sent to the client in Content-Disposition header. It may be useful to prevent some clients (e.g.
     * browsers) to open file but download it.
     */
    private final String fileName;

    Result(InputStream stream, String mediaType, String fileName, long time) {
        this.stream = stream;
        this.file = null;
        this.mediaType = mediaType;
        this.fileName = fileName;
        this.time = time;
    }

    Result(File file, String mediaType, String fileName, long time) {
        this.file = file;
        this.stream = null;
        this.mediaType = mediaType;
        this.fileName = fileName;
        this.time = time;
    }


    String getMediaType() {
        return mediaType;
    }

    InputStream getStream() throws IOException {
        return stream != null ? stream : new FileInputStream(file);
    }

    String getFileName() {
        return fileName;
    }

    long getTime() {
        return time;
    }

}
