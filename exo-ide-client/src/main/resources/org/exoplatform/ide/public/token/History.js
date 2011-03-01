var History = {
	"name" : "History",
	"type" : "CLASS",
	"code" : "history",
	"fqn" : "History",
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
}
