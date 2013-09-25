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
package com.codenvy.ide.resources.model;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Used for validating new resource names.
 * <b>Note, methods in this class check names only of limitation provided by Resource API</b>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *          //TODO check how to use GWT Validation API https://developers.google.com/web-toolkit/doc/latest/DevGuideValidation
 */
public class ResourceNameValidator {

    private static final RegExp PROJECT_VALIDATOR = RegExp.compile("^[A-Za-z0-9_]*$");

    private static final RegExp FOLDER_VALIDATOR = RegExp.compile("^[A-Za-z0-9_/.]*$");

    private static final RegExp FILE_VALIDATOR = RegExp.compile("^[A-Za-z0-9_.]*$");

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
