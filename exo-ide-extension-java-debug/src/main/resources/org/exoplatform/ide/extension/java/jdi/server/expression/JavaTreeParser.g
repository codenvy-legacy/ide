/**
 * For more information see the head comment within the 'java.g' grammar file
 * that defines the input for this tree grammar.
 *
 * BSD licence
 * 
 * Copyright (c) 2007-2008 by HABELITZ Software Developments
 *
 * All rights reserved.
 * 
 * http://www.habelitz.com
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *  3. The name of the author may not be used to endorse or promote products
 *     derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY HABELITZ SOFTWARE DEVELOPMENTS ('HSD') ``AS IS'' 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL 'HSD' BE LIABLE FOR ANY DIRECT, INDIRECT, 
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, 
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
tree grammar JavaTreeParser;

options {
  backtrack    = true;
  memoize      = true;
  tokenVocab   = Java;
  ASTLabelType = CommonTree;
}

@treeparser::header {
package org.exoplatform.ide.extension.java.jdi.server.expression;

import org.exoplatform.ide.extension.java.jdi.server.Debugger;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.DoubleValue;
import com.sun.jdi.FloatValue;
import com.sun.jdi.LongValue;
import com.sun.jdi.BooleanValue;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.PrimitiveValue;
import com.sun.jdi.StackFrame;
import com.sun.jdi.StringReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import com.sun.jdi.VirtualMachine;
}

@treeparser::members {
int i = 0;

boolean mMessageCollectionEnabled = false;
private boolean mHasErrors = false;
List<String> mMessages;

private VirtualMachine vm;
private Debugger debugger;

public JavaTreeParser(TreeNodeStream input, VirtualMachine vm, Debugger debugger) {
	this(input);
	this.vm = vm;
	this.debugger = debugger;
}

/**
 *  Switches error message collection on or of.
 *
 *  The standard destination for parser error messages is <code>System.err</code>.
 *  However, if <code>true</code> gets passed to this method this default
 *  behaviour will be switched off and all error messages will be collected
 *  instead of written to anywhere.
 *
 *  The default value is <code>false</code>.
 *
 *  @param pNewState  <code>true</code> if error messages should be collected.
 */
public void enableErrorMessageCollection(boolean pNewState) {
	mMessageCollectionEnabled = pNewState;
	if (mMessages == null && mMessageCollectionEnabled) {
		mMessages = new ArrayList<String>();
	}
}

/**
 *  Collects an error message or passes the error message to <code>
 *  super.emitErrorMessage(...)</code>.
 *
 *  The actual behaviour depends on whether collecting error messages
 *  has been enabled or not.
 *
 *  @param pMessage  The error message.
 */
@Override
public void emitErrorMessage(String pMessage) {
	if (mMessageCollectionEnabled) {
		mMessages.add(pMessage);
	} else {
		super.emitErrorMessage(pMessage);
	}
}

/**
 *  Returns collected error messages.
 *
 *  @return  A list holding collected error messages or <code>null</code> if
 *           collecting error messages hasn't been enabled. Of course, this
 *           list may be empty if no error message has been emited.
 */
public List<String> getMessages() {
	return mMessages;
}

/**
 *  Tells if parsing a Java source has caused any error messages.
 *
 *  @return  <code>true</code> if parsing a Java source has caused at least one error message.
 */
public boolean hasErrors() {
	return mHasErrors;
}

private ThreadReference getCurrentThread() {
	ThreadReference thread = debugger.getCurrentThread();
	if (thread == null) {
		throw new ExpressionException("Target Java VM is not suspended. ");
	}
	return thread;
}

private StackFrame getCurrentFrame() {
	try {
		return getCurrentThread().frame(0);
	} catch (Exception e) { // TODO
		throw new ExpressionException(e.getMessage(), e);
	}
}

private Value booleanValue(String text) {
	return value(Boolean.parseBoolean(text));
}

/*
 private Value byteValue(String text) {
 return value(Byte.parseByte(text));
 }

 private Value shortValue(String text) {
 return value(Short.parseShort(text));
 }

 private Value intValue(String text) {
 return value(Integer.parseInt(text));
 }
 */

