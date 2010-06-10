/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class SimpleParser
{
   private static String FUNCTION = "function";

   private static String VAR = "var";

   private static String LEFT_BRACE = "{";

   private static String RIGHT_BRACE = "}";

   private static String END_LINE = "\n";

   private static int countLines;

   public static List<Token> parse(String text)
   {
      List<Token> tokens = new ArrayList<Token>();
      int countLines = 1;
      if (text.trim().length() <= 0)
      {
         return tokens;
      }

      List<String> words = Arrays.asList(text.split(" "));
      words = checkEndLines(words);

      System.out.println();
      System.out.println();
      System.out.println();

      for (int i = 0; i < words.size(); i++)
      {
         String word = words.get(i);
         if (word.equals(FUNCTION))
         {
            String name = ((i + 1) < words.size()) ? words.get(i + 1) : "";
            Token token = new Token(getFunctionName(name), EnumTokenType.FUNCTION, countLines);
            tokens.add(token);
         }
         else if (word.equals(VAR))
         {
            String name = ((i + 1) < words.size()) ? words.get(i + 1) : "";
            Token token = new Token(getVariableName(name), EnumTokenType.VARIABLE, countLines);
            tokens.add(token);
         }
         else if (word.equals(END_LINE))
         {
            countLines++;
         }
      }
      return tokens;
   }

   private static String getVariableName(String name){
      return (name.endsWith(";")) ? name.replaceAll(";", "") : name;
   }
   
   private static String getFunctionName(String word)
   {
      int index = word.indexOf("(");
      return (index < 0) ? word : word.substring(0, index);
   }

   private static List<String> checkEndLines(List<String> words)
   {
      List<String> newList = new ArrayList<String>();
      for (String word : words)
      {
         if (word.equals(END_LINE))
         {
            newList.add(word);
         }
         //phrase has end line symbol
         else if (word.indexOf(END_LINE) >= 0)
         {
            //Check start with end line , then add it.
            String[] wordsArray = word.split(END_LINE);
            if (word.startsWith(END_LINE))
            {
               newList.add(END_LINE);
            }
            for (int i = 0; i < wordsArray.length - 1; i++)
            {
               newList.add(wordsArray[i]);
               newList.add(END_LINE);
            }
            newList.add(wordsArray[wordsArray.length - 1]);
            //Check phrase ends with end line symbol, then add last word+end of line
            if (word.endsWith(END_LINE))
            {
               newList.add(END_LINE);
            }
         }
         else if (!word.equals("") && !word.equals(" "))
         {
            newList.add(word);
         }
      }
      return newList;
   }

}
