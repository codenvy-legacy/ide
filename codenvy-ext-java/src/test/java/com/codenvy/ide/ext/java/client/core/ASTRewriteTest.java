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
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.client.core.dom.*;
import com.codenvy.ide.ext.java.client.core.dom.rewrite.ASTRewrite;
import com.codenvy.ide.ext.java.client.core.dom.rewrite.ITrackedNodePosition;
import com.codenvy.ide.ext.java.client.core.dom.rewrite.ListRewrite;

import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.text.edits.MalformedTreeException;
import com.codenvy.ide.text.edits.TextEdit;
import com.codenvy.ide.text.edits.UndoEdit;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 12:52:10 PM 34360 2009-07-22 23:58:59Z evgen $
 */
public class ASTRewriteTest extends ParserBaseTest {

    @Test
    public void testRewrite() {

        Document document = new DocumentImpl("import java.util.List;\nclass X {}\n");
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(document.get().toCharArray());
        CompilationUnit cu = (CompilationUnit)parser.createAST();
        AST ast = cu.getAST();
        ImportDeclaration id = ast.newImportDeclaration();
        id.setName(ast.newName(new String[]{"java", "util", "Set"}));
        ASTRewrite rewriter = ASTRewrite.create(ast);
        TypeDeclaration td = (TypeDeclaration)cu.types().get(0);
        ITrackedNodePosition tdLocation = rewriter.track(td);
        ListRewrite lrw = rewriter.getListRewrite(cu, CompilationUnit.IMPORTS_PROPERTY);
        lrw.insertLast(id, null);
        TextEdit edits = rewriter.rewriteAST(document, null);
        UndoEdit undo = null;
        try {
            undo = edits.apply(document);
        } catch (MalformedTreeException e) {
            e.printStackTrace();
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        Assert.assertTrue("import java.util.List;\nimport java.util.Set;\nclass X {}\n".equals(document.get()));
        // tdLocation.getStartPosition() and tdLocation.getLength()
        // are new source range for &quot;class X {}&quot; in document.get()
    }

}
