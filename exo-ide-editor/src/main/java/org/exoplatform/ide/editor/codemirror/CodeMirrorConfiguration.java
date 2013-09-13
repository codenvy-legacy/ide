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
package org.exoplatform.ide.editor.codemirror;

import com.google.gwt.core.client.GWT;

import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class CodeMirrorConfiguration {

    public static final String CODEMIRROR_DIRECTORY = "codemirror-0.94";

    public final static String PATH = GWT.getModuleBaseURL() + CODEMIRROR_DIRECTORY + "/";

    public static final String CODEMIRROR_START_PAGE = PATH + "codemirror.html";

    private String jsDirectory = PATH + "js/";

    private boolean isTextWrapping = false;

    /** 0 to turn off continuous scanning, or value like 100 in millisec as scanning period */
    private int continuousScanning = 0;

    private String codeParsers;

    private String codeStyles;

    private boolean canBeOutlined = false;

    private Parser parser;

    private CodeValidator codeValidator;

    private AutocompleteHelper autocompleteHelper;

    private CodeAssistant codeAssistant;

    private boolean canHaveSeveralMimeTypes = false;

    //private String codeErrorMarkStyle = CodeMirrorClientBundle.INSTANCE.css().codeErrorMarkStyle();
    private String codeErrorMarkStyle = CodeMirrorStyles.CODE_ERROR_MARK_STYLE;

    private TabMode tabMode = TabMode.SPACES;

    /** Preset configuration of plain text */
    public CodeMirrorConfiguration() {
        this.codeParsers = "['parsexml.js']";
        this.codeStyles = "['" + PATH + "css/xmlcolors.css']";
    }

    public String getCodeParsers() {
        return codeParsers;
    }

    /**
     * Set generic CodeMirror library parsing files *.js
     *
     * @param codeParsers
     * @return configuration instance
     */
    public CodeMirrorConfiguration setGenericParsers(String codeParsers) {
        this.codeParsers = codeParsers;
        return this;
    }

    public String getCodeStyles() {
        return codeStyles;
    }

    /**
     * Set generic CodeMirror library style files *.css
     *
     * @param codeStyles
     * @return configuration instance
     */
    public CodeMirrorConfiguration setGenericStyles(String codeStyles) {
        this.codeStyles = codeStyles;
        return this;
    }

    public boolean canBeOutlined() {
        return canBeOutlined;
    }

    public CodeMirrorConfiguration setCanBeOutlined(boolean canBeOutlined) {
        this.canBeOutlined = canBeOutlined;
        return this;
    }

    public boolean canBeAutocompleted() {
        return true; // this.parser != null && this.codeAssistant != null;
    }

    // public CodeMirrorConfiguration setCanBeAutocompleted(boolean canBeAutocompleted)
    // {
    // this.canBeAutocompleted = canBeAutocompleted;
    // return this;
    // }

    public boolean canBeValidated() {
        return this.parser != null && this.codeValidator != null && this.codeAssistant != null;
    }

    // public CodeMirrorConfiguration setCanBeValidated(boolean canBeValidated)
    // {
    // this.canBeValidated = canBeValidated;
    // return this;
    // }

    public Parser getParser() {
        return parser;
    }

    public CodeMirrorConfiguration setParser(Parser parser) {
        this.parser = parser;
        return this;
    }

    public CodeValidator getCodeValidator() {
        return codeValidator;
    }

    public CodeMirrorConfiguration setCodeValidator(CodeValidator codeValidator) {
        this.codeValidator = codeValidator;
        return this;
    }

    public AutocompleteHelper getAutocompleteHelper() {
        return autocompleteHelper;
    }

    public CodeMirrorConfiguration setAutocompleteHelper(AutocompleteHelper autocompleteHelper) {
        this.autocompleteHelper = autocompleteHelper;
        return this;
    }

    public boolean canHaveSeveralMimeTypes() {
        return canHaveSeveralMimeTypes;
    }

    public CodeMirrorConfiguration setCanHaveSeveralMimeTypes(boolean canHaveSeveralMimeTypes) {
        this.canHaveSeveralMimeTypes = canHaveSeveralMimeTypes;
        return this;
    }

    /** @return the textWrapping */
    public boolean isTextWrapping() {
        return isTextWrapping;
    }

    public CodeMirrorConfiguration setIsTextWrapping(boolean isTextWrapping) {
        this.isTextWrapping = isTextWrapping;
        return this;
    }

    /** @return the continuousScanning */
    public int getContinuousScanning() {
        return continuousScanning;
    }

    public CodeMirrorConfiguration setContinuousScanning(int continuousScanning) {
        this.continuousScanning = continuousScanning;
        return this;
    }

    /** @return the jsDirectory */
    public String getJsDirectory() {
        return jsDirectory;
    }

    public CodeMirrorConfiguration setJsDirectory(String jsDirectory) {
        this.jsDirectory = jsDirectory;
        return this;
    }

    /** @return the codeAssistant */
    public CodeAssistant getCodeAssistant() {
        return codeAssistant;
    }

    public CodeMirrorConfiguration setCodeAssistant(CodeAssistant codeAssistant) {
        this.codeAssistant = codeAssistant;
        return this;
    }

    public CodeMirrorConfiguration setCodeErrorMarkStyle(String codeErrorMarkStyle) {
        this.codeErrorMarkStyle = codeErrorMarkStyle;
        return this;
    }

    public String getCodeErrorMarkStyle() {
        return codeErrorMarkStyle;
    }

    /** @return the tabMode */
    public TabMode getTabMode() {
        return tabMode;
    }

    /**
     * @param tabMode
     *         the tabMode to set
     */
    public CodeMirrorConfiguration setTabMode(TabMode tabMode) {
        this.tabMode = tabMode;
        return this;
    }


}