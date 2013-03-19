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
package com.google.collide.client.editor.folding;

import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IRegion;
import org.exoplatform.ide.editor.shared.text.Position;
import org.exoplatform.ide.editor.shared.text.Region;
import org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FoldRange.java Mar 18, 2013 12:18:43 PM azatsarynnyy $
 *
 */
public class FoldRange extends Position implements IProjectionPosition
{

   /**
    * Creates a new fold range with the given offset and length.
    * 
    * @param offset the position offset, must be >= 0
    * @param length the position length, must be >= 0
    */
   public FoldRange(int offset, int length)
   {
      super(offset, length);
   }

   /**
    * @see org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition#computeProjectionRegions(org.exoplatform.ide.editor.shared.text.IDocument)
    */
   @Override
   public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException
   {
//      IRegion info1 = document.getLineInformation(1);
//      IRegion info2 = document.getLineInformation(2);
//      IRegion info4 = document.getLineInformation(4);
//      IRegion info5 = document.getLineInformation(5);
//      IRegion info6 = document.getLineInformation(6);
//      
//      Region region1 = new Region(info1.getOffset(), info1.getLength()+1 + info2.getLength()+1);
//      Region region2 = new Region(info4.getOffset(), info4.getLength()+1 + info5.getLength()+1 + +info6.getLength()+1);
//      
//      return new Region[]{region1, region2};
      
      int firstProjectionLineLength = document.getLineLength(document.getLineOfOffset(offset));
      return new Region[]{new Region(offset + firstProjectionLineLength, length - firstProjectionLineLength)};
   }

   /**
    * @see org.exoplatform.ide.editor.shared.text.projection.IProjectionPosition#computeCaptionOffset(org.exoplatform.ide.editor.shared.text.IDocument)
    */
   @Override
   public int computeCaptionOffset(IDocument document) throws BadLocationException
   {
      // TODO Auto-generated method stub
      return 0;
   }

}