private Value longValue(String text) {
	if (text.length() > 2 && '0' == text.charAt(0) && 'x' == text.charAt(1)) {
		return value(Long.parseLong(text.substring(2), 16));
	} else if (text.length() > 1 && '0' == text.charAt(0)) {
		return value(Long.parseLong(text.substring(1), 8));
	}
	return value(Long.parseLong(text));
}

/*
 private Value floatValue(String text) {
 return value(Float.parseFloat(text));
 }
 */

private Value doubleValue(String text) {
	return value(Double.parseDouble(text));
}

private Value charValue(String text) {
	return value(text.charAt(0));
}

private Value stringValue(String text) {
	return value(text.substring(1, text.length() - 1));
}

//

private Value value(boolean v) {
	return vm.mirrorOf(v);
}

private Value value(byte v) {
	return vm.mirrorOf(v);
}

private Value value(short v) {
	return vm.mirrorOf(v);
}

private Value value(int v) {
	return vm.mirrorOf(v);
}

private Value value(long l) {
	return vm.mirrorOf(l);
}

private Value value(float v) {
	return vm.mirrorOf(v);
}

private Value value(double v) {
	return vm.mirrorOf(v);
}

private Value value(char v) {
	return vm.mirrorOf(v);
}

private Value value(String v) {
	return vm.mirrorOf(v);
}

//

private Value getField(String text) {
	StackFrame frame = getCurrentFrame();
	try {
		ObjectReference object = frame.thisObject();
		if (object == null) {
			ReferenceType type = frame.location().declaringType();
			Field field = type.fieldByName(text);
			if (field != null) {
				return type.getValue(field);
			}
		} else {
			Field field = object.referenceType().fieldByName(text);
			if (field != null) {
				return object.getValue(field);
			}
		}
	} catch (Exception e) {
		// TODO
		e.printStackTrace();
	}
	// TODO
	return null;
}

private Value getLocalVariable(String text) {
	StackFrame frame = getCurrentFrame();
	try {
		LocalVariable var = frame.visibleVariableByName(text);
		if (var != null) {
			return frame.getValue(var);
		}
	} catch (Exception e) {
		// TODO
		e.printStackTrace();
	}
	// TODO
	return null;
}

private Value getArrayElement(Value vArray, Value indx) {
	ThreadReference thread = getCurrentThread();
	if (!(vArray instanceof ArrayReference)) {
		throw new ExpressionException("Unable get element of array. " + vArray
				+ " is not an array. ");
	}
	ArrayReference array = (ArrayReference) vArray;
	try {
		return array.getValue(((PrimitiveValue) indx).intValue());
	} catch (IndexOutOfBoundsException e) {
		throw new ExpressionException(e.getMessage(), e);
	}
}

