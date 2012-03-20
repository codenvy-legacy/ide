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
package org.exoplatform.ide.extension.heroku.server;

import org.exoplatform.ide.extension.heroku.shared.Stack;

/**
 * Heroku stack - deployment variable.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 28, 2011 4:09:51 PM anya $
 *
 */
public class StackBean implements Stack
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
   public StackBean()
   {
      
   }
   
   /**
    * @param name stack's name
    * @param current if <code>true</code> the stack is current for the application
    * @param beta if <code>true</code> the stack version is beta
    * @param requested f <code>true</code> the stack version is requested
    */
   public StackBean(String name, boolean current, boolean beta, boolean requested)
   {
      this.name = name;
      this.beta = beta;
      this.current = current;
      this.requested = requested;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#isBeta()
    */
   @Override
   public boolean isBeta()
   {
      return beta;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#setBeta(boolean)
    */
   @Override
   public void setBeta(boolean beta)
   {
      this.beta = beta;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#isCurrent()
    */ 
   @Override
   public boolean isCurrent()
   {
      return current;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#setCurrent(boolean)
    */
   @Override
   public void setCurrent(boolean current)
   {
      this.current = current;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#isRequested()
    */
   @Override
   public boolean isRequested()
   {
      return requested;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.shared.Stack#setRequested(boolean)
    */
   @Override
   public void setRequested(boolean requested)
   {
      this.requested = requested;
   }
}
