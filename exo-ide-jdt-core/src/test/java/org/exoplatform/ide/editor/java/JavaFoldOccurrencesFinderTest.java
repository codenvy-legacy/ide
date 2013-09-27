/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.editor.java;

import com.google.collide.client.editor.folding.AbstractFoldRange;
import com.googlecode.gwt.test.GwtTest;

import org.exoplatform.ide.editor.shared.text.Document;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaFoldOccurrencesFinderTest extends GwtTest {
    private JavaFoldOccurrencesFinder finder = new JavaFoldOccurrencesFinder();

    @Test
    public void testMethodWithJavaDoc() throws Exception {
        StringBuilder b = new StringBuilder("public class MyClass{\n");
        b.append("/**\n");
        b.append("* Ho ho\n");
        b.append("**/\n");
        b.append(" public int getI(){\n");
        b.append("return 0;\n");
        b.append("}\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(2, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals(" public int getI(){\nreturn 0;\n}\n", document.get(foldRange1.getOffset(), foldRange1.getLength()));
        AbstractFoldRange foldRange2 = foldRanges.get(1);

        assertEquals("/**\n* Ho ho\n**/\n", document.get(foldRange2.getOffset(), foldRange2.getLength()));
    }

    @Test
    public void testMethodWithError() throws Exception {
        StringBuilder b = new StringBuilder("public class MyClass{\n");
        b.append("public int getI(){\n");
        b.append("if(true){\n");
        b.append("return 0 sdagsaf glfdskg ;lkdfsnlgk ndsfg \n");
        b.append("}\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(1, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("public int getI(){\nif(true){\nreturn 0 sdagsaf glfdskg ;lkdfsnlgk ndsfg \n}\n}",
                     document.get(foldRange1.getOffset(), foldRange1.getLength()));
    }

    @Test
    public void testMethodWithBlockComment() throws Exception {
        StringBuilder b = new StringBuilder("public class MyClass{\n");
        b.append("/*\n");
        b.append("* Ho ho\n");
        b.append("*/\n");
        b.append("public int getI(){\n");
        b.append("return 0;\n");
        b.append("}\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(2, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("public int getI(){\nreturn 0;\n}\n", document.get(foldRange1.getOffset(), foldRange1.getLength()));
        AbstractFoldRange foldRange2 = foldRanges.get(1);

        assertEquals("/*\n* Ho ho\n*/\n", document.get(foldRange2.getOffset(), foldRange2.getLength()));
    }

    @Test
    public void testMethodWithInternalBlockComment() throws Exception {
        StringBuilder b = new StringBuilder("public class MyClass{\n");
        b.append("/*\n");
        b.append("* No no noooooooooooooooooo\n");
        b.append("*/\n");
        b.append("public int getI(){\n");
        b.append("       /*\n");
        b.append("* Ho ho\n");
        b.append("*/\n");
        b.append("return 0;\n");
        b.append("}\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(3, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("public int getI(){\n" +
                     "       /*\n" +
                     "* Ho ho\n" +
                     "*/\n" +
                     "return 0;\n" +
                     "}\n", document.get(foldRange1.getOffset(), foldRange1.getLength()));
    }

    @Test
    public void testHeader() throws Exception {
        StringBuilder b = new StringBuilder();
        b.append("/*\n" +
                 " * CODENVY CONFIDENTIAL\n" +
                 " * __________________\n" +
                 " *\n" +
                 " * [2012] - [2013] Codenvy, S.A.\n" +
                 " * All Rights Reserved.\n" +
                 " *\n" +
                 " * NOTICE:  All information contained herein is, and remains\n" +
                 " * the property of Codenvy S.A. and its suppliers,\n" +
                 " * if any.  The intellectual and technical concepts contained\n" +
                 " * herein are proprietary to Codenvy S.A.\n" +
                 " * and its suppliers and may be covered by U.S. and Foreign Patents,\n" +
                 " * patents in process, and are protected by trade secret or copyright law.\n" +
                 " * Dissemination of this information or reproduction of this material\n" +
                 " * is strictly forbidden unless prior written permission is obtained\n" +
                 " * from Codenvy S.A..\n" +
                 " */\n");
        b.append("public class MyClass{\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(1, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("/*\n" +
                     " * CODENVY CONFIDENTIAL\n" +
                     " * __________________\n" +
                     " *\n" +
                     " * [2012] - [2013] Codenvy, S.A.\n" +
                     " * All Rights Reserved.\n" +
                     " *\n" +
                     " * NOTICE:  All information contained herein is, and remains\n" +
                     " * the property of Codenvy S.A. and its suppliers,\n" +
                     " * if any.  The intellectual and technical concepts contained\n" +
                     " * herein are proprietary to Codenvy S.A.\n" +
                     " * and its suppliers and may be covered by U.S. and Foreign Patents,\n" +
                     " * patents in process, and are protected by trade secret or copyright law.\n" +
                     " * Dissemination of this information or reproduction of this material\n" +
                     " * is strictly forbidden unless prior written permission is obtained\n" +
                     " * from Codenvy S.A..\n" +
                     " */\n", document.get(foldRange1.getOffset(), foldRange1.getLength()));

    }

    @Test
    public void testInnerType() throws Exception {
        StringBuilder b = new StringBuilder("public class MyClass{\n");
        b.append("public class II{\n");
        b.append("}\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(1, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("public class II{\n"
                     + "}\n",
                     document.get(foldRange1.getOffset(), foldRange1.getLength()));
    }

    @Test
    public void testImports() throws Exception {
        StringBuilder b = new StringBuilder();
        b.append("package com.codenvy;\n");
        b.append("import java.util.List;\n");
        b.append("import java.util.Map;\n");
        b.append("public class MyClass{\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(1, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("import java.util.List;\n"
                     + "import java.util.Map;\n",
                     document.get(foldRange1.getOffset(), foldRange1.getLength()));
    }

    @Test
    public void testImport() throws Exception {
        StringBuilder b = new StringBuilder();
        b.append("package com.codenvy;\n");
        b.append("import java.util.List;\n");
        b.append("public class MyClass{\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(0, foldRanges.size());
    }

    @Test
    public void testStaticImport() throws Exception {
        StringBuilder b = new StringBuilder();
        b.append("package com.codenvy;\n");
        b.append("import static org.junit.Assert.assertEquals;\n");
        b.append("import java.util.List;\n");
        b.append("import java.util.Map;\n");
        b.append("public class MyClass{\n");
        b.append("}");
        IDocument document = new Document(b.toString());
        List<AbstractFoldRange> foldRanges = finder.findPositions(document);
        assertEquals(1, foldRanges.size());
        AbstractFoldRange foldRange1 = foldRanges.get(0);
        assertEquals("import static org.junit.Assert.assertEquals;\n" +
                     "import java.util.List;\n"
                     + "import java.util.Map;\n",
                     document.get(foldRange1.getOffset(), foldRange1.getLength()));
    }

    @Override
    public String getModuleName() {
        return "org.exoplatform.ide.editor.java.JavaEditorExtension";
    }
}
