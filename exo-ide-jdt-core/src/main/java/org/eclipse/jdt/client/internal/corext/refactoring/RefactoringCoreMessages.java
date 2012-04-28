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
package org.eclipse.jdt.client.internal.corext.refactoring;

import com.google.gwt.core.client.GWT;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface RefactoringCoreMessages extends Messages
{
   RefactoringCoreMessages INSTANCE = GWT.create(RefactoringCoreMessages.class);

   /**
    * @return
    */
   String StatementAnalyzer_do_body_expression();

   /**
    * @return
    */
   String StatementAnalyzer_for_initializer_expression();

   /**
    * @return
    */
   String StatementAnalyzer_for_expression_updater();

   /**
    * @return
    */
   String StatementAnalyzer_for_updater_body();

   /**
    * @return
    */
   String StatementAnalyzer_switch_statement();

   /**
    * @return
    */
   String StatementAnalyzer_synchronized_statement();

   /**
    * @return
    */
   String StatementAnalyzer_try_statement();

   /**
    * @return
    */
   String StatementAnalyzer_catch_argument();

   /**
    * @return
    */
   String StatementAnalyzer_while_expression_body();

   /**
    * @return
    */
   String StatementAnalyzer_beginning_of_selection();

   /**
    * @return
    */
   String StatementAnalyzer_end_of_selection();

   /**
    * @return
    */
   String CommentAnalyzer_starts_inside_comment();

   /**
    * @return
    */
   String CommentAnalyzer_ends_inside_comment();

   /**
    * @return
    */
   String CommentAnalyzer_internal_error();

   /**
    * @return
    */
   String CodeAnalyzer_array_initializer();

   /**
    * @return
    */
   String SurroundWithTryCatchAnalyzer_compile_errors();

   /**
    * @return
    */
   String SurroundWithTryCatchAnalyzer_doesNotCover();

   /**
    * @return
    */
   String SurroundWithTryCatchAnalyzer_doesNotContain();

   /**
    * @return
    */
   String SurroundWithTryCatchAnalyzer_onlyStatements();

   /**
    * @return
    */
   String SurroundWithTryCatchAnalyzer_cannotHandleSuper();

   /**
    * @return
    */
   String SurroundWithTryCatchAnalyzer_cannotHandleThis();

   /**
    * @return
    */
   String SurroundWithTryCatchRefactoring_name();

   /**
    * @return
    */
   String SurroundWithTryCatchRefactoring_notMultipleexceptions(); 

}
