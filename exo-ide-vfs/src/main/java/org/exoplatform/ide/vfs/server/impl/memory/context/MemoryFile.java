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
package org.exoplatform.ide.vfs.server.impl.memory.context;

import com.codenvy.commons.lang.NameGenerator;

import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.PropertyFilter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryFile extends MemoryItem {
    private byte[] bytes;
    private final AtomicReference<String> lockHolder = new AtomicReference<String>();
    private long contentLastModificationDate;
    private final Object contentLock = new Object();

    public MemoryFile(String name, String mediaType, InputStream content) throws IOException {
        this(ObjectIdGenerator.generateId(), name, mediaType, content == null ? null : readContent(content));
    }

    private static byte[] readContent(InputStream content) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int r;
        while ((r = content.read(buf)) != -1) {
            bout.write(buf, 0, r);
        }
        return bout.toByteArray();
    }

    MemoryFile(String id, String name, String mediaType, byte[] bytes) {
        super(id, name);
        setMediaType(mediaType);
        this.bytes = bytes;
        this.contentLastModificationDate = System.currentTimeMillis();
    }

    public final String lock() throws VirtualFileSystemException {
        final String lockToken = NameGenerator.generate(null, 32);
        if (!lockHolder.compareAndSet(null, lockToken)) {
            throw new LockException("File already locked. ");
        }
        lastModificationDate = System.currentTimeMillis();
        return lockToken;
    }

    public final void unlock(String lockToken) throws VirtualFileSystemException {
        if (lockToken == null) {
            throw new LockException("Null lock token. ");
        }
        final String thisLockToken = lockHolder.get();
        if (lockToken.equals(thisLockToken)) {
            lockHolder.compareAndSet(thisLockToken, null);
            lastModificationDate = System.currentTimeMillis();
        } else {
            if (thisLockToken == null) {
                throw new LockException("File is not locked. ");
            }
            throw new LockException("Unable remove lock from file. Lock token does not match. ");
        }
    }

    public final boolean isLocked() {
        return lockHolder.get() != null;
    }

    public boolean isLockTokenMatched(String lockToken) {
        final String thisLockToken = lockHolder.get();
        return thisLockToken == null || thisLockToken.equals(lockToken);
    }

    public final ContentStream getContent() throws VirtualFileSystemException {
        synchronized (contentLock) {
            byte[] bytes = this.bytes;
            if (bytes == null) {
                bytes = new byte[0];
            }
            return new ContentStream(getName(), new ByteArrayInputStream(bytes), getMediaType(), bytes.length,
                                     new Date(contentLastModificationDate));
        }
    }

    public final void setContent(InputStream content) throws IOException {
        synchronized (contentLock) {
            byte[] bytes = null;
            if (content != null) {
                bytes = readContent(content);
            }
            this.bytes = bytes;
        }
        lastModificationDate = contentLastModificationDate = System.currentTimeMillis();
    }

    @Override
    public final boolean isFile() {
        return true;
    }

    @Override
    public final boolean isFolder() {
        return false;
    }

    @Override
    public final boolean isProject() {
        return false;
    }

    @Override
    public MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException {
        byte[] bytes = this.bytes;
        MemoryFile copy = new MemoryFile(ObjectIdGenerator.generateId(), getName(), getMediaType(), Arrays.copyOf(bytes, bytes.length));
        copy.updateProperties(getProperties(PropertyFilter.ALL_FILTER));
        copy.updateACL(getACL(), true);
        parent.addChild(copy);
        return copy;
    }

    public final String getLatestVersionId() {
        return getId();
    }

    public final String getVersionId() {
        return "0";
    }

    @Override
    public String toString() {
        return "MemoryFile{" +
               "id='" + getId() + '\'' +
               ", path=" + getPath() +
               ", name='" + getName() + '\'' +
               ", isLocked='" + isLocked() + '\'' +
               '}';
    }
}
