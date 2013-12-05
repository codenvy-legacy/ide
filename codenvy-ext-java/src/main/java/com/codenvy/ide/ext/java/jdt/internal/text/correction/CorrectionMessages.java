/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Benjamin Muskalla <b.muskalla@gmx.net> - [quick fix] Quick fix for missing synchronized modifier - https://bugs.eclipse
 *     .org/bugs/show_bug.cgi?id=245250
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/** Helper class to get NLSed messages. */
public interface CorrectionMessages extends Messages {

    CorrectionMessages INSTANCE = GWT.create(CorrectionMessages.class);

    String FixCorrectionProposal_WarningAdditionalProposalInfo();

    String JavadocTagsSubProcessor_document_exception_description();

    String JavadocTagsSubProcessor_document_parameter_description();

    String LocalCorrectionsSubProcessor_renaming_duplicate_method(String string);

    String LocalCorrectionsSubProcessor_replacefieldaccesswithmethod_description(String string);

    String ModifierCorrectionSubProcessor_addstatic_description();

    String ModifierCorrectionSubProcessor_addstatictoparenttype_description();

    String ModifierCorrectionSubProcessor_addsynchronized_description();

    String ModifierCorrectionSubProcessor_changefieldmodifiertononstatic_description(String methodName);

    String ModifierCorrectionSubProcessor_changemodifiertostaticfinal_description();

    String ModifierCorrectionSubProcessor_overrides_deprecated_description();

    String ModifierCorrectionSubProcessor_remove_override();

    String ModifierCorrectionSubProcessor_removefinal_description();

    String ModifierCorrectionSubProcessor_removevolatile_description();

    String QuickAssistProcessor_convert_anonym_to_nested();

    String QuickAssistProcessor_convert_local_to_field_description();

    String QuickAssistProcessor_convert_to_message_format();

    String QuickAssistProcessor_convert_to_multiple_singletype_catch_blocks();

    String QuickAssistProcessor_convert_to_single_multicatch_block();

    String QuickAssistProcessor_convert_to_string_buffer_description(String mechanismName);

    String QuickAssistProcessor_exceptiontothrows_description();

    String QuickAssistProcessor_extract_to_constant_description();

    String QuickAssistProcessor_infer_diamond_description();

    String QuickAssistProcessor_inline_local_description();

//   String QuickAssistProcessor_name_extension_from_class();
//
//   String QuickAssistProcessor_name_extension_from_interface();

//   String SerialVersionHashOperation_computing_id();

//   String SerialVersionHashOperation_error_classnotfound();

//   String SerialVersionHashOperation_save_caption();

//   String SerialVersionHashOperation_save_message();

//   String SerialVersionDefaultProposal_message_default_info();

//   String SerialVersionHashProposal_message_generated_info();

//   String SerialVersionHashOperation_dialog_error_caption();

//   String SerialVersionHashOperation_dialog_error_message();

    String CorrectPackageDeclarationProposal_name();

//   String CorrectPackageDeclarationProposal_remove_description();

//   String CorrectPackageDeclarationProposal_add_description();

//   String CorrectPackageDeclarationProposal_change_description();

//   String ChangeCorrectionProposal_error_title();

//   String ChangeCorrectionProposal_error_message();

//   String ChangeCorrectionProposal_name_with_shortcut();

//   String CUCorrectionProposal_error_title();
//
//   String CUCorrectionProposal_error_message();

    String ReorgCorrectionsSubProcessor_renametype_description(String newTypeName);

//   String ReorgCorrectionsSubProcessor_renamecu_description();

//   String ReorgCorrectionsSubProcessor_movecu_default_description();

//   String ReorgCorrectionsSubProcessor_movecu_description();

    String ReorgCorrectionsSubProcessor_organizeimports_description();

//   String ReorgCorrectionsSubProcessor_addcp_project_description();

//   String ReorgCorrectionsSubProcessor_addcp_archive_description();

//   String ReorgCorrectionsSubProcessor_addcp_classfolder_description();

//   String ReorgCorrectionsSubProcessor_change_project_compliance_description(String requiredVersion);
//
//   String ReorgCorrectionsSubProcessor_change_workspace_compliance_description();

//   String ReorgCorrectionsSubProcessor_addcp_variable_description();

//   String ReorgCorrectionsSubProcessor_addcp_library_description();

