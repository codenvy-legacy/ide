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