private Value operation(Value left, Value right, int op) {
	if (left instanceof StringReference || right instanceof StringReference) {
		if (PLUS == op) {
			return value(valueToString(left) + valueToString(right));
		}
	}

	if (left instanceof ObjectReference && right instanceof ObjectReference) {
	  boolean result;
	  switch (op) {
      case EQUAL :
        result = left.equals(right); 
        break;
      case NOT_EQUAL :
        result = !(left.equals(right)); 
        break;
      default :
        throw new ExpressionException("Unsupported operation " + tokenNames[op] + " for object. ");
	  }
	  return value(result);
	}
	
	if (left instanceof PrimitiveValue && right instanceof PrimitiveValue) {
	  if (left instanceof DoubleValue || right instanceof DoubleValue) {
	    double l = ((PrimitiveValue)left).doubleValue();
	    double r = ((PrimitiveValue)right).doubleValue();
	    double result;
	    switch (op) {
	      case PLUS :
	        result = l + r; 
	        break;
	      case MINUS :
	        result = l - r; 
	        break;
	      case STAR :
	        result = l * r; 
	        break;
	      case DIV :
	        result = l / r; 
	        break;
	      case MOD :
	        result = l \% r; 
	        break;
	      default :
	        throw new ExpressionException("Unsupported operation " + tokenNames[op] + ". ");
	    }
	    return value(result);
	  }
	  if (left instanceof FloatValue || right instanceof FloatValue) {
	    float l = ((PrimitiveValue)left).floatValue();
	    float r = ((PrimitiveValue)right).floatValue();
	    float result;
	    switch (op) {
	      case PLUS :
	        result = l + r; 
	        break;
	      case MINUS :
	        result = l - r; 
	        break;
	      case STAR :
	        result = l * r; 
	        break;
	      case DIV :
	        result = l / r; 
	        break;
	      case MOD :
	        result = l \% r; 
	        break;
	      default :
	        throw new ExpressionException("Unsupported operation " + tokenNames[op] + ". ");
	    }
	    return value(result);
	  }
	  if (left instanceof LongValue || right instanceof LongValue) {
	    long l = ((PrimitiveValue)left).longValue();
	    long r = ((PrimitiveValue)right).longValue();
	    long result;
	    switch (op) {
	      case PLUS :
	        result = l + r; 
	        break;
	      case MINUS :
	        result = l - r; 
	        break;
	      case STAR :
	        result = l * r; 
	        break;
	      case DIV :
	        result = l / r; 
	        break;
	      case MOD :
	        result = l \% r; 
	        break;
	      default :
	        throw new ExpressionException("Unsupported operation " + tokenNames[op] + ". ");
	    }
	    return value(result);
	  }
    int l = ((PrimitiveValue)left).intValue();
    int r = ((PrimitiveValue)right).intValue();
    int result;
    switch (op) {
      case PLUS :
        result = l + r; 
        break;
      case MINUS :
        result = l - r; 
        break;
      case STAR :
        result = l * r; 
        break;
      case DIV :
        result = l / r; 
        break;
      case MOD :
        result = l \% r; 
        break;
      default :
        throw new ExpressionException("Unsupported operation " + tokenNames[op] + " ");
    }
    return value(result);
	}
	return null;
}

private Value unaryOperation(Value v, int op) {
	if (!(v instanceof PrimitiveValue)) {
		throw new ExpressionException("Operation " + op
				+ " is not supported for " + v);
	}
	double d = ((PrimitiveValue) v).doubleValue();
	if (UNARY_PLUS == op) {
		return value(++d);
	} else if (UNARY_PLUS == op) {
		return value(--d);
	}
	return null;
}

private String valueToString(Value value) {
	if (value == null) {
		return "null";
	}
	if (value instanceof StringReference) {
		return ((StringReference) value).value();
	}
	return value.toString();
}

private Value invokeMethod(Value vObject, String name, List<Value> arguments) {
	ThreadReference thread = getCurrentThread();
	if (!(vObject instanceof ObjectReference)) {
		throw new ExpressionException("Unable invoke method " + name + ". "
				+ vObject + " is not an object. ");
	}
	ObjectReference object = (ObjectReference) vObject;
	try {
		ReferenceType type = object.referenceType();
		List<com.sun.jdi.Method> methods = type.methodsByName(name);
		com.sun.jdi.Method m = methods.get(0);
		return object.invokeMethod(thread, m, arguments, 0);
	} catch (Exception e) {
		e.printStackTrace(); // TODO
	}
	return null;
}
}

// Starting point for parsing a Java file.

javaSource
  :
  ^(JAVA_SOURCE annotationList packageDeclaration? importDeclaration* typeDeclaration*)
  ;

packageDeclaration
  :
  ^(PACKAGE qualifiedIdentifier)
  ;

importDeclaration
  :
  ^(IMPORT STATIC? qualifiedIdentifier DOTSTAR?)
  ;

typeDeclaration
  :
  ^(CLASS modifierList IDENT genericTypeParameterList? extendsClause? implementsClause? classTopLevelScope)
  |
  ^(INTERFACE modifierList IDENT genericTypeParameterList? extendsClause? interfaceTopLevelScope)
  |
  ^(ENUM modifierList IDENT implementsClause? enumTopLevelScope)
  |
  ^(AT modifierList IDENT annotationTopLevelScope)
  ;

extendsClause // actually 'type' for classes and 'type+' for interfaces, but this has
  // been resolved by the parser grammar.
  :
  ^(EXTENDS_CLAUSE type+)
  ;

