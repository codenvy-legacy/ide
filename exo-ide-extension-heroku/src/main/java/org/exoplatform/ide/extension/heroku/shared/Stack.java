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
package org.exoplatform.ide.extension.heroku.shared;

/**
 * Heroku stack - deployment variable.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 28, 2011 4:09:51 PM anya $
 *
 */
public class Stack
{
   /**
    * Stack's name.
    */
   private String name;

   /**
    * Beta version. If <code>true</code> the stack version is beta. 
    */
   private boolean beta;

   /**
    * If <code>true</code> the stack is current for the application.
    */
   private boolean current;

   /**
    * 
    */
   private boolean requested;

   /**
    * Default constructor.
    */
   public Stack()
   {
      
   }
   
   /**
    * @param name stack's name
    * @param current if <code>true</code> the stack is current for the application
    * @param beta if <code>true</code> the stack version is beta
    * @param requested f <code>true</code> the stack version is requested
    */
   public Stack(String name, boolean current, boolean beta, boolean requested)
   {
      this.name = name;
      this.beta = beta;
      this.current = current;
      this.requested = requested;
   }

   /**
    * @return the name stack's name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name stack's name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the beta, if <code>true</code> the stack version is beta
    */
   public boolean isBeta()
   {
      return beta;
   }

   /**
    * @param beta, if <code>true</code> the stack version is beta
    */
   public void setBeta(boolean beta)
   {
      this.beta = beta;
   }

   /**
    * @return the current if <code>true</code> the stack is current for the application
    */ 
   public boolean isCurrent()
   {
      return current;
   }

   /**
    * @param current if <code>true</code> the stack is current for the application
    */
   public void setCurrent(boolean current)
   {
      this.current = current;
   }

   /**
    * @return the requested
    */
   public boolean isRequested()
   {
      return requested;
   }

   /**
    * @param requested the requested to set
    */
   public void setRequested(boolean requested)
   {
      this.requested = requested;
   }
}
