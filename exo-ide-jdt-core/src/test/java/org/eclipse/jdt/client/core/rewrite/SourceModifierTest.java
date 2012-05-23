/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.client.core.rewrite;

import org.eclipse.jdt.client.internal.core.dom.rewrite.SourceModifier;
import org.exoplatform.ide.editor.text.Document;
import org.exoplatform.ide.editor.text.edits.MultiTextEdit;
import org.exoplatform.ide.editor.text.edits.ReplaceEdit;
import org.junit.Test;

/**
 *
 */
public class SourceModifierTest extends ASTRewritingTest
{

   @Test
   public void testRemoveIndents() throws Exception
   {
      StringBuffer buf = new StringBuffer();
      buf.append("package test1;\n");
      buf.append("public class E {\n");
      buf.append("    public void foo() {\n");
      buf.append("        while (i == 0) {\n");
      buf.append("            foo();\n");
      buf.append("            i++; // comment\n");
      buf.append("            i++;\n");
      buf.append("        }\n");
      buf.append("        return;\n");
      buf.append("    }\n");
      buf.append("}\n");

      Document buffer = new Document(buf.toString());

      int offset = buf.toString().indexOf("while");
      int length = buf.toString().indexOf("return;") + "return;".length() - offset;

      String content = buffer.get(offset, length);
      SourceModifier modifier = new SourceModifier(2, "    ", 4, 4);
      MultiTextEdit edit = new MultiTextEdit(0, content.length());
      ReplaceEdit[] replaces = modifier.getModifications(content);
      for (int i = 0; i < replaces.length; i++)
      {
         edit.addChild(replaces[i]);
      }

      Document innerBuffer = new Document(content);
      edit.apply(innerBuffer);

      buffer.replace(offset, length, innerBuffer.get());

      String preview = buffer.get();

      buf = new StringBuffer();
      buf.append("package test1;\n");
      buf.append("public class E {\n");
      buf.append("    public void foo() {\n");
      buf.append("        while (i == 0) {\n");
      buf.append("        foo();\n");
      buf.append("        i++; // comment\n");
      buf.append("        i++;\n");
      buf.append("    }\n");
      buf.append("    return;\n");
      buf.append("    }\n");
      buf.append("}\n");
      String expected = buf.toString();

      assertEqualString(preview, expected);
   }

   @Test
   public void testAddIndents() throws Exception
   {
      StringBuffer buf = new StringBuffer();
      buf.append("package test1;\n");
      buf.append("public class E {\n");
      buf.append("    public void foo() {\n");
      buf.append("        while (i == 0) {\n");
      buf.append("            foo();\n");
      buf.append("            i++; // comment\n");
      buf.append("            i++;\n");
      buf.append("        }\n");
      buf.append("        return;\n");
      buf.append("    }\n");
      buf.append("}\n");

      Document buffer = new Document(buf.toString());

      int offset = buf.toString().indexOf("while");
      int length = buf.toString().indexOf("return;") + "return;".length() - offset;

      String content = buffer.get(offset, length);
      SourceModifier modifier = new SourceModifier(2, "            ", 4, 4);
      MultiTextEdit edit = new MultiTextEdit(0, content.length());
      ReplaceEdit[] replaces = modifier.getModifications(content);
      for (int i = 0; i < replaces.length; i++)
      {
         edit.addChild(replaces[i]);
      }

      Document innerBuffer = new Document(content);
      edit.apply(innerBuffer);

      buffer.replace(offset, length, innerBuffer.get());

      String preview = buffer.get();

      buf = new StringBuffer();
      buf.append("package test1;\n");
      buf.append("public class E {\n");
      buf.append("    public void foo() {\n");
      buf.append("        while (i == 0) {\n");
      buf.append("                foo();\n");
      buf.append("                i++; // comment\n");
      buf.append("                i++;\n");
      buf.append("            }\n");
      buf.append("            return;\n");
      buf.append("    }\n");
      buf.append("}\n");
      String expected = buf.toString();

      assertEqualString(preview, expected);
   }
}
