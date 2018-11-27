// Simple JavaScript Templating
// John Resig - http://ejohn.org/ - MIT Licensed
(function () {
	var newRowChar = "!!!newline!!!";
	var singleQ = "!!!singleQ!!!";
	var singleScriptQ = "!!!singleScriptQ!!!";
	
    var cache = {};
    this.Tmpl = function Tmpl(str, data) {
        // Figure out if we're getting a template, or if we need to
        // load the template - and be sure to cache the result.
        var fn = !/\W/.test(str) ?
            cache[str] = cache[str]:
        // Generate a reusable function that will serve as a template
        // generator (and which will be cached).
            new Function("obj",
            "var p=[],print=function(){p.push.apply(p,arguments);};" +
        // Introduce the data as local variables using with(){}
            "with(obj){p.push('" +
        // Convert the template into pure JavaScript
            str
            .replace(/<%([\w\W]*?)(?=%>)%>/ig,function(script){
            	return script.replace(/'/g,singleScriptQ).replace(/[\r\t\n]/g, " ");
            })
            .replace(/[\n]/g, newRowChar)
            .replace(/'/g,singleQ)
            .replace(/[\r\t]/g, " ")
            //.replace(/[\r\t\n]/g, " ")
            .split("<%").join("\t")
            .replace(/((^|%>)[^\t]*)'/g, "$1\r")
            .replace(/\t=(.*?)%>/g, "',$1,'")
            .split("\t").join("');")
            .split("%>").join("p.push('")
            .split("\r").join("\\'")
            .replace(/!!!singleScriptQ!!!/g,"'")
            + "');}return p.join('');");
        // Provide some basic currying to the user
        return data ? fn(data).replace(/!!!singleQ!!!/g,"'").replace(/!!!newline!!!/g,'\n') : fn;
    };
})();