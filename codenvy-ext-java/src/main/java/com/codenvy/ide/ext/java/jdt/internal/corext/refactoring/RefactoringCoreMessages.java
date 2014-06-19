/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface RefactoringCoreMessages extends Messages {
    RefactoringCoreMessages INSTANCE = GWT.create(RefactoringCoreMessages.class);

    /** @return  */
    String StatementAnalyzer_do_body_expression();

    /** @return  */
    String StatementAnalyzer_for_initializer_expression();

    /** @return  */
    String StatementAnalyzer_for_expression_updater();

    /** @return  */
    String StatementAnalyzer_for_updater_body();

    /** @return  */
    String StatementAnalyzer_switch_statement();

    /** @return  */
    String StatementAnalyzer_synchronized_statement();

    /** @return  */
    String StatementAnalyzer_try_statement();

    /** @return  */
    String StatementAnalyzer_catch_argument();

    /** @return  */
    String StatementAnalyzer_while_expression_body();

    /** @return  */
    String StatementAnalyzer_beginning_of_selection();

    /** @return  */
    String StatementAnalyzer_end_of_selection();

    /** @return  */
    String CommentAnalyzer_starts_inside_comment();

    /** @return  */
    String CommentAnalyzer_ends_inside_comment();

    /** @return  */
    String CommentAnalyzer_internal_error();

    /** @return  */
    String CodeAnalyzer_array_initializer();

    /** @return  */
    String SurroundWithTryCatchAnalyzer_compile_errors();

    /** @return  */
    String SurroundWithTryCatchAnalyzer_doesNotCover();

    /** @return  */
    String SurroundWithTryCatchAnalyzer_doesNotContain();

    /** @return  */
    String SurroundWithTryCatchAnalyzer_onlyStatements();

    /** @return  */
    String SurroundWithTryCatchAnalyzer_cannotHandleSuper();

    /** @return  */
    String SurroundWithTryCatchAnalyzer_cannotHandleThis();

    /** @return  */
    String SurroundWithTryCatchRefactoring_name();

    /** @return  */
    String SurroundWithTryCatchRefactoring_notMultipleexceptions();

    /** @return  */
    String ASTData_update_imports();

    /** @return  */
    String PromoteTempToFieldRefactoring_name();

    /** @return  */
    String PromoteTempToFieldRefactoring_editName();

    /** @return  */
    String PromoteTempToFieldRefactoring_Name_conflict_with_field();


    /**
     * @param fFieldName
     * @param bindingLabel
     * @return
     */
    String PromoteTempToFieldRefactoring_Name_conflict(String fFieldName, String bindingLabel);

    /** @return  */
    String PromoteTempToFieldRefactoring_select_declaration();

    /** @return  */
    String PromoteTempToFieldRefactoring_method_parameters();

    /** @return  */
    String PromoteTempToFieldRefactoring_exceptions();

    /** @return  */
    String PromoteTempToFieldRefactoring_cannot_promote();

    /** @return  */
    String PromoteTempToFieldRefactoring_uses_type_declared_locally();

    /** @return  */
    String ExtractMethodAnalyzer_ambiguous_return_value();

}