    String LocalCorrectionsSubProcessor_surroundwith_trycatch_description();

    String LocalCorrectionsSubProcessor_surroundwith_trymulticatch_description();

    String LocalCorrectionsSubProcessor_add_missing_cases_description();

    String LocalCorrectionsSubProcessor_addthrows_description();

    String ClasspathFixProcessorDescriptor_error_processing_processors();

    String LocalCorrectionsSubProcessor_addadditionalcatch_description();

    String LocalCorrectionsSubProcessor_addadditionalmulticatch_description();

    String LocalCorrectionsSubProcessor_addexceptiontoexistingcatch_description();

    String LocalCorrectionsSubProcessor_addexceptionstoexistingcatch_description();

    String LocalCorrectionsSubProcessor_unnecessaryinstanceof_description();

    String LocalCorrectionsSubProcessor_unnecessarythrow_description();

    String LocalCorrectionsSubProcessor_classtointerface_description(String typeName);

    String LocalCorrectionsSubProcessor_externalizestrings_description();

    String LocalCorrectionsSubProcessor_extendstoimplements_description();

    String LocalCorrectionsSubProcessor_setparenteses_bitop_description();

    String LocalCorrectionsSubProcessor_uninitializedvariable_description();

    String LocalCorrectionsSubProcessor_removesemicolon_description();

    String LocalCorrectionsSubProcessor_removeunreachablecode_description();

    String LocalCorrectionsSubProcessor_removeunreachablecode_including_condition_description();

    String LocalCorrectionsSubProcessor_removeelse_description();

    String LocalCorrectionsSubProcessor_hiding_local_label(String string);

    String LocalCorrectionsSubProcessor_hiding_field_label(String string);

    String LocalCorrectionsSubProcessor_rename_var_label(String string);

    String LocalCorrectionsSubProcessor_hiding_argument_label(String string);

    String LocalCorrectionsSubProcessor_setparenteses_description(String string);

    String LocalCorrectionsSubProcessor_setparenteses_instanceof_description();

    String LocalCorrectionsSubProcessor_InferGenericTypeArguments();

    String LocalCorrectionsSubProcessor_InferGenericTypeArguments_description();

    String TypeMismatchSubProcessor_addcast_description(String castType);

    String TypeMismatchSubProcessor_changecast_description(String castType);

    String TypeMismatchSubProcessor_changereturntype_description(String string);

    String TypeMismatchSubProcessor_changereturnofoverridden_description(String string);

    String TypeMismatchSubProcessor_changereturnofimplemented_description(String string);

    String TypeMismatchSubProcessor_removeexceptions_description(String string);

    String TypeMismatchSubProcessor_addexceptions_description(String string, String string2);

    String TypeMismatchSubProcessor_incompatible_for_each_type_description(String string, String string2);

    String TypeMismatchSubProcessor_insertnullcheck_description();

    String RemoveDeclarationCorrectionProposal_removeunusedfield_description(String name);

    String RemoveDeclarationCorrectionProposal_removeunusedmethod_description(String name);

    String RemoveDeclarationCorrectionProposal_removeunusedconstructor_description(String name);

    String RemoveDeclarationCorrectionProposal_removeunusedtype_description(String name);

    String RemoveDeclarationCorrectionProposal_removeunusedvar_description(String name);

    String RenameRefactoringProposal_additionalInfo();

    String RenameRefactoringProposal_name();

    String ModifierCorrectionSubProcessor_changemodifiertostatic_description(String name);

    String ModifierCorrectionSubProcessor_changemodifiertononstatic_description(String name);

    String ModifierCorrectionSubProcessor_changemodifiertofinal_description(String string);

    String ModifierCorrectionSubProcessor_changemodifiertononfinal_description(String name);

    String ModifierCorrectionSubProcessor_changevisibility_description(String name, String string);

    String ModifierCorrectionSubProcessor_removeabstract_description();

    String ModifierCorrectionSubProcessor_removebody_description();

    String ModifierCorrectionSubProcessor_default();

    String ModifierCorrectionSubProcessor_addabstract_description(String string);

    String ModifierCorrectionSubProcessor_removenative_description();

