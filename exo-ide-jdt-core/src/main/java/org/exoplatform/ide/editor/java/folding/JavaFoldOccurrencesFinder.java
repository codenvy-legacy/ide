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
package org.exoplatform.ide.editor.java.folding;

import com.google.collide.client.editor.folding.DefaultFoldRange;
import com.google.collide.client.editor.folding.FoldOccurrencesFinder;

import org.exoplatform.ide.editor.shared.text.IDocument;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: JavaFoldOccurrencesFinder.java Mar 19, 2013 1:34:14 AM azatsarynnyy $
 *
 */
public class JavaFoldOccurrencesFinder implements FoldOccurrencesFinder
{

   /**
    * @see com.google.collide.client.editor.folding.FoldOccurrencesFinder#computePositions(org.exoplatform.ide.editor.shared.text.IDocument)
    */
   @Override
   public List<DefaultFoldRange> computePositions(IDocument Document)
   {
      // for testing GreetingController sample
      List<DefaultFoldRange> positions = new ArrayList<DefaultFoldRange>();
      positions.add(new DefaultFoldRange(18, 28));
      positions.add(new DefaultFoldRange(72, 85));
      positions.add(new DefaultFoldRange(158, 86));
      positions.add(new DefaultFoldRange(245, 86));
      positions.add(new DefaultFoldRange(332, 86));
      positions.add(new DefaultFoldRange(419, 86));
      return positions;
   }

}
