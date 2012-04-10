/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Cagatay Calli <ccalli@gmail.com> - [find/replace] retain caps when replacing - https://bugs.eclipse.org/bugs/show_bug.cgi?id=28949
 *     Cagatay Calli <ccalli@gmail.com> - [find/replace] define & fix behavior of retain caps with other escapes and text before \C - https://bugs.eclipse.org/bugs/show_bug.cgi?id=217061
 *******************************************************************************/
package org.exoplatform.ide.editor.text;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import org.exoplatform.ide.editor.runtime.Assert;

import java.util.regex.PatternSyntaxException;

/**
 * Provides search and replace operations on {@link org.eclipse.jface.text.IDocument}.
 * <p>
 * Replaces {@link org.eclipse.jface.text.IDocument#search(int, String, boolean, boolean, boolean)}.
 * 
 * @since 3.0
 */
public class FindReplaceDocumentAdapter implements CharSequence
{

   /** Internal type for operation codes. */
   private static class FindReplaceOperationCode
   {
   }

   // Find/replace operation codes.
   private static final FindReplaceOperationCode FIND_FIRST = new FindReplaceOperationCode();

   private static final FindReplaceOperationCode FIND_NEXT = new FindReplaceOperationCode();

   private static final FindReplaceOperationCode REPLACE = new FindReplaceOperationCode();

   private static final FindReplaceOperationCode REPLACE_FIND_NEXT = new FindReplaceOperationCode();

   /** The adapted document. */
   private IDocument fDocument;

   /** State for findReplace. */
   private FindReplaceOperationCode fFindReplaceState = null;

   /** The matcher used in findReplace. */
   private RegExp regExp;

   /** The match offset from the last findReplace call. */
   private int fFindReplaceMatchOffset;

   /**
    * Constructs a new find replace document adapter.
    * 
    * @param document the adapted document
    */
   public FindReplaceDocumentAdapter(IDocument document)
   {
      Assert.isNotNull(document);
      fDocument = document;
   }

   /**
    * Returns the location of a given string in this adapter's document based on a set of search criteria.
    * 
    * @param startOffset document offset at which search starts
    * @param findString the string to find
    * @param forwardSearch the search direction
    * @param caseSensitive indicates whether lower and upper case should be distinguished
    * @param wholeWord indicates whether the findString should be limited by white spaces as defined by Character.isWhiteSpace.
    *           Must not be used in combination with <code>regExSearch</code>.
    * @param regExSearch if <code>true</code> findString represents a regular expression Must not be used in combination with
    *           <code>wholeWord</code>.
    * @return the find or replace region or <code>null</code> if there was no match
    * @throws BadLocationException if startOffset is an invalid document offset
    * @throws PatternSyntaxException if a regular expression has invalid syntax
    */
   public IRegion find(int startOffset, String findString, boolean forwardSearch, boolean caseSensitive,
      boolean wholeWord, boolean regExSearch) throws BadLocationException
   {
      Assert.isTrue(!(regExSearch && wholeWord));

      // Adjust offset to special meaning of -1
      if (startOffset == -1 && forwardSearch)
         startOffset = 0;
      if (startOffset == -1 && !forwardSearch)
         startOffset = length() - 1;
      return findReplace(FIND_FIRST, startOffset, findString, null, true, caseSensitive, wholeWord);
   }

   /**
    * Stateful findReplace executes a FIND, REPLACE, REPLACE_FIND or FIND_FIRST operation. In case of REPLACE and REPLACE_FIND it
    * sends a <code>DocumentEvent</code> to all registered <code>IDocumentListener</code>.
    * 
    * @param startOffset document offset at which search starts this value is only used in the FIND_FIRST operation and otherwise
    *           ignored
    * @param findString the string to find this value is only used in the FIND_FIRST operation and otherwise ignored
    * @param replaceText the string to replace the current match this value is only used in the REPLACE and REPLACE_FIND
    *           operations and otherwise ignored
    * @param forwardSearch the search direction
    * @param caseSensitive indicates whether lower and upper case should be distinguished
    * @param wholeWord indicates whether the findString should be limited by white spaces as defined by Character.isWhiteSpace.
    *           Must not be used in combination with <code>regExSearch</code>.
    * @param regExSearch if <code>true</code> this operation represents a regular expression Must not be used in combination with
    *           <code>wholeWord</code>.
    * @param operationCode specifies what kind of operation is executed
    * @return the find or replace region or <code>null</code> if there was no match
    * @throws BadLocationException if startOffset is an invalid document offset
    * @throws IllegalStateException if a REPLACE or REPLACE_FIND operation is not preceded by a successful FIND operation
    * @throws PatternSyntaxException if a regular expression has invalid syntax
    */
   private IRegion findReplace(final FindReplaceOperationCode operationCode, int startOffset, String findString,
      String replaceText, boolean forwardSearch, boolean caseSensitive, boolean wholeWord) throws BadLocationException
   {
      // Validate state
      if ((operationCode == REPLACE || operationCode == REPLACE_FIND_NEXT)
         && (fFindReplaceState != FIND_FIRST && fFindReplaceState != FIND_NEXT))
         throw new IllegalStateException("illegal findReplace state: cannot replace without preceding find"); //$NON-NLS-1$

      if (operationCode == FIND_FIRST)
      {
         // Reset

         if (findString == null || findString.length() == 0)
            return null;

         // Validate start offset
         if (startOffset < 0 || startOffset >= length())
            throw new BadLocationException();

         String patternFlags = "g";

         if (caseSensitive)
            patternFlags += "i";

         if (wholeWord)
            findString = "\\b" + findString + "\\b"; //$NON-NLS-1$ //$NON-NLS-2$

         fFindReplaceMatchOffset = startOffset;
         regExp = RegExp.compile(findString, patternFlags);
         regExp.setLastIndex(fFindReplaceMatchOffset);
      }

      // Set state
      fFindReplaceState = operationCode;

      if (operationCode != REPLACE)
      {

         if (forwardSearch)
         {
            MatchResult matchResult = regExp.exec(String.valueOf(this));
            if (matchResult != null && matchResult.getGroupCount() > 0 && !matchResult.getGroup(0).isEmpty())
               return new Region(matchResult.getIndex(), matchResult.getGroup(0).length());
            return null;
         }
      }

      return null;
   }

