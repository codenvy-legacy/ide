// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.codenvy.ide.api.texteditor.parser;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.util.Pair;
import com.codenvy.ide.util.StringUtils;

/** Parser {@link Token} related utilities. */
public class TokenUtil {

    /**
     * Map ["mode:width"] -> whitespace token of given mode and width.
     * <p/>
     * <p>Used by {@link #getPlaceholderForMode} to cache placeholder tokens.
     */
    private static final StringMap<Token> cachedPlaceholders = Collections.createStringMap();

    // Do not instantiate.
    private TokenUtil() {
    }

    /**
     * <p>Finds the mode (programming language) corresponding to the given column:
     * <ul>
     * <li>If the column is inside a token then the mode of the token is returned
     * <li>if the column is on the boundary between two tokens then the mode of
     * the first token is returned (this behavior works well for auto-completion)
     * <li>if the column is greater than the sum of all token lengths then the
     * mode of the last token is returned (so that we handle auto-completion for
     * fast typer in the best possible way)
     * <li>if the map is empty then null is returned
     * <li>if the column = 0 then the mode of the first tag is returned
     * <li>if the column is < 0 then IllegalArgumentException is thrown
     * </ul>
     *
     * @param initialMode
     *         mode before the first token.
     * @param modes
     *         "language map" built by {@link #buildModes}
     * @param column
     *         column to search for
     * @return the mode of the token covering the column
     * @throws IllegalArgumentException
     *         if the column is < 0
     */
    public static String findModeForColumn(String initialMode, Array<Pair<Integer, String>> modes, int column) {
        if (column < 0) {
            throw new IllegalArgumentException("Column should be >= 0 but was " + column);
        }
        String mode = initialMode;
        for (Pair<Integer, String> pair : modes.asIterable()) {
            if (pair.first >= column) {
                // We'll use last remembered mode.
                break;
            }
            mode = pair.second;
        }
        return mode;
    }

    /**
     * Builds a "language map".
     * <p/>
     * <p>Language map is an array of pairs {@code (column, mode)}, where column
     * is the boundary between previous mode and current mode.
     * <p/>
     * <p>The array is ordered by the column.
     *
     * @param initialMode
     *         mode before the first token
     * @param tokens
     *         tokens from which to build the map
     * @return array of pairs (column, mode)
     */
    public static Array<Pair<Integer, String>> buildModes(String initialMode, Array<Token> tokens) {
        Array<Pair<Integer, String>> modes = Collections.createArray();
        String currentMode = initialMode;
        int currentColumn = 0;

        for (Token token : tokens.asIterable()) {
            if (token.getType() == TokenType.NEWLINE) {
                // Avoid "brandless" mode to be the last mode in the map.
                break;
            }
            String mode = token.getMode();
            if (!mode.equals(currentMode)) {
                modes.add(new Pair<Integer, String>(currentColumn, mode));
                currentMode = mode;
            }
            currentColumn += token.getValue().length();
        }
        return modes;
    }

    static void addPlaceholders(String mode, StringMap<Array<Token>> splitTokenMap, int width) {
        for (String key : splitTokenMap.getKeys().asIterable()) {
            if (!key.equals(mode)) {
                splitTokenMap.get(key).add(getPlaceholderForMode(key, width));
            }
        }
    }

    private static Token getPlaceholderForMode(String mode, int width) {
        String key = mode + ":" + width;
        if (cachedPlaceholders.containsKey(key)) {
            return cachedPlaceholders.get(key);
        }

        Token token = new Token(mode, TokenType.WHITESPACE, StringUtils.getSpaces(width));
        cachedPlaceholders.put(key, token);
        return token;
    }
}
