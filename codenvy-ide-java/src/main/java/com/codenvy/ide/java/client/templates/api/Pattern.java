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

package com.codenvy.ide.java.client.templates.api;

import com.google.gwt.regexp.shared.SplitResult;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.regexp.shared.MatchResult;

import com.google.gwt.regexp.shared.RegExp;

/**
 * GWT adaptation of {@link java.util.regex.Pattern}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class Pattern
{

   /**
    * Declares that regular expressions should be matched across line borders.
    */
   public final static int MULTILINE = 1;

   /**
    * Declares that characters are matched reglardless of case.
    */
   public final static int CASE_INSENSITIVE = 2;

   private static RegExp createExpression(String pattern, int flags)
   {
      String sFlags = "";
      if ((flags & MULTILINE) != 0)
         sFlags += "m";
      if ((flags & CASE_INSENSITIVE) != 0)
         sFlags += "i";
      sFlags += "g";
      return RegExp.compile(pattern, sFlags);
   }

   private RegExp regExp;

   private final String pattern;

   private final int flags;

   public static Pattern compile(String pattern)
   {
      return new Pattern(pattern);
   }

   public static Pattern compile(String pattern, int flags)
   {
      return new Pattern(pattern, flags);
   }

   /**
    * Escape a provided string so that it will be interpreted as a literal in regular expressions. The current implementation does
    * escape each character even if not neccessary, generating verbose literals.
    * 
    * @param input
    * @return
    */
   public static String quote(String input)
   {
      String output = "";
      for (int i = 0; i < input.length(); i++)
      {
         output += "\\" + input.charAt(i);
      }
      return output;
   }

   /**
    * Class constructor
    * 
    * @param pattern Regular expression
    */
   public Pattern(String pattern)
   {
      this(pattern, 0);
   }

   /**
    * Class constructor
    * 
    * @param pattern Regular expression
    * @param flags
    */
   public Pattern(String pattern, int flags)
   {
      this.pattern = pattern;
      this.flags = flags;
      regExp = createExpression(pattern, flags);
   }

   /**
    * Create a matcher for this pattern and a given input character sequence
    * 
    * @param cs The input character sequence
    * @return A new matcher
    */
   public Matcher matcher(CharSequence cs)
   {
      // recreate regExp
      regExp = createExpression(pattern, flags);
      return new Matcher(this, cs);
   }

   private void _match(String text, List<String> matches)
   {
      MatchResult result = regExp.exec(text);
      if (result == null)
         return;
      for (int i = 0; i < result.getGroupCount(); i++)
         matches.add(result.getGroup(i));
   };

   private void _split(String input, List<String> results)
   {
      SplitResult parts = regExp.split(input);
      for (int i = 0; i < parts.length(); i++)
         results.add(parts.get(i));
   };

   /**
    * Split an input string by the pattern's regular expression
    * 
    * @param input
    * @return Array of strings
    */
   public String[] split(String input)
   {
      List<String> results = new ArrayList<String>();
      _split(input, results);
      String[] parts = new String[results.size()];
      for (int i = 0; i < results.size(); i++)
         parts[i] = (String)results.get(i);
      return parts;
   }

   /**
    * This method is borrowed from the JavaScript RegExp object. It parses a string and returns as an array any assignments to
    * parenthesis groups in the pattern's regular expression
    * 
    * @param text
    * @return Array of strings following java's Pattern convention for groups: Group 0 is the entire input string and the
    *         remaining groups are the matched parenthesis. In case nothing was matched an empty array is returned.
    */
   public String[] match(String text)
   {
      List<String> matches = new ArrayList<String>();
      _match(text, matches);
      String arr[] = new String[matches.size()];
      for (int i = 0; i < matches.size(); i++)
         arr[i] = matches.get(i);
      return arr;
   }

   /**
    * Determines wether a provided text matches the regular expression
    * 
    * @param text
    * @return
    */
   public MatchResult matches(String text)
   {
      return regExp.exec(text);
   }
}