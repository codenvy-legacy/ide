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

(function () {
    /*
     Copyright (c) 2011 Marijn Haverbeke

     Licensed under the MIT license:
     http://opensource.org/licenses/mit-license
     */
    CodeMirror.defineMode("css", function (n) {
        function j(c, a) {
            q = a;
            return c
        }

        function g(c, a) {
            var b = c.next();
            if ("@" == b)return c.eatWhile(/[\w\\\-]/), j("meta", c.current());
            if ("/" == b && c.eat("*"))return a.tokenize = d, d(c, a);
            if ("<" == b && c.eat("!"))return a.tokenize = t, t(c, a);
            if ("=" == b)j(null, "compare"); else {
                if (("~" == b || "|" == b) && c.eat("="))return j(null, "compare");
                if ('"' == b || "'" == b)return a.tokenize = u(b), a.tokenize(c, a);
                if ("#" == b)return c.eatWhile(/[\w\\\-]/), j("atom", "hash");
                if ("!" == b)return c.match(/^\s*\w*/), j("keyword",
                    "important");
                if (/\d/.test(b))return c.eatWhile(/[\w.%]/), j("number", "unit");
                if (/[,.+>*\/]/.test(b))return j(null, "select-op");
                if (/[;{}:\[\]]/.test(b))return j(null, b);
                c.eatWhile(/[\w\\\-]/);
                return j("variable", "variable")
            }
        }

        function d(c, a) {
            for (var b = !1, d; null != (d = c.next());) {
                if (b && "/" == d) {
                    a.tokenize = g;
                    break
                }
                b = "*" == d
            }
            return j("comment", "comment")
        }

        function t(c, a) {
            for (var b = 0, d; null != (d = c.next());) {
                if (2 <= b && ">" == d) {
                    a.tokenize = g;
                    break
                }
                b = "-" == d ? b + 1 : 0
            }
            return j("comment", "comment")
        }

        function u(c) {
            return function (a, b) {
                for (var d = !1, m; null != (m = a.next()) && (m != c || d);)d = !d && "\\" == m;
                d || (b.tokenize = g);
                return j("string", "string")
            }
        }

        var r = n.indentUnit, q;
        return{startState: function (c) {
            return{tokenize: g, baseIndent: c || 0, stack: []}
        }, token: function (c, a) {
            if (c.eatSpace())return null;
            var b = a.tokenize(c, a), d = a.stack[a.stack.length - 1];
            if ("hash" == q && "rule" != d)b = "string-2"; else if ("variable" == b)if ("rule" == d)b = "number"; else if (!d || "@media{" == d)b = "tag";
            "rule" == d && /^[\{\};]$/.test(q) && a.stack.pop();
            "{" == q ? "@media" == d ? a.stack[a.stack.length -
                1] = "@media{" : a.stack.push("{") : "}" == q ? a.stack.pop() : "@media" == q ? a.stack.push("@media") : "{" == d && "comment" != q && a.stack.push("rule");
            return b
        }, indent: function (c, a) {
            var b = c.stack.length;
            /^\}/.test(a) && (b -= "rule" == c.stack[c.stack.length - 1] ? 2 : 1);
            return c.baseIndent + b * r
        }, electricChars: "}"}
    });
    CodeMirror.defineMIME("text/css", "css");
})();