implementsClause
  :
  ^(IMPLEMENTS_CLAUSE type+)
  ;

genericTypeParameterList
  :
  ^(GENERIC_TYPE_PARAM_LIST genericTypeParameter+)
  ;

genericTypeParameter
  :
  ^(IDENT bound?)
  ;

bound
  :
  ^(EXTENDS_BOUND_LIST type+)
  ;

enumTopLevelScope
  :
  ^(ENUM_TOP_LEVEL_SCOPE enumConstant+ classTopLevelScope?)
  ;

enumConstant
  :
  ^(IDENT annotationList arguments? classTopLevelScope?)
  ;

classTopLevelScope
  :
  ^(CLASS_TOP_LEVEL_SCOPE classScopeDeclarations*)
  ;

classScopeDeclarations
  :
  ^(CLASS_INSTANCE_INITIALIZER block)
  |
  ^(CLASS_STATIC_INITIALIZER block)
  |
  ^(FUNCTION_METHOD_DECL modifierList genericTypeParameterList? type IDENT formalParameterList arrayDeclaratorList? throwsClause? block?)
  |
  ^(VOID_METHOD_DECL modifierList genericTypeParameterList? IDENT formalParameterList throwsClause? block?)
  |
  ^(VAR_DECLARATION modifierList type variableDeclaratorList)
  |
  ^(CONSTRUCTOR_DECL modifierList genericTypeParameterList? formalParameterList throwsClause? block)
  | typeDeclaration
  ;

interfaceTopLevelScope
  :
  ^(INTERFACE_TOP_LEVEL_SCOPE interfaceScopeDeclarations*)
  ;

interfaceScopeDeclarations
  :
  ^(FUNCTION_METHOD_DECL modifierList genericTypeParameterList? type IDENT formalParameterList arrayDeclaratorList? throwsClause?)
  |
  ^(VOID_METHOD_DECL modifierList genericTypeParameterList? IDENT formalParameterList throwsClause?)
  // Interface constant declarations have been switched to variable
  // declarations by 'java.g'; the parser has already checked that
  // there's an obligatory initializer.
  |
  ^(VAR_DECLARATION modifierList type variableDeclaratorList)
  | typeDeclaration
  ;

variableDeclaratorList
  :
  ^(VAR_DECLARATOR_LIST variableDeclarator+)
  ;

variableDeclarator
  :
  ^(VAR_DECLARATOR variableDeclaratorId variableInitializer?)
  ;

variableDeclaratorId
  :
  ^(IDENT arrayDeclaratorList?)
  ;

variableInitializer
  :
  arrayInitializer
  | expression
  ;

arrayDeclarator
  :
  LBRACK RBRACK
  ;

arrayDeclaratorList
  :
  ^(ARRAY_DECLARATOR_LIST ARRAY_DECLARATOR*)
  ;

arrayInitializer
  :
  ^(ARRAY_INITIALIZER variableInitializer*)
  ;

throwsClause
  :
  ^(THROWS_CLAUSE qualifiedIdentifier+)
  ;

modifierList
  :
  ^(MODIFIER_LIST modifier*)
  ;

modifier
  :
  PUBLIC
  | PROTECTED
  | PRIVATE
  | STATIC
  | ABSTRACT
  | NATIVE
  | SYNCHRONIZED
  | TRANSIENT
  | VOLATILE
  | STRICTFP
  | localModifier
  ;

localModifierList
  :
  ^(LOCAL_MODIFIER_LIST localModifier*)
  ;

localModifier
  :
  FINAL
  | annotation
  ;

type
  :
  ^(
    TYPE
    (
      primitiveType
      | qualifiedTypeIdent
    )
    arrayDeclaratorList?
   )
  ;

qualifiedTypeIdent
  :
  ^(QUALIFIED_TYPE_IDENT typeIdent+)
  ;

typeIdent
  :
  ^(IDENT genericTypeArgumentList?)
  ;

primitiveType
  :
  BOOLEAN
  | CHAR
  | BYTE
  | SHORT
  | INT
  | LONG
  | FLOAT
  | DOUBLE
  ;

