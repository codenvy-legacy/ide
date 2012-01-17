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

import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.client.internal.codeassist.ISearchRequestor;
import org.eclipse.jdt.client.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.client.internal.compiler.env.NameEnvironmentAnswer;
import org.eclipse.jdt.client.runtime.IProgressMonitor;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 13, 2012 3:10:43 PM evgen $
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
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#findType(char[], char[][])
    */
   @Override
   public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.client.internal.compiler.env.INameEnvironment#isPackage(char[][], char[])
    */
   @Override
   public boolean isPackage(char[][] parentPackageName, char[] packageName)
   {
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

   /**
    * @param qualifiedName
    * @param camelCaseMatch
    * @param completionEngine
    * @param monitor
    */
   public void findConstructorDeclarations(char[] qualifiedName, boolean camelCaseMatch,
      CompletionEngine completionEngine, IProgressMonitor monitor)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @param qualifiedName
    * @param completionEngine
    */
   public void findPackages(char[] qualifiedName, CompletionEngine completionEngine)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @param qualifiedName
    * @param b
    * @param camelCaseMatch
    * @param searchFor
    * @param completionEngine
    * @param monitor
    */
   public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
      CompletionEngine completionEngine, IProgressMonitor monitor)
   {
      // TODO Auto-generated method stub
   }

   /**
    * @param missingSimpleName
    * @param b
    * @param type
    * @param storage
    */
   public void findExactTypes(char[] missingSimpleName, boolean b, int type, ISearchRequestor storage)
   {
      // TODO Auto-generated method stub

   }

}
