/*******************************************************************************
 * Copyright (c) 2007, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.templates;

import org.eclipse.jdt.client.templates.api.TemplateContext;
import org.eclipse.jdt.client.templates.api.TemplateVariableResolver;

/**
 * Variable resolver for variable <code>exception_variable_name</code>. Resolves to an unused exception name.
 * 
 * @since 3.4
 */
public class ExceptionVariableNameResolver extends TemplateVariableResolver
{

   @Override
   protected String[] resolveAll(TemplateContext context)
   {
      if (context instanceof JavaContext)
      {
         // TODO
         // JavaContext jc= (JavaContext) context;
         // IJavaProject javaProject= jc.getJavaProject();
         // String exceptionVariableName= StubUtility.getExceptionVariableName();
         // return StubUtility.getLocalNameSuggestions(jc.getJavaProject(), exceptionVariableName, 0, jc.computeExcludes());
      }
      return new String[0];
   }

}
