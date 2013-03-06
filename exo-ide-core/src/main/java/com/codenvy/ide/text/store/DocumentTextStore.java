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
package com.codenvy.ide.text.store;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.LineTracker;
import com.codenvy.ide.text.TextStore;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class DocumentTextStore extends DocumentModel implements TextStore
{

   private LineTracker lineTracker;

   /**
    * 
    */
   public DocumentTextStore(LineTracker lineTracker)
   {
      this.lineTracker = lineTracker;
   }

   /**
    * @see com.codenvy.ide.text.TextStore#get(int)
    */
   @Override
   public char get(int offset)
   {
      return get(offset, 1).charAt(0);
   }

   /**
    * @see com.codenvy.ide.text.TextStore#get(int, int)
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
    * @see com.codenvy.ide.text.TextStore#getLength()
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
    * @see com.codenvy.ide.text.TextStore#replace(int, int, java.lang.String)
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
    * @see com.codenvy.ide.text.TextStore#set(java.lang.String)
    */
   @Override
   public void set(String text)
   {
      deleteText(getFirstLine(), 0, getLength());
      insertText(getFirstLine(), 0, 0, text);
   }

}
