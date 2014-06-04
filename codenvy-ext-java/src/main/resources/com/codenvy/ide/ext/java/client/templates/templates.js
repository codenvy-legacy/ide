/*
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 */
{
    "templates"
:
    [
        {
            "name": "for",
            "description": "iterate over array",
            "id": "org.eclipse.jdt.ui.templates.for_array",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "for (int ${index} = 0; ${index} < ${array}.length; ${index}++) {\n    ${line_selection}${cursor}\n}"
        },
        {
            "name": "for",
            "description": "iterate over array with temporary variable",
            "id": "org.eclipse.jdt.ui.templates.for_temp",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "for (int ${index} = 0; ${index} < ${array}.length; ${index}++) {\n    ${array_type} ${array_element} = ${array}[${index}];\n  ${cursor}\n}"
        },
        {
            "name": "for",
            "description": "iterate over collection",
            "id": "org.eclipse.jdt.ui.templates.for_collection",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "for (${iteratorType:newType(java.util.Iterator)} ${iterator} = ${collection}.iterator(); ${iterator}.hasNext(); ) {\n ${type:elemType(collection)} ${name:newName(type)} = (${type}) ${iterator}.next();\n    ${cursor} \n}"
        },
        {
            "name": "foreach",
            "description": "iterate over an array or Iterable",
            "id": "org.eclipse.jdt.ui.templates.for_iterable",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "for (${iterable_type} ${iterable_element} : ${iterable}) {\n  ${cursor}\n}"
        },
        {
            "name": "while",
            "description": "iterate with enumeration",
            "id": "org.eclipse.jdt.ui.templates.while_enumeration",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "while (${en:var(java.util.Enumeration)}.hasMoreElements()) {\n    ${type:argType(en)} ${elem:newName(type)} = (${type}) ${en}.nextElement();\n    ${cursor}\n}"
        },
        {
            "name": "while",
            "description": "iterate with iterator",
            "id": "org.eclipse.jdt.ui.templates.while_iterator",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "while (${it:var(java.util.Iterator)}.hasNext()) {\n    ${type:argType(it)} ${elem:newName(type)} = (${type}) ${it}.next();\n    ${cursor}\n}"
        },
        {
            "name": "do",
            "description": "do while statement",
            "id": "org.eclipse.jdt.ui.templates.do",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "do {\n    ${line_selection}${cursor}\n} while (${condition:var(boolean)});"
        },
        {
            "name": "switch",
            "description": "switch case statement",
            "id": "org.eclipse.jdt.ui.templates.switch",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "switch (${key}) {\n    case ${value}:\n        ${cursor}\n        break;\n    default:\n        break;\n}"
        },
        {
            "name": "if",
            "description": "if statement",
            "id": "org.eclipse.jdt.ui.templates.if",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "if (${condition:var(boolean)}) {\n    ${line_selection}${cursor}\n}"
        },
        {
            "name": "ifelse",
            "description": "if else statement",
            "id": "org.eclipse.jdt.ui.templates.ifelse",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "if (${condition:var(boolean)}) {\n    ${cursor}\n} else {\n\n}"
        },
        {
            "name": "elseif",
            "description": "else if block",
            "id": "org.eclipse.jdt.ui.templates.elseif",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "else if (${condition:var(boolean)}) {\n    ${cursor}\n}"
        },
        {
            "name": "else",
            "description": "else block",
            "id": "org.eclipse.jdt.ui.templates.else",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "else {\n    ${cursor}\n}"
        },
        {
            "name": "try",
            "description": "try catch block",
            "id": "org.eclipse.jdt.ui.templates.try",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "try {\n    ${line_selection}${cursor}\n} catch (${Exception} ${exception_variable_name}) {\n    // ${todo}: handle exception\n}"
        },
        {
            "name": "catch",
            "description": "catch block",
            "id": "org.eclipse.jdt.ui.templates.catch",
            "context": "java",
            "enabled": true,
            "autoinsert": false,
            "text": "catch (${Exception} ${exception_variable_name}) {\n    ${cursor}// ${todo}: handle exception\n}"
        },
        {
            "name": "main",
            "description": "main method",
            "id": "org.eclipse.jdt.ui.templates.main",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "public static void main(String[] args) {\n    ${cursor}\n}"
        },
        {
            "name": "public_method",
            "description": "public method",
            "id": "org.eclipse.jdt.ui.templates.public_method",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "public ${return_type} ${name}(${}) {\n    ${cursor}\n}"
        },
        {
            "name": "protected_method",
            "description": "protected method",
            "id": "org.eclipse.jdt.ui.templates.protected_method",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "protected ${return_type} ${name}(${}) {\n    ${cursor}\n}"
        },
        {
            "name": "private_method",
            "description": "private method",
            "id": "org.eclipse.jdt.ui.templates.private_method",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "private ${return_type} ${name}(${}) {\n    ${cursor}\n}"
        },
        {
            "name": "private_static_method",
            "description": "private static method",
            "id": "org.eclipse.jdt.ui.templates.private_static_method",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "private static ${return_type} ${name}(${}) {\n    ${cursor}\n}"
        },
        {
            "name": "instanceof",
            "description": "dynamic type test and cast",
            "id": "org.eclipse.jdt.ui.templates.instanceof",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "if (${name:var} instanceof ${type}) {\n    ${type} ${new_name} = (${type})${name};\n    ${cursor}\n}"
        },
        {
            "name": "cast",
            "description": "dynamic cast",
            "id": "org.eclipse.jdt.ui.templates.cast",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "${type} ${new_name} = (${type}) ${name};"
        },
        {
            "name": "new",
            "description": "create new object",
            "id": "org.eclipse.jdt.ui.templates.new",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "${type} ${name} = new ${type}(${arguments});"
        },
        {
            "name": "lazy",
            "description": "lazy creation",
            "id": "org.eclipse.jdt.ui.templates.lazy",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "if (${name:var} == null) {\n    ${name} = new ${type}(${arguments});\n    ${cursor}\n}\n\nreturn ${name};"
        },
        {
            "name": "toarray",
            "description": "convert collection to array",
            "id": "org.eclipse.jdt.ui.templates.toarray",
            "context": "java",
            "enabled": true,
            "autoinsert": false,
            "text": "(${type:elemType(collection)}[]) ${collection}.toArray(new ${type}[${collection}.size()])"
        },
        {
            "name": "sysout",
            "description": "print to standard out",
            "id": "org.eclipse.jdt.ui.templates.sysout",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": true,
            "text": "System.out.println(${word_selection}${});${cursor}"
        },
        {
            "name": "syserr",
            "description": "print to standard error",
            "id": "org.eclipse.jdt.ui.templates.syserr",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": true,
            "text": "System.err.println(${word_selection}${});${cursor}"
        },
        {
            "name": "systrace",
            "description": "print current method to standard out",
            "id": "org.eclipse.jdt.ui.templates.systrace",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": true,
            "text": "System.out.println(\"${enclosing_type}.${enclosing_method}()\");"
        },
        {
            "name": "<code>",
            "description": "<code></code>",
            "id": "org.eclipse.jdt.ui.templates.code_tag",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<code>${word_selection}${}</code>${cursor}"
        },
        {
            "name": "null",
            "description": "<code>null</code>",
            "id": "org.eclipse.jdt.ui.templates.code_tag_null",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<code>null</code>"
        },
        {
            "name": "true",
            "description": "<code>true</code>",
            "id": "org.eclipse.jdt.ui.templates.code_tag_true",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<code>true</code>"
        },
        {
            "name": "false",
            "description": "<code>false</code>",
            "id": "org.eclipse.jdt.ui.templates.code_tag_false",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<code>false</code>"
        },
        {
            "name": "<pre>",
            "description": "<pre></pre>",
            "id": "org.eclipse.jdt.ui.templates.pre_tag",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<pre>${word_selection}${}</pre>${cursor}"
        },
        {
            "name": "<b>",
            "description": "<b></b>",
            "id": "org.eclipse.jdt.ui.templates.b_tag",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<b>${word_selection}${}</b>${cursor}"
        },
        {
            "name": "<i>",
            "description": "<i></i>",
            "id": "org.eclipse.jdt.ui.templates.i_tag",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "<i>${word_selection}${}</i>${cursor}"
        },
        {
            "name": "@author",
            "description": "author name",
            "id": "org.eclipse.jdt.ui.templates.author",
            "context": "javadoc",
            "enabled": true,
            "autoinsert": true,
            "text": "@author ${user}"
        },
        {
            "name": "while",
            "description": "while loop with condition",
            "id": "org.eclipse.jdt.ui.templates.while_condition",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "while (${condition:var(boolean)}) {\n    ${line_selection}${cursor}\n}"
        },
        {
            "name": "test",
            "description": "test method",
            "id": "org.eclipse.jdt.ui.templates.test",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "public void test${name}() throws Exception {\n    ${cursor}\n}"
        },
        {
            "name": "Test",
            "description": "test method (JUnit 4)",
            "id": "org.eclipse.jdt.ui.templates.test_junit4",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "@${testType:newType(org.junit.Test)}\npublic void ${testName}() throws Exception {\n    ${staticImport:importStatic('org.junit.Assert.*')}${cursor}\n}"
        },
        {
            "name": "nls",
            "description": "non-externalized string marker",
            "id": "org.eclipse.jdt.ui.templates.non-nls",
            "context": "java",
            "enabled": true,
            "autoinsert": false,
            "text": "//$$NON-NLS-${N}$$"
        },
        {
            "name": "fall-through",
            "description": "$FALL-THROUGH$ marker",
            "id": "org.eclipse.jdt.ui.templates.fall-through",
            "context": "java",
            "enabled": true,
            "autoinsert": false,
            "text": "//$$FALL-THROUGH$$"
        },
        {
            "name": "synchronized",
            "description": "synchronized block",
            "id": "org.eclipse.jdt.ui.templates.synchronized",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "synchronized (${mutex:var}) {\n    ${line_selection}\n}"
        },
        {
            "name": "runnable",
            "description": "runnable",
            "id": "org.eclipse.jdt.ui.templates.runnable",
            "context": "java",
            "enabled": true,
            "autoinsert": false,
            "text": "new Runnable() {\n    public void run() {\n        ${line_selection}\n    }\n}"
        },
        {
            "name": "arraymerge",
            "description": "merge two arrays into one",
            "id": "org.eclipse.jdt.ui.templates.arraymerge",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "${array_type}[] ${result:newName(array1)} = new ${array_type}[${array1:array}.length + ${array}.length];\nSystem.arraycopy(${array1}, 0, ${result}, 0, ${array1}.length);\nSystem.arraycopy(${array}, 0, ${result}, ${array1}.length, ${array}.length);"
        },
        {
            "name": "arrayadd",
            "description": "add an element to an array",
            "id": "org.eclipse.jdt.ui.templates.arrayadd",
            "context": "java-statements",
            "enabled": true,
            "autoinsert": false,
            "text": "${array_type}[] ${result:newName(array)} = new ${array_type}[${array}.length + 1];\nSystem.arraycopy(${array}, 0, ${result}, 0, ${array}.length);\n${result}[${array}.length]= ${var};"
        },
        {
            "name": "static_final",
            "description": "static final field",
            "id": "org.eclipse.jdt.ui.templates.static_final",
            "context": "java-members",
            "enabled": true,
            "autoinsert": false,
            "text": "${visibility:link(public,protected,private)} static final ${type:link(String,int)} ${NAME};"
        }
    ]
}