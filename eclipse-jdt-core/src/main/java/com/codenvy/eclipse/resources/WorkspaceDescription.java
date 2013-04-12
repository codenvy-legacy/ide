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
package com.codenvy.eclipse.resources;

import com.codenvy.eclipse.core.resources.IWorkspaceDescription;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class WorkspaceDescription implements IWorkspaceDescription {

    private long fileStateLongevity;

    private int maxBuildIterations;

    private long maxFileStateSize;

    private boolean applyFileStatePolicy;

    private long snapshotInterval;

    private String[] buildOrder;

    private boolean autoBuilding;


    /** {@inheritDoc} */
    @Override
    public String[] getBuildOrder() {
        return buildOrder;
    }

    /** {@inheritDoc} */
    @Override
    public long getFileStateLongevity() {
        return fileStateLongevity;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxBuildIterations() {
        return maxBuildIterations;
    }

    /** {@inheritDoc} */
    @Override
    public int getMaxFileStates() {
        return maxBuildIterations;
    }

    /** {@inheritDoc} */
    @Override
    public long getMaxFileStateSize() {
        return maxFileStateSize;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isApplyFileStatePolicy() {
        return applyFileStatePolicy;
    }

    /** {@inheritDoc} */
    @Override
    public long getSnapshotInterval() {
        return snapshotInterval;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isAutoBuilding() {
        return autoBuilding;
    }

    /** {@inheritDoc} */
    @Override
    public void setAutoBuilding(boolean value) {
        this.autoBuilding = value;
    }

    /** {@inheritDoc} */
    @Override
    public void setBuildOrder(String[] value) {
        this.buildOrder = value;
    }

    /** {@inheritDoc} */
    @Override
    public void setFileStateLongevity(long time) {
        this.fileStateLongevity = time;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxBuildIterations(int number) {
        this.maxBuildIterations = number;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxFileStates(int number) {
        this.maxBuildIterations = number;
    }

    /** {@inheritDoc} */
    @Override
    public void setMaxFileStateSize(long size) {
        this.maxFileStateSize = size;
    }

    /** {@inheritDoc} */
    @Override
    public void setApplyFileStatePolicy(boolean apply) {
        this.applyFileStatePolicy = apply;
    }

    /** {@inheritDoc} */
    @Override
    public void setSnapshotInterval(long delay) {
        this.snapshotInterval = delay;
    }
}
