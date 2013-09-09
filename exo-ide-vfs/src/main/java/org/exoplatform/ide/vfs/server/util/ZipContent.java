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

import org.apache.commons.io.input.CountingInputStream;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ZipContent {
    /** Memory threshold. If zip stream over this size it spooled in file. */
    private static final int  BUFFER        = 100 * 1024; // 100k
    private static final int  BUFFER_SIZE   = 8 * 1024; // 8k
    /** The threshold after that checking of ZIP ratio started. */
    private static final long ZIP_THRESHOLD = 1000000;
    /**
     * Max compression ratio. If the number of bytes uncompressed data is exceed the number
     * of bytes of compressed stream more than this ratio (and number of uncompressed data
     * is more than threshold) then VirtualFileSystemRuntimeException is thrown.
     */
    private static final int  ZIP_RATIO     = 100;

    public static ZipContent newInstance(InputStream in) throws IOException {
        java.io.File file = null;
        byte[] inMemory = null;

        int count = 0;
        ByteArrayOutputStream inMemorySpool = new ByteArrayOutputStream(BUFFER);

        int bytes;
        final byte[] buff = new byte[BUFFER_SIZE];
        while (count <= BUFFER && (bytes = in.read(buff)) != -1) {
            inMemorySpool.write(buff, 0, bytes);
            count += bytes;
        }

        InputStream spool;
        if (count > BUFFER) {
            file = java.io.File.createTempFile("import", ".zip");
            FileOutputStream fileSpool = new FileOutputStream(file);
            try {
                inMemorySpool.writeTo(fileSpool);
                while ((bytes = in.read(buff)) != -1) {
                    fileSpool.write(buff, 0, bytes);
                }
            } finally {
                fileSpool.close();
            }
            spool = new FileInputStream(file);
        } else {
            inMemory = inMemorySpool.toByteArray();
            spool = new ByteArrayInputStream(inMemory);
        }

        ZipInputStream zip = null;
        try {
            // Counts numbers of compressed data.
            final CountingInputStream compressedCounter = new CountingInputStream(spool);
            zip = new ZipInputStream(compressedCounter);
            // Counts number of uncompressed data.
            CountingInputStream uncompressedCounter = new CountingInputStream(zip) {
                @Override
                public int read() throws IOException {
                    int i = super.read();
                    checkCompressionRatio();
                    return i;
                }

                @Override
                public int read(byte[] b, int off, int len) throws IOException {
                    int i = super.read(b, off, len);
                    checkCompressionRatio();
                    return i;
                }

                @Override
                public int read(byte[] b) throws IOException {
                    int i = super.read(b);
                    checkCompressionRatio();
                    return i;
                }

                @Override
                public long skip(long length) throws IOException {
                    long i = super.skip(length);
                    checkCompressionRatio();
                    return i;
                }

                private void checkCompressionRatio() {
                    long uncompressedBytes = getByteCount(); // number of uncompressed bytes
                    if (uncompressedBytes > ZIP_THRESHOLD) {
                        long compressedBytes = compressedCounter.getByteCount(); // number of compressed bytes
                        if (uncompressedBytes > (ZIP_RATIO * compressedBytes)) {
                            throw new VirtualFileSystemRuntimeException("Zip bomb detected. ");
                        }
                    }
                }
            };

            boolean isProject = false;

            ZipEntry zipEntry;
            while ((zipEntry = zip.getNextEntry()) != null) {
                if (".project".equals(zipEntry.getName())) {
                    isProject = true;
                } else if (!zipEntry.isDirectory()) {
                    while (uncompressedCounter.read(buff) != -1) {
                        // Read full data from stream to be able detect zip-bomb.
                    }
                }
            }

            return new ZipContent(
                    inMemory != null ? new ByteArrayInputStream(inMemory) : new DeleteOnCloseFileInputStream(file),
                    isProject,
                    file == null);
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
    }

    public final InputStream zippedData;
    public final boolean     isProject;
    public final boolean     inMemory;

    private ZipContent(InputStream zippedData, boolean project, boolean inMemory) {
        this.zippedData = zippedData;
        isProject = project;
        this.inMemory = inMemory;
    }
}