    String ModifierCorrectionSubProcessor_addmissingbody_description();

    String ModifierCorrectionSubProcessor_setmethodabstract_description();

    String ModifierCorrectionSubProcessor_changemethodtononfinal_description(String methodLabel);

    String ModifierCorrectionSubProcessor_changeoverriddenvisibility_description(String methodLabel, String string);

    String ModifierCorrectionSubProcessor_changemethodvisibility_description(String string);

    String ModifierCorrectionSubProcessor_changemethodtononstatic_description(String methodLabel);

    String ModifierCorrectionSubProcessor_removeinvalidmodifiers_description(String methodName);

    String ReturnTypeSubProcessor_constrnamemethod_description();

    String ReturnTypeSubProcessor_voidmethodreturns_description(String string);

    String ReturnTypeSubProcessor_removereturn_description();

    String ReturnTypeSubProcessor_missingreturntype_description(String string);

    String ReturnTypeSubProcessor_wrongconstructorname_description(String constructorName);

    String ReturnTypeSubProcessor_changetovoid_description();

    String MissingReturnTypeCorrectionProposal_addreturnstatement_description();

    String MissingReturnTypeCorrectionProposal_changereturnstatement_description();

    String TypeArgumentMismatchSubProcessor_removeTypeArguments();

    String UnimplementedMethodsCorrectionProposal_description();

    String UnimplementedMethodsCorrectionProposal_enum_info();

    String UnimplementedMethodsCorrectionProposal_info_singular();

    String UnimplementedMethodsCorrectionProposal_info_plural(String string);

    String UnimplementedCodeFix_DependenciesErrorMessage();

    String UnimplementedCodeFix_DependenciesStatusMessage();

    String UnimplementedCodeFix_MakeAbstractFix_label();

    String UnimplementedCodeFix_TextEditGroup_label();

    String UnresolvedElementsSubProcessor_swaparguments_description(String string, String string2);

    String UnresolvedElementsSubProcessor_addargumentcast_description(String string, String castTypeName);

    String UnresolvedElementsSubProcessor_changemethod_description(String string);

    String UnresolvedElementsSubProcessor_changetoouter_description(String string);

    String UnresolvedElementsSubProcessor_changetomethod_description(String string);

    String UnresolvedElementsSubProcessor_createmethod_description(String sig);

    String UnresolvedElementsSubProcessor_createmethod_other_description(String sig, String string);

//   String UnresolvedElementsSubProcessor_createconstructor_description();

    String UnresolvedElementsSubProcessor_changetype_description(String simpleName, String packName);

    String UnresolvedElementsSubProcessor_changetype_nopack_description(String simpleName);

    String UnresolvedElementsSubProcessor_importtype_description(String simpleName, String packName);

    String UnresolvedElementsSubProcessor_changevariable_description(String currName);

    String UnresolvedElementsSubProcessor_createfield_description(String nameLabel);

    String UnresolvedElementsSubProcessor_createfield_other_description(String nameLabel, String string);

    String UnresolvedElementsSubProcessor_createlocal_description(String name);

    String UnresolvedElementsSubProcessor_createparameter_description(String string);

    String UnresolvedElementsSubProcessor_createconst_description(String nameLabel);

    String UnresolvedElementsSubProcessor_createenum_description(String nameLabel, String string);

    String UnresolvedElementsSubProcessor_createconst_other_description(String nameLabel, String string);

    String UnresolvedElementsSubProcessor_removestatement_description();

//   String UnresolvedElementsSubProcessor_changeparamsignature_description();

    String UnresolvedElementsSubProcessor_changemethodtargetcast_description();

//   String UnresolvedElementsSubProcessor_changeparamsignature_constr_description();

//   String UnresolvedElementsSubProcessor_swapparams_description();
//
//   String UnresolvedElementsSubProcessor_swapparams_constr_description();

    String UnresolvedElementsSubProcessor_removeparam_description(String signature, String typeNames);

    String UnresolvedElementsSubProcessor_removeparams_description(String signature, String typeNames);

    String UnresolvedElementsSubProcessor_removeparam_constr_description(String signature, String typeNames);

    String UnresolvedElementsSubProcessor_removeparams_constr_description(String signature, String typeNames);