genericTypeArgumentList
  :
  ^(GENERIC_TYPE_ARG_LIST genericTypeArgument+)
  ;

genericTypeArgument
  :
  type
  |
  ^(QUESTION genericWildcardBoundType?)
  ;

genericWildcardBoundType
  :
  ^(EXTENDS type)
  |
  ^(SUPER type)
  ;

formalParameterList
  :
  ^(FORMAL_PARAM_LIST formalParameterStandardDecl* formalParameterVarargDecl?)
  ;

formalParameterStandardDecl
  :
  ^(FORMAL_PARAM_STD_DECL localModifierList type variableDeclaratorId)
  ;

formalParameterVarargDecl
  :
  ^(FORMAL_PARAM_VARARG_DECL localModifierList type variableDeclaratorId)
  ;

qualifiedIdentifier
  :
  IDENT
  |
  ^(DOT qualifiedIdentifier IDENT)
  ;

// ANNOTATIONS

annotationList
  :
  ^(ANNOTATION_LIST annotation*)
  ;

annotation
  :
  ^(AT qualifiedIdentifier annotationInit?)
  ;

annotationInit
  :
  ^(ANNOTATION_INIT_BLOCK annotationInitializers)
  ;

annotationInitializers
  :
  ^(ANNOTATION_INIT_KEY_LIST annotationInitializer+)
  |
  ^(ANNOTATION_INIT_DEFAULT_KEY annotationElementValue)
  ;

annotationInitializer
  :
  ^(IDENT annotationElementValue)
  ;

annotationElementValue
  :
  ^(ANNOTATION_INIT_ARRAY_ELEMENT annotationElementValue*)
  | annotation
  | expression
  ;

annotationTopLevelScope
  :
  ^(ANNOTATION_TOP_LEVEL_SCOPE annotationScopeDeclarations*)
  ;

annotationScopeDeclarations
  :
  ^(ANNOTATION_METHOD_DECL modifierList type IDENT annotationDefaultValue?)
  |
  ^(VAR_DECLARATION modifierList type variableDeclaratorList)
  | typeDeclaration
  ;

annotationDefaultValue
  :
  ^(DEFAULT annotationElementValue)
  ;

// STATEMENTS / BLOCKS

block
  :
  ^(BLOCK_SCOPE blockStatement*)
  ;

blockStatement
  :
  localVariableDeclaration
  | typeDeclaration
  | statement
  ;

localVariableDeclaration
  :
  ^(VAR_DECLARATION localModifierList type variableDeclaratorList)
  ;

statement
  :
  block
  |
  ^(ASSERT expression expression?)
  |
  ^(IF parenthesizedExpression statement statement?)
  |
  ^(FOR forInit forCondition forUpdater statement)
  |
  ^(FOR_EACH localModifierList type IDENT expression statement)
  |
  ^(WHILE parenthesizedExpression statement)
  |
  ^(DO statement parenthesizedExpression)
  |
  ^(TRY block catches? block?) // The second optional block is the optional finally block.
  |
  ^(SWITCH parenthesizedExpression switchBlockLabels)
  |
  ^(SYNCHRONIZED parenthesizedExpression block)
  |
  ^(RETURN expression?)
  |
  ^(THROW expression)
  |
  ^(BREAK IDENT?)
  |
  ^(CONTINUE IDENT?)
  |
  ^(LABELED_STATEMENT IDENT statement)
  | expression
  | SEMI // Empty statement.
  ;

catches
  :
  ^(CATCH_CLAUSE_LIST catchClause+)
  ;

catchClause
  :
  ^(CATCH formalParameterStandardDecl block)
  ;

switchBlockLabels
  :
  ^(SWITCH_BLOCK_LABEL_LIST switchCaseLabel* switchDefaultLabel? switchCaseLabel*)
  ;

switchCaseLabel
  :
  ^(CASE expression blockStatement*)
  ;

switchDefaultLabel
  :
  ^(DEFAULT blockStatement*)
  ;

forInit
  :
  ^(
    FOR_INIT
    (
      localVariableDeclaration
      | expression*
    )?
   )
  ;

forCondition
  :
  ^(FOR_CONDITION expression?)
  ;

