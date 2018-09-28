package utils.template

import org.apache.commons.lang3.StringEscapeUtils
import org.slf4j.LoggerFactory
import utils.data.DataManipulator
import utils.extensions.type.getString
import utils.file.FileUtil
import utils.formula.parser.SimpleFormulaParser
import utils.json.parser.JsonFormatter
import utils.json.parser.JsonParser
import utils.math.MathUtil
import utils.stream.CharacterStreamHandler
import utils.template.cache.EhcacheTemplateHandler
import utils.template.cache.TemplateCacheHandler
import utils.template.constant.ConstantHandler
import java.io.*
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.nio.charset.Charset
import java.util.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2014-01-08
 * Time: 18:05
 */

/*
    Known issues:

    End tag in string literals will end parser prematurely.

    Example: $@F={"method": "test", "paramMap": {"msg": "Function tag start tag: $@F=, end tag: =F@$"}}=F@$
    Expected function descriptor:  {"method": "test", "paramMap": {"msg": "Function tag start tag: $@F=, end tag: =F@$"}}
    Actual parser read descriptor: {"method": "test", "paramMap": {"msg": "Function tag start tag: $@F=, end tag: =F@$
        which is a broken json definition

    - Not handling since end tags in string are rarely used expressions. Parser backtracking may introduce too much complexity

    Workaround: Use unicode expression in string to escape the end tag (This is may cover most situation).
    Example: $@F={"method": "test", "paramMap": {"msg": "Function tag start tag: $@F=, end tag: =F@\u0024"}}=F@$
 */
