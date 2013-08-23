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