forUpdater
  :
  ^(FOR_UPDATE expression*)
  ;

// EXPRESSIONS

eval
  :
  a=expression 
              {
               System.out.printf("RESULT: \%s\%n", $a.value);
              }
  ;

parenthesizedExpression returns [Value value]
  :
  ^(PARENTESIZED_EXPR expression)
  
  {
   $value = $expression.value;
  }
  ;

expression returns [Value value]
  :
  ^(EXPR expr)
  
  {
   $value = $expr.value;
  }
  ;

expr returns [Value value]
  :
  ^(ASSIGN expr expr)
  |
  ^(PLUS_ASSIGN expr expr)
  |
  ^(MINUS_ASSIGN expr expr)
  |
  ^(STAR_ASSIGN expr expr)
  |
  ^(DIV_ASSIGN expr expr)
  |
  ^(AND_ASSIGN expr expr)
  |
  ^(OR_ASSIGN expr expr)
  |
  ^(XOR_ASSIGN expr expr)
  |
  ^(MOD_ASSIGN expr expr)
  |
  ^(BIT_SHIFT_RIGHT_ASSIGN expr expr)
  |
  ^(SHIFT_RIGHT_ASSIGN expr expr)
  |
  ^(SHIFT_LEFT_ASSIGN expr expr)
  |
  ^(QUESTION expr expr expr)
  |
  ^(LOGICAL_OR expr expr)
  |
  ^(LOGICAL_AND expr expr)
  |
  ^(OR expr expr)
  |
  ^(XOR expr expr)
  |
  ^(AND expr expr)
  |
  ^(EQUAL a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $EQUAL.type);
  }
  |
  ^(NOT_EQUAL a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $NOT_EQUAL.type);
  }
  |
  ^(INSTANCEOF expr type)
  
  {
   throw new ExpressionException("Operation 'instanceof' is not supported yet. ");
  }
  |
  ^(LESS_OR_EQUAL expr expr)
  |
  ^(GREATER_OR_EQUAL expr expr)
  |
  ^(BIT_SHIFT_RIGHT expr expr)
  |
  ^(SHIFT_RIGHT expr expr)
  |
  ^(GREATER_THAN expr expr)
  |
  ^(SHIFT_LEFT expr expr)
  |
  ^(LESS_THAN expr expr)
  |
  ^(PLUS a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $PLUS.type);
  }
  |
  ^(MINUS a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $MINUS.type);
  }
  |
  ^(STAR a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $STAR.type);
  }
  |
  ^(DIV a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $DIV.type);
  }
  |
  ^(MOD a=expr b=expr)
  
  {
   $value = operation($a.value, $b.value, $MOD.type);
  }
  |
  ^(UNARY_PLUS expr)
  
  {
   throw new ExpressionException("Operation 'unary +' is not supported yet. ");
  }
  |
  ^(UNARY_MINUS expr)
  
  {
   throw new ExpressionException("Operation 'unary -' is not supported yet. ");
  }
  |
  ^(PRE_INC a=expr)
  
  {
   throw new ExpressionException("Operation '++' is not supported yet. ");
  }
  |
  ^(PRE_DEC expr)
  
  {
   throw new ExpressionException("Operation '--' is not supported yet. ");
  }
  |
  ^(POST_INC a=expr)
  
  {
   throw new ExpressionException("Operation '++' is not supported yet. ");
  }
  |
  ^(POST_DEC expr)
  
  {
   throw new ExpressionException("Operation '--' is not supported yet. ");
  }
  |
  ^(NOT expr)
  |
  ^(LOGICAL_NOT expr)
  |
  ^(CAST_EXPR type expr)
  
  {
   throw new ExpressionException("Operation 'cast' is not supported yet. ");
  }
  | primaryExpression 
                     {
                      $value = $primaryExpression.value;
                     }
  ;

