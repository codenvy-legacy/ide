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
   
   private String[] result; //An array to hold the results.
   
   private String prefix;
   
   private String suffix;
   
   
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
      prefix = "<>+-&";
      suffix = "=>&:";
   }
   
   public static Token[] tokens(String source, String prefix, String suffix)
   {
      //TODO:
      return null;
   }
   
}
