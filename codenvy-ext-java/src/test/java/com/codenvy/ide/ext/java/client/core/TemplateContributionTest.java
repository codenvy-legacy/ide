/*******************************************************************************
 * Copyright (c) 2007, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.ext.java.jdt.templates.CodeTemplateContextType;
import com.codenvy.ide.ext.java.jdt.templates.ContextTypeRegistry;
import com.codenvy.ide.ext.java.jdt.templates.ElementTypeResolver;
import com.codenvy.ide.ext.java.jdt.templates.ExceptionVariableNameResolver;
import com.codenvy.ide.ext.java.jdt.templates.FieldResolver;
import com.codenvy.ide.ext.java.jdt.templates.ImportsResolver;
import com.codenvy.ide.ext.java.jdt.templates.JavaContextType;
import com.codenvy.ide.ext.java.jdt.templates.JavaDocContextType;
import com.codenvy.ide.ext.java.jdt.templates.LinkResolver;
import com.codenvy.ide.ext.java.jdt.templates.LocalVarResolver;
import com.codenvy.ide.ext.java.jdt.templates.NameResolver;
import com.codenvy.ide.ext.java.jdt.templates.StaticImportResolver;
import com.codenvy.ide.ext.java.jdt.templates.TemplateStore;
import com.codenvy.ide.ext.java.jdt.templates.TypeResolver;
import com.codenvy.ide.ext.java.jdt.templates.TypeVariableResolver;
import com.codenvy.ide.ext.java.jdt.templates.VarResolver;
import com.codenvy.ide.ext.java.jdt.templates.api.Template;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateBuffer;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateContextType;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateException;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateTranslator;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateVariable;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateVariableResolver;

import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * Template contribution tests.
 *
 * @since 3.4
 */
public class TemplateContributionTest extends BaseTest {

    private ContextTypeRegistry fCodeTemplateContextTypeRegistry;

    private void checkContribution(String resolverContextTypeId, String contextTypeId) throws TemplateException {
        ContextTypeRegistry registry = getTemplateContextRegistry();
        TemplateContextType context = registry.getContextType(resolverContextTypeId);

        TemplateStore templateStore = new TemplateStore();
        Template[] templates = templateStore.getTemplates(contextTypeId);

        for (int i = 0; i < templates.length; i++) {
            Template template = templates[i];
            TemplateTranslator translator = new TemplateTranslator();
            TemplateBuffer buffer = translator.translate(template);
            TemplateVariable[] variables = buffer.getVariables();
            for (int j = 0; j < variables.length; j++) {
                TemplateVariable variable = variables[j];
                if (!variable.getType().equals(variable.getName())) {
                    Assert.assertTrue(
                            "No resolver found for variable '" + variable.getType() + "' in template '" + template.getName()
                            + "'\n\n" + template.getPattern(), canHandle(context, variable));
                }
            }
        }
    }

    /** @return  */
    public ContextTypeRegistry getTemplateContextRegistry() {
        if (fCodeTemplateContextTypeRegistry == null) {
            fCodeTemplateContextTypeRegistry = new ContextTypeRegistry();

            CodeTemplateContextType.registerContextTypes(fCodeTemplateContextTypeRegistry);
            JavaContextType contextTypeAll = new JavaContextType(JavaContextType.ID_ALL);

            contextTypeAll.initializeContextTypeResolvers();

            FieldResolver fieldResolver = new FieldResolver();
            fieldResolver.setType("field");
            contextTypeAll.addResolver(fieldResolver);

            LocalVarResolver localVarResolver = new LocalVarResolver();
            localVarResolver.setType("localVar");
            contextTypeAll.addResolver(localVarResolver);
            VarResolver varResolver = new VarResolver();
            varResolver.setType("var");
            contextTypeAll.addResolver(varResolver);
            NameResolver nameResolver = new NameResolver();
            nameResolver.setType("newName");
            contextTypeAll.addResolver(nameResolver);
            TypeResolver typeResolver = new TypeResolver();
            typeResolver.setType("newType");
            contextTypeAll.addResolver(typeResolver);
            ElementTypeResolver elementTypeResolver = new ElementTypeResolver();
            elementTypeResolver.setType("elemType");
            contextTypeAll.addResolver(elementTypeResolver);
            TypeVariableResolver typeVariableResolver = new TypeVariableResolver();
            typeVariableResolver.setType("argType");
            contextTypeAll.addResolver(typeVariableResolver);
            LinkResolver linkResolver = new LinkResolver();
            linkResolver.setType("link");
            contextTypeAll.addResolver(linkResolver);
            ImportsResolver importsResolver = new ImportsResolver();
            importsResolver.setType("import");
            StaticImportResolver staticImportResolver = new StaticImportResolver();
            staticImportResolver.setType("importStatic");
            contextTypeAll.addResolver(staticImportResolver);
            ExceptionVariableNameResolver exceptionVariableNameResolver = new ExceptionVariableNameResolver();
            exceptionVariableNameResolver.setType("exception_variable_name");
            contextTypeAll.addResolver(exceptionVariableNameResolver);
            fCodeTemplateContextTypeRegistry.addContextType(contextTypeAll);
            fCodeTemplateContextTypeRegistry.addContextType(new JavaDocContextType());
            JavaContextType contextTypeMembers = new JavaContextType(JavaContextType.ID_MEMBERS);
            JavaContextType contextTypeStatements = new JavaContextType(JavaContextType.ID_STATEMENTS);
            contextTypeMembers.initializeResolvers(contextTypeAll);
            contextTypeStatements.initializeResolvers(contextTypeAll);
            fCodeTemplateContextTypeRegistry.addContextType(contextTypeMembers);
            fCodeTemplateContextTypeRegistry.addContextType(contextTypeStatements);
        }

        return fCodeTemplateContextTypeRegistry;
    }

    @Test
    public void testJavaContribution() throws Exception {
        checkContribution(JavaContextType.ID_ALL, JavaContextType.ID_ALL);
        //TODO HtmlUnit has a bug with RegExp emulation
        //      checkContribution(JavaContextType.ID_ALL, JavaContextType.ID_MEMBERS);
        //      checkContribution(JavaContextType.ID_ALL, JavaContextType.ID_STATEMENTS);
        //      checkContribution(JavaContextType.ID_MEMBERS, JavaContextType.ID_MEMBERS);
        //      checkContribution(JavaContextType.ID_STATEMENTS, JavaContextType.ID_STATEMENTS);
    }

    @Test
    public void testJavaDocContribution() throws Exception {
        checkContribution(JavaDocContextType.ID, JavaDocContextType.ID);
    }

    // public void testSWTContributionAll() throws Exception {
    // checkContribution(SWTContextType.ID_ALL, SWTContextType.ID_ALL);
    // checkContribution(SWTContextType.ID_ALL, SWTContextType.ID_MEMBERS);
    // checkContribution(SWTContextType.ID_ALL, SWTContextType.ID_STATEMENTS);
    // checkContribution(SWTContextType.ID_MEMBERS, SWTContextType.ID_MEMBERS);
    // checkContribution(SWTContextType.ID_STATEMENTS, SWTContextType.ID_STATEMENTS);
    // }

    private boolean canHandle(TemplateContextType context, TemplateVariable variable) {
        for (Iterator iterator = context.resolvers(); iterator.hasNext(); ) {
            TemplateVariableResolver resolver = (TemplateVariableResolver)iterator.next();
            if (variable.getType().equals(resolver.getType()))
                return true;
        }
        return false;
    }

}