   /**
    * Substitutes the previous match with the given text. Sends a <code>DocumentEvent</code> to all registered
    * <code>IDocumentListener</code>.
    * 
    * @param text the substitution text
    * @param regExReplace if <code>true</code> <code>text</code> represents a regular expression
    * @return the replace region or <code>null</code> if there was no match
    * @throws BadLocationException if startOffset is an invalid document offset
    * @throws IllegalStateException if a REPLACE or REPLACE_FIND operation is not preceded by a successful FIND operation
    * @throws PatternSyntaxException if a regular expression has invalid syntax
    * 
    * @see DocumentEvent
    * @see IDocumentListener
    */
   public IRegion replace(String text, boolean regExReplace) throws BadLocationException
   {
      // TODO
      // return findReplace(REPLACE, -1, null, text, false, false, false, regExReplace);
      return null;
   }

   // ---------- CharSequence implementation ----------

   /* @see java.lang.CharSequence#length() */
   public int length()
   {
      return fDocument.getLength();
   }

   /* @see java.lang.CharSequence#charAt(int) */
   public char charAt(int index)
   {
      try
      {
         return fDocument.getChar(index);
      }
      catch (BadLocationException e)
      {
         throw new IndexOutOfBoundsException();
      }
   }

   /* @see java.lang.CharSequence#subSequence(int, int) */
   public CharSequence subSequence(int start, int end)
   {
      try
      {
         return fDocument.get(start, end - start);
      }
      catch (BadLocationException e)
      {
         throw new IndexOutOfBoundsException();
      }
   }

   /* @see java.lang.Object#toString() */
   public String toString()
   {
      return fDocument.get();
   }

   /**
    * Escapes special characters in the string, such that the resulting pattern matches the given string.
    * 
    * @param string the string to escape
    * @return a regex pattern that matches the given string
    * @since 3.5
    */
   public static String escapeForRegExPattern(String string)
   {
      // implements https://bugs.eclipse.org/bugs/show_bug.cgi?id=44422

      StringBuffer pattern = new StringBuffer(string.length() + 16);
      int length = string.length();
      for (int i = 0; i < length; i++)
      {
         char ch = string.charAt(i);
         switch (ch)
         {
            case '\\' :
            case '(' :
            case ')' :
            case '[' :
            case ']' :
            case '{' :
            case '}' :
            case '.' :
            case '?' :
            case '*' :
            case '+' :
            case '|' :
            case '^' :
            case '$' :
               pattern.append('\\').append(ch);
               break;

            case '\r' :
               if (i + 1 < length && string.charAt(i + 1) == '\n')
                  i++;
               //$FALL-THROUGH$
            case '\n' :
               pattern.append("\\R"); //$NON-NLS-1$
               break;
            case '\t' :
               pattern.append("\\t"); //$NON-NLS-1$
               break;
            case '\f' :
               pattern.append("\\f"); //$NON-NLS-1$
               break;
            case 0x07 :
               pattern.append("\\a"); //$NON-NLS-1$
               break;
            case 0x1B :
               pattern.append("\\e"); //$NON-NLS-1$
               break;

            default :
               if (0 <= ch && ch < 0x20)
               {
                  pattern.append("\\x"); //$NON-NLS-1$
                  pattern.append(Integer.toHexString(ch).toUpperCase());
               }
               else
               {
                  pattern.append(ch);
               }
         }
      }
      return pattern.toString();
   }
}
