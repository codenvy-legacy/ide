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
package org.eclipse.jdt.client.env;

import org.eclipse.jdt.client.core.IJavaElement;
import org.eclipse.jdt.client.core.IPackageFragment;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.dom.Name;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class PackageFragment implements IPackageFragment
{

   
   private final String packageFragment;

   /**
    * @param fullyQualifiedName
    */
   public PackageFragment(String fullyQualifiedName)
   {
      this.packageFragment = Signature.getQualifier(fullyQualifiedName);
   }
   
   /**
    * 
    */
   public PackageFragment(Name name)
   {
      packageFragment = name.getFullyQualifiedName();
   }

   /**
    * @see org.eclipse.jdt.client.core.IJavaElement#getElementName()
    */
   @Override
   public String getElementName()
   {
      return packageFragment;
   }

   /**
    * @see org.eclipse.jdt.client.core.IJavaElement#getElementType()
    */
   @Override
   public int getElementType()
   {
      return IJavaElement.PACKAGE_FRAGMENT;
   }

}
