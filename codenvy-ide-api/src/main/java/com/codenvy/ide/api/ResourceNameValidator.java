/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Used for validating new resource names.
 * <b>Note, methods in this class check names only of limitation provided by Resource API</b>
 *
 * @author Evgen Vidolob
 * //TODO check how to use GWT Validation API https://developers.google.com/web-toolkit/doc/latest/DevGuideValidation
 */
public class ResourceNameValidator {

    //TODO : need specify rules for resource names for now make same as for project name
    private static final RegExp PROJECT_VALIDATOR = RegExp.compile("^[A-Za-z0-9_][A-Za-z0-9_\\-\\.]*$");

    private static final RegExp FOLDER_VALIDATOR = RegExp.compile("^[A-Za-z0-9_][A-Za-z0-9_\\-\\.]*$");

    private static final RegExp FILE_VALIDATOR = RegExp.compile("^[A-Za-z0-9_][A-Za-z0-9_\\-\\.]*$");

    public static boolean isFileNameValid(String name) {
        return FILE_VALIDATOR.test(name);
    }

    public static boolean isFolderNameValid(String name) {
        return FOLDER_VALIDATOR.test(name);
    }

    public static boolean isProjectNameValid(String name) {
        return PROJECT_VALIDATOR.test(name);
    }

}