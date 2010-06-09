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
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class Token
{
   private String id;
   
   private EnumTokenType type;
   
   private String value;
   
   private int from;

   private int to;

   private EnumArity arity;
   
   private String error;

   /**
    * @return the from
    */
   public int getFrom()
   {
      return from;
   }

   /**
    * @param from the from to set
    */
   public void setFrom(int from)
   {
      this.from = from;
   }

   /**
    * @return the to
    */
   public int getTo()
   {
      return to;
   }

   /**
    * @param to the to to set
    */
   public void setTo(int to)
   {
      this.to = to;
   }

   /**
    * @return the value
    */
   public String getValue()
   {
      return value;
   }

   /**
    * @param value the value to set
    */
   public void setValue(String value)
   {
      this.value = value;
   }

   /**
    * @return the arity
    */
   public EnumArity getArity()
   {
      return arity;
   }

   /**
    * @param arity the arity to set
    */
   public void setArity(EnumArity arity)
   {
      this.arity = arity;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the error
    */
   public String getError()
   {
      return error;
   }

   /**
    * @param error the error to set
    */
   public void setError(String error)
   {
      this.error = error;
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
