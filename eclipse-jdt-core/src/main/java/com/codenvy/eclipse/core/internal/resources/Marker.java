/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     James Blackburn (Broadcom Corp.) - ongoing development
 *******************************************************************************/
package com.codenvy.eclipse.core.internal.resources;

import com.codenvy.eclipse.core.resources.IMarker;
import com.codenvy.eclipse.core.resources.IResource;
import com.codenvy.eclipse.core.resources.IWorkspace;
import com.codenvy.eclipse.core.runtime.Assert;
import com.codenvy.eclipse.core.runtime.CoreException;
import com.codenvy.eclipse.core.runtime.PlatformObject;

import java.util.Map;

/**
 * An abstract marker implementation.
 * Subclasses must implement the <code>clone</code> method, and
 * are free to declare additional field and method members.
 * <p>
 * Note: Marker objects do not store whether they are "standalone"
 * vs. "attached" to the workspace. This information is maintained
 * by the workspace.
 * </p>
 *
 * @see com.codenvy.eclipse.core.resources.IMarker
 */
public class Marker extends PlatformObject implements IMarker {

    /** Marker identifier. */
    protected long id;

    /** Resource with which this marker is associated. */
    protected IResource resource;

    /** Constructs a new marker object. */
    public Marker(IResource resource, long id) {
        Assert.isLegal(resource != null);
        this.resource = resource;
        this.id = id;
    }

    /**
     //    * Checks the given marker info to ensure that it is not null.
     //    * Throws an exception if it is.
     //    */
    //   private void checkInfo(MarkerInfo info) throws CoreException
    //   {
    //      if (info == null)
    //      {
    //         String message = "resources marker Not Found " + Long.toString(id);
    //         throw new ResourceException(
    //            new ResourceStatus(IResourceStatus.MARKER_NOT_FOUND, resource.getFullPath(), message));
    //      }
    //   }

