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
package org.exoplatform.ide.extension.java.jdi.client;

import org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest;
import org.exoplatform.ide.extension.java.jdi.shared.VariablePath;

/**
 * Implementation of {@link UpdateVariableRequest} interface.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UpdateVarImpl.java Apr 27, 2012 5:30:48 PM azatsarynnyy $
 */
public class UpdateVariableRequestImpl implements UpdateVariableRequest {

    private String expression;

    private VariablePath variablePath;

    public UpdateVariableRequestImpl(VariablePath variablePath, String expression) {
        this.variablePath = variablePath;
        this.expression = expression;
    }

    public UpdateVariableRequestImpl() {
    }

    /** @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#getVariablePath() */
    @Override
    public VariablePath getVariablePath() {
        return variablePath;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#setVariablePath(org.exoplatform.ide.extension.java.jdi
     * .shared.VariablePath) */
    @Override
    public void setVariablePath(VariablePath variablePath) {
        this.variablePath = variablePath;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#getExpression() */
    @Override
    public String getExpression() {
        return expression;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.shared.UpdateVariableRequest#setExpression(java.lang.String) */
    @Override
    public void setExpression(String expression) {
        this.expression = expression;
    }

}
