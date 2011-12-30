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
package org.exoplatform.ide.jdt.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelStatus;
import org.eclipse.jdt.core.IJavaModelStatusConstants;
import org.eclipse.jdt.internal.core.util.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z evgen $
 *
 */
public class JavaModelStatus implements IJavaModelStatus, IJavaModelStatusConstants
{
   /**
    * Singleton OK object
    */
   public static final IJavaModelStatus VERIFIED_OK = new JavaModelStatus(OK, OK, Messages.status_OK);

   /**
    * @param nestedCoreException
    */
   public JavaModelStatus(CoreException nestedCoreException)
   {
      // TODO Auto-generated constructor stub
   }

   /**
    * @param code
    * @param e
    */
   public JavaModelStatus(int code, Throwable e)
   {
      // TODO Auto-generated constructor stub
   }

   /**
    * @param ok
    * @param ok2
    * @param status_OK
    */
   public JavaModelStatus(int ok, int ok2, String status_OK)
   {
      // TODO Auto-generated constructor stub
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#getChildren()
    */
   @Override
   public IStatus[] getChildren()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#getCode()
    */
   @Override
   public int getCode()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#getException()
    */
   @Override
   public Throwable getException()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#getMessage()
    */
   @Override
   public String getMessage()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#getPlugin()
    */
   @Override
   public String getPlugin()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#getSeverity()
    */
   @Override
   public int getSeverity()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#isMultiStatus()
    */
   @Override
   public boolean isMultiStatus()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#isOK()
    */
   @Override
   public boolean isOK()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.runtime.IStatus#matches(int)
    */
   @Override
   public boolean matches(int severityMask)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaModelStatus#getElements()
    */
   @Override
   public IJavaElement[] getElements()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaModelStatus#getPath()
    */
   @Override
   public IPath getPath()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaModelStatus#getString()
    */
   @Override
   public String getString()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaModelStatus#isDoesNotExist()
    */
   @Override
   public boolean isDoesNotExist()
   {
      // TODO Auto-generated method stub
      return false;
   }

}
