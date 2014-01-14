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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class ExpressionParser {
    /**
     * Create new instance of parser for specified Java expression.
     *
     * @param expression
     *         Java language expression
     * @return concrete implementation of ExpressionParser
     */
    public static ExpressionParser newInstance(String expression) {
        // At the moment create instance of ANTLRExpressionParser directly.
        return new ANTLRExpressionParser(expression);
    }

    private final String expression;

    protected ExpressionParser(String expression) {
        this.expression = expression;
    }

    /**
     * Get expression for this parser.
     *
     * @return expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Evaluate expression.
     *
     * @param ev
     *         Evaluator
     * @return result of evaluation
     * @throws ExpressionException
     *         if specified expression is invalid or another error occurs when try to evaluate expression
     */
    public abstract com.sun.jdi.Value evaluate(Evaluator ev);
}