class TemplateProcessor @JvmOverloads constructor(
        val builder: StringBuilder, private val resourceLoader: ResourceLoader, private val fallbackConstantHandler: ConstantHandler? = null
) {
    companion object {
        private var CACHE_HANDLER: TemplateCacheHandler? = null

        private val PRIMITIVE_TYPE_MAP = HashMap<Class<*>, Class<*>>()

        private val PRIMITIVE_CLASS_MAP = HashMap<String, Class<*>>()

        init {
            PRIMITIVE_TYPE_MAP.put(java.lang.Boolean::class.java, java.lang.Boolean.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Byte::class.java, java.lang.Byte.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Short::class.java, java.lang.Short.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Character::class.java, Character.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Integer::class.java, Integer.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Float::class.java, java.lang.Float.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Double::class.java, java.lang.Double.TYPE)
            PRIMITIVE_TYPE_MAP.put(java.lang.Long::class.java, java.lang.Long.TYPE)
            PRIMITIVE_CLASS_MAP.put("boolean", java.lang.Boolean.TYPE)
            PRIMITIVE_CLASS_MAP.put("byte", java.lang.Byte.TYPE)
            PRIMITIVE_CLASS_MAP.put("short", java.lang.Short.TYPE)
            PRIMITIVE_CLASS_MAP.put("char", Character.TYPE)
            PRIMITIVE_CLASS_MAP.put("int", Integer.TYPE)
            PRIMITIVE_CLASS_MAP.put("float", java.lang.Float.TYPE)
            PRIMITIVE_CLASS_MAP.put("double", java.lang.Double.TYPE)
            PRIMITIVE_CLASS_MAP.put("long", java.lang.Long.TYPE)
        }

        @JvmStatic
        fun setCacheHandler(cacheHandler: TemplateCacheHandler) {
            if (TemplateProcessor.CACHE_HANDLER !== cacheHandler) {
                TemplateProcessor.CACHE_HANDLER = cacheHandler
            }
        }

        @JvmStatic
        fun enableDefaultCacheHandler() {
            CACHE_HANDLER = EhcacheTemplateHandler
        }

        @JvmStatic
        fun disableCacheHandler() {
            CACHE_HANDLER = null
        }
    }

    private val LOGGER = LoggerFactory.getLogger(TemplateProcessor::class.java)

    private var showMissingMsg = true


    @JvmOverloads
    constructor(builder: StringBuilder = StringBuilder(), parentResourcePath: String = "", fallbackConstantHandler: ConstantHandler? = null) : this(builder, ResourceLoader(parentResourcePath), fallbackConstantHandler)

    constructor(resourceLoader: ResourceLoader) : this(StringBuilder(), resourceLoader)

    constructor(parentResourcePath: String) : this(StringBuilder(), ResourceLoader(parentResourcePath))

    constructor(resourceLoader: ResourceLoader, fallbackConstantHandler: ConstantHandler?) : this(StringBuilder(), resourceLoader, fallbackConstantHandler)

    constructor(parentResourcePath: String, fallbackConstantHandler: ConstantHandler?) : this(StringBuilder(), parentResourcePath, fallbackConstantHandler)

    private fun getInputStreamContent(inStream: InputStream?): String? {
        if (inStream != null) {
            val content = FileUtil.getFileContent(inStream)
            FileUtil.safeClose(inStream)
            return content
        } else {
            return null
        }
    }

    private fun getResourceContent(clz: Class<*>?, resourceLocation: String): String? {
        if (clz != null) {
            val url = clz.getResource(resourceLocation)
            if (url != null) {
                if (CACHE_HANDLER == null) {
                    val content = getInputStreamContent(clz.getResourceAsStream(resourceLocation))

                    if (content == null) {
                        warnMissing(
                            "Resource [" + resourceLocation + "] of class [" + clz.name + "] is missing!"
                        )
                    }

                    return content
                } else {
                    val path = url.path

                    val cachedContent = CACHE_HANDLER!!.getCache(path)

                    if (cachedContent == null) {
                        val content = getInputStreamContent(clz.getResourceAsStream(resourceLocation))
                        CACHE_HANDLER!!.setCache(path, content ?: "")
                        return content
                    } else {
                        return cachedContent
                    }
                }
            } else {
                warnMissing("Resource url of [$resourceLocation] is missing!")
            }
        } else {
            LOGGER.error("Class locator is null, use class loader instead!")
        }

        // Fallback to null if something is wrong!
        return null
    }
    
    fun getResourceContent(resourceLocation: String, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?): String? {
        val resourceContent = getResourceContent(resourceLocation)

        if (resourceContent != null) {
            try {
                return getResourceContent(CharacterStreamHandler(resourceContent), instance, variableMap)
            } catch (e: IOException) {
                LOGGER.error("Error processing resource content: {}", resourceContent)
                return null
            }
        } else {
            return null
        }
    }
    
    private fun getResourceContent(
        hdl: CharacterStreamHandler, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): String {
        val localBuilder = StringBuilder();
        
        while (hdl.hasNext()) {
            val buffer = hdl.readUntil(TemplateTag.START_PREFIX)

            localBuilder.append(buffer)

            val nextTag = findNextTagPattern(hdl)

            if (nextTag != null) {
                processTag(instance, hdl, variableMap, nextTag)
            } else if (hdl.hasNext()) {
                localBuilder.append(TemplateTag.START_PREFIX)
                hdl.skip(TemplateTag.START_PREFIX.length.toLong())
            }
        }

        hdl.close()

        return localBuilder.toString();
    }

    private fun getResourceContent(resourceLocation: String): String? {
        val url = resourceLoader.getResource(resourceLocation)
        if (url != null) {
            if (CACHE_HANDLER == null) {
                val content = getInputStreamContent(resourceLoader.getResourceAsStream(resourceLocation))

                if (content == null) {
                    warnMissing("Resource [$resourceLocation] is missing!")
                }

                return content
            } else {
                val path = url.path

                val cachedContent = CACHE_HANDLER!!.getCache(path)

                if (cachedContent == null) {
                    val content = getInputStreamContent(resourceLoader.getResourceAsStream(resourceLocation))
                    CACHE_HANDLER!!.setCache(path, content ?: "")
                    return content
                } else {
                    return cachedContent
                }
            }
        } else {
            warnMissing("Resource url of [$resourceLocation] is missing!")
        }

        // Fallback to null if something is wrong!
        return null
    }

    fun setShowMissingMsg(flag: Boolean) {
        this.showMissingMsg = flag
    }

    @Throws(IOException::class)
    private fun readParameters(
        paramString: String, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): Map<String, Any> {
        val jsonParser = JsonParser.getInstance()

        val resultMap = HashMap<String, Any>()
        
        val paramMap = jsonParser.parseMap<Any>(paramString)

        if (paramMap != null) {
            resultMap.putAll(paramMap)
            
            if (resultMap.containsKey("start")) {
                val startObj = resultMap["start"]

                if (startObj != null && startObj is String) {
                    resultMap["start"] =
                        MathUtil.parseInt(substituteInlineContent(startObj, instance, variableMap), 10, 0)
                }
            }

            if (resultMap.containsKey("length")) {
                val lengthObj = resultMap["length"]

                if (lengthObj != null && lengthObj is String) {
                    resultMap["length"] =
                        MathUtil.parseInt(substituteInlineContent(lengthObj, instance, variableMap), 10, 0)
                }
            }
        }
        
        return resultMap
    }

    @Throws(IOException::class)
    private fun findNextTagPattern(hdl: CharacterStreamHandler): TemplateTag? {
        for (tag in TemplateTag.values()) {
            if (hdl.checkPattern(tag.startTag)) {
                return tag
            }
        }

        return null
    }

    @Throws(IOException::class)
    private fun skipTagContent(hdl: CharacterStreamHandler, tag: TemplateTag) {
        hdl.skip(tag.startTag.length.toLong())
        hdl.readUntil(tag.endTag)
        hdl.skip(tag.endTag.length.toLong())
    }

    @Throws(IOException::class)
    private fun readTagContent(hdl: CharacterStreamHandler, tag: TemplateTag): String {
        hdl.skip(tag.startTag.length.toLong())
        val content = hdl.readUntil(tag.endTag).trim { it <= ' ' }
        hdl.skip(tag.endTag.length.toLong())
        return content
    }

    @Throws(IOException::class)
    private fun processTag(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?, tag: TemplateTag
    ) {
        when (tag) {
            TemplateTag.VARIABLE -> substituteVariable(instance, hdl, variableMap)
            TemplateTag.SUBVARIABLE -> substituteSubVariable(instance, hdl, variableMap)
            TemplateTag.BRREPLACE -> substituteVariableReplaceNewLineToBr(instance, hdl, variableMap)
            TemplateTag.NLREPLACE -> substituteVariableReplaceBrToNewLine(instance, hdl, variableMap)
            TemplateTag.FUNCTION -> substituteFunctionOutput(instance, hdl, variableMap)
            TemplateTag.ARITHMETIC -> substituteArithmeticOutput(instance, hdl, variableMap)
            TemplateTag.REPEAT -> substituteRepeatingContent(instance, hdl, variableMap)
            TemplateTag.INCLUDE -> includeContent(instance, hdl, variableMap)
            TemplateTag.COMMENT -> skipComment(hdl)
            else -> LOGGER.error("Missing tag process implementation, skipping: " + readTagContent(hdl, tag))
        }
    }

    @Throws(IOException::class)
    private fun substituteVariable(
            builder: StringBuilder, instance: ValueSubstitutor, hdl: CharacterStreamHandler) {
        val key = readTagContent(hdl, TemplateTag.VARIABLE)

        var v: Any? = instance.getVariableValue<Any>(key, false)

        if (v == null) {
            if (fallbackConstantHandler != null) {
                v = fallbackConstantHandler.getConstantValue<Any>(key, false)
            }
        }

        if (v != null) {
            builder.append(v.toString())
        } else {
            warnMissing("Missing variable value: " + key)
        }
    }

    private fun warnMissing(msg: String) {
        if (showMissingMsg) {
            LOGGER.warn(msg)
        }
    }

    private fun getTemplateVar(key: String): String? {
        for (tag in TemplateTag.values()) {
            if (key == tag.tagName + ".startTag") {
                return tag.startTag
            } else if (key == tag.tagName + ".endTag") {
                return tag.endTag
            }
        }
        return null
    }

    private fun extractStringValue(key: String, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?): String {
        val v = extractVariableValue(key, instance, variableMap)

        if (v != null) {
            val formatter = JsonFormatter.getInstance()

            if (v is Map<*, *>) {
                return formatter.format(v as Map<String, Any?>)
            } else if (v is List<*>) {
                return formatter.format(v as List<Any?>)
            } else {
                return v.toString()
            }
        } else {
            warnMissing("Missing variable value: " + key)
            return ""
        }
    }

    private fun extractVariableValue(
        key: String, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): Any? {
        var v: Any? = if (variableMap != null) variableMap[key] else null

        if (v == null && instance != null) {
            v = instance.getVariableValue<Any>(key)
        }

        if (v == null) {
            if (fallbackConstantHandler != null) {
                v = fallbackConstantHandler.getConstantValue<Any>(key, false)
            }
        }

        if (v == null) {
            // check if this is tag specific variable
            v = getTemplateVar(key)
        }

        return v
    }

    @Throws(IOException::class)
    private fun substituteVariableReplaceNewLineToBr(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val key = readTagContent(hdl, TemplateTag.BRREPLACE)

        val v = extractVariableValue(key, instance, variableMap)

        if (v != null) {
            val jsonFormatter = JsonFormatter.getInstance()

            if (v is Map<*, *>) {
                builder.append(replaceNewLIneToBr(jsonFormatter.format(v as Map<String, Any?>)))
            } else if (v is List<*>) {
                builder.append(replaceNewLIneToBr(jsonFormatter.format(v as List<Any?>)))
            } else {
                builder.append(replaceNewLIneToBr(v.toString()))
            }
        } else {
            warnMissing("Missing variable value (replace br): " + key)
        }
    }

    private fun replaceNewLIneToBr(s: String): String {
        return s.replace("\n".toRegex(), "<br/>")
    }

    @Throws(IOException::class)
    private fun substituteVariableReplaceBrToNewLine(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val key = readTagContent(hdl, TemplateTag.NLREPLACE)

        val v = extractVariableValue(key, instance, variableMap)

        if (v != null) {
            val formatter = JsonFormatter.getInstance()

            if (v is Map<*, *>) {
                builder.append(replaceBrToNewLine(formatter.format(v as Map<String, Any?>)))
            } else if (v is List<*>) {
                builder.append(replaceBrToNewLine(formatter.format(v as List<Any?>)))
            } else {
                builder.append(replaceBrToNewLine(v.toString()))
            }
        } else {
            warnMissing("Missing variable value (replace new line): " + key)
        }
    }

    private fun replaceBrToNewLine(s: String): String {
        return s.replace("<br\\s*/?>".toRegex(), "\n")
    }

    @Throws(IOException::class)
    private fun substituteSubVariable(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val content = readTagContent(hdl, TemplateTag.SUBVARIABLE)

        val contentMap = JsonParser.getInstance().parseMap<Any>(content)

        if (contentMap != null && contentMap.isNotEmpty() && contentMap.containsKey("key")) {
            val key = DataManipulator.getStringValue(contentMap, "key", "")!!

            var keyContent = extractStringValue(key, instance, variableMap)

            if (contentMap.containsKey("substitutes")) {
                val substitutes = contentMap["substitutes"] as List<String>

                for (i in substitutes.indices) {
                    var substitute = substitutes[i]

                    substitute = substituteInlineContent(substitute, instance, variableMap)

                    keyContent = keyContent.replace(Regex("\\{$i\\}"), substitute)
                }
            }

            builder.append(keyContent)
        } else {
            builder.append(substituteInlineContent(content, instance, variableMap))
        }

    }

    @Throws(IOException::class)
    private fun substituteVariable(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val key = readTagContent(hdl, TemplateTag.VARIABLE)

        var v: Any? = null

        if (key.contains(",")) {
            val keys = key.split(",").dropLastWhile { it.isEmpty() }.toTypedArray()

            for (k in keys) {
                val o = extractVariableValue(k.trim { it <= ' ' }, instance, variableMap)

                if (o != null) {
                    v = o
                    break
                }
            }
        } else {
            v = extractVariableValue(key, instance, variableMap)
        }

        if (v != null) {
            val formatter = JsonFormatter.getInstance()

            if (v is Map<*, *>) {
                builder.append(formatter.format(v as Map<String, Any?>))
            } else if (v is List<*>) {
                builder.append(formatter.format(v as List<Any?>))
            } else {
                builder.append(v.toString())
            }
        } else {
            warnMissing("Missing variable value: " + key)
        }
    }

    private fun canConvertToPrimitive(clz: Class<*>): Boolean {
        return PRIMITIVE_TYPE_MAP.containsKey(clz)
    }

    // Classes should contains non primitive classes only
    @Throws(NoSuchMethodException::class)
    private fun findMethodWithParameterClasses(
        targetClass: Class<*>, methodName: String, classes: Array<Class<*>>
    ): Method {
        val nonPrimitiveParameterClasses = arrayOfNulls<Class<*>>(classes.size)
        val primitiveParameterClasses = arrayOfNulls<Class<*>>(classes.size)

        var hasAtLeastOnePrimitive = false

        for (i in classes.indices) {
            val clz = classes[i]

            if (canConvertToPrimitive(clz)) {
                hasAtLeastOnePrimitive = true
                primitiveParameterClasses[i] = PRIMITIVE_TYPE_MAP[clz]
            } else {
                primitiveParameterClasses[i] = clz
            }

            nonPrimitiveParameterClasses[i] = clz
        }

        val method: Method = 
            try {
                targetClass.getMethod(methodName, *nonPrimitiveParameterClasses)
            } catch (e: NoSuchMethodException) {
                if (hasAtLeastOnePrimitive) {
                    targetClass.getMethod(methodName, *primitiveParameterClasses)
                } else {
                    throw e
                }
            }

        return method
    }

    @Throws(IOException::class)
    private fun substituteFunctionOutput(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val functionDescriptorString = readTagContent(hdl, TemplateTag.FUNCTION)

        val functionDescriptor = JsonParser.getInstance().parseMap<Any>(functionDescriptorString)!!

        var clz: Class<*>?

        var invokeInstance: Any?

        if (functionDescriptor.containsKey("className")) {
            try {
                clz = Class.forName(functionDescriptor["className"] as String)

                try {
                    val getInstanceMethod = clz!!.getMethod("getInstance")

                    invokeInstance = getInstanceMethod.invoke(null)
                } catch (nsme: NoSuchMethodException) {
                    // getInstance not found for clz, simply set invokeInstance to null
                    invokeInstance = null
                } catch (e: InvocationTargetException) {
                    // Failed the getInstance invocation, invokeInstance set to null.
                    invokeInstance = null
                } catch (e: IllegalAccessException) {
                    invokeInstance = null
                }

            } catch (e: ClassNotFoundException) {
                clz = null
                invokeInstance = null
                LOGGER.error("Class not found, className: " + (functionDescriptor as Map<String, Any>).getString("className", ""), e)
            } catch (e: NullPointerException) {
                clz = null
                invokeInstance = null
                LOGGER.error("Class not found, className: " + (functionDescriptor as Map<String, Any>).getString("className", ""), e)
            }

        } else {
            clz = instance?.javaClass
            invokeInstance = instance
        }

        if (clz != null) {
            var targetMethod: Method? = null
            val paramMap = if (functionDescriptor.containsKey("paramMap")) functionDescriptor["paramMap"] as MutableMap<String, Any> else null
            
            if (paramMap != null) {
                for ((key, value) in paramMap) {
                    if (value is String) {
                        paramMap[key] = substituteInlineContent(value, instance, variableMap);
                    }
                }
            }

            val paramList = if (functionDescriptor.containsKey("params")) functionDescriptor["params"] as MutableList<Any> else null
            
            if (paramList != null) {
                for (i in paramList.indices) {
                    if (paramList[i] is String) {
                        paramList[i] = substituteInlineContent(paramList[i] as String, instance, variableMap)
                    }
                }
            } 

            val methodName = functionDescriptor["method"] as String

            try {
                if (paramMap != null) {
                    targetMethod = clz.getMethod(methodName, Map::class.java)
                } else if (paramList != null) {
                    val paramClasses = Array<Class<*>>(
                            paramList.size, { paramList[it].javaClass }
                    )

                    targetMethod = findMethodWithParameterClasses(clz, methodName, paramClasses)
                } else {
                    targetMethod = clz.getMethod(methodName)
                }
            } catch (nsme: NoSuchMethodException) {
                LOGGER.error("Method not found", nsme)
            } catch (npe: NullPointerException) {
                LOGGER.error("Null data found, process ended prematurely!", npe)
            }

            if (targetMethod != null) {
                try {
                    val v: Any?
                    if (paramMap != null) {
                        v = targetMethod.invoke(invokeInstance, paramMap)
                    } else if (paramList != null) {
                        val params = arrayOfNulls<Any>(paramList.size)
                        var i = 0
                        for (param in paramList) {
                            params[i++] = param
                        }
                        v = targetMethod.invoke(invokeInstance, *params)
                    } else {
                        v = targetMethod.invoke(invokeInstance)
                    }
                    if (v != null) {
                        val formatter = JsonFormatter.getInstance()

                        var output: String

                        if (v is Map<*, *>) {
                            output = formatter.format(v as Map<String, Any?>)
                        } else if (v is List<*>) {
                            output = formatter.format(v as List<Any?>)
                        } else {
                            output = v.toString()
                        }

                        if (functionDescriptor.containsKey("escape") && functionDescriptor.containsKey("escape")) {
                            output = escape(output)
                        }

                        builder.append(output)
                    }
                } catch (e: InvocationTargetException) {
                    LOGGER.error("Function invocation error", e)
                } catch (e: IllegalAccessException) {
                    LOGGER.error("Function invocation error", e)
                }

            }
        }
    }

    fun searchForMethod(clz: Class<*>, methodName: String, vararg paramClasses: Class<*>): Method? {
        val methods = clz.methods

        var m: Method? = null

        for (method in methods) {
            val acceptedParameters = method.parameterTypes

            if (methodName == method.name && paramClasses.size == acceptedParameters.size) {
                var parameterMatch = true
                for (i in acceptedParameters.indices) {
                    val paramClass = paramClasses[i]
                    val acceptedParamClass = acceptedParameters[i]

                    if (paramClass != acceptedParamClass && !acceptedParamClass.isAssignableFrom(paramClass)) {
                        parameterMatch = false
                        break
                    }
                }

                if (parameterMatch) {
                    m = method
                    break
                }
            }
        }

        return m
    }

    @Throws(IOException::class)
    private fun substituteArithmeticOutput(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val content = readTagContent(hdl, TemplateTag.ARITHMETIC)

        val formulaExpression = substituteInlineContent(content, instance, variableMap)

        val formula = SimpleFormulaParser.parseFormula(formulaExpression)

        val result = formula.compute()
        
        builder.append(result.toString())
    }

    @Throws(IOException::class)
    private fun substituteInlineContent(
            content: String, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): String {
        val contentStreamHandler = CharacterStreamHandler(content)

        val contentTemplateProcessor = TemplateProcessor(resourceLoader, fallbackConstantHandler)

        contentTemplateProcessor.appendResourceContent(contentStreamHandler, instance, variableMap)

        return contentTemplateProcessor.builder.toString()
    }

    @Throws(IOException::class)
    private fun skipComment(hdl: CharacterStreamHandler) {
        skipTagContent(hdl, TemplateTag.COMMENT)
    }

    @Throws(IOException::class)
    private fun includeContent(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val paramString = readTagContent(hdl, TemplateTag.INCLUDE)

        val paramMap = readParameters(paramString, instance, variableMap)

        if (paramMap.containsKey("file")) {
            val file = paramMap["file"] as String

            if (paramMap.containsKey("package")) {
                val incTempProcessor = TemplateProcessor(
                        paramMap["package"] as String, fallbackConstantHandler)

                incTempProcessor.appendResourceContent(file, instance, variableMap)

                appendDirectContent(incTempProcessor.toString())
            } else {
                appendResourceContent(file, instance, variableMap)
            }
        } else {
            LOGGER.error("Include template param failure: " + paramString)
        }
    }

    @Throws(IOException::class)
    private fun substituteRepeatingContent(
        instance: ValueSubstitutor?, hdl: CharacterStreamHandler, variableMap: Map<String, Any?>?
    ) {
        val paramString = readTagContent(hdl, TemplateTag.REPEAT)

        val paramMap = readParameters(paramString, instance, variableMap)

        if (paramMap.containsKey("template")) {
            val substituteMap = HashMap<String, Any>()

            if (variableMap != null) {
                for (key in variableMap.keys) {
                    substituteMap.put(key, variableMap[key] ?: "")
                }
            }

            val template = paramMap["template"] as String

            val start: Int
            var length: Int
            if (paramMap.containsKey("start")) {
                start = DataManipulator.extractInteger(paramMap["start"], 0)
            } else {
                start = 0
            }

            if (paramMap.containsKey("length")) {
                length = DataManipulator.extractInteger(paramMap["length"], 0)
            } else {
                length = 0
            }

            substituteMap.put("start", start)
            substituteMap.put("length", length)

            if (paramMap.containsKey("valueLists")) {
                val valueListMap = HashMap<String, List<*>>()

                val valueListObj = paramMap["valueLists"]

                if (valueListObj != null && valueListObj is List<*>) {
                    val valueListNames = valueListObj as List<String>?

                    for (valueListName in valueListNames!!) {
                        val valueList: Any

                        if (variableMap != null && variableMap.containsKey(valueListName)) {
                            valueList = variableMap[valueListName] ?: ""
                        } else {
                            valueList = instance?.getVariableValue<Any>(valueListName, false) ?: ""
                        }

                        if (valueList is List<*>) {
                            valueListMap.put(valueListName, valueList)
                        }
                    }

                    if (length <= 0) {
                        for (key in valueListMap.keys) {
                            val valueList = valueListMap[key]

                            if (length < valueList?.size ?: 0) {
                                length = valueList?.size ?: 0
                            }
                        }
                        substituteMap.put("length", length)
                    }


                    var c = start

                    if (length > 0) {
                        while (true) {
                            if (length > 0 && c >= length) {
                                break
                            }

                            for (key in valueListMap.keys) {
                                val valueList = valueListMap[key]
                                if (c < valueList?.size ?: 0) {
                                    substituteMap[key] = valueListMap[key]?.get(c) ?: ""
                                } else {
                                    // Add as an empty string to avoid falling back to instance's variable value.
                                    substituteMap.put(key, "")
                                }
                            }

                            substituteMap.put("currentIndex", c)

                            appendResourceContent(StringReader(template), instance, substituteMap)

                            c++
                        }
                    }
                }
            } else if (paramMap.containsKey("valueMaps")) {
                var valueMaps: List<Map<String, Any>>? = null

                val valueKey = paramMap["valueMaps"]

                if (valueKey != null && valueKey is String) {
                    val v: Any?

                    if (variableMap != null && variableMap.containsKey(valueKey.toString())) {
                        v = variableMap[valueKey.toString()]
                    } else {
                        v = instance?.getVariableValue<Any>(valueKey.toString(), false) ?: ""
                    }

                    if (v != null && v is List<*>) {
                        valueMaps = v as List<Map<String, Any>>?
                    }

                    if (length <= 0) {
                        if (valueMaps != null) {
                            length = valueMaps.size
                        }
                        substituteMap.put("length", length)
                    }

                    var c = start

                    if (length > 0) {

                        while (true) {
                            if (length > 0 && c >= length) {
                                break
                            }

                            substituteMap.clear()

                            if (c < valueMaps?.size ?: 0) {
                                substituteMap.putAll(valueMaps!![c])
                            }

                            substituteMap.put("currentIndex", c)

                            appendResourceContent(StringReader(template), instance, substituteMap)

                            c++
                        }
                    }
                }
            } else if (length > 0) {
                for (i in start..length - 1) {
                    substituteMap.put("currentIndex", i)
                    appendResourceContent(StringReader(template), instance, substituteMap)
                }
            }
        }
    }

    fun escape(value: String): String {
        return StringEscapeUtils.escapeHtml4(value)
    }

    fun appendDirectContent(content: String): TemplateProcessor {
        builder.append(content)

        return this
    }

    fun appendTemplateContent(
            templateContent: String, variableMap: Map<String, Any>): TemplateProcessor {
        return appendTemplateContent(templateContent, null, variableMap)
    }

    fun appendTemplateContent(
            templateContent: String, valueSubstitutor: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        try {
            appendResourceContent(CharacterStreamHandler(templateContent), valueSubstitutor, variableMap)
        } finally {
            // No exception is expected, since the character stream is initialized from existing string
            return this
        }
    }

    fun clean() {
        builder.setLength(0)
    }

    fun appendResourceContent(resourceLocation: String): TemplateProcessor {
        val templateContent = getResourceContent(resourceLocation)

        if (templateContent != null) {
            try {
                return appendResourceContent(CharacterStreamHandler(templateContent), EmptyValueSubstitutor, null)
            } catch (e: IOException) {
                warnMissing("Resource [$resourceLocation] does not exists!")
                return this
            }

        } else {
            return this
        }
    }

    fun appendResourceContents(resourceLocations: List<String>): TemplateProcessor {
        for (resourceLocation in resourceLocations) {
            appendResourceContent(resourceLocation)
        }
        return this
    }

    fun appendResourceContent(resourceLocation: String, variableMap: Map<String, Any?>): TemplateProcessor {
        val templateContent = getResourceContent(resourceLocation)

        if (templateContent != null) {
            try {
                return appendResourceContent(CharacterStreamHandler(templateContent), EmptyValueSubstitutor, variableMap)
            } catch (e: IOException) {
                warnMissing("Resource [$resourceLocation] does not exists!")
                return this
            }
        } else {
            return this
        }
    }

    fun appendResourceContents(resourceLocations: List<String>, variableMap: Map<String, Any>): TemplateProcessor {
        for (resourceLocation in resourceLocations) {
            appendResourceContent(resourceLocation, variableMap)
        }
        return this
    }

    @JvmOverloads 
    fun appendResourceContent(
        reader: Reader?, variableMap: Map<String, Any?>? = null
    ): TemplateProcessor {
        if (reader != null) {
            try {
                return appendResourceContent(
                        CharacterStreamHandler(reader), EmptyValueSubstitutor, variableMap)
            } catch (iox: IOException) {
                LOGGER.error("Error reading resource content")
            }

        }

        return this
    }

    @JvmOverloads 
    fun appendResourceContent(
        resourceStream: InputStream?, variableMap: Map<String, Any?>? = null
    ): TemplateProcessor {
        if (resourceStream != null) {
            return appendResourceContent(InputStreamReader(resourceStream, Charset.forName("UTF-8")), variableMap)
        }
        return this
    }

    fun appendResourceContent(reader: Reader?, instance: ValueSubstitutor?): TemplateProcessor {
        if (reader != null) {
            try {
                return appendResourceContent(CharacterStreamHandler(reader), instance, null)
            } catch (iox: IOException) {
                LOGGER.error(
                    "Error reading resource content of ${if (instance != null) instance.javaClass.name else "null"}",
                    iox
                )
            }

        }

        return this
    }

    fun appendResourceContent(resourceStream: InputStream?, instance: ValueSubstitutor?): TemplateProcessor {
        if (resourceStream != null) {
            return appendResourceContent(InputStreamReader(resourceStream, Charset.forName("UTF-8")), instance)
        }

        return this
    }

    @JvmOverloads 
    fun appendResourceContent(
            resourceLocation: String, instance: ValueSubstitutor?, variableMap: Map<String, Any?>? = null
    ): TemplateProcessor {
        val resourceContent = getResourceContent(resourceLocation)

        if (resourceContent != null) {
            try {
                return appendResourceContent(CharacterStreamHandler(resourceContent), instance, variableMap)
            } catch (e: IOException) {
                LOGGER.error("Error processing resource content: {}", resourceContent)
                return this
            }
        } else {
            return this
        }
    }

    fun appendResourceContent(
        reader: Reader?, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        if (variableMap == null || variableMap.isEmpty()) {
            return appendResourceContent(reader, instance)
        } else {
            val clz = instance?.javaClass

            if (reader != null) {
                try {
                    return appendResourceContent(CharacterStreamHandler(reader), instance, variableMap)
                } catch (iox: IOException) {
                    LOGGER.error("Error reading resource content of " + if (clz != null) clz.name else "null", iox)
                }

            }

            return this
        }
    }

    fun appendResourceContent(
        resourceStream: InputStream?, instance: ValueSubstitutor, variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        if (resourceStream != null) {
            return appendResourceContent(
                InputStreamReader(resourceStream, Charset.forName("UTF-8")), instance, variableMap
            )
        }

        return this
    }

    fun appendResourceContent(
        clz: Class<out ValueSubstitutor>, substitutorInstance: ValueSubstitutor, resourceLocation: String
    ): TemplateProcessor {
        return appendResourceContent(clz, substitutorInstance, resourceLocation, null)
    }

    fun appendResourceContents(
        clz: Class<out ValueSubstitutor>, substitutorInstance: ValueSubstitutor, resourceLocations: List<String>
    ): TemplateProcessor {
        return appendResourceContents(clz, substitutorInstance, resourceLocations, null)
    }

    @Throws(IOException::class)
    fun appendResourceContent(
        hdl: CharacterStreamHandler, instance: ValueSubstitutor?, variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        while (hdl.hasNext()) {
            val buffer = hdl.readUntil(TemplateTag.START_PREFIX)

            builder.append(buffer)

            val nextTag = findNextTagPattern(hdl)

            if (nextTag != null) {
                processTag(instance, hdl, variableMap, nextTag)
            } else if (hdl.hasNext()) {
                builder.append(TemplateTag.START_PREFIX)
                hdl.skip(TemplateTag.START_PREFIX.length.toLong())
            }
        }

        hdl.close()

        return this
    }

    fun appendResourceContent(
        clz: Class<*>, substitutorInstance: ValueSubstitutor, resourceLocation: String, variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        val templateContent = getResourceContent(clz, resourceLocation)

        if (templateContent != null) {
            try {
                return appendResourceContent(CharacterStreamHandler(templateContent), substitutorInstance, variableMap)
            } catch (iox: IOException) {
                LOGGER.error("Error reading resource [" + resourceLocation + "] content of " + clz.name, iox)
            }

        } else {
            warnMissing("Resource [$resourceLocation] does not exists!")
        }

        return this
    }

    fun appendResourceContents(
        clz: Class<*>, substitutorInstance: ValueSubstitutor, resourceLocations: List<String>,
        variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        for (resourceLocation in resourceLocations) {
            appendResourceContent(clz, substitutorInstance, resourceLocation, variableMap)
        }
        return this
    }

    fun appendResourceContent(
        substitutorInstance: ValueSubstitutor, resourceLocation: String, variableMap: Map<String, Any?>?
    ): TemplateProcessor {
        val templateContent = getResourceContent(resourceLocation)

        if (templateContent != null) {
            try {
                return appendResourceContent(CharacterStreamHandler(templateContent), substitutorInstance, variableMap)
            } catch (iox: IOException) {
                LOGGER.error("Error reading resource [$resourceLocation] content of System", iox)
            }

        } else {
            warnMissing("Resource [$resourceLocation] does not exists!")
        }

        return this
    }

    override fun toString(): String {
        return builder.toString()
    }
}
