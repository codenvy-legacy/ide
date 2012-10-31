/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.texteditor.api.reconciler;

import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.Region;

/**
 * A reconciling strategy is used by an reconciler to reconcile a model
 * based on text of a particular content type.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface ReconcilingStrategy
{
   /**
    * Tells this reconciling strategy on which document it will
    * work. This method will be called before any other method
    * and can be called multiple times. The regions passed to the
    * other methods always refer to the most recent document
    * passed into this method.
    *
    * @param document the document on which this strategy will work
    */
   void setDocument(Document document);
   
   /**
    * Activates incremental reconciling of the specified dirty region.
    * As a dirty region might span multiple content types, the segment of the
    * dirty region which should be investigated is also provided to this
    * reconciling strategy. The given regions refer to the document passed into
    * the most recent call of {@link #setDocument(Document)}.
    *
    * @param dirtyRegion the document region which has been changed
    * @param subRegion the sub region in the dirty region which should be reconciled
    */
   void reconcile(DirtyRegion dirtyRegion, Region subRegion);

   /**
    * Activates non-incremental reconciling. The reconciling strategy is just told
    * that there are changes and that it should reconcile the given partition of the
    * document most recently passed into {@link #setDocument(Document)}.
    *
    * @param partition the document partition to be reconciled
    */
   void reconcile(Region partition);
}
