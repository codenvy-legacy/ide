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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Delete java.io.File after closing.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class DeleteOnCloseFileInputStream extends FileInputStream {
    private final java.io.File file;
    private boolean deleted = false;

    public DeleteOnCloseFileInputStream(java.io.File file) throws FileNotFoundException {
        super(file);
        this.file = file;
    }

    /** @see java.io.FileInputStream#close() */
    @Override
    public void close() throws IOException {
        try {
            super.close();
        } finally {
            if (!deleted) {
                deleted = file.delete();
            }
        }
        //System.out.println("---> " + file.getAbsolutePath() + ", exists : " + file.exists());
    }
}