    /** @see com.codenvy.eclipse.core.resources.IMarker#delete() */
    public void delete() throws CoreException {
        //		final ISchedulingRule rule = getWorkspace().getRuleFactory().markerRule(resource);
        //		try {
        //			getWorkspace().prepareOperation(rule, null);
        //			getWorkspace().beginOperation(true);
        //			getWorkspace().getMarkerManager().removeMarker(getResource(), getId());
        //		} finally {
        //			getWorkspace().endOperation(rule, false, null);
        //		}
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#equals(Object) */
    public boolean equals(Object object) {
        if (!(object instanceof IMarker)) {
            return false;
        }
        IMarker other = (IMarker)object;
        return (id == other.getId() && resource.equals(other.getResource()));
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#exists() */
    public boolean exists() {
        return true;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getAttribute(String) */
    public Object getAttribute(String attributeName) throws CoreException {
        //      Assert.isNotNull(attributeName);
        //      MarkerInfo info = getInfo();
        //      checkInfo(info);
        //      return info.getAttribute(attributeName);
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getAttribute(String, int) */
    public int getAttribute(String attributeName, int defaultValue) {
        //      Assert.isNotNull(attributeName);
        //      MarkerInfo info = getInfo();
        //      if (info == null)
        //      {
        //         return defaultValue;
        //      }
        //      Object value = info.getAttribute(attributeName);
        //      if (value instanceof Integer)
        //      {
        //         return ((Integer)value).intValue();
        //      }
        //      return defaultValue;
        return 0;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getAttribute(String, String) */
    public String getAttribute(String attributeName, String defaultValue) {
        //      Assert.isNotNull(attributeName);
        //      MarkerInfo info = getInfo();
        //      if (info == null)
        //      {
        //         return defaultValue;
        //      }
        //      Object value = info.getAttribute(attributeName);
        //      if (value instanceof String)
        //      {
        //         return (String)value;
        //      }
        //      return defaultValue;
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getAttribute(String, boolean) */
    public boolean getAttribute(String attributeName, boolean defaultValue) {
        //      Assert.isNotNull(attributeName);
        //      MarkerInfo info = getInfo();
        //      if (info == null)
        //      {
        //         return defaultValue;
        //      }
        //      Object value = info.getAttribute(attributeName);
        //      if (value instanceof Boolean)
        //      {
        //         return ((Boolean)value).booleanValue();
        //      }
        //      return defaultValue;
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getAttributes() */
    public Map<String, Object> getAttributes() throws CoreException {
        //      MarkerInfo info = getInfo();
        //      checkInfo(info);
        //      return info.getAttributes();
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getAttributes(String[]) */
    public Object[] getAttributes(String[] attributeNames) throws CoreException {
        //      Assert.isNotNull(attributeNames);
        //      MarkerInfo info = getInfo();
        //      checkInfo(info);
        //      return info.getAttributes(attributeNames);
        return null;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getCreationTime() */
    public long getCreationTime() throws CoreException {
        //      MarkerInfo info = getInfo();
        //      checkInfo(info);
        //      return info.getCreationTime();
        return 0;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getId() */
    public long getId() {
        return id;
    }

    //   protected MarkerInfo getInfo()
    //   {
    //      //		return getWorkspace().getMarkerManager().findMarkerInfo(resource, id);
    //      return null;
    //   }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getResource() */
    public IResource getResource() {
        return resource;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#getType() */
    public String getType() throws CoreException {
        //      MarkerInfo info = getInfo();
        //      checkInfo(info);
        //      return info.getType();
        return null;
    }

    /**
     * Returns the workspace which manages this marker.  Returns
     * <code>null</code> if this resource does not have an associated
     * resource.
     */
    private IWorkspace getWorkspace() {
        return resource == null ? null : resource.getWorkspace();
    }

    public int hashCode() {
        return (int)id + resource.hashCode();
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#isSubtypeOf(String) */
    public boolean isSubtypeOf(String type) throws CoreException {
        //		return getWorkspace().getMarkerManager().isSubtype(getType(), type);
        return false;
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#setAttribute(String, int) */
    public void setAttribute(String attributeName, int value) throws CoreException {
        setAttribute(attributeName, new Integer(value));
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#setAttribute(String, Object) */
    public void setAttribute(String attributeName, Object value) throws CoreException {
        //		Assert.isNotNull(attributeName);
        //		Workspace workspace = getWorkspace();
        //		MarkerManager manager = workspace.getMarkerManager();
        //		try {
        //			workspace.prepareOperation(null, null);
        //			workspace.beginOperation(true);
        //			MarkerInfo markerInfo = getInfo();
        //			checkInfo(markerInfo);
        //
        //			//only need to generate delta info if none already
        //			boolean needDelta = !manager.hasDelta(resource.getFullPath(), id);
        //			MarkerInfo oldInfo = needDelta ? (MarkerInfo) markerInfo.clone() : null;
        //			boolean validate = manager.isPersistentType(markerInfo.getType());
        //			markerInfo.setAttribute(attributeName, value, validate);
        //			if (manager.isPersistent(markerInfo))
        //				((Resource) resource).getResourceInfo(false, true).set(ICoreConstants.M_MARKERS_SNAP_DIRTY);
        //			if (needDelta) {
        //				MarkerDelta delta = new MarkerDelta(IResourceDelta.CHANGED, resource, oldInfo);
        //				manager.changedMarkers(resource, new MarkerDelta[] {delta});
        //			}
        //		} finally {
        //			workspace.endOperation(null, false, null);
        //		}
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#setAttribute(String, boolean) */
    public void setAttribute(String attributeName, boolean value) throws CoreException {
        setAttribute(attributeName, value ? Boolean.TRUE : Boolean.FALSE);
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#setAttributes(String[], Object[]) */
    public void setAttributes(String[] attributeNames, Object[] values) throws CoreException {
        //		Assert.isNotNull(attributeNames);
        //		Assert.isNotNull(values);
        //		Workspace workspace = getWorkspace();
        //		MarkerManager manager = workspace.getMarkerManager();
        //		try {
        //			workspace.prepareOperation(null, null);
        //			workspace.beginOperation(true);
        //			MarkerInfo markerInfo = getInfo();
        //			checkInfo(markerInfo);
        //
        //			//only need to generate delta info if none already
        //			boolean needDelta = !manager.hasDelta(resource.getFullPath(), id);
        //			MarkerInfo oldInfo = needDelta ? (MarkerInfo) markerInfo.clone() : null;
        //			boolean validate = manager.isPersistentType(markerInfo.getType());
        //			markerInfo.setAttributes(attributeNames, values, validate);
        //			if (manager.isPersistent(markerInfo))
        //				((Resource) resource).getResourceInfo(false, true).set(ICoreConstants.M_MARKERS_SNAP_DIRTY);
        //			if (needDelta) {
        //				MarkerDelta delta = new MarkerDelta(IResourceDelta.CHANGED, resource, oldInfo);
        //				manager.changedMarkers(resource, new MarkerDelta[] {delta});
        //			}
        //		} finally {
        //			workspace.endOperation(null, false, null);
        //		}
    }

    /** @see com.codenvy.eclipse.core.resources.IMarker#setAttributes(java.util.Map) */
    public void setAttributes(Map<String, ? extends Object> values) throws CoreException {
        //		Workspace workspace = getWorkspace();
        //		MarkerManager manager = workspace.getMarkerManager();
        //		try {
        //			workspace.prepareOperation(null, null);
        //			workspace.beginOperation(true);
        //			MarkerInfo markerInfo = getInfo();
        //			checkInfo(markerInfo);
        //
        //			//only need to generate delta info if none already
        //			boolean needDelta = !manager.hasDelta(resource.getFullPath(), id);
        //			MarkerInfo oldInfo = needDelta ? (MarkerInfo) markerInfo.clone() : null;
        //			boolean validate = manager.isPersistentType(markerInfo.getType());
        //			markerInfo.setAttributes(values, validate);
        //			if (manager.isPersistent(markerInfo))
        //				((Resource) resource).getResourceInfo(false, true).set(ICoreConstants.M_MARKERS_SNAP_DIRTY);
        //			if (needDelta) {
        //				MarkerDelta delta = new MarkerDelta(IResourceDelta.CHANGED, resource, oldInfo);
        //				manager.changedMarkers(resource, new MarkerDelta[] {delta});
        //			}
        //		} finally {
        //			workspace.endOperation(null, false, null);
        //		}
    }
}
