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
package org.exoplatform.ide.java.client.editor;

import org.exoplatform.ide.java.client.core.compiler.IProblem;
import org.exoplatform.ide.java.client.core.dom.AST;
import org.exoplatform.ide.java.client.core.dom.ASTNode;
import org.exoplatform.ide.java.client.core.dom.ASTParser;
import org.exoplatform.ide.java.client.core.dom.CompilationUnit;
import org.exoplatform.ide.java.client.internal.codeassist.ISearchRequestor;
import org.exoplatform.ide.java.client.internal.compiler.env.INameEnvironment;
import org.exoplatform.ide.java.client.internal.compiler.env.NameEnvironmentAnswer;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.texteditor.api.reconciler.DirtyRegion;
import org.exoplatform.ide.texteditor.api.reconciler.ReconcilingStrategy;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaReconcilerStrategy implements ReconcilingStrategy
{

   private Document document;
   
   

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDocument(Document document)
   {
      this.document = document;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void reconcile(DirtyRegion dirtyRegion, Region subRegion)
   {
      parse();
   }

   /**
    * 
    */
   private void parse()
   {

      ASTParser parser = ASTParser.newParser(AST.JLS3);
      parser.setSource(document.get());
      parser.setKind(ASTParser.K_COMPILATION_UNIT);
//      parser.setUnitName(file.getName().substring(0, file.getName().lastIndexOf('.')));
      parser.setResolveBindings(true);
      parser.setNameEnvironment(new INameEnvironment()
      {
         
         @Override
         public boolean isPackage(char[][] parentPackageName, char[] packageName)
         {
            // TODO Auto-generated method stub
            return false;
         }
         
         @Override
         public void findTypes(char[] qualifiedName, boolean b, boolean camelCaseMatch, int searchFor,
            ISearchRequestor requestor)
         {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public NameEnvironmentAnswer findType(char[] typeName, char[][] packageName)
         {
            // TODO Auto-generated method stub
            return null;
         }
         
         @Override
         public NameEnvironmentAnswer findType(char[][] compoundTypeName)
         {
            // TODO Auto-generated method stub
            return null;
         }
         
         @Override
         public void findPackages(char[] qualifiedName, ISearchRequestor requestor)
         {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void findExactTypes(char[] missingSimpleName, boolean b, int type, ISearchRequestor storage)
         {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void findConstructorDeclarations(char[] prefix, boolean camelCaseMatch, ISearchRequestor requestor)
         {
            // TODO Auto-generated method stub
            
         }
         
         @Override
         public void cleanup()
         {
            // TODO Auto-generated method stub
            
         }
      });
      ASTNode ast = parser.createAST();
      CompilationUnit unit = (CompilationUnit)ast;
      IProblem[] problems = unit.getProblems();
      for(IProblem p : problems)
      {
         System.out.println(p);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void reconcile(Region partition)
   {
      parse();
   }

}