    String UnresolvedElementsSubProcessor_addargument_description(String arg);

    String UnresolvedElementsSubProcessor_addarguments_description(String arg);

    String UnresolvedElementsSubProcessor_removeargument_description(String string);

    String UnresolvedElementsSubProcessor_removearguments_description(String string);

//   String UnresolvedElementsSubProcessor_addparam_description();
//
//   String UnresolvedElementsSubProcessor_addparams_description();
//
//   String UnresolvedElementsSubProcessor_addparam_constr_description();
//
//   String UnresolvedElementsSubProcessor_addparams_constr_description();

//   String UnresolvedElementsSubProcessor_importexplicit_description();

    String UnresolvedElementsSubProcessor_missingcastbrackets_description();

    String UnresolvedElementsSubProcessor_methodtargetcast2_description(String targetName);

    String UnresolvedElementsSubProcessor_changemethodtargetcast2_description(String targetName);

    String UnresolvedElementsSubProcessor_methodtargetcast_description();

    String UnresolvedElementsSubProcessor_arraychangetomethod_description(String currName);

    String UnresolvedElementsSubProcessor_arraychangetolength_description();

    String UnresolvedElementsSubProcessor_addnewkeyword_description();

    String JavadocTagsSubProcessor_addjavadoc_method_description();

    String JavadocTagsSubProcessor_addjavadoc_type_description();

    String JavadocTagsSubProcessor_addjavadoc_field_description();

    String JavadocTagsSubProcessor_addjavadoc_paramtag_description();

    String JavadocTagsSubProcessor_addjavadoc_throwstag_description();

    String JavadocTagsSubProcessor_addjavadoc_returntag_description();

    String JavadocTagsSubProcessor_addjavadoc_enumconst_description();

    String JavadocTagsSubProcessor_addjavadoc_allmissing_description();

    String JavadocTagsSubProcessor_qualifylinktoinner_description();

    String JavadocTagsSubProcessor_removetag_description();

    String NoCorrectionProposal_description();

//   String MarkerResolutionProposal_additionaldesc();

//   String NewCUCompletionUsingWizardProposal_createclass_description();
//
//   String NewCUCompletionUsingWizardProposal_createenum_description();
//
//   String NewCUCompletionUsingWizardProposal_createclass_inpackage_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerclass_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerenum_description();
//
//   String NewCUCompletionUsingWizardProposal_createannotation_description();

//   String NewCUCompletionUsingWizardProposal_createinnerclass_intype_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerenum_intype_description();
//
//   String NewCUCompletionUsingWizardProposal_createinterface_description();
//
//   String NewCUCompletionUsingWizardProposal_createinterface_inpackage_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerinterface_description();
//
//   String NewCUCompletionUsingWizardProposal_createenum_inpackage_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerannotation_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerinterface_intype_description();
//
//   String NewCUCompletionUsingWizardProposal_createinnerannotation_intype_description();
//
//   String NewCUCompletionUsingWizardProposal_createannotation_inpackage_description();
//
//   String NewCUCompletionUsingWizardProposal_createclass_info();
//
//   String NewCUCompletionUsingWizardProposal_createenum_info();
//
//   String NewCUCompletionUsingWizardProposal_createinterface_info();
//
//   String NewCUCompletionUsingWizardProposal_createannotation_info();

    String ConstructorFromSuperclassProposal_description(String string);

    String AssignToVariableAssistProposal_assigntolocal_description();

    String AssignToVariableAssistProposal_assigntofield_description();

    String AssignToVariableAssistProposal_assignparamtofield_description();

    String QuickAssistProcessor_catchclausetothrows_description();

    String QuickAssistProcessor_removecatchclause_description();

    String QuickAssistProcessor_removeexception_description();

    String QuickAssistProcessor_unwrap_ifstatement();

    String QuickAssistProcessor_unwrap_whilestatement();

    String QuickAssistProcessor_unwrap_forstatement();

    String QuickAssistProcessor_unwrap_dostatement();

    String QuickAssistProcessor_unwrap_trystatement();

    String QuickAssistProcessor_unwrap_anonymous();

    String QuickAssistProcessor_unwrap_block();

    String QuickAssistProcessor_unwrap_labeledstatement();

