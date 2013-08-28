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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.internal.corext.util.CodeFormatterUtil;
import org.exoplatform.ide.client.framework.editor.CodeFormatter;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.edits.TextEdit;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 3:18:10 PM Apr 2, 2012 evgen $
 */
public class JavaCodeFormatter implements CodeFormatter {

    /** @see org.exoplatform.ide.client.framework.editor.CodeFormatter#format(org.exoplatform.ide.editor.shared.text.IDocument) */
    @Override
    public TextEdit format(IDocument document) {
        return CodeFormatterUtil.format2(org.eclipse.jdt.client.core.formatter.CodeFormatter.K_COMPILATION_UNIT,
                                         document.get(), 0, null, JavaCore.getOptions());
    }

}
