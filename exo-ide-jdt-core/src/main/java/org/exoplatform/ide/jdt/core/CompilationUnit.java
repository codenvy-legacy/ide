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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportContainer;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IProblemRequestor;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.compiler.util.SuffixConstants;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 34360 2009-07-22 23:58:59Z aheritier $
 *
 */
public class CompilationUnit implements ICompilationUnit, org.eclipse.jdt.internal.compiler.env.ICompilationUnit, SuffixConstants
{

   /**
    * @see org.eclipse.jdt.core.ITypeRoot#findPrimaryType()
    */
   @Override
   public IType findPrimaryType()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ITypeRoot#getElementAt(int)
    */
   @Override
   public IJavaElement getElementAt(int position) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#exists()
    */
   @Override
   public boolean exists()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getAncestor(int)
    */
   @Override
   public IJavaElement getAncestor(int ancestorType)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getAttachedJavadoc(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public String getAttachedJavadoc(IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

//   /**
//    * @see org.eclipse.jdt.core.IJavaElement#getCorrespondingResource()
//    */
//   @Override
//   public IResource getCorrespondingResource() throws JavaModelException
//   {
//      // TODO Auto-generated method stub
//      return null;
//   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getElementName()
    */
   @Override
   public String getElementName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getElementType()
    */
   @Override
   public int getElementType()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getHandleIdentifier()
    */
   @Override
   public String getHandleIdentifier()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getJavaModel()
    */
   @Override
   public IJavaModel getJavaModel()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getJavaProject()
    */
   @Override
   public IJavaProject getJavaProject()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getOpenable()
    */
   @Override
   public IOpenable getOpenable()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getParent()
    */
   @Override
   public IJavaElement getParent()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getPath()
    */
   @Override
   public IPath getPath()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#getPrimaryElement()
    */
   @Override
   public IJavaElement getPrimaryElement()
   {
      // TODO Auto-generated method stub
      return null;
   }

//   /**
//    * @see org.eclipse.jdt.core.IJavaElement#getResource()
//    */
//   @Override
//   public IResource getResource()
//   {
//      // TODO Auto-generated method stub
//      return null;
//   }
//
//   /**
//    * @see org.eclipse.jdt.core.IJavaElement#getSchedulingRule()
//    */
//   @Override
//   public ISchedulingRule getSchedulingRule()
//   {
//      // TODO Auto-generated method stub
//      return null;
//   }
//
//   /**
//    * @see org.eclipse.jdt.core.IJavaElement#getUnderlyingResource()
//    */
//   @Override
//   public IResource getUnderlyingResource() throws JavaModelException
//   {
//      // TODO Auto-generated method stub
//      return null;
//   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IJavaElement#isStructureKnown()
    */
   @Override
   public boolean isStructureKnown() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class adapter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IParent#getChildren()
    */
   @Override
   public IJavaElement[] getChildren() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IParent#hasChildren()
    */
   @Override
   public boolean hasChildren() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#close()
    */
   @Override
   public void close() throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#findRecommendedLineSeparator()
    */
   @Override
   public String findRecommendedLineSeparator() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#getBuffer()
    */
   @Override
   public IBuffer getBuffer() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#hasUnsavedChanges()
    */
   @Override
   public boolean hasUnsavedChanges() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#isConsistent()
    */
   @Override
   public boolean isConsistent() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#isOpen()
    */
   @Override
   public boolean isOpen()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#makeConsistent(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void makeConsistent(IProgressMonitor progress) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#open(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void open(IProgressMonitor progress) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.IOpenable#save(org.eclipse.core.runtime.IProgressMonitor, boolean)
    */
   @Override
   public void save(IProgressMonitor progress, boolean force) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ISourceReference#getSource()
    */
   @Override
   public String getSource() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ISourceReference#getSourceRange()
    */
   @Override
   public ISourceRange getSourceRange() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ISourceReference#getNameRange()
    */
   @Override
   public ISourceRange getNameRange() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICodeAssist#codeComplete(int, org.eclipse.jdt.core.CompletionRequestor, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void codeComplete(int offset, CompletionRequestor requestor, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ICodeAssist#codeComplete(int, org.eclipse.jdt.core.CompletionRequestor)
    */
   @Override
   public void codeComplete(int offset, CompletionRequestor requestor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ICodeAssist#codeSelect(int, int)
    */
   @Override
   public IJavaElement[] codeSelect(int offset, int length) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ISourceManipulation#copy(org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement, java.lang.String, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void copy(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
      IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ISourceManipulation#delete(boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void delete(boolean force, IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ISourceManipulation#move(org.eclipse.jdt.core.IJavaElement, org.eclipse.jdt.core.IJavaElement, java.lang.String, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void move(IJavaElement container, IJavaElement sibling, String rename, boolean replace,
      IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ISourceManipulation#rename(java.lang.String, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void rename(String name, boolean replace, IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.internal.compiler.env.IDependent#getFileName()
    */
   @Override
   public char[] getFileName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.internal.compiler.env.ICompilationUnit#getContents()
    */
   @Override
   public char[] getContents()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.internal.compiler.env.ICompilationUnit#getMainTypeName()
    */
   @Override
   public char[] getMainTypeName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.internal.compiler.env.ICompilationUnit#getPackageName()
    */
   @Override
   public char[][] getPackageName()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#becomeWorkingCopy(org.eclipse.jdt.core.IProblemRequestor, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void becomeWorkingCopy(IProblemRequestor problemRequestor, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#becomeWorkingCopy(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void becomeWorkingCopy(IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#commitWorkingCopy(boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public void commitWorkingCopy(boolean force, IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#createImport(java.lang.String, org.eclipse.jdt.core.IJavaElement, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IImportDeclaration createImport(String name, IJavaElement sibling, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#createImport(java.lang.String, org.eclipse.jdt.core.IJavaElement, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IImportDeclaration createImport(String name, IJavaElement sibling, int flags, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#createPackageDeclaration(java.lang.String, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IPackageDeclaration createPackageDeclaration(String name, IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#createType(java.lang.String, org.eclipse.jdt.core.IJavaElement, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public IType createType(String contents, IJavaElement sibling, boolean force, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#discardWorkingCopy()
    */
   @Override
   public void discardWorkingCopy() throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#findElements(org.eclipse.jdt.core.IJavaElement)
    */
   @Override
   public IJavaElement[] findElements(IJavaElement element)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getAllTypes()
    */
   @Override
   public IType[] getAllTypes() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getImport(java.lang.String)
    */
   @Override
   public IImportDeclaration getImport(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getImportContainer()
    */
   @Override
   public IImportContainer getImportContainer()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getImports()
    */
   @Override
   public IImportDeclaration[] getImports() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getPrimary()
    */
   @Override
   public ICompilationUnit getPrimary()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getPackageDeclaration(java.lang.String)
    */
   @Override
   public IPackageDeclaration getPackageDeclaration(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getPackageDeclarations()
    */
   @Override
   public IPackageDeclaration[] getPackageDeclarations() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getType(java.lang.String)
    */
   @Override
   public IType getType(String name)
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getTypes()
    */
   @Override
   public IType[] getTypes() throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getWorkingCopy(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public ICompilationUnit getWorkingCopy(IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#getWorkingCopy(org.eclipse.jdt.core.IProblemRequestor, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public ICompilationUnit getWorkingCopy(IProblemRequestor problemRequestor, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#hasResourceChanged()
    */
   @Override
   public boolean hasResourceChanged()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#isWorkingCopy()
    */
   @Override
   public boolean isWorkingCopy()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#reconcile(int, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public org.eclipse.jdt.core.dom.CompilationUnit reconcile(int astLevel, boolean forceProblemDetection,
      IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#reconcile(int, boolean, boolean, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public org.eclipse.jdt.core.dom.CompilationUnit reconcile(int astLevel, boolean forceProblemDetection,
      boolean enableStatementsRecovery, IProgressMonitor monitor) throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#reconcile(int, int, org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   public org.eclipse.jdt.core.dom.CompilationUnit reconcile(int astLevel, int reconcileFlags, IProgressMonitor monitor)
      throws JavaModelException
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.eclipse.jdt.core.ICompilationUnit#restore()
    */
   @Override
   public void restore() throws JavaModelException
   {
      // TODO Auto-generated method stub
      
   }

}
