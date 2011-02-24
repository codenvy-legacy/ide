var java_script_api = [
		{
			"name" : "Array",
			"type" : "CLASS",
			"code" : "Array",
			"fullDescription" : "<p>The Data class provides abstract methods to access external resources using Ajax (XMLHttpRequest) requests.</p>",
			"subTokenList" : [

					{
						"name" : "constructor",
						"type" : "PROPERTY",
						"code" : "constructor",
						"fqn" : "Array",
						"fullDescription" : "<p>The constructor property returns the function that created the array object's  prototype.</p><h<h4>Syntax</h4> <p><pre>array.constructor</pre></p>"
					},

					{
						"name" : "length",
						"type" : "PROPERTY",
						"code" : "length",
						"shortDescription" : ":Number",
						"fqn" : "Array",
						"fullDescription" : "<p>The length property sets or returns the number of elements in an array.</p> <h4>Syntax</h4> <p>&lt;scriptarray.length</pre></p>"
					},

					{
						"name" : "prototype",
						"type" : "PROPERTY",
						"code" : "prototype",
						"fqn" : "Array",
						"fullDescription" : "<p>The prototype property allows you to add properties and methods to any object.</p> <h4>Syntax</h4> <p><pre>object.prototype.name=value </pre></p><h4>Note</h4><p>Prototype is a global property which is available with almost all JavaScript objects.</p>"
					},

					{
						"name" : "concat",
						"type" : "METHOD",
						"code" : "concat(array2)",
						"shortDescription" : "(array2, array3, ...): Array",
						"fqn" : "Array",
						"fullDescription" : "<p>The concat() method is used to join two or more arrays.</p> <p>This method does not change the existing arrays, it only returns a copy of the joined arrays.</p> <h4>Syntax</h4> <p><pre>array.concat(array2, array3, ..., arrayX);</pre></p>"
					},

					{
						"name" : "join",
						"type" : "METHOD",
						"code" : "join(separator)",
						"shortDescription" : "(separator): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The join() method joins all elements of an array into a string, and returns the string.</p> <p>The elements will be separated by a specified separator. The default separator is comma (,).</p> <h4>Syntax</h4> <p><pre>array.join(separator)</pre></p>"
					},

					{
						"name" : "pop",
						"type" : "METHOD",
						"code" : "pop()",
						"shortDescription" : "(): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The pop() method removes the last element of an array, and returns that element.</p> <h4>Syntax</h4> <p><pre>array.pop()</pre></p><h4>Note</h4><p>This method changes the length of an array!</p>"
					},

					{
						"name" : "push",
						"type" : "METHOD",
						"code" : "push()",
						"shortDescription" : "(): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The push() method adds new elements to the end of an array, and returns the new length.</p> <h4>Syntax</h4> <p><pre>array.push(element1, element2, ..., elementX)</pre></p><h4>Note</h4><p>This method changes the length of an array!</p>"
					},

					{
						"name" : "reverse",
						"type" : "METHOD",
						"code" : "reverse()",
						"shortDescription" : "(): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The reverse() method reverses the order of the elements in an array (makes the last element first, and the first element last).</p> <h4>Syntax</h4> <p><pre>array.reverse()</pre></p><h4>Example</h4><p>&lt;script type=\"text/javascript\"></p><br><br><p><pre>var fruits = [\"Banana\", \"Orange\", \"Apple\", \"Mango\"];</pre></p><br><p>document.write(fruits.reverse());</p><br><br><p><pre>&lt;/script></pre></p><h4>Note</h4><p>This method changes the original array!</p>"
					},

					{
						"name" : "shift",
						"type" : "METHOD",
						"code" : "shift()",
						"shortDescription" : "(): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The shift() method removes the first element of an array, and returns that element.</p> <h4>Syntax</h4> <p><pre>array.shift()</pre></p><h4>Note</h4><p>This method changes the original array!</p>"
					},

					{
						"name" : "slice",
						"type" : "METHOD",
						"code" : "slice(start, end)",
						"shortDescription" : "(start, end): Array",
						"fqn" : "Array",
						"fullDescription" : "<p>The slice() method selects a part of an array, and returns the new array.</p> <h4>Syntax</h4> <p><pre>array.slice(start, end)</pre></p><h4>Note</h4><p>The original array will not be changed.</p>"
					},

					{
						"name" : "sort",
						"type" : "METHOD",
						"code" : "sort(sortfunc)",
						"shortDescription" : "(sortfunc): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The sort() method sorts the elements of an array.</p> <h4>Syntax</h4> <p><pre>array.sort(sortfunc)</pre></p><h4>Note</h4><p>This method changes the original array!</p>"
					},

					{
						"name" : "splice",
						"type" : "METHOD",
						"code" : "splice(index, howmany, element1)",
						"shortDescription" : "(index, howmany, element1,...): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The splice() method adds and/or removes elements to/from an array, and returns the removed element(s).</p> <h4>Syntax</h4> <p><pre>array.splice(index,howmany,element1,.....,elementX)</pre></p><h4>Note</h4><p>This method changes the original array!</p>"
					},

					{
						"name" : "toString",
						"type" : "METHOD",
						"code" : "toString()",
						"shortDescription" : "(): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The toString() method converts an array to a string and returns the result.</p><h4>Note</h4><p>The returned string will separate the elements in the array with commas.</p> <h4>Syntax</h4> <p><pre>array.toString()</pre></p> <p><pre>&lt;script type=\"text/javascript\"><br><br>var fruits = [\"Banana\", \"Orange\", \"Apple\", \"Mango\"];<br>document.write(fruits.toString());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "unshift",
						"type" : "METHOD",
						"code" : "unshift(element1)",
						"shortDescription" : "(element1,element2, ...): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The unshift() method adds new elements to the beginning of an array, and returns the new length.</p> <h4>Syntax</h4> <p><pre>array.unshift(element1,element2, ..., elementX)</pre></p><h4>Note</h4><p>This method changes the original array!</p>"
					},

					{
						"name" : "valueOf",
						"type" : "METHOD",
						"code" : "valueOf()",
						"shortDescription" : "(): String",
						"fqn" : "Array",
						"fullDescription" : "<p>The valueOf() method returns the primitive value of an array.</p> <h4>Syntax</h4> <p><pre>array.valueOf()</pre></p> <p><pre> &lt;script type=\"text/javascript\"><br><br> var fruits = [\"Banana\", \"Orange\", \"Apple\", \"Mango\"];<br> document.write(fruits.valueOf());<br><br> &lt;/script></pre></p><h4>Note</h4><p>This method is usually called automatically by JavaScript behind the scenes, and not explicitly in code.</p>"
					},

			]

		},
		{
			"name" : "String",
			"type" : "CLASS",
			"code" : "String",
			"fullDescription" : "<p>The String object is used to manipulate a stored piece of text. String objects are created with new String().</p>",
			"subTokenList" : [

					{
						"name" : "constructor",
						"type" : "PROPERTY",
						"code" : "constructor",
						"shortDescription" : ": Function",
						"fqn" : "String",
						"fullDescription" : "<p>The constructor property returns the function that created the String object's prototype.</p><h<h4>Syntax</h4> <p><pre>string.constructor</pre></p>"
					},

					{
						"name" : "length",
						"type" : "PROPERTY",
						"code" : "length",
						"shortDescription" : ": Symbol",
						"fqn" : "String",
						"fullDescription" : "<p>The length property returns the length of a string (in characters).</p><h<h4>Syntax</h4><p><pre>string.length</pre></p>"
					},

					{
						"name" : "prototype",
						"type" : "PROPERTY",
						"code" : "prototype",
						"shortDescription" : ": String",
						"fqn" : "String",
						"fullDescription" : "<p>The prototype property allows you to add properties and methods to an object.</p><h<h4>Syntax</h4><p><pre>object.prototype.name=value</pre></p><h4>Note</h4><p>Prototype is a global property which is available with almost all JavaScript objects.</p>"
					},

					{
						"name" : "charAt",
						"type" : "METHOD",
						"code" : "charAt(index)",
						"shortDescription" : "(index): Symbol",
						"fqn" : "String",
						"fullDescription" : "<p>The charAt() method returns the character at the specified index in a string.The index of the first character is 0, and the index of the last character in a string called \"txt\", is txt.length-1.</p><h<h4>Syntax</h4><p><pre>string.charAt(index)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(\"First character: \" + str.charAt(0) + \"<br />\");<br>document.write(\"Last character: \" + str.charAt(str.length-1));<br><br>&lt;/script></pre></p><h4>Note</h4><p>Prototype is a global property which is available with almost all JavaScript objects.</p>"
					},

					{
						"name" : "charCodeAt",
						"type" : "METHOD",
						"code" : "charCodeAt(index)",
						"shortDescription" : "(index): Symbol",
						"fqn" : "String",
						"fullDescription" : "<p>The charCodeAt() method returns the Unicode of the character at the specified index in a string.The index of the first character is 0, and the index of the last character in a string called \"txt\", is txt.length-1.</p><h4>Syntax</h4><p><pre>string.charAt(index)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(\"First character: \" + str.charCodeAt(0) + \"<br />\");<br>document.write(\"Last character: \" + str.charCodeAt(str.length-1));<br><br>&lt;/script></pre></p><h4>Note</h4><p>Prototype is a global property which is available with almost all JavaScript objects.</p>"
					},

					{
						"name" : "concat",
						"type" : "METHOD",
						"code" : "concat(string2, string3, ...)",
						"shortDescription" : "(string2, string3, ...): String",
						"fqn" : "String",
						"fullDescription" : "<p>The concat() method is used to join two or more strings.This method does not change the existing strings, it only returns a copy of the joined strings.</p><h4>Syntax</h4><p><pre>string.concat(string2, string3, ..., stringX)</pre></p>"
					},

					{
						"name" : "fromCharCode",
						"type" : "METHOD",
						"code" : "fromCharCode(n1, n2, ...)",
						"shortDescription" : "(n1, n2, ...): Symbol",
						"fqn" : "String",
						"fullDescription" : "<p>The fromCharCode() method converts Unicode values to characters.</p><h4>Syntax</h4><p><pre>String.fromCharCode(n1, n2, ..., nX)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>document.write(String.fromCharCode(72,69,76,76,79));<br><br>&lt;/script></pre></p><h4>Note</h4><p>This method is a static method of the String object. The syntax is always String.fromCharCode() and not string.fromCharCode().</p>"
					},

					{
						"name" : "indexOf",
						"type" : "METHOD",
						"code" : "indexOf(searchstring, start)",
						"shortDescription" : "(searchstring, start): String",
						"fqn" : "String",
						"fullDescription" : "<p>The indexOf() method returns the position of the first occurrence of a specified value in a string.This method returns -1 if the value to search for never occurs.</p><h4>Syntax</h4><p><pre>string.indexOf(searchstring, start)</pre></p> "
					},

					{
						"name" : "lastIndexOf",
						"type" : "METHOD",
						"code" : "lastIndexOf(searchstring, start)",
						"shortDescription" : "(searchstring, start): String",
						"fqn" : "String",
						"fullDescription" : "<p>The lastIndexOf() method returns the position of the last found occurrence of a specified value in a string.</p><h4>Syntax</h4><p><pre>string.lastIndexOf(searchstring, start)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>document.write(String.fromCharCode(72,69,76,76,79));<br><br>&lt;/script></pre></p><h4>Note</h4><p><pre>The string is searched backward, but the index returned is the character position from left to right (starting at 0). This method returns -1 if the value to search for never occurs.</pre></p>"
					},

					{
						"name" : "match",
						"type" : "METHOD",
						"code" : "match(regexp)",
						"shortDescription" : "(regexp): String",
						"fqn" : "String",
						"fullDescription" : "<p>The match() method searches for a match between a regular expression and a string, and returns the matches.This method returns an array of matches, or null if no match is found.</p><h4>Syntax</h4><p><pre>string.match(regexp)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"> <br><br>var str=\"The rain in SPAIN stays mainly in the plain\";<br>var patt1=/ain/gi;<br>document.write(str.match(patt1));<br>&lt;/script></pre></p>"
					},

					{
						"name" : "replace",
						"type" : "METHOD",
						"code" : "replace(regexp/substr,newstring)",
						"shortDescription" : "(regexp/substr,newstring): String",
						"fqn" : "String",
						"fullDescription" : "<p>The replace() method searches for a match between a substring (or regular expression) and a string, and replaces the matched substring with a new substring</p><h4>Syntax</h4><p><pre>string.replace(regexp/substr,newstring)</pre></p> "
					},

					{
						"name" : "search",
						"type" : "METHOD",
						"code" : "search(regexp)",
						"shortDescription" : "(regexp): Symbol",
						"fqn" : "String",
						"fullDescription" : "<p>The search() method searches for a match between a regular expression and a string.This method returns the position of the match, or -1 if no match is found.</p><h4>Syntax</h4><p><pre>string.search(regexp)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str=\"Visit W3Schools!\";<br>document.write(str.search(\"W3SCHOOLS\"));<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "slice",
						"type" : "METHOD",
						"code" : "slice(begin,end)",
						"shortDescription" : "(begin,end): String",
						"fqn" : "String",
						"fullDescription" : "<p>The slice() method extracts a part of a string and returns the extracted part in a new string. Otherwise it returns -1.</p><h4>Syntax</h4><p><pre>string.slice(begin,end)</pre></p> "
					},

					{
						"name" : "split",
						"type" : "METHOD",
						"code" : "split(separator, limit)",
						"shortDescription" : "(separator, limit): String",
						"fqn" : "String",
						"fullDescription" : "<p>The split() method is used to split a string into an array of substrings, and returns the new array.</p><h4>Syntax</h4><p><pre>string.split(separator, limit)</pre></p> "
					},

					{
						"name" : "substr",
						"type" : "METHOD",
						"code" : "substr(start,length)",
						"shortDescription" : "(start,length): Symbols",
						"fqn" : "String",
						"fullDescription" : "<p>The substr() method extracts the characters from a string, beginning at \"start\" and through the specified number of character, and returns the new sub string.</p><h4>Syntax</h4><p><pre>string.substr(start,length)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str=\"Hello world!\";<br>document.write(str.substr(3)+\"<br />\");<br>document.write(str.substr(3,4));<br>&lt;/script></pre></p>"
					},

					{
						"name" : "substring",
						"type" : "METHOD",
						"code" : "substring(from, to)",
						"shortDescription" : "(from, to): Symbols",
						"fqn" : "String",
						"fullDescription" : "<p>The substring() method extracts the characters from a string, between two specified indices, and returns the new sub string.This method extracts the characters in a string between \"from\" and \"to\", not including \"to\" itself.</p><h4>Syntax</h4><p><pre>string.substring(from, to)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str=\"Hello world!\";<br>document.write(str.substr(3)+\"<br />\");<br>document.write(str.substr(3,7));<br>&lt;/script></pre></p>"
					},

					{
						"name" : "toUpperCase",
						"type" : "METHOD",
						"code" : "toUpperCase()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The toUpperCase() method converts a string to uppercase letters.</p><h4>Syntax</h4><p><pre>string.toUpperCase()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str=\"Hello world!\";<br>document.write(str.toUpperCase());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "toLowerCase",
						"type" : "METHOD",
						"code" : "toLowerCase()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The toLowerCase() method converts a string to lowercase letters.</p><h4>Syntax</h4><p><pre>string.toLowerCase()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str=\"Hello world!\";<br>document.write(str.toLowerCase());;<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "valueOf",
						"type" : "METHOD",
						"code" : "valueOf()",
						"shortDescription" : "():String",
						"fqn" : "String",
						"fullDescription" : "<p>The valueOf() method returns the primitive value of a String object</p><h4>Syntax</h4><p><pre>string.valueOf()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str=\"Hello world!\";<br>document.write(str.valueOf());<br><br>&lt;/script></pre></p> <h4>Note</h4><p>This method is usually called automatically by JavaScript behind the scenes, and not explicitly in code.</p>"
					},

					{
						"name" : "anchor",
						"type" : "METHOD",
						"code" : "anchor(name)",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The anchor() method is used to create an HTML anchor.This method returns the string embedded in the <a> tag, like this:<a name=\"anchorname\">string</a></pre></p> <h4>Syntax</h4><p><pre>string.anchor(name)</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var txt = \"Chapter 10\";<br>txt.anchor(\"chap10\");<br>alert(txt.anchor(\"myanchor\"));<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "big",
						"type" : "METHOD",
						"code" : "big()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The big() method is used to display a string in a big font.This method returns the string embedded in the <big> tag, like this:<big>string</big>></p> <h4>Syntax</h4><p><pre>string.big()</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.big());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "blink",
						"type" : "METHOD",
						"code" : "blink()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The blink() method is used to display a blinking string.This method returns the string embedded in the <blink> tag, like this:<blink>string</blink></p> <h4>Syntax</h4><p><pre>string.blink()</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.blink());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "bold",
						"type" : "METHOD",
						"code" : "bold()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The bold() method is used to display a string in bold.This method returns the string embedded in the <b> tag, like this:<b>string</b></p> <h4>Syntax</h4><p><pre>string.bold()</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.bold());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "fixed",
						"type" : "METHOD",
						"code" : "fixed()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The fixed() method is used to display a string as teletype text.This method returns the string embedded in the <tt> tag, like this:<tt>string</tt></p> <h4>Syntax</h4><p><pre>string.fixed()</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.fixed());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "fontcolor",
						"type" : "METHOD",
						"code" : "fontcolor(color)",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The fontcolor() method is used to display a string in a specified color.This method returns the string embedded in the <font> tag, like this:<font color=\"colorvalue\">string</font></p> <h4>Syntax</h4><p><pre>string.fontcolor(color)</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.(str.fontcolor(\"green\"));<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "fontsize",
						"type" : "METHOD",
						"code" : "fontsize(size)",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "The fontsize() method is used to display a string in a specified size.This method returns the string embedded in the <font> tag, like this:<font size=\"size\">string</font></p> <h4>Syntax</h4><p><pre>string.fontsize(size)</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.fontsize(7));<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "italics",
						"type" : "METHOD",
						"code" : "italics()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The italics() method is used to display a string in italic.This method returns the string embedded in the <i> tag, like this:<i>string</i></p> <h4>Syntax</h4><p><pre>string.italics()</pre></p>  <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.(str.italics());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "link",
						"type" : "METHOD",
						"code" : "link(url)",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The link() method is used to display a string as a hyperlink.This method returns the string embedded in the <a> tag, like this:<a href=\"url\">string</a></p> <h4>Syntax</h4><p><pre>string.link(url)</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Free Web Building Tutorials!\";<br>document.write(str.link(\"http://www.w3schools.com\"));<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "small",
						"type" : "METHOD",
						"code" : "small()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The small() method is used to display a string in a small font.This method returns the string embedded in the <small> tag, like this:<small>string</small></p> <h4>Syntax</h4><p><pre>string.small()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.small());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "strike",
						"type" : "METHOD",
						"code" : "strike()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The strike() method is used to display a string that is struck out.This method returns the string embedded in the <strike> tag, like this:<strike>string</strike></p> <h4>Syntax</h4><p><pre>string.strike()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.strike());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "sup",
						"type" : "METHOD",
						"code" : "sup()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The sup() method is used to display a string as superscript text.This method returns the string embedded in the <sup> tag, like this:<sup>string</sup></p> <h4>Syntax</h4><p><pre>string.sup()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.sup());<br><br>&lt;/script></pre></p>"
					},

					{
						"name" : "sub",
						"type" : "METHOD",
						"code" : "sub()",
						"shortDescription" : "()",
						"fqn" : "String",
						"fullDescription" : "<p>The sub() method is used to display a string as subscript text.This method returns the string embedded in the <sub> tag, like this:<sub>string</sub></p> <h4>Syntax</h4><p><pre>string.sub()</pre></p> <h4>Example</h4> <p><pre>&lt;script type=\"text/javascript\"><br><br>var str = \"Hello world!\";<br>document.write(str.sub());<br><br>&lt;/script></pre></p>"
					} ]
		} ]

