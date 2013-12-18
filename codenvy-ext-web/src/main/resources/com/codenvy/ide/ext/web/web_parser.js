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

    CodeMirror.defineMode("javascript", function (n, j) {
        function g(l, e) {
            for (var f = !1, a; null != (a = l.next());) {
                if (a == e && !f)return!1;
                f = !f && "\\" == a
            }
            return f
        }

        function d(l, e, f) {
            F = l;
            K = f;
            return e
        }

        function t(l, e) {
            var f = l.next();
            if ('"' == f || "'" == f) {
                var a;
                a = l;
                var b = e, f = u(f);
                b.tokenize = f;
                return a = f(a, b)
            }
            if (/[\[\]{}\(\),;\:\.]/.test(f))return d(f);
            if ("0" == f && l.eat(/x/i))return l.eatWhile(/[\da-f]/i), d("number", "number");
            if (/\d/.test(f))return l.match(/^\d*(?:\.\d*)?(?:[eE][+\-]?\d+)?/), d("number", "number");
            if ("/" == f) {
                if (l.eat("*"))return a =
                    l, b = e, f = r, b.tokenize = f, a = f(a, b);
                if (l.eat("/"))return l.skipToEnd(), d("comment", "comment");
                if (e.reAllowed)return g(l, "/"), l.eatWhile(/[gimy]/), d("regexp", "string-2");
                l.eatWhile(H);
                return d("operator", null, l.current())
            }
            if ("#" == f)return l.skipToEnd(), d("error", "error");
            if (H.test(f))return l.eatWhile(H), d("operator", null, l.current());
            l.eatWhile(/[\w\$_]/);
            a = l.current();
            return(b = J.propertyIsEnumerable(a) && J[a]) && e.kwAllowed ? d(b.type, b.style, a) : d("variable", "variable", a)
        }

        function u(l) {
            return function (f, e) {
                g(f, l) || (e.tokenize = t);
                return d("string", "string")
            }
        }

        function r(l, f) {
            for (var e = !1, a; a = l.next();) {
                if ("/" == a && e) {
                    f.tokenize = t;
                    break
                }
                e = "*" == a
            }
            return d("comment", "comment")
        }

        function q(l, f, e, a, b, c) {
            this.indented = l;
            this.column = f;
            this.type = e;
            this.prev = b;
            this.info = c;
            null != a && (this.align = a)
        }

        function c() {
            for (var l = arguments.length - 1; 0 <= l; l--)y.cc.push(arguments[l])
        }

        function a() {
            c.apply(null, arguments);
            return!0
        }

        function b(l) {
            var f = y.state;
            if (f.context) {
                y.marked = "def";
                for (var e = f.localVars; e; e = e.next)if (e.name ==
                    l)return;
                f.localVars = {name: l, next: f.localVars}
            }
        }

        function w() {
            y.state.context || (y.state.localVars = f);
            y.state.context = {prev: y.state.context, vars: y.state.localVars}
        }

        function m() {
            y.state.localVars = y.state.context.vars;
            y.state.context = y.state.context.prev
        }

        function s(l, f) {
            var e = function () {
                var e = y.state;
                e.lexical = new q(e.indented, y.stream.column(), l, null, e.lexical, f)
            };
            e.lex = !0;
            return e
        }

        function h() {
            var l = y.state;
            l.lexical.prev && (")" == l.lexical.type && (l.indented = l.lexical.indented), l.lexical = l.lexical.prev)
        }

        function k(l) {
            return function (f) {
                return f == l ? a() : ";" == l ? c() : a(arguments.callee)
            }
        }

        function i(f) {
            return"var" == f ? a(s("vardef"), C, k(";"), h) : "keyword a" == f ? a(s("form"), o, i, h) : "keyword b" == f ? a(s("form"), i, h) : "{" == f ? a(s("}"), z, h) : ";" == f ? a() : "function" == f ? a(I) : "for" == f ? a(s("form"), k("("), s(")"), P, k(")"), h, i, h) : "variable" == f ? a(s("stat"), D) : "switch" == f ? a(s("form"), o, s("}", "switch"), k("{"), z, h, h) : "case" == f ? a(o, k(":")) : "default" == f ? a(k(":")) : "catch" == f ? a(s("form"), w, k("("), L, k(")"), i, h, m) : c(s("stat"), o, k(";"),
                h)
        }

        function o(f) {
            return x.hasOwnProperty(f) ? a(v) : "function" == f ? a(I) : "keyword c" == f ? a(p) : "(" == f ? a(s(")"), p, k(")"), h, v) : "operator" == f ? a(o) : "[" == f ? a(s("]"), e(o, "]"), h, v) : "{" == f ? a(s("}"), e(B, "}"), h, v) : a()
        }

        function p(f) {
            return f.match(/[;\}\)\],]/) ? c() : c(o)
        }

        function v(f, b) {
            if ("operator" == f && /\+\+|--/.test(b))return a(v);
            if ("operator" == f)return a(o);
            if (";" != f) {
                if ("(" == f)return a(s(")"), e(o, ")"), h, v);
                if ("." == f)return a(A, v);
                if ("[" == f)return a(s("]"), o, k("]"), h, v)
            }
        }

        function D(f) {
            return":" == f ? a(h, i) : c(v, k(";"),
                h)
        }

        function A(f) {
            if ("variable" == f)return y.marked = "property", a()
        }

        function B(f) {
            "variable" == f && (y.marked = "property");
            if (x.hasOwnProperty(f))return a(k(":"), o)
        }

        function e(f, e) {
            function b(c) {
                return"," == c ? a(f, b) : c == e ? a() : a(k(e))
            }

            return function (z) {
                return z == e ? a() : c(f, b)
            }
        }

        function z(f) {
            return"}" == f ? a() : c(i, z)
        }

        function C(f, e) {
            return"variable" == f ? (b(e), a(M)) : a()
        }

        function M(f, e) {
            if ("=" == e)return a(o, M);
            if ("," == f)return a(C)
        }

        function P(f) {
            return"var" == f ? a(C, G) : ";" == f ? c(G) : "variable" == f ? a(Q) : c(G)
        }

        function Q(f, e) {
            return"in" == e ? a(o) : a(v, G)
        }

        function G(f, e) {
            return";" == f ? a(N) : "in" == e ? a(o) : a(o, k(";"), N)
        }

        function N(f) {
            ")" != f && a(o)
        }

        function I(f, c) {
            if ("variable" == f)return b(c), a(I);
            if ("(" == f)return a(s(")"), w, e(L, ")"), h, i, m)
        }

        function L(f, e) {
            if ("variable" == f)return b(e), a()
        }

        var E = n.indentUnit, O = j.json, J = function () {
            function f(e) {
                return{type: e, style: "keyword"}
            }

            var e = f("keyword a"), a = f("keyword b"), b = f("keyword c"), c = f("operator"), z = {type: "atom", style: "atom"};
            return{"if": e, "while": e, "with": e, "else": a, "do": a, "try": a,
                "finally": a, "return": b, "break": b, "continue": b, "new": b, "delete": b, "throw": b, "var": f("var"), "const": f("var"), let: f("var"), "function": f("function"), "catch": f("catch"), "for": f("for"), "switch": f("switch"), "case": f("case"), "default": f("default"), "in": c, "typeof": c, "instanceof": c, "true": z, "false": z, "null": z, undefined: z, NaN: z, Infinity: z}
        }(), H = /[+\-*&%=<>!?|]/, F, K, x = {atom: !0, number: !0, variable: !0, string: !0, regexp: !0}, y = {state: null, column: null, marked: null, cc: null}, f = {name: "this", next: {name: "arguments"}};
        h.lex = !0;
        return{startState: function (f) {
            return{tokenize: t, reAllowed: !0, kwAllowed: !0, cc: [], lexical: new q((f || 0) - E, 0, "block", !1), localVars: j.localVars, context: j.localVars && {vars: j.localVars}, indented: 0}
        }, token: function (f, e) {
            f.sol() && (e.lexical.hasOwnProperty("align") || (e.lexical.align = !1), e.indented = f.indentation());
            if (f.eatSpace())return null;
            var a = e.tokenize(f, e);
            if ("comment" == F)return a;
            e.reAllowed = !!("operator" == F || "keyword c" == F || F.match(/^[\[{}\(,;:]$/));
            e.kwAllowed = "." != F;
            var b;
            a:{
                var c = e, z = F, w = K, d =
                    f, C = c.cc;
                y.state = c;
                y.stream = d;
                y.marked = null;
                y.cc = C;
                c.lexical.hasOwnProperty("align") || (c.lexical.align = !0);
                for (; ;)if (d = C.length ? C.pop() : O ? o : i, d(z, w)) {
                    for (; C.length && C[C.length - 1].lex;)C.pop()();
                    if (y.marked) {
                        b = y.marked;
                        break a
                    }
                    if (b = "variable" == z)b:{
                        for (c = c.localVars; c; c = c.next)if (c.name == w) {
                            b = !0;
                            break b
                        }
                        b = void 0
                    }
                    if (b) {
                        b = "variable-2";
                        break a
                    }
                    b = a;
                    break a
                }
            }
            return b
        }, indent: function (f, e) {
            if (f.tokenize != t)return 0;
            var a = e && e.charAt(0), b = f.lexical, c = b.type, z = a == c;
            return"vardef" == c ? b.indented + 4 : "form" == c &&
                "{" == a ? b.indented : "stat" == c || "form" == c ? b.indented + E : "switch" == b.info && !z ? b.indented + (/^(?:case|default)\b/.test(e) ? E : 2 * E) : b.align ? b.column + (z ? 0 : 1) : b.indented + (z ? 0 : E)
        }, electricChars: ":{}"}
    });
    CodeMirror.defineMIME("text/javascript", "javascript");
    CodeMirror.defineMIME("application/json", {name: "javascript", json: !0});
    /*

     Copyright (c) 2011 Marijn Haverbeke

     Licensed under the MIT license:
     http://opensource.org/licenses/mit-license
     */


    CodeMirror.mimeModes.hasOwnProperty("text/html") || CodeMirror.defineMIME("text/html", {name: "xml", htmlMode: !0});
    /*
     Copyright (c) 2012 Marijn Haverbeke

     Licensed under the MIT license:
     http://opensource.org/licenses/mit-license
     */
    CodeMirror.defineMode("htmlmixed", function (n) {
        function j(c, a) {
            var b = u.token(c, a.htmlState);
            "tag" == b && (">" == c.current() && a.htmlState.context) && (/^script$/i.test(a.htmlState.context.tagName) ? (a.token = d, a.localState = r.startState(u.indent(a.htmlState, "")), a.mode = "javascript") : /^style$/i.test(a.htmlState.context.tagName) && (a.token = t, a.localState = q.startState(u.indent(a.htmlState, "")), a.mode = "css"));
            return b
        }

        function g(c, a, b) {
            var d = c.current(), a = d.search(a);
            -1 < a && c.backUp(d.length - a);
            return b
        }

        function d(c, a) {
            return c.match(/^<\/\s*script\s*>/i, !1) ? (a.token = j, a.localState = null, a.mode = "html", j(c, a)) : g(c, /<\/\s*script\s*>/, r.token(c, a.localState))
        }

        function t(c, a) {
            return c.match(/^<\/\s*style\s*>/i, !1) ? (a.token = j, a.localState = null, a.mode = "html", j(c, a)) : g(c, /<\/\s*style\s*>/, q.token(c, a.localState))
        }

        var u = CodeMirror.getMode(n, {name: "xml", htmlMode: !0}), r = CodeMirror.getMode(n, "javascript"), q = CodeMirror.getMode(n, "css");
        return{startState: function () {
            var c = u.startState();
            return{token: j, localState: null, mode: "html",
                htmlState: c}
        }, copyState: function (c) {
            if (c.localState)var a = CodeMirror.copyState(c.token == t ? q : r, c.localState);
            return{token: c.token, localState: a, mode: c.mode, htmlState: CodeMirror.copyState(u, c.htmlState)}
        }, token: function (c, a) {
            return a.token(c, a)
        }, indent: function (c, a) {
            return c.token == j || /^\s*<\//.test(a) ? u.indent(c.htmlState, a) : c.token == d ? r.indent(c.localState, a) : q.indent(c.localState, a)
        }, compareStates: function (c, a) {
            return c.mode != a.mode ? !1 : c.localState ? CodeMirror.Pass : u.compareStates(c.htmlState, a.htmlState)
        },
            electricChars: "/{}:"}
    });
    CodeMirror.defineMIME("text/html", "htmlmixed");
})();