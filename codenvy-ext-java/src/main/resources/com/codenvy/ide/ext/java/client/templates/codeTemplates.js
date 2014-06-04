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
            "autoinsert": true,
            "context": "gettercomment_context",
            "deleted": false,
            "description": "Comment for getter method",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.gettercomment",
            "name": "gettercomment",
            "text": "/**\n * @return the ${bare_field_name}\n */"
        },
        {
            "autoinsert": true,
            "context": "settercomment_context",
            "deleted": false,
            "description": "Comment for setter method",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.settercomment",
            "name": "settercomment",
            "text": "/**\n * @param ${param} the ${bare_field_name} to set\n */"
        },
        {
            "autoinsert": true,
            "context": "constructorcomment_context",
            "deleted": false,
            "description": "Comment for created constructors",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.constructorcomment",
            "name": "constructorcomment",
            "text": "/**\b * ${tags}\n */"
        },
        {
            "autoinsert": true,
            "context": "fieldcomment_context",
            "deleted": false,
            "description": "Comment for fields",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.fieldcomment",
            "name": "fieldcomment",
            "text": "/**\n *\n */"
        },
        {
            "autoinsert": true,
            "context": "methodcomment_context",
            "deleted": false,
            "description": "Comment for non-overriding methods",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.methodcomment",
            "name": "methodcomment",
            "text": "/**\n * ${tags}\n */"
        },
        {
            "autoinsert": false,
            "context": "overridecomment_context",
            "deleted": false,
            "description": "Comment for overriding methods",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.overridecomment",
            "name": "overridecomment",
            "text": "/**\n * ${see_to_overridden}\n */"
        },
        {
            "autoinsert": true,
            "context": "delegatecomment_context",
            "deleted": false,
            "description": "Comment for delegate methods",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.delegatecomment",
            "name": "delegatecomment",
            "text": "/**\n * ${tags}\n * ${see_to_target}\n */"
        },
        {
            "autoinsert": true,
            "context": "newtype_context",
            "deleted": false,
            "description": "Newly created files",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.newtype",
            "name": "newtype",
            "text": "${filecomment}\n${package_declaration}\n\n${typecomment}\n${type_declaration}"
        },
        {
            "autoinsert": true,
            "context": "catchblock_context",
            "deleted": false,
            "description": "Code in new catch blocks",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.catchblock",
            "name": "catchblock",
            "text": "// ${todo} Auto-generated catch block\n${exception_var}.printStackTrace();"
        },
        {
            "autoinsert": true,
            "context": "methodbody_context",
            "deleted": false,
            "description": "Code in created method stubs",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.methodbody",
            "name": "methodbody",
            "text": "// ${todo} Auto-generated method stub\n${body_statement}"
        },
        {
            "autoinsert": true,
            "context": "constructorbody_context",
            "deleted": false,
            "description": "Code in created constructor stubs",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.constructorbody",
            "name": "constructorbody",
            "text": "${body_statement}\n// ${todo} Auto-generated constructor stub"
        },
        {
            "autoinsert": true,
            "context": "getterbody_context",
            "deleted": false,
            "description": "Code in created getters",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.getterbody",
            "name": "getterbody",
            "text": "return ${field};"
        },
        {
            "autoinsert": true,
            "context": "setterbody_context",
            "deleted": false,
            "description": "Code in created setters",
            "enabled": true,
            "id": "org.eclipse.jdt.ui.text.codetemplates.setterbody",
            "name": "setterbody",
            "text": "${field} = ${param};"
        }
    ]

}