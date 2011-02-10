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