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
package org.exoplatform.ide.text.store;

import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.ILineTracker;
import org.exoplatform.ide.text.ITextStore;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class DocumentTextStore extends TextStore implements ITextStore
{

   private ILineTracker lineTracker;

   /**
    * 
    */
   public DocumentTextStore(ILineTracker lineTracker)
   {
      this.lineTracker = lineTracker;
   }

   /**
    * @see org.exoplatform.ide.text.ITextStore#get(int)
    */
   @Override
   public char get(int offset)
   {
      return get(offset, 1).charAt(0);
   }

   /**
    * @see org.exoplatform.ide.text.ITextStore#get(int, int)
    */
   @Override
   public String get(int offset, int length)
   {
      try
      {
         int lineNumber = lineTracker.getLineNumberOfOffset(offset);
         LineInfo line = getLineFinder().findLine(lineNumber);
         int lineOffset = lineTracker.getLineOffset(lineNumber);
         return getText(line.line(), offset - lineOffset, length);
         
      }
      catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return "";
   }

   /**
    * @see org.exoplatform.ide.text.ITextStore#getLength()
    */
   @Override
   public int getLength()
   {
      int lenght = 0;
      for (Line line = getFirstLine(); line != null; line = line.getNextLine()) {
         lenght += line.getText().length();
       }
      return lenght;
   }

   /**
    * @see org.exoplatform.ide.text.ITextStore#replace(int, int, java.lang.String)
    */
   @Override
   public void replace(int offset, int length, String text)
   {

      try
      {
         int lineNumber = lineTracker.getLineNumberOfOffset(offset);
         LineInfo line = getLineFinder().findLine(lineNumber);
         int lineOffset = lineTracker.getLineOffset(lineNumber);
         if(length != 0)
            deleteText(line.line(), lineNumber, offset - lineOffset, length);
         if (text == null)
            return;
         insertText(line.line(), lineNumber, offset - lineOffset, text);
      }
      catch (BadLocationException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.text.ITextStore#set(java.lang.String)
    */
   @Override
   public void set(String text)
   {
      deleteText(getFirstLine(), 0, getLength());
      insertText(getFirstLine(), 0, 0, text);
   }

}
