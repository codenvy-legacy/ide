/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: UpdateStorageTask.java Oct 29, 2012 vetal $
 */
public class UpdateStorageTask {
    private final String                      id;
    private final Future<UpdateStorageResult> f;

    public UpdateStorageTask(String id, Future<UpdateStorageResult> f) {
        this.id = id;
        this.f = f;
    }

    /**
     * Get build unique ID.
     *
     * @return build ID
     */
    public String getId() {
        return id;
    }

    /**
     * Check is update done or not. Note update may be successful or failed.
     *
     * @return <code>true</code> if build is done and <code>false</code> otherwise
     */
    public boolean isDone() {
        return f.isDone();
    }

    /** Cancel update. */
    public void cancel() {
        f.cancel(true);
    }

    /**
     * Get result of maven build.
     *
     * @return result of maven build. <b>NOTE</b> If build is not finished yet this method returns <code>null</code>
     * @throws MavenInvocationException
     *         if maven task cannot be run because to incorrect input parameters
     */
    public UpdateStorageResult getResult() {
        if (f.isDone()) {
            try {
                return f.get();
            } catch (InterruptedException e) {
                // Should not happen since we checked is task done or not.
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                final Throwable cause = e.getCause();
                if (cause instanceof Error) {
                    throw (Error)cause;
                }
                if (cause instanceof RuntimeException) {
                    throw (RuntimeException)cause;
                }
            }
        }
        return null;
    }
}
