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
package org.exoplatform.ide.java.client.core;

import org.exoplatform.ide.java.client.core.compiler.IProblem;
import org.exoplatform.ide.java.client.internal.compiler.impl.CompilerOptions;
import org.exoplatform.ide.java.client.internal.compiler.problem.ProblemReporter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CorrectionEngine
{

   /**
    * Return an array of strings which contains one entry per warning token
    * accepted by the <code>@SuppressWarnings</code> annotation. This array is
    * neither null nor empty, it contains at least the String <code>all</code>.
    * It should not be modified by the caller (please take a copy if modifications
    * are needed).<br>
    * <b>Note:</b> The tokens returned are not necessarily standardized across Java
    * compilers. If you were to use one of these tokens in a <code>@SuppressWarnings</code>
    * annotation in the Java source code, the effects (if any) may vary from
    * compiler to compiler.
    *
    * @return an array of strings which contains one entry per warning token
    *          accepted by the <code>@SuppressWarnings</code> annotation.
    */
   public static String[] getAllWarningTokens()
   {
      return CompilerOptions.warningTokens;
   }

   /**
    * Returns a token which can be used to suppress a given warning using
    * <code>@SuppressWarnings</code> annotation, for a given problem ID
    * ({@link IProblem }). If a particular problem is not suppressable,
    * <code>null</code> will be returned.
    * <p>
    * <b>Note:</b> <code>@SuppressWarnings</code> can only suppress warnings,
    * which means that if some problems got promoted to ERROR using custom compiler
    * settings ({@link IJavaProject#setOption(String, String)}), the
    * <code>@SuppressWarnings</code> annotation will be ineffective.
    * </p>
    * <p>
    * <b>Note:</b> <code>@SuppressWarnings</code> can be argumented with
    * <code>"all"</code> so as to suppress all possible warnings at once.
    * </p>
    * <p>
    * <b>Note:</b> The tokens returned are not necessarily standardized across Java
    * compilers. If you were to use one of these tokens in an @SuppressWarnings
    * annotation in the Java source code, the effects (if any) may vary from
    * compiler to compiler.
    * </p>
    * @param problemID
    *         the ID of a given warning to suppress
    * @return a String which can be used in <code>@SuppressWarnings</code> annotation,
    * or <code>null</code> if unable to suppress this warning.
    */
   public static String getWarningToken(int problemID)
   {
      int irritant = ProblemReporter.getIrritant(problemID);
      if (irritant != 0)
      {
         return CompilerOptions.warningTokenFromIrritant(irritant);
      }
      return null;
   }
}
