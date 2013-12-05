/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
