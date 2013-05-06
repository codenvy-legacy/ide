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
package com.codenvy.ide.commons;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class NamedThreadFactory implements ThreadFactory {
    private static final AtomicLong threadPoolNumGen = new AtomicLong();

    private final String  namePrefix;
    private final boolean daemon;

    public NamedThreadFactory(String namePrefix, boolean daemon) {
        if (namePrefix == null) {
            throw new IllegalArgumentException();
        }
        this.namePrefix = namePrefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        final Thread t = new Thread(r, namePrefix + threadPoolNumGen.getAndIncrement());
        if (daemon) {
            t.setDaemon(true);
        }
        return t;
    }
}
