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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.client.internal.compiler.env.NameEnvironmentAnswer;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Jan 13, 2012 3:10:43 PM evgen $
 *
 */
public class DummyNameEnvirement implements INameEnvironment
{

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[][])
    */
   @Override
   public NameEnvironmentAnswer findType(char[][] compoundTypeName)
   {
      System.out.println("DummyNameEnvirement.findType()");
      System.out.println(compoundTypeName);
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[], char[][])
    */
   @Override
   public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName)
   {
      // TODO Auto-generated method stub
      System.out.println("DummyNameEnvirement.findType()");
      System.out.println(typeName);
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#isPackage(char[][], char[])
    */
   @Override
   public boolean isPackage(char[][] parentPackageName, char[] packageName)
   {
      System.out.println("DummyNameEnvirement.isPackage()");
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#cleanup()
    */
   @Override
   public void cleanup()
   {
      // TODO Auto-generated method stub
      
   }

}
