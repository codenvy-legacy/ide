/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.jdt.internal.corext.util;

import com.codenvy.eclipse.jdt.core.ICompilationUnit;
import com.codenvy.eclipse.jdt.core.IJavaElement;
import com.codenvy.eclipse.jdt.core.JavaCore;
import com.codenvy.eclipse.jdt.core.search.SearchEngine;
import com.codenvy.eclipse.jdt.core.search.SearchMatch;
import com.codenvy.eclipse.jdt.core.search.SearchParticipant;
import com.codenvy.eclipse.jdt.core.search.SearchPattern;

public class SearchUtils
{

   /**
    * @param match the search match
    * @return the enclosing {@link com.codenvy.eclipse.jdt.core.IJavaElement}, or null iff none
    */
   public static IJavaElement getEnclosingJavaElement(SearchMatch match)
   {
      Object element = match.getElement();
      if (element instanceof IJavaElement)
      {
         return (IJavaElement)element;
      }
      else
      {
         return null;
      }
   }

   /**
    * @param match the search match
    * @return the enclosing {@link com.codenvy.eclipse.jdt.core.ICompilationUnit} of the given match, or null iff none
    */
   public static ICompilationUnit getCompilationUnit(SearchMatch match)
   {
      IJavaElement enclosingElement = getEnclosingJavaElement(match);
      if (enclosingElement != null)
      {
         if (enclosingElement instanceof ICompilationUnit)
         {
            return (ICompilationUnit)enclosingElement;
         }
         ICompilationUnit cu = (ICompilationUnit)enclosingElement.getAncestor(IJavaElement.COMPILATION_UNIT);
         if (cu != null)
         {
            return cu;
         }
      }

      IJavaElement jElement = JavaCore.create(match.getResource());
      if (jElement != null && jElement.exists() && jElement.getElementType() == IJavaElement.COMPILATION_UNIT)
      {
         return (ICompilationUnit)jElement;
      }
      return null;
   }

   public static SearchParticipant[] getDefaultSearchParticipants()
   {
      return new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()};
   }

   /**
    * Constant for use as matchRule in {@link com.codenvy.eclipse.jdt.core.search.SearchPattern#createPattern(com.codenvy.eclipse.jdt.core.IJavaElement, int, int)}
    * to get search behavior as of 3.1M3 (all generic instantiations are found).
    */
   public final static int GENERICS_AGNOSTIC_MATCH_RULE = SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE | SearchPattern.R_ERASURE_MATCH;

   /**
    * Returns whether the given pattern is a camel case pattern or not.
    * <em>Note: this method does not consider the
    * {@link com.codenvy.eclipse.jdt.core.search.SearchPattern#R_CAMELCASE_SAME_PART_COUNT_MATCH} variant.<em>
    *
    * @param pattern the pattern to inspect
    * @return whether it is a camel case pattern or not
    */
   public static boolean isCamelCasePattern(String pattern)
   {
      return SearchPattern.validateMatchRule(pattern,
         SearchPattern.R_CAMELCASE_MATCH) == SearchPattern.R_CAMELCASE_MATCH;
   }
}