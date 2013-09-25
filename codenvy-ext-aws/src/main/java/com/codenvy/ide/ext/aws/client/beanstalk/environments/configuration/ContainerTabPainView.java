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
package com.codenvy.ide.ext.aws.client.beanstalk.environments.configuration;

import com.codenvy.ide.api.mvp.View;

/**
 * The view for {@link ContainerTabPainPresenter}.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface ContainerTabPainView extends View<ContainerTabPainView.ActionDelegate> {
    /** Interface which must implement presenter to process any actions. */
    interface ActionDelegate {
    }

    /** Reset modifiable state for user inputs. */
    void resetModifiedFields();

    /**
     * Set initial heap size.
     *
     * @param heapSize
     *         heap size.
     */
    void setInitialHeapSize(String heapSize);

    /**
     * Get initial heap size.
     *
     * @return heap size.
     */
    String getInitialHeapSize();

    /**
     * Is heap size modified.
     *
     * @return true if modified.
     */
    boolean isInitialHeapSizeModified();

    /**
     * Set max heap size.
     *
     * @param maxHeapSize
     *         max heap size.
     */
    void setMaxHeapSize(String maxHeapSize);

    /**
     * Get max heap size.
     *
     * @return max heap size.
     */
    String getMaxHeapSize();

    /**
     * Is max heap size modified.
     *
     * @return true if modified.
     */
    boolean isMaxHeapSizeModified();

    /**
     * Set max perm gen size.
     *
     * @param maxPermGenSize
     *         max perm gen size.
     */
    void setMaxPermGenSize(String maxPermGenSize);

    /**
     * Get max perm gen size.
     *
     * @return max perm gen size.
     */
    String getMaxPermGenSize();

    /**
     * Is max perm gen size modified.
     *
     * @return true if modified.
     */
    boolean isMaxPermGenSizeModified();

    /**
     * Set JVM command line options.
     *
     * @param jvmCommandLineOpt
     *         command line options.
     */
    void setJVMCommandLineOpt(String jvmCommandLineOpt);

    /**
     * Get JVM command line options.
     *
     * @return command line options.
     */
    String getJVMCommandLineOpt();

    /**
     * Is command line options modified.
     *
     * @return true if modified.
     */
    boolean isJVMCommandLineOptModified();
}
