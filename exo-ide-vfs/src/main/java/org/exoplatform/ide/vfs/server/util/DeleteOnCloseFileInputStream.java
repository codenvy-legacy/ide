/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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