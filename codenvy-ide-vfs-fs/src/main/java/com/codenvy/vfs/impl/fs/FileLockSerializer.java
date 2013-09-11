/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.vfs.impl.fs;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.EOFException;
import java.io.IOException;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class FileLockSerializer implements DataSerializer<FileLock> {
    @Override
    public void write(DataOutput output, FileLock lock) throws IOException {
        output.writeUTF(lock.getLockToken());
        output.writeLong(lock.getExpired());
    }

    @Override
    public FileLock read(DataInput input) throws IOException {
        String lockToken = input.readUTF();
        long expired = Long.MAX_VALUE; // Timeout added after start use in production. Need respect looks without timeout.
        try {
            expired = input.readLong();
        } catch (EOFException ignored) {
        }
        return new FileLock(lockToken, expired);
    }
}