    String QuickAssistProcessor_unwrap_methodinvocation();

    String QuickAssistProcessor_unwrap_synchronizedstatement();

    String QuickAssistProcessor_splitdeclaration_description();

    String QuickAssistProcessor_joindeclaration_description();

    String QuickAssistProcessor_addfinallyblock_description();

    String QuickAssistProcessor_addelseblock_description();

    String QuickAssistProcessor_replacethenwithblock_description();

    String QuickAssistProcessor_replaceelsewithblock_description();

    String QuickAssistProcessor_replacethenelsewithblock_description();

    String QuickAssistProcessor_replacebodywithblock_description();

    String QuickAssistProcessor_invertequals_description();

    String QuickAssistProcessor_typetoarrayInitializer_description();

//   String QuickAssistProcessor_createmethodinsuper_description();

    String LinkedNamesAssistProposal_proposalinfo();

    String LinkedNamesAssistProposal_description();

//   String QuickTemplateProcessor_surround_label();

//   String NewCUCompletionUsingWizardProposal_dialogtitle();
//
//   String NewCUCompletionUsingWizardProposal_tooltip_enclosingtype();
//
//   String NewCUCompletionUsingWizardProposal_tooltip_package();

    String JavaCorrectionProcessor_addquote_description();

    String JavaCorrectionProcessor_error_quickfix_message();

    String JavaCorrectionProcessor_error_status();

    String JavaCorrectionProcessor_error_quickassist_message();

//   String JavaCorrectionProcessor_go_to_closest_using_menu();
//
//   String JavaCorrectionProcessor_go_to_closest_using_key();
//
//   String JavaCorrectionProcessor_go_to_original_using_menu();

//   String JavaCorrectionProcessor_go_to_original_using_key();

    String TaskMarkerProposal_description();

    String TypeChangeCompletionProposal_field_name(String string, String typeName);

    String TypeChangeCompletionProposal_variable_name(String string, String typeName);

    String TypeChangeCompletionProposal_param_name(String string, String typeName);

    String TypeChangeCompletionProposal_method_name(String string, String typeName);

    String ImplementInterfaceProposal_name(String string, String string2);

    String AddUnimplementedMethodsOperation_AddMissingMethod_group();

    String AdvancedQuickAssistProcessor_convertToIfElse_description();

    String AdvancedQuickAssistProcessor_inverseIf_description();

    String AdvancedQuickAssistProcessor_inverseBooleanVariable();

    String AdvancedQuickAssistProcessor_castAndAssign();

    String AdvancedQuickAssistProcessor_pullNegationUp();

    String AdvancedQuickAssistProcessor_joinIfSequence();

    String AdvancedQuickAssistProcessor_pickSelectedString();

    String AdvancedQuickAssistProcessor_negatedVariableName(String string);

    String AdvancedQuickAssistProcessor_pushNegationDown();

    String AdvancedQuickAssistProcessor_putConditionalExpressionInParentheses();

    String AdvancedQuickAssistProcessor_convertSwitchToIf();

    String AdvancedQuickAssistProcessor_convertIfElseToSwitch();

    String AdvancedQuickAssistProcessor_inverseIfContinue_description();

    String AdvancedQuickAssistProcessor_inverseIfToContinue_description();

    String AdvancedQuickAssistProcessor_exchangeInnerAndOuterIfConditions_description();

    String AdvancedQuickAssistProcessor_inverseConditions_description();

    String AdvancedQuickAssistProcessor_inverseConditionalExpression_description();

    String AdvancedQuickAssistProcessor_replaceIfWithConditional();

    String AdvancedQuickAssistProcessor_replaceConditionalWithIf();

    String AdvancedQuickAssistProcessor_joinWithOuter_description();

    String AdvancedQuickAssistProcessor_joinWithInner_description();

    String AdvancedQuickAssistProcessor_splitAndCondition_description();

    String AdvancedQuickAssistProcessor_joinWithOr_description();

    String AdvancedQuickAssistProcessor_splitOrCondition_description();

    String AdvancedQuickAssistProcessor_exchangeOperands_description();

    String AddTypeParameterProposal_method_label(String fTypeParamName, String string);

    String AddTypeParameterProposal_type_label(String fTypeParamName, String string);

