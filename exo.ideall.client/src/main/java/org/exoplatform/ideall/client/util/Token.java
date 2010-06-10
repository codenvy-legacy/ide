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
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class Token
{
   private String name;

   private EnumTokenType type;

   private int line;

   private List<Token> tokens;

   public Token(String name, EnumTokenType type, int line)
   {
      this.type = type;
      this.name = name;
      this.line = line;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the line
    */
   public int getLine()
   {
      return line;
   }

   /**
    * @param line the line to set
    */
   public void setLine(int line)
   {
      this.line = line;
   }

   /**
    * @return the tokens
    */
   public List<Token> getTokens()
   {
      if (tokens == null)
      {
         tokens = new ArrayList<Token>();
      }
      return tokens;
   }

   /**
    * @return the type
    */
   public EnumTokenType getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(EnumTokenType type)
   {
      this.type = type;
   }

}
