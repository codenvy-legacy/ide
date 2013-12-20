/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.corext.fix;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface FixMessages extends Messages {

    FixMessages INSTANCE = GWT.create(FixMessages.class);

//   String CleanUpPostSaveListener_name();

//   String CleanUpPostSaveListener_SaveAction_ChangeName();

//   String CleanUpPostSaveListener_SlowCleanUpDialog_link();

//   String CleanUpPostSaveListener_SlowCleanUpDialog_title();

//   String CleanUpPostSaveListener_SlowCleanUpWarningDialog_explain();

//   String CleanUpPostSaveListener_unknown_profile_error_message();

//   String CleanUpRefactoring_checkingPostConditions_message();

//   String CleanUpRefactoring_clean_up_multi_chang_name();

//   String CleanUpRefactoring_could_not_retrive_profile();

//   String CleanUpRefactoring_Parser_Startup_message();

//   String CleanUpRefactoring_Refactoring_name();
//
//   String CleanUpRefactoring_ProcessingCompilationUnit_message();
//
//   String CleanUpRefactoring_Initialize_message();
//
//   String CleanUpRegistry_ErrorTabPage_description();
//
//   String CleanUpRegistry_ErrorTabPage_preview();
//
//   String CleanUpRegistry_UnknownInitializerKind_errorMessage();
//
//   String CleanUpRegistry_WrongKindForConfigurationUI_error();
//
//   String CleanUpRegistry_cleanUpAlwaysEnabled_error();
//
//   String CleanUpRegistry_cleanUpCreation_error();

    String CompilationUnitRewriteOperationsFix_nullChangeError(String string);

    String CodeStyleFix_change_name();

    String ControlStatementsFix_change_name();

    String ConvertIterableLoopOperation_RemoveUpdateExpression_Warning(String string);

    String ConvertIterableLoopOperation_RemoveUpdateExpressions_Warning();

    String ConvertIterableLoopOperation_semanticChangeWarning();

    String ExpressionsFix_add_parentheses_change_name();

    String ExpressionsFix_remove_parentheses_change_name();

    String ImportsFix_OrganizeImports_Description();

    String Java50Fix_add_annotations_change_name();

    String Java50Fix_add_type_parameters_change_name();
//
//   String PotentialProgrammingProblemsFix_add_id_change_name();
//
//   String PotentialProgrammingProblemsFix_calculatingUIDFailed_exception();
//
//   String PotentialProgrammingProblemsFix_calculatingUIDFailed_unknown();

    String SortMembersFix_Change_description();

    String SortMembersFix_Fix_description();

    String UnusedCodeFix_change_name();

    String UnusedCodeFix_RemoveFieldOrLocal_AlteredAssignments_preview_singular();

    String UnusedCodeFix_RemoveFieldOrLocal_AlteredAssignments_preview_plural(String string);

    String UnusedCodeFix_RemoveFieldOrLocal_description(String name);

    String UnusedCodeFix_RemoveFieldOrLocal_RemovedAssignments_preview_singular();

    String UnusedCodeFix_RemoveFieldOrLocal_RemovedAssignments_preview_plural(String string);

    String UnusedCodeFix_RemoveFieldOrLocalWithInitializer_description(String name);

    String UnusedCodeFix_RemoveMethod_description(String name);

    String UnusedCodeFix_RemoveConstructor_description(String name);

    String UnusedCodeFix_RemoveType_description(String name);

    String UnusedCodeFix_RemoveImport_description();

    String UnusedCodeFix_RemoveCast_description();

    String UnusedCodeFix_RemoveUnusedType_description();

    String UnusedCodeFix_RemoveUnusedConstructor_description();

    String UnusedCodeFix_RemoveUnusedPrivateMethod_description();

    String UnusedCodeFix_RemoveUnusedField_description();

    String UnusedCodeFix_RemoveUnusedVariabl_description();

//   String Java50Fix_AddMissingAnnotation_description();

    String Java50Fix_AddDeprecated_description();

    String Java50Fix_AddOverride_description();

    String Java50Fix_ConvertToEnhancedForLoop_description();

//   String Java50Fix_AddTypeArguments_description();

//   String Java50Fix_SerialVersion_default_description();

//   String Java50Fix_SerialVersion_hash_description();

//   String Java50Fix_InitializeSerialVersionId_subtask_description();
//
//   String Java50Fix_SerialVersion_CalculateHierarchy_description();

    String StringFix_AddRemoveNonNls_description();

    String StringFix_AddNonNls_description();

    String StringFix_RemoveNonNls_description();

    String CodeStyleFix_ChangeAccessToStatic_description(String string);

    String CodeStyleFix_QualifyWithThis_description(String nameLabel, String qualifierLabel);

    String CodeStyleFix_ChangeAccessToStaticUsingInstanceType_description(String string);

    String CodeStyleFix_ChangeStaticAccess_description(String string);

    String CodeStyleFix_ChangeIfToBlock_desription();

    String CodeStyleFix_ChangeElseToBlock_description();

    String CodeStyleFix_ChangeControlToBlock_description();

    String CodeStyleFix_removeThis_groupDescription();

    String CodeStyleFix_ChangeAccessUsingDeclaring_description();

    String CodeStyleFix_QualifyMethodWithDeclClass_description();

    String CodeStyleFix_QualifyFieldWithDeclClass_description();

    String SerialVersion_group_description();

    String ControlStatementsFix_removeIfBlock_proposalDescription();

    String ControlStatementsFix_removeElseBlock_proposalDescription();

    String ControlStatementsFix_removeIfElseBlock_proposalDescription();

    String ControlStatementsFix_removeBrackets_proposalDescription();

    String ExpressionsFix_addParanoiacParentheses_description();

    String ExpressionsFix_removeUnnecessaryParentheses_description();

    String VariableDeclarationFix_add_final_change_name();

    String VariableDeclarationFix_changeModifierOfUnknownToFinal_description();

    String VariableDeclarationFix_ChangeMidifiersToFinalWherPossible_description();

}
