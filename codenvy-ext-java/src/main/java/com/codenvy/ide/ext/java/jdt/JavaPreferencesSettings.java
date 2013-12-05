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
package com.codenvy.ide.ext.java.jdt;

import com.codenvy.ide.ext.java.jdt.internal.corext.codemanipulation.CodeGenerationSettings;

public class JavaPreferencesSettings {

    public static CodeGenerationSettings getCodeGenerationSettings() {
        //TODO create PreferenceConstants service
        CodeGenerationSettings res = new CodeGenerationSettings();
        res.createComments =
                true; //Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_ADD_COMMENTS,
                // project)).booleanValue();
        res.useKeywordThis =
                true; //Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_KEYWORD_THIS,
                // project)).booleanValue();
        res.overrideAnnotation =
                true; //Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.CODEGEN_USE_OVERRIDE_ANNOTATION,
                // project)).booleanValue();
        res.importIgnoreLowercase =
                true;//Boolean.valueOf(PreferenceConstants.getPreference(PreferenceConstants.ORGIMPORTS_IGNORELOWERCASE,
                // project)).booleanValue();
        res.tabWidth = 3;//CodeFormatterUtil.getTabWidth(project);
        res.indentWidth = 3; //CodeFormatterUtil.getIndentWidth(project);
        return res;
    }

}
