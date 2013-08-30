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
package org.exoplatform.ide.editor.ckeditor;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $
 */
public class CKEditorConfiguration {

    public enum Language {
        ENGLISH("en"), FRENCH("fr"), RUSSIAN("ru"), UKRAINIAN("uk"), VIETNAMESE("vi"), DEFAULT("en");

        private String language;

        Language(String language) {
            this.language = language;
        }

        @Override
        public String toString() {
            return this.language;
        }
    }

    public final static Language LANGUAGE = Language.DEFAULT;

    public enum Toolbar {
        IDE("IDE"), DEFAULT("IDE");

        private String toolbar;

        Toolbar(String toolbar) {
            this.toolbar = toolbar;
        }

        @Override
        public String toString() {
            return this.toolbar;
        }
    }

    public final static Toolbar TOOLBAR = Toolbar.IDE;

    public enum Theme {
        DEFAULT("default");

        private String theme;

        Theme(String theme) {
            this.theme = theme;
        }

        @Override
        public String toString() {
            return this.theme;
        }
    }

    public final static Theme THEME = Theme.DEFAULT;

    public enum Skin {
        IDE("ide"), DEFAULT("ide");

        private String skin;

        Skin(String skin) {
            this.skin = skin;
        }

        @Override
        public String toString() {
            return this.skin;
        }
    }

    public final static Skin SKIN = Skin.IDE; // Skin.V2

    public final static boolean READ_ONLY = false;

    public final static int CONTINUOUS_SCANNING = 100;

    public final static String CKEDITOR_DIRECTORY = "ckeditor-3.1";

    public final static String BASE_PATH = GWT.getModuleBaseURL() + CKEDITOR_DIRECTORY + "/";

    public enum StartupMode {
        WYSIWYG("wysiwyg"), SOURCE("source");

        private String startupMode;

        StartupMode(String startupMode) {
            this.startupMode = startupMode;
        }

        @Override
        public String toString() {
            return this.startupMode;
        }
    }

    public final static StartupMode STARTUP_MODE = StartupMode.WYSIWYG;

    private static boolean fullPage = false;

    /**
     * @param fullPage
     *         <b>true</b> - ckeditor will add <i>html, head, body</i> - tags of html-file; <b>false</b> - ckeditor will
     *         remove <i>html, head, body</i> - tags of html-file
     */
    public static void setFullPage(boolean fullPage) {
        CKEditorConfiguration.fullPage = fullPage;
    }

    /**
     * @return <b>true</b> - ckeditor will add <i>html, head, body</i> - tags of html-file; <b>false</b> - ckeditor will remove
     *         <i>html, head, body</i> - tags of html-file
     */
    public static boolean isFullPage() {
        return fullPage;
    }

}