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
package org.exoplatform.ide.editor.ruby.client.codemirror;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.outline.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.ruby.client.RubyClientBundle;

/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class RubyOutlineItemCreator extends OutlineItemCreatorImpl {
    @Override
    public ImageResource getTokenIcon(TokenBeenImpl token) {
        switch (token.getType()) {
            case LOCAL_VARIABLE:
                return RubyClientBundle.INSTANCE.variable();

            case GLOBAL_VARIABLE:
                return RubyClientBundle.INSTANCE.rubyGlobalVariable();

            case CLASS_VARIABLE:
                return RubyClientBundle.INSTANCE.rubyClassVariable();

            case INSTANCE_VARIABLE:
                return RubyClientBundle.INSTANCE.rubyObjectVariable();

            case CONSTANT:
                return RubyClientBundle.INSTANCE.rubyConstant();

            case MODULE:
                return RubyClientBundle.INSTANCE.module();

            case CLASS:
                return RubyClientBundle.INSTANCE.classItem();

            case METHOD:
                return RubyClientBundle.INSTANCE.publicMethod();

            default:
                return null;
        }
    }

    @Override
    public String getTokenDisplayTitle(TokenBeenImpl token) {
        String label = token.getName();

        // Add "()" to the end of method's label
        if (TokenType.METHOD.equals(token.getType())) {
            label += "()";
        }

        if (token.getElementType() != null) {
            label += getElementType(token);
        }

        return label;
    }

}
