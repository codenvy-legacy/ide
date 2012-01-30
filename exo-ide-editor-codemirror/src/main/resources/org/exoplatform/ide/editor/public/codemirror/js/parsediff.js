var DiffParser = Editor.Parser = (function() {
	function completeTo(source, cls) {
		while(!source.endOfLine())
			source.next();
		
		return cls;
	}
	
	function tokenizeDiff(source) {
		var ch = source.next();
		if(/\d/.test(ch) || ch == '@' && source.peek() == '@')
			return completeTo(source, 'diff-header');

		if(ch == '>' || ch == '+')
			return completeTo(source, 'diff-add');
		
		if(ch == '*') {
			if(source.lookAhead('** '))
				return completeTo(source, 'diff-info');
			
			if(source.lookAhead('***'))
				return completeTo(source, 'diff-header');
		}
		
		if(ch == '<')
			return completeTo(source, 'diff-remove');
		
		if(ch == '-') {
			if(source.lookAhead('-- '))
				return completeTo(source, 'diff-info');
		
			return completeTo(source, 'diff-remove');
		}
		
		if(ch == '!' && (source.peek() == ' ' || source.endOfLine()))
			return completeTo(source, 'diff-change');
		
		return completeTo(source, 'diff-nochange');
	}
  
	return {
		make: function(source) {
			source = tokenizer(source, tokenizeDiff);
		
			var iter = {
				next: function() {
					var token = source.next();
					
					if(token.value == '\n') {
						token.indentation = function() {
							return 0;
						};
					}

					return token;
				},
			
				copy: function() {
					return function(_source) {
						source = tokenizer(_source, tokenizeDiff);
						return iter;
					};
				}
			};
			return iter;
		}
	};
})();
