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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.*;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Connection to virtual file system. Instances of this class are not safe to be used by multiple concurrent threads.
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class VirtualFileSystemURLConnection extends URLConnection {
    private static final Log LOG = ExoLogger.getLogger(VirtualFileSystemURLConnection.class);

    private final VirtualFileSystemRegistry registry;

    private final EventListenerList listeners;

    private VirtualFileSystem vfs;

    private Item item;

    /**
     * @param url
     *         the URL
     * @param registry
     *         virtual file system registry
     */
    public VirtualFileSystemURLConnection(URL url,
                                          VirtualFileSystemRegistry registry,
                                          EventListenerList listeners) {
        super(check(url)); // Be sure URL is correct.
        this.registry = registry;
        this.listeners = listeners;
    }

    private static URL check(URL url) {
        if (!"ide+vfs".equals(url.getProtocol())) {
            throw new IllegalArgumentException("Invalid URL: " + url);
        }
        return url;
    }

    /** @see java.net.URLConnection#connect() */
    @Override
    public void connect() throws IOException {
        final URI theUri = URI.create(getURL().toString());
        final String path = theUri.getPath();
        final String vfsId =
                (path == null || "/".equals(path)) ? null : (path.startsWith("/")) ? path.substring(1) : path;
        try {
            vfs = registry.getProvider(vfsId).newInstance(null, listeners);
            final String itemIdentifier = theUri.getFragment();
            item = (itemIdentifier.startsWith("/")) //
                   ? vfs.getItemByPath(itemIdentifier, null, false, PropertyFilter.NONE_FILTER) //
                   : vfs.getItem(itemIdentifier, false, PropertyFilter.NONE_FILTER);
        } catch (VirtualFileSystemException e) {
            throw new IOException(e.getMessage(), e);
        }
        connected = true;
    }

    public void disconnect() {
        item = null;
        vfs = null;
        connected = false;
    }

    /** @see java.net.URLConnection#getContentLength() */
    @Override
    public int getContentLength() {
        try {
            if (!connected) {
                connect();
            }
            if (item.getItemType() == ItemType.FILE) {
                return (int)((File)item).getLength();
            }
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return -1;
    }

    /** @see java.net.URLConnection#getContentType() */
    @Override
    public String getContentType() {
        try {
            if (!connected) {
                connect();
            }
            return item.getMimeType();
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return null;
    }

    /** @see java.net.URLConnection#getLastModified() */
    @Override
    public long getLastModified() {
        try {
            if (!connected) {
                connect();
            }
            if (item.getItemType() == ItemType.FILE) {
                return ((File)item).getLastModificationDate();
            }
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return 0;
    }

    /** @see java.net.URLConnection#getContent() */
    @Override
    public Object getContent() throws IOException {
        if (!connected) {
            connect();
        }
        return item;
    }

    /** @see java.net.URLConnection#getContent(java.lang.Class[]) */
    @SuppressWarnings("rawtypes")
    @Override
    public Object getContent(Class[] classes) throws IOException {
        throw new UnsupportedOperationException();
    }

    /** @see java.net.URLConnection#getInputStream() */
    @Override
    public InputStream getInputStream() throws IOException {
        if (!connected) {
            connect();
        }
        try {
            if (item.getItemType() == ItemType.FILE) {
                ContentStream content = vfs.getContent(item.getId());
                return content.getStream();
            }
            // Folder. Show plain list of child.
            ItemList<Item> children = vfs.getChildren(item.getId(), -1, 0, null, false, PropertyFilter.NONE_FILTER);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Writer w = new OutputStreamWriter(out);
            for (Item i : children.getItems()) {
                w.write(i.getName());
                w.write('\n');
            }
            w.flush();
            w.close();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (VirtualFileSystemException e) {
            throw new IOException(e.getMessage(), e);
        }
    }
}