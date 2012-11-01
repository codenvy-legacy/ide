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
package org.eclipse.jdt.client.templates;

import org.eclipse.jdt.client.JdtExtension;
import org.eclipse.jdt.client.create.CreateJavaClassPresenter;
import org.eclipse.jdt.client.templates.api.Template;
import org.eclipse.jdt.client.templates.api.TemplateBuffer;
import org.eclipse.jdt.client.templates.api.TemplateContext;
import org.eclipse.jdt.client.templates.api.TemplateException;
import org.eclipse.jdt.client.templates.api.TemplateTranslator;
import org.eclipse.jdt.client.templates.api.TemplateVariableResolver;
import org.exoplatform.ide.editor.runtime.Assert;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.DefaultLineTracker;
import org.exoplatform.ide.editor.text.ILineTracker;
import org.exoplatform.ide.editor.text.IRegion;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.Iterator;

public class CodeTemplateContext extends TemplateContext
{

   private String fLineDelimiter;

   public CodeTemplateContext(String contextTypeName, String lineDelim)
   {
      super(JdtExtension.get().getTemplateContextRegistry().getContextType(contextTypeName));
      fLineDelimiter = lineDelim;
   }

   /*
    * @see org.eclipse.jdt.internal.corext.template.TemplateContext#evaluate(org.eclipse.jdt.internal.corext.template.Template)
    */
   @Override
   public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException
   {
      // test that all variables are defined
      Iterator<TemplateVariableResolver> iterator = getContextType().resolvers();
      while (iterator.hasNext())
      {
         TemplateVariableResolver var = iterator.next();
         if (var instanceof CodeTemplateContextType.CodeTemplateVariableResolver)
         {
            Assert.isNotNull(getVariable(var.getType()), "Variable " + var.getType() + "not defined"); //$NON-NLS-1$ //$NON-NLS-2$
         }
      }

      if (!canEvaluate(template))
         return null;

      String pattern = changeLineDelimiter(template.getPattern(), fLineDelimiter);

      TemplateTranslator translator = new TemplateTranslator();
      TemplateBuffer buffer = translator.translate(pattern);
      getContextType().resolve(buffer, this);
      return buffer;
   }

   private static String changeLineDelimiter(String code, String lineDelim)
   {
      try
      {
         ILineTracker tracker = new DefaultLineTracker();
         tracker.set(code);
         int nLines = tracker.getNumberOfLines();
         if (nLines == 1)
         {
            return code;
         }

         StringBuffer buf = new StringBuffer();
         for (int i = 0; i < nLines; i++)
         {
            if (i != 0)
            {
               buf.append(lineDelim);
            }
            IRegion region = tracker.getLineInformation(i);
            String line = code.substring(region.getOffset(), region.getOffset() + region.getLength());
            buf.append(line);
         }
         return buf.toString();
      }
      catch (BadLocationException e)
      {
         // can not happen
         return code;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.jdt.internal.corext.template.TemplateContext#canEvaluate(org.eclipse.jdt.internal.corext.template.Template)
    */
   @Override
   public boolean canEvaluate(Template template)
   {
      return true;
   }

   public void setCompilationUnitVariables(FileModel file)
   {

      setVariable(CodeTemplateContextType.FILENAME, file.getName());
      setVariable(CodeTemplateContextType.PACKAGENAME, getPackage(file));
      setVariable(CodeTemplateContextType.PROJECTNAME, file.getProject().getName());
   }

   private String getPackage(FileModel file)
   {
      ProjectModel project = file.getProject();
      String sourcePath =
         project.hasProperty("sourceFolder") ? (String)project.getPropertyValue("sourceFolder")
            : CreateJavaClassPresenter.DEFAULT_SOURCE_FOLDER;
      String parentPath = file.getPath();
      String packageText = parentPath.substring((project.getPath() + "/" + sourcePath + "/").length());
      if (packageText.isEmpty())
         return "";
      if (packageText.endsWith("/"))
         packageText = packageText.substring(0, packageText.length() - 1);
      return packageText.replaceAll("/", ".");
   }
}