    String LocalCorrectionsSubProcessor_externalizestrings_additional_info();

    String LocalCorrectionsSubProcessor_generate_hashCode_equals_additional_info();

    String LocalCorrectionsSubProcessor_generate_hashCode_equals_description();

    String AssignToVariableAssistProposal_assigntoexistingfield_description(String string);

//   String ReorgCorrectionsSubProcessor_50_compliance_operation();

//   String ReorgCorrectionsSubProcessor_no_required_jre_title();

//   String ReorgCorrectionsSubProcessor_no_required_jre_message();

//   String ReorgCorrectionsSubProcessor_required_compliance_changeworkspace_description();

//   String ReorgCorrectionsSubProcessor_required_compliance_changeproject_description();

//   String GetterSetterCorrectionSubProcessor_creategetterunsingencapsulatefield_description();

//   String GetterSetterCorrectionSubProcessor_encapsulate_field_error_message();

//   String GetterSetterCorrectionSubProcessor_additional_info();

//   String GetterSetterCorrectionSubProcessor_encapsulate_field_error_title();

    String GetterSetterCorrectionSubProcessor_replacewithgetter_description(String string);

    String GetterSetterCorrectionSubProcessor_replacewithsetter_description(String string);

//   String ReorgCorrectionsSubProcessor_50_compliance_changeProjectJREToDefault_description();
//
//   String ReorgCorrectionsSubProcessor_50_compliance_changeWorkspaceJRE_description();
//
//   String ReorgCorrectionsSubProcessor_50_compliance_changeProjectJRE_description();

    String ModifierCorrectionSubProcessor_default_visibility_label();

//   String ReorgCorrectionsSubProcessor_configure_buildpath_label();

//   String ReorgCorrectionsSubProcessor_configure_buildpath_description();

    String QuickAssistProcessor_extract_to_local_all_description();

    String QuickAssistProcessor_extract_to_local_description();

    String QuickAssistProcessor_extractmethod_description();

    String QuickAssistProcessor_move_exception_to_separate_catch_block();

    String QuickAssistProcessor_move_exceptions_to_separate_catch_block();

    String SuppressWarningsSubProcessor_suppress_warnings_label(String warningToken, String name);

    String ReorgCorrectionsSubProcessor_accessrules_description();

//   String ReorgCorrectionsSubProcessor_project_seup_fix_description();
//
//   String ReorgCorrectionsSubProcessor_project_seup_fix_info();

    String UnresolvedElementsSubProcessor_change_full_type_description(String string);

    String LocalCorrectionsSubProcessor_remove_allocated_description();

    String LocalCorrectionsSubProcessor_remove_redundant_superinterface();

    String LocalCorrectionsSubProcessor_remove_type_arguments();

    String LocalCorrectionsSubProcessor_return_allocated_description();

    String LocalCorrectionsSubProcessor_qualify_left_hand_side_description();

    String LocalCorrectionsSubProcessor_LocalCorrectionsSubProcessor_qualify_right_hand_side_description();

    String UnresolvedElementsSubProcessor_UnresolvedElementsSubProcessor_changetoattribute_description(String curr);

//   String UnresolvedElementsSubProcessor_UnresolvedElementsSubProcessor_createattribute_description();

    String MissingAnnotationAttributesProposal_add_missing_attributes_label();

    String FixCorrectionProposal_ErrorAdditionalProposalInfo();

    String FixCorrectionProposal_MultiFixChange_label();

    String FixCorrectionProposal_HitCtrlEnter_description();

    String FixCorrectionProposal_hitCtrlEnter_variable_description(int count);

    String LocalCorrectionsSubProcessor_insert_break_statement();

    String LocalCorrectionsSubProcessor_insert_fall_through();

    String LocalCorrectionsSubProcessor_override_hashCode_description();

    String LocalCorrectionsSubProcessor_throw_allocated_description();

    String SuppressWarningsSubProcessor_fix_suppress_token_label(String curr);

    String SuppressWarningsSubProcessor_remove_annotation_label(String string);

    String VarargsWarningsSubProcessor_add_safevarargs_label();

//   String VarargsWarningsSubProcessor_add_safevarargs_to_method_label();

    String VarargsWarningsSubProcessor_remove_safevarargs_label();
}
