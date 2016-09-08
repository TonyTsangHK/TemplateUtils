package utils.template

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2016-09-07
 * Time: 15:02
 */
enum class TemplateTag(val tagName: String, val startTag: String, val endTag: String) {
    /**
     * Tags:

     * Variable: $@=  [variable key]  =@$,
     * Alternate format: $@= [ multiple keys separated by comma ] =@$, e.g. $@=labels.a,labels.b,currentValue=@$
     * if multiple keys are supplied, the value be the first non null value represented by the supplied keys.

     * Replace new line to br: $@BR= [variable key] =BR@$

     * Replace br to new line: $@NL= [variable key] =NL@$

     * Function: $@F= [json function call definitions] =F@$
     * className: full class name, optional, if not provided, the valueSubstitutor will be used
     * method: method name, if no paramMap / params default to empty parameter method (must be a static method if className is provided)
     * paramMap: parameter map, optional if both params & paramMap exists only paramMap will be processed
     * params: parameters array, optional if paramMap not exists, (parameter class mismatch can be problematic, it is recommended to use paramMap instead)
     * escape: escape the output, optional default false

     * Arithmetic: $@{ [formula expression] }@$

     * Repeat: $@RPT= [parameters (json formatted)] =RPT@$
     * valueLists, value list names array
     * start, startIndex, optional, default 0
     * length, length, optional, default to max list size (biggest size of valueLists, empty string as placeholder if list does not have element on specific index)
     * template, repeating template

     * Variable substitute: $@SUB= [parameters (json formatted) or inline substitute content ] =SUB@$
     * parameters:
     * key: template key, key to search for template content {#} e.g. {0}, {1} ... etc, will be treated as substitute key with the number within as index.
     * substitutes: substitution values, each substitute value is processed as inline template

     * inline content substitute: other template tag can be used within the content
     * Since template processor is using a simple start & end tag matching approach to parse the content
     * Nesting SubVariable tag will break the whole template!!

     * Include: $@INC= [parameters (json formatted)] =INC@$
     * package, resource parent package path (separated by / not .)
     * file: resource file name

     * Comment: $@* [comments] *@$

     */
    VARIABLE("Variable", "$@=", "=@$"),
    FUNCTION("Function", "$@F=", "=F@$"),
    SUBVARIABLE("SubVariable", "$@SUB=", "=SUB@$"),
    BRREPLACE("ReplaceNewLineToBr", "$@BR=", "=BR@$"), NLREPLACE("ReplaceBrToNewLine", "$@NL=", "=NL@$"),
    ARITHMETIC("Arithmetic", "$@{", "}@$"), REPEAT("Repeat", "$@RPT=", "=RPT@$"),
    INCLUDE("Include", "$@INC=", "=INC@$"), COMMENT("Comment", "$@*", "*@$");

    companion object {
        @JvmField val START_PREFIX = "$@"
        @JvmField val END_POSTFIX = "@$"
    }


    override fun toString(): String {
        return "$startTag [...] $endTag"
    }
}