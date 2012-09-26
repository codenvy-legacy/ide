/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.text;

import org.exoplatform.ide.runtime.Assert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Event describing the change of document partitionings.
 * 
 */
public class DocumentPartitioningChangedEvent
{

   /** The document whose partitionings changed */
   private final Document fDocument;

   /** The map of partitionings to changed regions. */
   private final Map<String, IRegion> fMap = new HashMap<String, IRegion>();

   /**
    * Creates a new document partitioning changed event for the given document. Initially this event is empty, i.e. does not
    * describe any change.
    * 
    * @param document the changed document
    */
   public DocumentPartitioningChangedEvent(Document document)
   {
      fDocument = document;
   }

   /**
    * Returns the changed document.
    * 
    * @return the changed document
    */
   public Document getDocument()
   {
      return fDocument;
   }

   /**
    * Returns the changed region of the given partitioning or <code>null</code> if the given partitioning did not change.
    * 
    * @param partitioning the partitioning
    * @return the changed region of the given partitioning or <code>null</code>
    */
   public IRegion getChangedRegion(String partitioning)
   {
      return (IRegion)fMap.get(partitioning);
   }

   /**
    * Returns the set of changed partitionings.
    * 
    * @return the set of changed partitionings
    */
   public String[] getChangedPartitionings()
   {
      String[] partitionings = new String[fMap.size()];
      fMap.keySet().toArray(partitionings);
      return partitionings;
   }

   /**
    * Sets the specified range as changed region for the given partitioning.
    * 
    * @param partitioning the partitioning
    * @param offset the region offset
    * @param length the region length
    */
   public void setPartitionChange(String partitioning, int offset, int length)
   {
      Assert.isNotNull(partitioning);
      fMap.put(partitioning, new Region(offset, length));
   }

   /**
    * Returns <code>true</code> if the set of changed partitionings is empty, <code>false</code> otherwise.
    * 
    * @return <code>true</code> if the set of changed partitionings is empty
    */
   public boolean isEmpty()
   {
      return fMap.isEmpty();
   }

   /**
    * Returns the coverage of this event. This is the minimal region that contains all changed regions of all changed
    * partitionings.
    * 
    * @return the coverage of this event
    */
   public IRegion getCoverage()
   {
      if (fMap.isEmpty())
         return new Region(0, 0);

      int offset = -1;
      int endOffset = -1;
      Iterator<IRegion> e = fMap.values().iterator();
      while (e.hasNext())
      {
         IRegion r = e.next();

         if (offset < 0 || r.getOffset() < offset)
            offset = r.getOffset();

         int end = r.getOffset() + r.getLength();
         if (end > endOffset)
            endOffset = end;
      }

      return new Region(offset, endOffset - offset);
   }
}
