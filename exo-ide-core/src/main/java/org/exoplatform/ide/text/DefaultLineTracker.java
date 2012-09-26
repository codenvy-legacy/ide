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

/**
 * Standard implementation of {@link org.eclipse.LineTracker.text.ILineTracker}.
 * <p>
 * The line tracker considers the three common line delimiters which are '\n', '\r', '\r\n'.
 * <p>
 * This class is not intended to be subclassed.
 * </p>
 * 
 * @noextend This class is not intended to be subclassed by clients.
 */
public class DefaultLineTracker extends AbstractLineTracker
{

   /** The predefined delimiters of this tracker */
   public final static String[] DELIMITERS = {"\r", "\n", "\r\n"}; //$NON-NLS-3$ //$NON-NLS-1$ //$NON-NLS-2$

   /** A predefined delimiter information which is always reused as return value */
   private DelimiterInfo fDelimiterInfo = new DelimiterInfo();

   /** Creates a standard line tracker. */
   public DefaultLineTracker()
   {
   }

   /* @see org.eclipse.jface.text.ILineTracker#getLegalLineDelimiters() */
   public String[] getLegalLineDelimiters()
   {
      return new String[]{"\n"};
   }

   /*
    * @see org.eclipse.jface.text.AbstractLineTracker#nextDelimiterInfo(java.lang .String, int)
    */
   protected DelimiterInfo nextDelimiterInfo(String text, int offset)
   {

      char ch;
      int length = text.length();
      for (int i = offset; i < length; i++)
      {

         ch = text.charAt(i);
         if (ch == '\r')
         {

            if (i + 1 < length)
            {
               if (text.charAt(i + 1) == '\n')
               {
                  fDelimiterInfo.delimiter = DELIMITERS[2];
                  fDelimiterInfo.delimiterIndex = i;
                  fDelimiterInfo.delimiterLength = 2;
                  return fDelimiterInfo;
               }
            }

            fDelimiterInfo.delimiter = DELIMITERS[0];
            fDelimiterInfo.delimiterIndex = i;
            fDelimiterInfo.delimiterLength = 1;
            return fDelimiterInfo;

         }
         else if (ch == '\n')
         {

            fDelimiterInfo.delimiter = DELIMITERS[1];
            fDelimiterInfo.delimiterIndex = i;
            fDelimiterInfo.delimiterLength = 1;
            return fDelimiterInfo;
         }
      }

      return null;
   }
}
