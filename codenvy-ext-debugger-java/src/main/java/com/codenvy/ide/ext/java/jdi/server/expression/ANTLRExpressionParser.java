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
package com.codenvy.ide.ext.java.jdi.server.expression;

import com.sun.jdi.Value;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeNodeStream;

/**
 * ANTLR based implementation of ExpressionParser.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ANTLRExpressionParser extends ExpressionParser {
    private CommonTreeNodeStream nodes;

    public ANTLRExpressionParser(String expression) {
        super(expression);
    }

    @Override
    public Value evaluate(Evaluator ev) {
        try {
            if (nodes == null) {
                parse();
            } else {
                nodes.reset();
            }
            JavaTreeParser walker = new JavaTreeParser(nodes, ev);
            return walker.evaluate();
        } catch (RecognitionException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
    }

    private void parse() throws RecognitionException {
        JavaLexer lexer = new JavaLexer(new ANTLRStringStream(getExpression()));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);
        nodes = new CommonTreeNodeStream(parser.expression().getTree());
    }
}
