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
/* Parse function for Java. Makes use of the tokenizer from
 * parsejspmixed.js. Note that your parsers do not have to be
 * this complicated -- if you don't want to recognize local variables,
 * in many languages it is enough to just look for braces, semicolons,
 * parentheses, etc, and know when you are inside a string or comment.
 *
 * See manual.html for more info about the parser interface.
 */

var JspParser = Editor.Parser = (function () {
    if (!(CSSParser && JSParser && XMLParser && JavaParser))
        throw new Error("CSS, JS, XML and JavaParser parsers must be loaded for JSP mode to work.");
    XMLParser.configure({useHTMLKludges: true});

    function parseMixed(stream) {
        var htmlParser = XMLParser.make(stream), localParser = null, inTag = false;
        var iter = {next: top, copy: copy};

        function top() {
            var token = htmlParser.next();

            // see if this opens a java block
            if ((token.content == "<%") && (token.style == "jsp-java")) {
                iter.next = local(JavaParser, "%>");
            }

            if (token.content == "<")
                inTag = true;
            else if (token.style == "xml-tagname" && inTag === true)
                inTag = token.content.toLowerCase();

            else if (token.content == ">") {
                if (inTag == "script")
                    iter.next = local(JSParser, "</script");
                else if (inTag == "style")
                    iter.next = local(CSSParser, "</style");

                inTag = false;
            }
            return token;
        }

        function local(parser, tag) {
            var baseIndent = htmlParser.indentation();
            localParser = parser.make(stream, baseIndent + indentUnit);
            return function () {
                if (stream.lookAhead(tag, false, false, true)) {
                    localParser = null;
                    iter.next = top;
                    return top();
                }

                var token = localParser.next();
                var lt = token.value.lastIndexOf("<"), sz = Math.min(token.value.length - lt, tag.length);
                if (lt != -1 && token.value.slice(lt, lt + sz).toLowerCase() == tag.slice(0, sz) &&
                    stream.lookAhead(tag.slice(sz), false, false, true)) {
                    stream.push(token.value.slice(lt));
                    token.value = token.value.slice(0, lt);
                }

                if (token.indentation) {
                    var oldIndent = token.indentation;
                    token.indentation = function (chars) {
                        if (chars == "</" || chars == "%>")
                            return baseIndent;
                        else
                            return oldIndent(chars);
                    }
                } else {
                    token.indentation = function () {
                    };
                }

                return token;
            };
        }

        function copy() {
            var _html = htmlParser.copy(), _local = localParser && localParser.copy(),
                _next = iter.next, _inTag = inTag;
            return function (_stream) {
                stream = _stream;
                htmlParser = _html(_stream);
                localParser = _local && _local(_stream);
                iter.next = _next;
                inTag = _inTag;
                return iter;
            };
        }

        return iter;
    }

    return {
        make: parseMixed,
        electricChars: "{}/:"
    };

})();
