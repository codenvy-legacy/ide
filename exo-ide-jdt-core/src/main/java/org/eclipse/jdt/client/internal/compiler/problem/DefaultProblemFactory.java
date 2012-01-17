/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.internal.compiler.problem;

import com.google.gwt.core.client.GWT;

import org.eclipse.jdt.client.core.compiler.CategorizedProblem;
import org.eclipse.jdt.client.core.compiler.CharOperation;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.internal.compiler.IProblemFactory;
import org.eclipse.jdt.client.internal.compiler.util.HashtableOfInt;
import org.eclipse.jdt.client.internal.compiler.util.Util;

public class DefaultProblemFactory implements IProblemFactory
{

   public Messages messageTemplates;

   private static HashtableOfInt DEFAULT_LOCALE_TEMPLATES;

   private final static char[] DOUBLE_QUOTES = "''".toCharArray(); //$NON-NLS-1$

   private final static char[] SINGLE_QUOTE = "'".toCharArray(); //$NON-NLS-1$

   private final static char[] FIRST_ARGUMENT = "{0}".toCharArray(); //$NON-NLS-1$

   public DefaultProblemFactory()
   {
      messageTemplates = GWT.create(Messages.class);
   }

   /**
    * Answer a new IProblem created according to the parameters value
    * <ul>
    * <li>originatingFileName the name of the file name from which the problem is originated
    * <li>problemId the problem id
    * <li>problemArguments the fully qualified arguments recorded inside the problem
    * <li>messageArguments the arguments needed to set the error message (shorter names than problemArguments ones)
    * <li>severity the severity of the problem
    * <li>startPosition the starting position of the problem
    * <li>endPosition the end position of the problem
    * <li>lineNumber the line on which the problem occured
    * </ul>
    * 
    * @param originatingFileName char[]
    * @param problemId int
    * @param problemArguments String[]
    * @param messageArguments String[]
    * @param severity int
    * @param startPosition int
    * @param endPosition int
    * @param lineNumber int
    * @return CategorizedProblem
    */
   public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments,
      String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber)
   {

      return new DefaultProblem(originatingFileName, this.getLocalizedMessage(problemId, messageArguments), problemId,
         problemArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
   }

   public CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments,
      int elaborationId, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber,
      int columnNumber)
   {
      return new DefaultProblem(originatingFileName, this.getLocalizedMessage(problemId, elaborationId,
         messageArguments), problemId, problemArguments, severity, startPosition, endPosition, lineNumber, columnNumber);
   }

   private final static String keyFromID(int id)
   {
      return "$" + id;
   }

   public final String getLocalizedMessage(int id, String[] problemArguments)
   {
      return getLocalizedMessage(id, 0, problemArguments);
   }

   public final String getLocalizedMessage(int id, int elaborationId, String[] problemArguments)
   {
      String rawMessage = messageTemplates.getString(keyFromID(id & IProblem.IgnoreCategoriesMask));
      if (rawMessage == null)
      {
         return "Unable to retrieve the error message for problem id: " //$NON-NLS-1$
            + (id & IProblem.IgnoreCategoriesMask) + ". Check compiler resources."; //$NON-NLS-1$
      }
      char[] message = rawMessage.toCharArray();
      if (elaborationId != 0)
      {
         String elaboration = messageTemplates.getString(keyFromID(elaborationId));
         if (elaboration == null)
         {
            return "Unable to retrieve the error message elaboration for elaboration id: " //$NON-NLS-1$
               + elaborationId + ". Check compiler resources."; //$NON-NLS-1$
         }
         message = CharOperation.replace(message, FIRST_ARGUMENT, elaboration.toCharArray());
      }

      // for compatibility with MessageFormat which eliminates double quotes in original message
      message = CharOperation.replace(message, DOUBLE_QUOTES, SINGLE_QUOTE);

      if (problemArguments == null)
      {
         return new String(message);
      }

      int length = message.length;
      int start = 0;
      int end = length;
      StringBuffer output = null;
      if ((id & IProblem.Javadoc) != 0)
      {
         output = new StringBuffer(10 + length + problemArguments.length * 20);
         output.append(messageTemplates.getString(keyFromID(IProblem.JavadocMessagePrefix
            & IProblem.IgnoreCategoriesMask)));
      }
      while (true)
      {
         if ((end = CharOperation.indexOf('{', message, start)) > -1)
         {
            if (output == null)
               output = new StringBuffer(length + problemArguments.length * 20);
            output.append(message, start, end - start);
            if ((start = CharOperation.indexOf('}', message, end + 1)) > -1)
            {
               try
               {
                  output.append(problemArguments[CharOperation.parseInt(message, end + 1, start - end - 1)]);
               }
               catch (NumberFormatException nfe)
               {
                  output.append(message, end + 1, start - end);
               }
               catch (ArrayIndexOutOfBoundsException e)
               {
                  return "Cannot bind message for problem (id: " //$NON-NLS-1$
                     + (id & IProblem.IgnoreCategoriesMask) + ") \"" //$NON-NLS-1$
                     + new String(message) + "\" with arguments: {" //$NON-NLS-1$
                     + Util.toString(problemArguments) + "}"; //$NON-NLS-1$
               }
               start++;
            }
            else
            {
               output.append(message, end, length);
               break;
            }
         }
         else
         {
            if (output == null)
            {
               return new String(message);
            }
            output.append(message, start, length - start);
            break;
         }
      }

      // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=120410
      return new String(output.toString());
   }

   /**
    * @param problem CategorizedProblem
    * @return String
    */
   public final String localizedMessage(CategorizedProblem problem)
   {
      return getLocalizedMessage(problem.getID(), problem.getArguments());
   }

}
