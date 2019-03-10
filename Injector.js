"use strict";

var output	= new Array();
var debug	= false;

[
	$ARRAY
].forEach(function(func, index) {
	let source = func.toSource();
	
	if(debug) {
		if(source == '(function (e, t) {})') {
			java.lang.System.err.println('[' + (index + 1) + '] ' + source);
		} else {
			java.lang.System.out.println('[' + (index + 1) + '] ' + source);		
		}
	}
	
	output[index] = source;
});

if(debug) {
	java.lang.System.err.println('Functions Length: ' + output.length);
}