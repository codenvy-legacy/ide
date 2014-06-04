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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;

/**
 * QuickFixResolver 
 *
 * @author Evgen Vidolob
 */
public class QuickFixResolver {
    public static boolean hasCorrections(int problemId) {
        switch (problemId) {
            case IProblem.UnterminatedString:
            case IProblem.UnusedImport:
            case IProblem.DuplicateImport:
            case IProblem.CannotImportPackage:
            case IProblem.ConflictingImport:
            case IProblem.ImportNotFound:
            case IProblem.UndefinedMethod:
            case IProblem.UndefinedConstructor:
            case IProblem.ParameterMismatch:
            case IProblem.MethodButWithConstructorName:
            case IProblem.UndefinedField:
            case IProblem.UndefinedName:
            case IProblem.UnresolvedVariable:
            case IProblem.PublicClassMustMatchFileName:
            case IProblem.PackageIsNotExpectedPackage:
            case IProblem.UndefinedType:
            case IProblem.TypeMismatch:
            case IProblem.UnhandledException:
            case IProblem.UnhandledExceptionOnAutoClose:
            case IProblem.UnreachableCatch:
            case IProblem.InvalidCatchBlockSequence:
            case IProblem.InvalidUnionTypeReferenceSequence:
            case IProblem.VoidMethodReturnsValue:
            case IProblem.ShouldReturnValue:
            case IProblem.MissingReturnType:
            case IProblem.NonExternalizedStringLiteral:
            case IProblem.NonStaticAccessToStaticField:
            case IProblem.NonStaticAccessToStaticMethod:
            case IProblem.StaticMethodRequested:
            case IProblem.NonStaticFieldFromStaticInvocation:
            case IProblem.InstanceMethodDuringConstructorInvocation:
            case IProblem.InstanceFieldDuringConstructorInvocation:
            case IProblem.NotVisibleMethod:
            case IProblem.NotVisibleConstructor:
            case IProblem.NotVisibleType:
            case IProblem.NotVisibleField:
            case IProblem.BodyForAbstractMethod:
            case IProblem.AbstractMethodInAbstractClass:
            case IProblem.AbstractMethodMustBeImplemented:
            case IProblem.EnumAbstractMethodMustBeImplemented:
            case IProblem.AbstractMethodsInConcreteClass:
            case IProblem.AbstractMethodInEnum:
            case IProblem.EnumConstantMustImplementAbstractMethod:
            case IProblem.ShouldImplementHashcode:
            case IProblem.BodyForNativeMethod:
            case IProblem.OuterLocalMustBeFinal:
            case IProblem.UninitializedLocalVariable:
            case IProblem.UndefinedConstructorInDefaultConstructor:
            case IProblem.UnhandledExceptionInDefaultConstructor:
            case IProblem.NotVisibleConstructorInDefaultConstructor:
            case IProblem.AmbiguousType:
            case IProblem.UnusedPrivateMethod:
            case IProblem.UnusedPrivateConstructor:
            case IProblem.UnusedPrivateField:
            case IProblem.UnusedPrivateType:
            case IProblem.LocalVariableIsNeverUsed:
            case IProblem.ArgumentIsNeverUsed:
            case IProblem.MethodRequiresBody:
            case IProblem.NeedToEmulateFieldReadAccess:
            case IProblem.NeedToEmulateFieldWriteAccess:
            case IProblem.NeedToEmulateMethodAccess:
            case IProblem.NeedToEmulateConstructorAccess:
            case IProblem.SuperfluousSemicolon:
            case IProblem.UnnecessaryCast:
            case IProblem.UnnecessaryInstanceof:
            case IProblem.IndirectAccessToStaticField:
            case IProblem.IndirectAccessToStaticMethod:
            case IProblem.Task:
            case IProblem.UnusedMethodDeclaredThrownException:
            case IProblem.UnusedConstructorDeclaredThrownException:
            case IProblem.UnqualifiedFieldAccess:
            case IProblem.JavadocMissing:
            case IProblem.JavadocMissingParamTag:
            case IProblem.JavadocMissingReturnTag:
            case IProblem.JavadocMissingThrowsTag:
            case IProblem.JavadocUndefinedType:
            case IProblem.JavadocAmbiguousType:
            case IProblem.JavadocNotVisibleType:
            case IProblem.JavadocInvalidThrowsClassName:
            case IProblem.JavadocDuplicateThrowsClassName:
            case IProblem.JavadocDuplicateReturnTag:
            case IProblem.JavadocDuplicateParamName:
            case IProblem.JavadocInvalidParamName:
            case IProblem.JavadocUnexpectedTag:
            case IProblem.JavadocInvalidTag:
            case IProblem.NonBlankFinalLocalAssignment:
            case IProblem.DuplicateFinalLocalInitialization:
            case IProblem.FinalFieldAssignment:
            case IProblem.DuplicateBlankFinalFieldInitialization:
            case IProblem.AnonymousClassCannotExtendFinalClass:
            case IProblem.ClassExtendFinalClass:
            case IProblem.FinalMethodCannotBeOverridden:
            case IProblem.InheritedMethodReducesVisibility:
            case IProblem.MethodReducesVisibility:
            case IProblem.OverridingNonVisibleMethod:
            case IProblem.CannotOverrideAStaticMethodWithAnInstanceMethod:
            case IProblem.CannotHideAnInstanceMethodWithAStaticMethod:
            case IProblem.UnexpectedStaticModifierForMethod:
            case IProblem.LocalVariableHidingLocalVariable:
            case IProblem.LocalVariableHidingField:
            case IProblem.FieldHidingLocalVariable:
            case IProblem.FieldHidingField:
            case IProblem.ArgumentHidingLocalVariable:
            case IProblem.ArgumentHidingField:
            case IProblem.DuplicateField:
            case IProblem.DuplicateMethod:
            case IProblem.DuplicateTypeVariable:
            case IProblem.DuplicateNestedType:
            case IProblem.IllegalModifierForInterfaceMethod:
            case IProblem.IllegalModifierForInterface:
            case IProblem.IllegalModifierForClass:
            case IProblem.IllegalModifierForInterfaceField:
            case IProblem.IllegalModifierForMemberInterface:
            case IProblem.IllegalModifierForMemberClass:
            case IProblem.IllegalModifierForLocalClass:
            case IProblem.IllegalModifierForArgument:
            case IProblem.IllegalModifierForField:
            case IProblem.IllegalModifierForMethod:
            case IProblem.IllegalModifierForConstructor:
            case IProblem.IllegalModifierForVariable:
            case IProblem.IllegalModifierForEnum:
            case IProblem.IllegalModifierForEnumConstant:
            case IProblem.IllegalModifierForEnumConstructor:
            case IProblem.IllegalModifierForMemberEnum:
            case IProblem.UnexpectedStaticModifierForField:
            case IProblem.IllegalModifierCombinationFinalVolatileForField:
            case IProblem.IllegalVisibilityModifierForInterfaceMemberType:
            case IProblem.IncompatibleReturnType:
            case IProblem.IncompatibleExceptionInThrowsClause:
            case IProblem.NoMessageSendOnArrayType:
            case IProblem.InvalidOperator:
            case IProblem.MissingSerialVersion:
            case IProblem.UnnecessaryElse:
            case IProblem.SuperclassMustBeAClass:
            case IProblem.UseAssertAsAnIdentifier:
            case IProblem.UseEnumAsAnIdentifier:
            case IProblem.RedefinedLocal:
            case IProblem.RedefinedArgument:
            case IProblem.CodeCannotBeReached:
            case IProblem.DeadCode:
            case IProblem.InvalidUsageOfTypeParameters:
            case IProblem.InvalidUsageOfStaticImports:
            case IProblem.InvalidUsageOfForeachStatements:
            case IProblem.InvalidUsageOfTypeArguments:
            case IProblem.InvalidUsageOfEnumDeclarations:
            case IProblem.InvalidUsageOfVarargs:
            case IProblem.InvalidUsageOfAnnotations:
            case IProblem.InvalidUsageOfAnnotationDeclarations:
            case IProblem.FieldMissingDeprecatedAnnotation:
            case IProblem.OverridingDeprecatedMethod:
            case IProblem.MethodMissingDeprecatedAnnotation:
            case IProblem.TypeMissingDeprecatedAnnotation:
            case IProblem.MissingOverrideAnnotation:
            case IProblem.MissingOverrideAnnotationForInterfaceMethodImplementation:
            case IProblem.MethodMustOverride:
            case IProblem.MethodMustOverrideOrImplement:
            case IProblem.IsClassPathCorrect:
            case IProblem.MethodReturnsVoid:
            case IProblem.ForbiddenReference:
            case IProblem.DiscouragedReference:
            case IProblem.UnnecessaryNLSTag:
            case IProblem.AssignmentHasNoEffect:
            case IProblem.UnsafeTypeConversion:
            case IProblem.RawTypeReference:
            case IProblem.UnsafeRawMethodInvocation:
            case IProblem.RedundantSpecificationOfTypeArguments:
            case IProblem.UndefinedAnnotationMember:
            case IProblem.MissingValueForAnnotationMember:
            case IProblem.FallthroughCase:
            case IProblem.NonGenericType:
            case IProblem.UnhandledWarningToken:
            case IProblem.UnusedWarningToken:
            case IProblem.RedundantSuperinterface:
            case IProblem.JavadocInvalidMemberTypeQualification:
            case IProblem.IncompatibleTypesInForeach:
            case IProblem.MissingEnumConstantCase:
            case IProblem.MissingSynchronizedModifierInInheritedMethod:
            case IProblem.UnusedObjectAllocation:
            case IProblem.MethodCanBeStatic:
            case IProblem.MethodCanBePotentiallyStatic:
            case IProblem.AutoManagedResourceNotBelow17:
            case IProblem.MultiCatchNotBelow17:
            case IProblem.PolymorphicMethodNotBelow17:
            case IProblem.BinaryLiteralNotBelow17:
            case IProblem.UnderscoresInLiteralsNotBelow17:
            case IProblem.SwitchOnStringsNotBelow17:
            case IProblem.DiamondNotBelow17:
            case IProblem.PotentialHeapPollutionFromVararg:
            case IProblem.UnsafeGenericArrayForVarargs:
            case IProblem.SafeVarargsOnFixedArityMethod:
            case IProblem.SafeVarargsOnNonFinalInstanceMethod:
                return true;
            default:
//                return SuppressWarningsSubProcessor.hasSuppressWarningsProposal(problemId);
                return true;
        }
    }
}
