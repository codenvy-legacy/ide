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
package com.codenvy.ide.texteditor.api;

import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.RegionImpl;

/**
 * The interface of a document content formatter. The formatter formats ranges
 * within documents. The documents are modified by the formatter.<p>
 * The content formatter is assumed to determine the partitioning of the document
 * range to be formatted. For each partition, the formatter determines based
 * on the partition's content type the formatting strategy to be used. Before
 * the first strategy is activated all strategies are informed about the
 * start of the formatting process. After that, the formatting strategies are
 * activated in the sequence defined by the partitioning of the document range to be
 * formatted. It is assumed that a strategy must be finished before the next strategy
 * can be activated. After the last strategy has been finished, all strategies are
 * informed about the termination of the formatting process.</p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface ContentFormatter
{
   /**
    * Formats the given region of the specified document.The formatter may safely
    * assume that it is the only subject that modifies the document at this point in time.
    *
    * @param document the document to be formatted
    * @param region the region within the document to be formatted
    */
   void format(Document document, RegionImpl region);

   //TODO
//   /**
//    * Returns the formatting strategy registered for the given content type.
//    *
//    * @param contentType the content type for which to look up the formatting strategy
//    * @return the formatting strategy for the given content type, or
//    *    <code>null</code> if there is no such strategy
//    */
//   IFormattingStrategy getFormattingStrategy(String contentType);
}
