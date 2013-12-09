/*******************************************************************************
 * Copyright (c) 2005, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.templates;

import com.codenvy.ide.ext.java.jdt.templates.api.TemplateContext;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateVariable;
import com.codenvy.ide.ext.java.jdt.templates.api.TemplateVariableResolver;

import java.util.List;

public class ElementTypeResolver extends TemplateVariableResolver {

    public ElementTypeResolver() {
    }

    /*
     * @see org.eclipse.jface.text.templates.TemplateVariableResolver#resolve(org.eclipse.jface.text.templates.TemplateVariable,
     * org.eclipse.jface.text.templates.TemplateContext)
     * @since 3.3
     */
    @Override
    public void resolve(TemplateVariable variable, TemplateContext context) {
        if (!(variable instanceof MultiVariable)) {
            super.resolve(variable, context);
            return;
        }
        MultiVariable mv = (MultiVariable)variable;
        List<String> params = variable.getVariableType().getParams();
        if (params.isEmpty()) {
            super.resolve(variable, context);
            return;
        }

        JavaContext jc = (JavaContext)context;
        String reference = params.get(0);
        TemplateVariable refVar = jc.getTemplateVariable(reference);
        if (refVar instanceof JavaVariable) {
            JavaVariable jvar = (JavaVariable)refVar;
            resolve(mv, jvar, jc);
            return;
        }

        super.resolve(variable, context);
    }

    private void resolve(MultiVariable variable, JavaVariable master, JavaContext context) {
        Object[] choices = master.getChoices();
        if (choices instanceof CompilationUnitCompletion.Variable[]) {
            CompilationUnitCompletion.Variable[] variables = (CompilationUnitCompletion.Variable[])choices;

            for (int i = 0; i < variables.length; i++)
                variable.setChoices(variables[i], variables[i].getMemberTypeNames());

            context.addDependency(master, variable);
            variable.setKey(master.getCurrentChoice());
        } else {
            super.resolve(variable, context);
        }
    }
}
