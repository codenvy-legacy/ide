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
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class TokenHelper
{
   private char c; //the current character
   
   private int from; //The index of the start of the token.
   
   private int i = 0; //The index of the current character.
   
   private int length;
   
   private int n; //The number value.
   
   private char q; //The quote character.
   
   private String str; //The string value.
   
   private List<Token> result = new ArrayList<Token>(); //An array to hold the results.
   
   private String prefix;
   
   private String suffix;
   
   private String source;
   
   
   public TokenHelper(String prefix, String suffix)
   {
      this.prefix = prefix;
      this.suffix = suffix;
   }
   
   private Token make(EnumTokenType type, String value)
   {
      return new Token(type, value, from, i);
   }
   
   private void init()
   {
      if (prefix == null || prefix.length() < 1)
      {
         prefix = "<>+-&";
      }
      if (suffix == null || suffix.length() < 1)
      {
         suffix = "=>&:";
      }
   }
   
   public static List<Token> tokens(String source, String prefix, String suffix)
   {
      if (source == null || source.length() < 1)
      {
         return null;
      }
      
      //TODO:
      return null;
   }
   
   private void loop()
   {
      c = source.charAt(i);
      //TODO: пока не конец строки
      while (c != '\n')
      {
         from = i;
      }
      
      //Ignore whitespace
      if (c <= ' ')
      {
         i += 1;
         c = source.charAt(i);
      }
      else if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z')
      {
         str = String.valueOf(c);
         i += 1;
         for (;;)
         {
            c = source.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') 
                     || (c >= '0' && c <= '9') || (c == '_'))
            {
               str += String.valueOf(c);
               i += 1;
            }
            else
            {
               break;
            }
         }
         
         result.add(make(EnumTokenType.NAME, str));
      } 
   // number.

   // A number cannot start with a decimal point. It must start with a digit,
   // possibly '0'.
      else if (c >= '0' && c <= '9')
      {
         str = String.valueOf(c);
         i += 1;
      // Look for more digits.
         
         for (;;)
         {
            c = source.charAt(i);
            if (c < '0' || c > '9')
            {
               break;
            }
            i += 1;
            str += String.valueOf(c);
         }
         
         // Look for a decimal fraction part.
         if (c == '.')
         {
            i += 1;
            str += String.valueOf(c);
            
            for (;;)
            {
               c = source.charAt(i);
               if (c < '0' || c >'9')
               {
                  break;
               }
               i += 1;
               str += String.valueOf(c);
            }
         }
         
         // Look for an exponent part.
         if (c == 'e' || c == 'E')
         {
            i += 1;
            str += String.valueOf(c);
            c = source.charAt(i);
            if (c == '-' || c == '+')
            {
               i += 1;
               str += String.valueOf(c);
               c = source.charAt(i);
            }
            if (c < '0' || c > '9')
            {
               make(EnumTokenType.NUMBER, str).setError("Bad exponent");
            }
            else
            {
               while (c >= '0' && c <= '9')
               {
                  i += 1;
                  str += String.valueOf(c);
                  c = source.charAt(i);
               }
            }
         }
         
         // Make sure the next character is not a letter.
         if (c >= 'a' && c <= 'z')
         {
            str += String.valueOf(c);
            i += 1;
            make(EnumTokenType.NUMBER, str).setError("Bad number");
         }
      }
      
      
      
      
   }
   
}
