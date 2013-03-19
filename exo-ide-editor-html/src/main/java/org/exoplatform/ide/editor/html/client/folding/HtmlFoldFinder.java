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
package org.exoplatform.ide.editor.html.client.folding;

import com.google.collide.client.editor.folding.FoldOccurrencesFinder;
import com.google.collide.client.editor.folding.FoldRange;

import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlFoldFinder.java Mar 19, 2013 1:34:14 AM azatsarynnyy $
 *
 */
public class HtmlFoldFinder implements FoldOccurrencesFinder
{

   /**
    * @see com.google.collide.client.editor.folding.FoldOccurrencesFinder#computePositions(org.exoplatform.ide.editor.shared.text.IDocument)
    */
   @Override
   public List<FoldRange> computePositions(IDocument Document)
   {
//    XmlReconcilingStrategy xmlReconcilingStrategy = new XmlReconcilingStrategy();
//    xmlReconcilingStrategy.setDocument(iDoc);
//    xmlReconcilingStrategy.initialReconcile();
//    return xmlReconcilingStrategy.getfPositions();

      List<FoldRange> positions = new ArrayList<FoldRange>();

      // TODO parse document content & fill position list
      positions.add(new FoldRange(0, 310)); // html
      positions.add(new FoldRange(7, 274)); // head
      positions.add(new FoldRange(281, 21)); // body
      return positions;
   }

}
