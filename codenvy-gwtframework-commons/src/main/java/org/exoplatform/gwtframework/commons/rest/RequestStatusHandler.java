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
package org.exoplatform.gwtframework.commons.rest;


/**
 * Callback interface, this interface needs to {@link AsyncRequest} can tell the application execution state async REST Service
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 16, 2011 evgen $
 */
public interface RequestStatusHandler {

    /**
     * Calls when service started or in progress.
     *
     * @param id
     *         the Async REST Service id
     */
    void requestInProgress(String id);

    /**
     * Calls when service work done.
     *
     * @param id
     *         the Async REST Service id
     */
    void requestFinished(String id);

    /**
     * Calls when service return error
     *
     * @param id
     *         the Async REST Service id
     * @param exception
     *         the exception received from service
     */
    void requestError(String id, Throwable exception);
}
