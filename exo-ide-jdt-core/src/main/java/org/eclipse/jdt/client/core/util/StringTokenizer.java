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
package org.eclipse.jdt.client.core.util;

import java.util.NoSuchElementException;

public class StringTokenizer
{
   private int currentPosition;

   private int maxPosition;

   private String str;

   private String delimiters;

   private boolean retTokens;

   /**
    * Constructs a string tokenizer for the specified string. All characters in the <code>delim</code> argument are the delimiters
    * for separating tokens.
    * <p>
    * If the <code>returnTokens</code> flag is <code>true</code>, then the delimiter characters are also returned as tokens. Each
    * delimiter is returned as a string of length one. If the flag is <code>false</code>, the delimiter characters are skipped and
    * only serve as separators between tokens.
    * 
    * @param str a string to be parsed.
    * @param delim the delimiters.
    * @param returnTokens flag indicating whether to return the delimiters as tokens.
    */
   public StringTokenizer(String str, String delim, boolean returnTokens)
   {
      currentPosition = 0;
      this.str = str;
      maxPosition = str.length();
      delimiters = delim;
      retTokens = returnTokens;
   }

   /**
    * Constructs a string tokenizer for the specified string. The characters in the <code>delim</code> argument are the delimiters
    * for separating tokens. Delimiter characters themselves will not be treated as tokens.
    * 
    * @param str a string to be parsed.
    * @param delim the delimiters.
    */
   public StringTokenizer(String str, String delim)
   {
      this(str, delim, false);
   }

   /**
    * Constructs a string tokenizer for the specified string. The tokenizer uses the default delimiter set, which is
    * <code>"&#92;t&#92;n&#92;r&#92;f"</code>: the space character, the tab character, the newline character, the carriage-return
    * character, and the form-feed character. Delimiter characters themselves will not be treated as tokens.
    * 
    * @param str a string to be parsed.
    */
   public StringTokenizer(String str)
   {
      this(str, " \t\n\r\f", false);
   }

   /**
    * Skips delimiters.
    */
   private void skipDelimiters()
   {
      while (!retTokens && (currentPosition < maxPosition) && (delimiters.indexOf(str.charAt(currentPosition)) >= 0))
      {
         currentPosition++;
      }
   }

   /**
    * Tests if there are more tokens available from this tokenizer's string. If this method returns <tt>true</tt>, then a
    * subsequent call to <tt>nextToken</tt> with no argument will successfully return a token.
    * 
    * @return <code>true</code> if and only if there is at least one token in the string after the current position;
    *         <code>false</code> otherwise.
    */
   public boolean hasMoreTokens()
   {
      skipDelimiters();
      return (currentPosition < maxPosition);
   }

   /**
    * Returns the next token from this string tokenizer.
    * 
    * @return the next token from this string tokenizer.
    * @exception NoSuchElementException if there are no more tokens in this tokenizer's string.
    */
   public String nextToken()
   {
      skipDelimiters();

      if (currentPosition >= maxPosition)
      {
         throw new NoSuchElementException();
      }

      int start = currentPosition;
      while ((currentPosition < maxPosition) && (delimiters.indexOf(str.charAt(currentPosition)) < 0))
      {
         currentPosition++;
      }
      if (retTokens && (start == currentPosition) && (delimiters.indexOf(str.charAt(currentPosition)) >= 0))
      {
         currentPosition++;
      }
      return str.substring(start, currentPosition);
   }

   /**
    * Returns the next token in this string tokenizer's string. First, the set of characters considered to be delimiters by this
    * <tt>StringTokenizer</tt> object is changed to be the characters in the string <tt>delim</tt>. Then the next token in the
    * string after the current position is returned. The current position is advanced beyond the recognized token. The new
    * delimiter set remains the default after this call.
    * 
    * @param delim the new delimiters.
    * @return the next token, after switching to the new delimiter set.
    * @exception NoSuchElementException if there are no more tokens in this tokenizer's string.
    */
   public String nextToken(String delim)
   {
      delimiters = delim;
      return nextToken();
   }

   /**
    * Returns the same value as the <code>hasMoreTokens</code> method. It exists so that this class can implement the
    * <code>Enumeration</code> interface.
    * 
    * @return <code>true</code> if there are more tokens; <code>false</code> otherwise.
    * @see java.util.Enumeration
    * @see java.util.StringTokenizer#hasMoreTokens()
    */
   public boolean hasMoreElements()
   {
      return hasMoreTokens();
   }

   /**
    * Returns the same value as the <code>nextToken</code> method, except that its declared return value is <code>Object</code>
    * rather than <code>String</code>. It exists so that this class can implement the <code>Enumeration</code> interface.
    * 
    * @return the next token in the string.
    * @exception NoSuchElementException if there are no more tokens in this tokenizer's string.
    * @see java.util.Enumeration
    * @see java.util.StringTokenizer#nextToken()
    */
   public Object nextElement()
   {
      return nextToken();
   }

   /**
    * Calculates the number of times that this tokenizer's <code>nextToken</code> method can be called before it generates an
    * exception. The current position is not advanced.
    * 
    * @return the number of tokens remaining in the string using the current delimiter set.
    * @see java.util.StringTokenizer#nextToken()
    */
   public int countTokens()
   {
      int count = 0;
      int currpos = currentPosition;

      while (currpos < maxPosition)
      {
         /*
          * This is just skipDelimiters(); but it does not affect currentPosition.
          */
         while (!retTokens && (currpos < maxPosition) && (delimiters.indexOf(str.charAt(currpos)) >= 0))
         {
            currpos++;
         }

         if (currpos >= maxPosition)
         {
            break;
         }

         int start = currpos;
         while ((currpos < maxPosition) && (delimiters.indexOf(str.charAt(currpos)) < 0))
         {
            currpos++;
         }
         if (retTokens && (start == currpos) && (delimiters.indexOf(str.charAt(currpos)) >= 0))
         {
            currpos++;
         }
         count++;

      }
      return count;
   }
}