var java_script_grobal_var = [
		{
			"name" : "Window",
			"type" : "CLASS",
			"code" : "window",
			"fullDescription" : "<p>The window object represents an open window in a browser.</p><p>If a document contain frames (&lt;frame&gt; or &lt;iframe&gt; tags), the browser creates one window object for the HTML document, and one additional window object for each frame.</p>",
			"subTokenList" : [
					{
						"name" : "closed",
						"type" : "PROPERTY",
						"code" : "closed",
						"shortDescription" : " : Boolean",
						"fqn" : "Window",
						"fullDescription" : "<p>The closed property returns a Boolean value indicating whether a window has been closed or not.</p><h4>Syntax</h4><p><pre>window.closed</pre></p>"
					},
					{
						"name" : "defaultStatus",
						"type" : "PROPERTY",
						"code" : "defaultStatus",
						"shortDescription" : " : String",
						"fqn" : "Window",
						"fullDescription" : "<p>Sets or returns the default text in the statusbar of a window.</p><h4>Syntax</h4><p><pre>window.closed</pre></p><h4>Note</h4><p>The defaultStatus property does not work in the default configuration of IE, Firefox, Chrome, or Safari. To allow scripts to change the text of the status, the user must set the dom.disable_window_status_change preference to false in the about:config screen. (or in Firefox: \"Tools - Options - Content -Enable JavaScript / Advanced - Allow scripts to change status bar text\").</p>"
					},
					{
						"name" : "frames",
						"type" : "PROPERTY",
						"code" : "frames",
						"shortDescription" : " : Array",
						"fqn" : "Window",
						"fullDescription" : "<p>The frames property returns an array of all the frames (including iframes) in the current window.</p><h4>Syntax</h4><p><pre>window.frames</pre></p>"
					},
					{
						"name" : "innerWidth",
						"type" : "PROPERTY",
						"code" : "innerWidth",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>The innerWidth property sets or returns the inner width of a window's content area.</p><h4>Syntax</h4><p><pre>window.innerWidth=pixels</pre></p>"
					},
					{
						"name" : "innerHeight",
						"type" : "PROPERTY",
						"code" : "innerHeight",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>The innerHeight property sets or returns the inner height of a window's content area.</p><h4>Syntax</h4><p><pre>window.innerHeight=pixels</pre></p>"
					},
					{
						"name" : "length",
						"type" : "PROPERTY",
						"code" : "length",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>The length property returns the number of frames (including iframes) in the current window.</p><h4>Syntax</h4><p><pre>window.length</pre></p>"
					},
					{
						"name" : "name",
						"type" : "PROPERTY",
						"code" : "name",
						"shortDescription" : " : String",
						"fqn" : "Window",
						"fullDescription" : "<p>Sets or returns the name of a window</p><h4>Syntax</h4><p><pre>window.name</pre></p>"
					},
					{
						"name" : "opener",
						"type" : "PROPERTY",
						"code" : "opener",
						"shortDescription" : " : Object",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns a reference to the window that created the window.</p><h4>Syntax</h4><p><pre>window.opener</pre></p>"
					},
					{
						"name" : "outerHeight",
						"type" : "PROPERTY",
						"code" : "outerHeight",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Sets or returns the outer height of a window, including toolbars/scrollbars.</p><h4>Syntax</h4><p><pre>window.outerHeight=pixels</pre></p>"
					},
					{
						"name" : "outerWidth",
						"type" : "PROPERTY",
						"code" : "outerWidth",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Sets or returns the outer width of a window, including toolbars/scrollbars.</p><h4>Syntax</h4><p><pre>window.outerWidth=pixels</pre></p>"
					},
					{
						"name" : "pageXOffset",
						"type" : "PROPERTY",
						"code" : "pageXOffset",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the pixels the current document has been scrolled (horizontally) from the upper left corner of the window</p><h4>Syntax</h4><p><pre>window.pageXOffset</pre></p>"
					},
					{
						"name" : "pageYOffset",
						"type" : "PROPERTY",
						"code" : "pageYOffset",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the pixels the current document has been scrolled (vertically) from the upper left corner of the window.</p><h4>Syntax</h4><p><pre>window.pageYOffset</pre></p>"
					},
					{
						"name" : "parent",
						"type" : "PROPERTY",
						"code" : "parent",
						"shortDescription" : " : Window",
						"fqn" : "Window",
						"fullDescription" : "<p>The parent property returns the parent window of the current window.</p><h4>Syntax</h4><p><pre>window.parent</pre></p>"
					},
					{
						"name" : "screenLeft",
						"type" : "PROPERTY",
						"code" : "screenLeft",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the x coordinate of the window relative to the screen</p><h4>Syntax</h4><p><pre>window.screenLeft</pre></p>"
					},
					{
						"name" : "screenTop",
						"type" : "PROPERTY",
						"code" : "screenTop",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the y coordinate of the window relative to the screen.</p><h4>Syntax</h4><p><pre>window.screenTop</pre></p>"
					},
					{
						"name" : "screenX",
						"type" : "PROPERTY",
						"code" : "screenX",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the x coordinate of the window relative to the screen.</p><h4>Syntax</h4><p><pre>window.screenX</pre></p>"
					},
					{
						"name" : "screenY",
						"type" : "PROPERTY",
						"code" : "screenY",
						"shortDescription" : " : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the y coordinate of the window relative to the screen.</p><h4>Syntax</h4><p><pre>window.screenY</pre></p>"
					},
					{
						"name" : "self",
						"type" : "PROPERTY",
						"code" : "self",
						"shortDescription" : " : Window",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the current window.</p><h4>Syntax</h4><p><pre>window.self</pre></p>"
					},
					{
						"name" : "status",
						"type" : "PROPERTY",
						"code" : "status",
						"shortDescription" : " : String",
						"fqn" : "Window",
						"fullDescription" : "<p>Sets the text in the statusbar of a window.</p><h4>Syntax</h4><p><pre>window.status</pre></p><h4>Note</h4><p>The status property does not work in the default configuration of IE, Firefox, Chrome, or Safari. To allow scripts to change the text of the status, the user must set the dom.disable_window_status_change preference to false in the about:config screen. (or in Firefox: \"Tools - Options - Content -Enable JavaScript / Advanced - Allow scripts to change status bar text\").</p>"
					},
					{
						"name" : "top",
						"type" : "PROPERTY",
						"code" : "top",
						"shortDescription" : " : Window",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the topmost browser window.</p><h4>Syntax</h4><p><pre>window.top</pre></p>"
					},
					{
						"name" : "alert",
						"type" : "METHOD",
						"code" : "alert(message)",
						"shortDescription" : "(message)",
						"fqn" : "Window",
						"fullDescription" : "<p>Displays an alert box with a message and an OK button.</p><h4>Syntax</h4><p><pre>alert(message)</pre></p>"
					},
					{
						"name" : "blur",
						"type" : "METHOD",
						"code" : "blur()",
						"shortDescription" : "()",
						"fqn" : "Window",
						"fullDescription" : "<p>Removes focus from the current window.</p><h4>Syntax</h4><p><pre>alert(message)</pre></p>"
					},
					{
						"name" : "clearInterval",
						"type" : "METHOD",
						"code" : "clearInterval(id_of_setinterval)",
						"shortDescription" : "(id_of_setinterval)",
						"fqn" : "Window",
						"fullDescription" : "<p>The clearInterval() method clears a timer set with the setInterval() method.The ID value returned by setInterval() is used as the parameter for the clearInterval() method.</p><h4>Syntax</h4><p><pre>clearInterval(id_of_setinterval))</pre></p>"
					},
					{
						"name" : "clearTimeout",
						"type" : "METHOD",
						"code" : "clearTimeout(id_of_settimeout)",
						"shortDescription" : "(id_of_settimeout)",
						"fqn" : "Window",
						"fullDescription" : "<p>The clearTimeout() method clears a timer set with the setTimeout() method. The ID value returned by setTimeout() is used as the parameter for the clearTimeout() method.</p><h4>Syntax</h4><p><pre>clearTimeout(id_of_settimeout)</pre></p>"
					},
					{
						"name" : "close",
						"type" : "METHOD",
						"code" : "close()",
						"shortDescription" : "()",
						"fqn" : "Window",
						"fullDescription" : "<p>Closes the current window.</p><h4>Syntax</h4><p><pre>window.close()</pre></p>"
					},
					{
						"name" : "confirm",
						"type" : "METHOD",
						"code" : "confirm(message)",
						"shortDescription" : "(message) : Boolean",
						"fqn" : "Window",
						"fullDescription" : "<p>Displays a dialog box with a specified message, along with an OK and a Cancel button. This method returns true if the visitor clicked \"OK\", and false otherwise.</p><h4>Syntax</h4><p><pre>confirm(message)</pre></p>"
					},
					{
						"name" : "createPopup",
						"type" : "METHOD",
						"code" : "createPopup()",
						"shortDescription" : "() : Window",
						"fqn" : "Window",
						"fullDescription" : "<p>Used to create a pop-up window.</p><h4>Syntax</h4><p><pre>window.createPopup()</pre></p>"
					},
					{
						"name" : "focus",
						"type" : "METHOD",
						"code" : "focus()",
						"shortDescription" : "()",
						"fqn" : "Window",
						"fullDescription" : "<p>Sets focus to the current window.</p><h4>Syntax</h4><p><pre>window.focus()</pre></p>"
					},
					{
						"name" : "moveBy",
						"type" : "METHOD",
						"code" : "moveBy",
						"shortDescription" : "(x,y)",
						"fqn" : "Window",
						"fullDescription" : "<p>Moves a window relative to its current position.</p><h4>Syntax</h4><p><pre>window.moveBy(x,y)</pre></p>"
					},
					{
						"name" : "moveTo",
						"type" : "METHOD",
						"code" : "moveTo(x,y)",
						"shortDescription" : "(x,y)",
						"fqn" : "Window",
						"fullDescription" : "<p>Moves a window to the specified position.</p><h4>Syntax</h4><p><pre>window.moveTo(x,y)</pre></p>"
					},
					{
						"name" : "open",
						"type" : "METHOD",
						"code" : "open(URL,name,specs,replace)",
						"shortDescription" : "(URL,name,specs,replace) : Window",
						"fqn" : "Window",
						"fullDescription" : "<p>Opens a new browser window.</p><h4>Syntax</h4><p><pre>window.open(URL,name,specs,replace)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"5\" border=\"1\" width=\"100%\"><tbody><tr><th>Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>URL</td><td>Optional. Specifies the URL of the page to open. If no URL is specified, a new window with about:blank is opened</td></tr><tr><td>name</td><td>Optional. Specifies the target attribute or the name of the window. The following values are supported:<ul><li>_blank - URL is loaded into a new window. This is default</li><li>_parent - URL is loaded into the parent frame</li><li>_self - URL replaces the current page</li><li>_top - URL replaces any framesets that may be loaded</li><li><i>name</i> - The name of the window</li></ul></td></tr><tr><td>specs</td><td>Optional. A comma-separated list of items. The following values are supported:<br><br><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><td>channelmode=yes|no|1|0</td><td>Whether or not to display the window in theater mode. Default is no. IE only</td></tr><tr><td>directories=yes|no|1|0</td><td>Whether or not to add directory buttons. Default is yes. IE only</td></tr><tr><td>fullscreen=yes|no|1|0</td><td>Whether or not to display the browser in full-screen mode. Default is no. A window in full-screen mode must also be in theater mode. IE only</td></tr><tr><td>height=pixels</td><td>The height of the window. Min. value is 100</td></tr><tr><td>left=pixels</td><td>The left position of the window</td></tr><tr><td>location=yes|no|1|0</td><td>Whether or not to display the address field. Default is yes</td></tr><tr><td>menubar=yes|no|1|0</td><td>Whether or not to display the menu bar. Default is yes</td></tr><tr><td>resizable=yes|no|1|0</td><td>Whether or not the window is resizable. Default is yes</td></tr><tr><td>scrollbars=yes|no|1|0</td><td>Whether or not to display scroll bars. Default is yes</td></tr><tr><td>status=yes|no|1|0</td><td>Whether or not to add a status bar. Default is yes</td></tr><tr><td>titlebar=yes|no|1|0</td><td>Whether or not to display the title bar. Ignored unless the calling application is an HTML Application or a trusted dialog box. Default is yes</td></tr><tr><td>toolbar=yes|no|1|0</td><td>Whether or not to display the browser toolbar. Default is yes</td></tr><tr><td>top=pixels</td><td>The top position of the window. IE only</td></tr><tr><td>width=pixels</td><td>The width of the window. Min. value is 100</td></tr></tbody></table><br></td></tr><tr><td>replace</td><td>Optional.Specifies whether the URL creates a new entry or replaces the current entry in the history list. The following values are supported:<ul><li>true - URL replaces the current document in the history list</li><li>false - URL creates a new entry in the history list</li></ul></td></tr></tbody></table><p></p>"
					},
					{
						"name" : "print",
						"type" : "METHOD",
						"code" : "print()",
						"shortDescription" : "()",
						"fqn" : "Window",
						"fullDescription" : "<p>Prints the content of the current window.</p><h4>Syntax</h4><p><pre>window.print()</pre></p>"
					},
					{
						"name" : "prompt",
						"type" : "METHOD",
						"code" : "prompt(msg,defaultText)",
						"shortDescription" : "(msg,defaultText) : String",
						"fqn" : "Window",
						"fullDescription" : "<p>Displays a dialog box that prompts the visitor for input. This method returns the string the visitor has entered.</p><h4>Syntax</h4><p><pre>prompt(msg,defaultText)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>msg</td><td>Required. The message to display in the dialog box</td></tr><tr><td>defaultText</td><td>Optional. The default input value</td></tr></tbody></table>"
					},
					{
						"name" : "resizeBy",
						"type" : "METHOD",
						"code" : "resizeBy(width,height)",
						"shortDescription" : "(width,height)",
						"fqn" : "Window",
						"fullDescription" : ""
					},
					{
						"name" : "resizeTo",
						"type" : "METHOD",
						"code" : "resizeTo(width,height)",
						"shortDescription" : "(width,height)",
						"fqn" : "Window",
						"fullDescription" : "<p>Resizes the window by the specified pixels.</p><h4>Note</h4><p>This method moves the bottom right corner of the window by the specified number of pixels defined. The top left corner will not be moved (it stays in its original coordinates).</p><h4>Syntax</h4><p><pre>resizeBy(width,height)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr>  <tr><td>width</td><td>Required. A positive or a negative number that specifies how many pixels to resize the width by</td></tr><tr><td>height</td><td>Required. A positive or a negative number that specifies how many pixels to resize the height by</td></tr></tbody></table>"
					},
					{
						"name" : "scrollBy",
						"type" : "METHOD",
						"code" : "scrollBy(xnum,ynum)",
						"shortDescription" : "(xnum,ynum)",
						"fqn" : "Window",
						"fullDescription" : "<p>Scrolls the content by the specified number of pixels.</p><h4>Note</h4><p>For this method to work, the visible property of the window's scrollbar must be set to true!</p><h4>Syntax</h4><p><pre>scrollBy(xnum,ynum)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>xnum</td><td>Required. How many pixels to scroll by, along the x-axis (horizontal)</td></tr><tr><td>ynum</td><td>Required. How many pixels to scroll by, along the y-axis (vertical)</td></tr></tbody></table>"
					},
					{
						"name" : "scrollTo",
						"type" : "METHOD",
						"code" : "scrollTo(xpos,ypos)",
						"shortDescription" : "(xpos,ypos)",
						"fqn" : "Window",
						"fullDescription" : "<p>Scrolls the content to the specified coordinates.</p><h4>Syntax</h4><p><pre>scrollTo(xpos,ypos)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>xpos</td><td>Required. The coordinate to scroll to, along the x-axis</td></tr><tr><td>ypos</td><td>Required. The coordinate to scroll to, along the y-axis</td></tr></tbody></table>"
					},
					{
						"name" : "setInterval",
						"type" : "METHOD",
						"code" : "setInterval(code,millisec,lang)",
						"shortDescription" : "(code,millisec,lang) : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Calls a function or evaluates an expression at specified intervals (in milliseconds). The setInterval() method will continue calling the function until <i>clearInterval()</i> is called, or the window is closed. The ID value returned by setInterval() is used as the parameter for the clearInterval() method.</p><h4>Syntax</h4><p><pre>setInterval(code,millisec,lang)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>code</td><td>Required. A reference to the function or the code to be executed</td></tr><tr><td>millisec</td><td>Required. The intervals (in milliseconds) on how often to execute the code</td></tr><tr><td>lang</td><td>Optional. JScript | VBScript | JavaScript</td></tr></tbody></table>"
					},
					{
						"name" : "setTimeout",
						"type" : "METHOD",
						"code" : "setTimeout(code,millisec,lang)",
						"shortDescription" : "(code,millisec,lang) : Number",
						"fqn" : "Window",
						"fullDescription" : "<p>Calls a function or evaluates an expression after a specified number of milliseconds.</p><h4>Syntax</h4><p><pre>setTimeout(code,millisec,lang)</pre></p><h4>Parameters</h4><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"20%\">Parameter</th><th align=\"left\" width=\"80%\">Description</th></tr><tr><td>code</td><td>Required. A reference to the function or the code to be executed</td></tr><tr><td>millisec</td><td>Required. The number of milliseconds to wait before executing the code</td></tr><tr><td>lang</td><td>Optional. The scripting language: JScript | VBScript | JavaScript</td></tr></tbody></table>"
					},
					{
						"name" : "navigator",
						"type" : "PROPERTY",
						"code" : "navigator",
						"shortDescription" : " : Navigator",
						"varType" : "Navigator",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the Navigator object for the window</p>"
					},
					{
						"name" : "history",
						"type" : "PROPERTY",
						"code" : "history",
						"shortDescription" : " : History",
						"varType" : "History",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the History object for the window.</p>"
					},
					{
						"name" : "location",
						"type" : "PROPERTY",
						"code" : "location",
						"shortDescription" : " : Location",
						"varType" : "Location",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the Location object for the window.</p>"
					},
					{
						"name" : "screen",
						"type" : "PROPERTY",
						"code" : "screen",
						"shortDescription" : " : Screen",
						"varType" : "Screen",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the Screen object for the window.</p>"
					},
					{
						"name" : "document",
						"type" : "PROPERTY",
						"code" : "document",
						"shortDescription" : " : Document",
						"varType" : "Document",
						"fqn" : "Window",
						"fullDescription" : "<p>Returns the Document object for the window.</p>"
					} ]
		},
		{
			"name" : "Screen",
			"type" : "CLASS",
			"code" : "screen",
			"fullDescription" : "<p>The screen object contains information about the visitor's screen.</p>",
			"subTokenList" : [
					{
						"name" : "availHeight",
						"type" : "PROPERTY",
						"code" : "availHeight",
						"shortDescription" : " : Number",
						"fqn" : "Screen",
						"fullDescription" : "<p>The availHeight property returns the height of the visitor's screen, in pixels, minus interface features like the Windows Taskbar.</p><h4>Syntax</h4><p><pre>screen.availHeight</pre></p>"
					},
					{
						"name" : "availWidth",
						"type" : "PROPERTY",
						"code" : "availWidth",
						"shortDescription" : " : Number",
						"fqn" : "Screen",
						"fullDescription" : "<p>The availWidth property returns the width of the visitor's screen, in pixels, minus interface features like the Windows Taskbar.</p><h4>Syntax</h4><p><pre>screen.availWidth</pre></p>"
					},
					{
						"name" : "colorDepth",
						"type" : "PROPERTY",
						"code" : "colorDepth",
						"shortDescription" : " : Number",
						"fqn" : "Screen",
						"fullDescription" : "<p>The colorDepth property returns the bit depth of the color palette for displaying images (in bits per pixel).</p><h4>Syntax</h4><p><pre>screen.colorDepth</pre></p>"
					},
					{
						"name" : "height",
						"type" : "PROPERTY",
						"code" : "height",
						"shortDescription" : " : Number",
						"fqn" : "Screen",
						"fullDescription" : "<p>The height property returns the total height of the visitor's screen, in pixels.</p><h4>Syntax</h4><p><pre>screen.height</pre></p>"
					},
					{
						"name" : "pixelDepth",
						"type" : "PROPERTY",
						"code" : "pixelDepth",
						"shortDescription" : " : Number",
						"fqn" : "Screen",
						"fullDescription" : "<p>The pixelDepth property returns the color resolution (in bits per pixel) of the visitor's screen.</p><h4>Syntax</h4><p><pre>screen.pixelDepth</pre></p>"
					},
					{
						"name" : "width",
						"type" : "PROPERTY",
						"code" : "width",
						"shortDescription" : " : Number",
						"fqn" : "Screen",
						"fullDescription" : "<p>The width property returns the total width of the visitor's screen, in pixels.</p><h4>Syntax</h4><p><pre>screen.width</pre></p>"
					} ]
		},
		{
			"name" : "Navigator",
			"type" : "CLASS",
			"code" : "navigator",
			"fullDescription" : "<p>The navigator object contains information about the browser.</p>",
			"subTokenList" : [
					{
						"name" : "appCodeName",
						"type" : "PROPERTY",
						"code" : "appCodeName",
						"shortDescription" : " : String",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Returns the code name of the browser.</p><h4>Syntax</h4><p><pre>navigator.appCodeName</pre></p>"
					},
					{
						"name" : "appName",
						"type" : "PROPERTY",
						"code" : "appName",
						"shortDescription" : " : String",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Returns the name of the browser.</p><h4>Syntax</h4><p><pre>navigator.appName</pre></p>"
					},
					{
						"name" : "appVersion",
						"type" : "PROPERTY",
						"code" : "appVersion",
						"shortDescription" : " : String",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Returns the version information of the browser.</p><h4>Syntax</h4><p><pre>navigator.appVersion</pre></p>"
					},
					{
						"name" : "cookieEnabled",
						"type" : "PROPERTY",
						"code" : "cookieEnabled",
						"shortDescription" : " : Boolean",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Determines whether cookies are enabled in the browser.</p><h4>Syntax</h4><p><pre>navigator.cookieEnabled</pre></p>"
					},
					{
						"name" : "platform",
						"type" : "PROPERTY",
						"code" : "platform",
						"shortDescription" : " : String",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Returns for which platform the browser is compiled.</p><h4>Syntax</h4><p><pre>navigator.platform</pre></p>"
					},
					{
						"name" : "userAgent",
						"type" : "PROPERTY",
						"code" : "userAgent",
						"shortDescription" : " : String",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Returns the user-agent header sent by the browser to the server</p><h4>Syntax</h4><p><pre>navigator.userAgent</pre></p>"
					},
					{
						"name" : "javaEnabled",
						"type" : "METHOD",
						"code" : "javaEnabled()",
						"shortDescription" : "() : Boolean",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Specifies whether or not the browser has Java enabled.</p><h4>Syntax</h4><p><pre>navigator.javaEnabled()</pre></p>"
					},
					{
						"name" : "taintEnabled",
						"type" : "METHOD",
						"code" : "taintEnabled",
						"shortDescription" : "() : Boolean",
						"fqn" : "Navigator",
						"fullDescription" : "<p>Specifies whether or not the browser has Java enabled.</p><h4>Syntax</h4><p><pre></pre>navigator.taintEnabled()</p>"
					} ]
		},
		{
			"name" : "Location",
			"type" : "CLASS",
			"code" : "location",
			"fullDescription" : "The location object contains information about the current URL. The location object is part of the window object and is accessed through the window.location property.",
			"subTokenList" : [
					{
						"name" : "hash",
						"type" : "PROPERTY",
						"code" : "hash",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the anchor portion of a URL.</p><h4>Syntax</h4><p><pre>location.hash</pre></p>"
					},
					{
						"name" : "host",
						"type" : "PROPERTY",
						"code" : "host",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the hostname and port of a URL.</p><h4>Syntax</h4><p><pre>location.host</pre></p>"
					},
					{
						"name" : "hostname",
						"type" : "PROPERTY",
						"code" : "hostname",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the hostname of a URL.</p><h4>Syntax</h4><p><pre>location.hostname</pre></p>"
					},
					{
						"name" : "href",
						"type" : "PROPERTY",
						"code" : "href",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the entire URL.</p><h4>Syntax</h4><p><pre>location.href</pre></p>"
					},
					{
						"name" : "pathname",
						"type" : "PROPERTY",
						"code" : "pathname",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the path name of a URL.</p><h4>Syntax</h4><p><pre>location.pathname</pre></p>"
					},
					{
						"name" : "port",
						"type" : "PROPERTY",
						"code" : "port",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the port number the server uses for a URL.</p><h4>Syntax</h4><p><pre>location.port</pre></p>"
					},
					{
						"name" : "protocol",
						"type" : "PROPERTY",
						"code" : "protocol",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the protocol of a URL.</p><h4>Syntax</h4><p><pre>location.protocol</pre></p>"
					},
					{
						"name" : "search",
						"type" : "PROPERTY",
						"code" : "search",
						"shortDescription" : " : String",
						"fqn" : "Location",
						"fullDescription" : "<p>Returns the query portion of a URL.</p><h4>Syntax</h4><p><pre>location.search</pre></p>"
					},
					{
						"name" : "assign",
						"type" : "METHOD",
						"code" : "assign(URL)",
						"shortDescription" : "(URL)",
						"fqn" : "Location",
						"fullDescription" : "<p>Loads a new document.</p><h4>Syntax</h4><p><pre>location.assign(URL)</pre></p>"
					},
					{
						"name" : "reload",
						"type" : "METHOD",
						"code" : "reload()",
						"shortDescription" : "()",
						"fqn" : "Location",
						"fullDescription" : "<p>Reloads the current document.</p><h4>Syntax</h4><p><pre>location.reload()</pre></p>"
					},
					{
						"name" : "replace",
						"type" : "METHOD",
						"code" : "replace(newURL)",
						"shortDescription" : "(newURL)",
						"fqn" : "Location",
						"fullDescription" : "<p>Replaces the current document with a new one.</p><h4>Syntax</h4><p><pre>location.replace(newURL)</pre></p>"
					} ]
		},
		{
			"name" : "History",
			"type" : "CLASS",
			"code" : "history",
			"fullDescription" : "<p>The history object contains the URLs visited by the user (within a browser window).The history object is part of the window object and is accessed through the window.history property.</p>",
			"subTokenList" : [
					{
						"name" : "length",
						"type" : "PROPERTY",
						"code" : "length",
						"shortDescription" : " : Number",
						"fqn" : "History",
						"fullDescription" : "<p>The length property returns the number of URLs in the history list. Internet Explorer and Opera start at 0, while Firefox, Chrome, and Safari start at 1.</p><h4>Syntax</h4><p><pre>history.length</pre></p>"
					},
					{
						"name" : "back",
						"type" : "METHOD",
						"code" : "back()",
						"shortDescription" : "()",
						"fqn" : "History",
						"fullDescription" : "<p>Loads the previous URL in the history list</p><h4>Syntax</h4><p><pre>history.back()</pre></p>"
					},
					{
						"name" : "forward",
						"type" : "METHOD",
						"code" : "forward()",
						"shortDescription" : "()",
						"fqn" : "History",
						"fullDescription" : "<p>Loads the next URL in the history list</p><h4>Syntax</h4><p><pre>history.forward()</pre></p>"
					},
					{
						"name" : "go",
						"type" : "METHOD",
						"code" : "go(number|URL)",
						"shortDescription" : "(number|URL)",
						"fqn" : "History",
						"fullDescription" : "<p>Loads a specific URL from the history list. The parameter can either be a number which goes to the URL within the specific position (-1 goes back one page, 1 goes forward one page), or a string. The string must be a partial or full URL, and the function will go to the first URL that matches the string.</p><h4>Syntax</h4><p><pre>history.go(number|URL)</pre></p>"
					} ]
		},
		{
			"name" : "document",
			"type" : "CLASS",
			"code" : "document",
			"fullDescription" : "Each HTML document loaded into a browser window becomes a Document object. The Document object provides access to all HTML elements in a page, from within a script. Tip: The Document object is also part of the Window object, and can be accessed through the window.document property.",
			"subTokenList" : [
					{
						"name" : "cookie",
						"type" : "PROPERTY",
						"code" : "cookie",
						"fqn" : "document",
						"fullDescription" : "<p>The cookie property returns all name/value pairs of cookies in the current document.</p>"
					},
					{
						"name" : "domain",
						"type" : "PROPERTY",
						"code" : "domain",
						"fqn" : "document",
						"fullDescription" : "The domain property returns the domain name of the server that loaded the current document.</p>"
					},
					{
						"name" : "lastModified",
						"type" : "PROPERTY",
						"code" : "lastModified",
						"fqn" : "document",
						"fullDescription" : "The lastModified property returns the date and time the current document was last modified.</p>"
					},
					{
						"name" : "readyState",
						"type" : "PROPERTY",
						"code" : "readyState",
						"fqn" : "document",
						"fullDescription" : "<p>The readyState property returns the (loading) status of the current document.</p><p>This property returns one of four values:</p><ul><li>uninitialized - Has not started loading yet</li><li>loading - Is loading</li><li>interactive - Has loaded enough and the user can interact with it</li><li>complete - Fully loaded<br></li></ul><p><b>The readyState property is supported in all major browsers, except Firefox.</b></p>"
					},
					{
						"name" : "referrer",
						"type" : "PROPERTY",
						"code" : "referrer",
						"fqn" : "document",
						"fullDescription" : "<p>The referrer property returns the URL of the document that loaded the current document.</p>"
					},
					{
						"name" : "title",
						"type" : "PROPERTY",
						"code" : "title",
						"fqn" : "document",
						"fullDescription" : "<p>The title property returns the title of the current document (the text inside the HTML title element).</p>"
					},
					{
						"name" : "URL",
						"type" : "PROPERTY",
						"code" : "URL",
						"fqn" : "document",
						"fullDescription" : "<p>The URL property returns the full URL of the current document.</p>"
					},
					{
						"name" : "close",
						"type" : "METHOD",
						"code" : "close()",
						"shortDescription" : "()",
						"fqn" : "document",
						"fullDescription" : "<p>The close() method closes the output stream previously opened with the <b>document.open()</b> method, and displays the collected data in this process.</p>"
					},
					{
						"name" : "getElementById",
						"type" : "METHOD",
						"code" : "getElementById(id)",
						"shortDescription" : "(id)",
						"fqn" : "document",
						"fullDescription" : "<p>The getElementById() method accesses the first element with the specified id.</p><h4>Syntax</h4><p><pre>document.getElementById(\"id\")</pre></p><p><b>id</b>The id of the element you want to access/manipulate</p>"
					},
					{
						"name" : "getElementsByName",
						"type" : "METHOD",
						"code" : "getElementsByName(name)",
						"shortDescription" : "(name)",
						"fqn" : "document",
						"fullDescription" : "<p>The getElementsByName() method accesses all elements with the specified name.</p><h4>Syntax</h4><p><pre>document.getElementsByName(name)</pre></p><b>name</b> The name of the element you want to access/manipulate"
					},
					{
						"name" : "getElementsByTagName",
						"type" : "METHOD",
						"code" : "getElementsByTagName(tagname)",
						"shortDescription" : "(tagname)",
						"fqn" : "document",
						"fullDescription" : "<p>The getElementsByTagName() method accesses all elements with the specified tagname.</p><h4>Syntax</h4><p><pre>document.getElementsByTagName(tagname)</pre></p><b>tagname</b> The tagname of the element you want to access/manipulate"
					},
					{
						"name" : "open",
						"type" : "METHOD",
						"code" : "open()",
						"shortDescription" : "()",
						"fqn" : "document",
						"fullDescription" : "<p>The open() method opens an output stream to collect the output from any <b>document.write()</b> or <b>document.writeln()</a> methods.</p><p>Once all the writes are performed, the <b>document.close()</b> method causes any output written to the output stream to bedisplayed.</p><p><b>Note:</b> If a document already exists in the target, it will be cleared. If this method has no arguments, a new window(about:blank) is displayed.</p><table cellspacing=\"0\" cellpadding=\"0\" border=\"1\" width=\"100%\"><tbody><tr><th align=\"left\" width=\"15%\">Parameter</th><th align=\"left\" width=\"85%\">Description</th></tr><tr><td>MIMEtype</td><td>Optional. The type of document you are writing to. Default value is \"text/html\"</td></tr><tr><td>replace</td><td>Optional. If set, the history entry for the new document inherits the history entry from the document which opened this document</td></tr></tbody></table>"
					},
					{
						"name" : "write",
						"type" : "METHOD",
						"code" : "write()",
						"shortDescription" : "()",
						"fqn" : "document",
						"fullDescription" : "<p>The write() method writes HTML expressions or JavaScript code to a document.</p>"
					},
					{
						"name" : "writeln",
						"type" : "METHOD",
						"code" : "writeln()",
						"shortDescription" : "()",
						"fqn" : "document",
						"fullDescription" : "<p>The writeln() method is identical to the write() method, with the addition of writing a newline character after each statement.</p>"
					},
					{
						"name" : "anchors",
						"type" : "PROPERTY",
						"code" : "anchors[]",
						"fqn" : "document",
						"fullDescription" : "<p>The anchors collection returns an array of all the anchors in the current document.</p>"
					},
					{
						"name" : "forms",
						"type" : "PROPERTY",
						"code" : "forms[]",
						"fqn" : "document",
						"fullDescription" : "<p>The forms collection returns an array of all the forms in the current document.</p>"
					},
					{
						"name" : "images",
						"type" : "PROPERTY",
						"code" : "images[]",
						"fqn" : "document",
						"fullDescription" : "<p>The images collection returns an array of all the images in the current document.</p>"
					},
					{
						"name" : "links",
						"type" : "PROPERTY",
						"code" : "links[]",
						"fqn" : "document",
						"fullDescription" : "<p>The links collection returns an array of all the links in the current document.</p>"
					}

			]
		} ]