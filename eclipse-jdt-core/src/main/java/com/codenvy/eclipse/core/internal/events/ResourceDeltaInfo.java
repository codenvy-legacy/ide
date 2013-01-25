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
package com.codenvy.eclipse.core.internal.events;

import com.codenvy.eclipse.core.runtime.IPath;
import com.codenvy.eclipse.resources.MarkerSet;
import com.codenvy.eclipse.resources.WorkspaceResource;

import java.util.Map;

public class ResourceDeltaInfo
{
   protected WorkspaceResource workspace;

   protected Map<IPath, MarkerSet> allMarkerDeltas;

   protected NodeIDMap nodeIDMap;

   protected ResourceComparator comparator;

   public ResourceDeltaInfo(WorkspaceResource workspace, Map<IPath, MarkerSet> markerDeltas,
      ResourceComparator comparator)
   {
      super();
      this.workspace = workspace;
      this.allMarkerDeltas = markerDeltas;
      this.comparator = comparator;
   }

   public ResourceComparator getComparator()
   {
      return comparator;
   }

   /**
    * Table of all marker deltas, IPath -> MarkerSet
    */
   public Map<IPath, MarkerSet> getMarkerDeltas()
   {
      return allMarkerDeltas;
   }

   public NodeIDMap getNodeIDMap()
   {
      return nodeIDMap;
   }

   public WorkspaceResource getWorkspace()
   {
      return workspace;
   }

   public void setMarkerDeltas(Map<IPath, MarkerSet> value)
   {
      allMarkerDeltas = value;
   }

   public void setNodeIDMap(NodeIDMap map)
   {
      nodeIDMap = map;
   }
}
