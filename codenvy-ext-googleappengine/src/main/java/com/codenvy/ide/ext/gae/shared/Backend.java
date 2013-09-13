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
package com.codenvy.ide.ext.gae.shared;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;

/**
 * Information about Google App Engine backend.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 4:45:33 PM anya $
 */
@DTO
public interface Backend {
    public enum Option {
        DYNAMIC, FAIL_FAST, PUBLIC;
    }

    /**
     * Backend state.
     */
    public enum State {
        START, STOP;
    }

    /**
     * Get instance class.
     *
     * @return instance class.
     */
    String getInstanceClass();

    /**
     * Get count of the backend instances.
     *
     * @return instances count.
     */
    Integer getInstances();

    /**
     * Get maximum of concurrent requests.
     *
     * @return maximum of concurrent requests.
     */
    Integer getMaxConcurrentRequests();

    /**
     * Get backend name.
     *
     * @return name of the backend.
     */
    String getName();

    /**
     * Get backends options.
     *
     * @return list of backends options.
     */
    JsonArray<Option> getOptions();

    /**
     * Get backend state.
     *
     * @return one of two possible values: START, STOP.
     */
    State getState();

    /**
     * Is backend is dynamic.
     *
     * @return true if dynamic, otherwise false.
     */
    Boolean isDynamic();

    /**
     * Is fail fast.
     *
     * @return true if fail fast otherwise false.
     */
    Boolean isFailFast();

    /**
     * Is backend public.
     *
     * @return true if public, otherwise false.
     */
    Boolean isPublic();
}