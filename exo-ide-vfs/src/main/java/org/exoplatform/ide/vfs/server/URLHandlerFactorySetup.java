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

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

/**
 * Setup {@link URLStreamHandlerFactory} to be able use URL for access to virtual file system. It is not possible to
 * provide
 * correct {@link URLStreamHandler} by system property 'java.protocol.handler.pkgs'. Bug in Oracle JDK:
 * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4648098
 *
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class URLHandlerFactorySetup {
    private static final Log LOG = ExoLogger.getExoLogger(URLHandlerFactorySetup.class);

    public synchronized static void setup(VirtualFileSystemRegistry registry, EventListenerList listeners) {
        try {
            new URL("ide+vfs", "", "");
        } catch (MalformedURLException mue) {
            // URL with protocol 'ide+vfs' is not supported yet. Need register URLStreamHandlerFactory.

            if (LOG.isDebugEnabled()) {
                LOG.debug("--> Try setup URLStreamHandlerFactory for protocol 'ide+vfs'. ");
            }
            try {
                // Get currently installed URLStreamHandlerFactory.
                Field factoryField = URL.class.getDeclaredField("factory");
                factoryField.setAccessible(true);
                URLStreamHandlerFactory currentFactory = (URLStreamHandlerFactory)factoryField.get(null);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("--> Current instance of URLStreamHandlerFactory: "
                              + (currentFactory != null ? currentFactory.getClass().getName() : null));
                }

                //
                URLStreamHandlerFactory vfsURLFactory = new VirtualFileSystemURLHandlerFactory(currentFactory,
                                                                                               registry, listeners);
                factoryField.set(null, vfsURLFactory);
            } catch (SecurityException se) {
                throw new VirtualFileSystemRuntimeException(se.getMessage(), se);
            } catch (NoSuchFieldException nfe) {
                throw new VirtualFileSystemRuntimeException(nfe.getMessage(), nfe);
            } catch (IllegalAccessException ae) {
                throw new VirtualFileSystemRuntimeException(ae.getMessage(), ae);
            }

            // Check 'ide+vfs' again. From now it should be possible to use such URLs.
            // At the same time we force URL to remember our protocol handler.
            // URL knows about it even if the URLStreamHandlerFactory is changed.

            try {
                new URL("ide+vfs", "", "");

                //
                if (LOG.isDebugEnabled()) {
                    LOG.debug("--> URLStreamHandlerFactory installed. ");
                }
            } catch (MalformedURLException e) {
                throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
        }
    }

    protected URLHandlerFactorySetup() {
    }
}
