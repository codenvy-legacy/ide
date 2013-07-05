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