primaryExpression returns [Value value, String method]
  :
  ^(
    DOT
    (
      e=primaryExpression 
                         {
                          $value = $e.value;
                         }
      (
        IDENT 
             {
              if ($start.getParent().getType() == METHOD_CALL) {
              	$method = $IDENT.text;
              } else {
              	$value = getField($IDENT.text);
              }
             }
        | THIS 
              {
               $value = getCurrentFrame().thisObject();
              }
        | SUPER
        | innerNewExpression 
                            {
                             throw new ExpressionException(
                             		"Unable create new instance. Operation not supported yet. ");
                            }
        | CLASS
      )
      | primitiveType CLASS
      | VOID CLASS
    )
   )
  | parenthesizedExpression 
                           {
                            $value = $parenthesizedExpression.value;
                           }
  | IDENT 
         {
          if ($start.getParent().getType() == METHOD_CALL) {
          	$method = $IDENT.text;
          } else {
          	$value = getLocalVariable($IDENT.text);
          	if ($value == null) {
          		$value = getField($IDENT.text);
          	}
          }
         }
  |
  ^(METHOD_CALL o=primaryExpression genericTypeArgumentList? arguments)
  
  {
   $value = invokeMethod($o.value, $o.method, $arguments.args);
  }
  | explicitConstructorCall 
                           {
                            throw new ExpressionException(
                            		"Unable create new instance. Operation not supported yet. ");
                           }
  |
  ^(ARRAY_ELEMENT_ACCESS arr=primaryExpression indx=expression)
  
  {
   $value = getArrayElement($arr.value, $indx.value);
  }
  | literal 
           {
            $value = $literal.value;
           }
  | newExpression 
                 {
                  throw new ExpressionException(
                  		"Unable create new instance. Operation not supported yet. ");
                 }
  | THIS 
        {
         $value = getCurrentFrame().thisObject();
        }
  | arrayTypeDeclarator
  | SUPER 
         {
          $value = getCurrentFrame().thisObject();
         }
  ;

explicitConstructorCall
  :
  ^(THIS_CONSTRUCTOR_CALL genericTypeArgumentList? arguments)
  |
  ^(SUPER_CONSTRUCTOR_CALL primaryExpression? genericTypeArgumentList? arguments)
  ;

arrayTypeDeclarator
  :
  ^(
    ARRAY_DECLARATOR
    (
      arrayTypeDeclarator
      | qualifiedIdentifier
      | primitiveType
    )
   )
  ;

newExpression
  :
  ^(
    STATIC_ARRAY_CREATOR
    (
      primitiveType newArrayConstruction
      | genericTypeArgumentList? qualifiedTypeIdent newArrayConstruction
    )
   )
  |
  ^(CLASS_CONSTRUCTOR_CALL genericTypeArgumentList? qualifiedTypeIdent arguments classTopLevelScope?)
  ;

innerNewExpression // something like 'InnerType innerType = outer.new InnerType();'
  :
  ^(CLASS_CONSTRUCTOR_CALL genericTypeArgumentList? IDENT arguments classTopLevelScope?)
  ;

newArrayConstruction
  :
  arrayDeclaratorList arrayInitializer
  | expression+ arrayDeclaratorList?
  ;

arguments returns [List < Value > args]
  :
  
  {
   $args = new ArrayList<Value>();
  }
  ^(
    ARGUMENT_LIST
    (
      e=expression 
                  {
                   args.add($e.value);
                  }
    )*
   )
  ;

literal returns [Value value]
  :
  HEX_LITERAL 
             {
              $value = longValue($HEX_LITERAL.text);
             }
  | OCTAL_LITERAL 
                 {
                  $value = longValue($OCTAL_LITERAL.text);
                 }
  | DECIMAL_LITERAL 
                   {
                    $value = longValue($DECIMAL_LITERAL.text);
                   }
  | FLOATING_POINT_LITERAL 
                          {
                           $value = doubleValue($FLOATING_POINT_LITERAL.text);
                          }
  | CHARACTER_LITERAL 
                     {
                      $value = charValue($CHARACTER_LITERAL.text);
                     }
  | STRING_LITERAL 
                  {
                   $value = stringValue($STRING_LITERAL.text);
                  }
  | TRUE 
        {
         $value = booleanValue($TRUE.text);
        }
  | FALSE 
         {
          $value = booleanValue($FALSE.text);
         }
  | NULL 
        {
         $value = null;
        }
  ;
