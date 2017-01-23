package utils.template.constant

import utils.stream.CharacterStreamHandler
import utils.string.StringUtil
import utils.file.FileUtil
import utils.json.parser.JsonParser
import utils.math.MathUtil
import utils.template.ResourceLoader
import utils.template.ValueSubstitutorKt

import java.io.File
import java.io.IOException
import java.math.BigDecimal
import java.net.URL
import java.util.*

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2014-01-09
 * Time: 10:24
 */
class ConstantHandler: ValueSubstitutorKt {
    private var constantMap: MutableMap<String, Any?>? = null
    private var overrideConstantMap: MutableMap<String, Map<String, Any>>? = null

    private var clz: Class<*>? = null

    private var parentConstantHandler: ConstantHandler? = null

    private var classLoader: ClassLoader? = null
    private var resourceLoader: ResourceLoader? = null
    
    private var resourceName: String? = null
    private var overrideResources: List<String>? = null

    private var resourceFile: File? = null

    private var resourceVersion: Long = -1
    private var resourceEditable = false

    constructor(clz: Class<*>, resourceName: String, vararg overrideResources: String) {
        this.clz = clz
        this.resourceName = resourceName
        if (overrideResources.isNotEmpty()) {
            this.overrideResources = listOf(*overrideResources)
        } else {
            this.overrideResources = null
        }
        this.overrideConstantMap = null
    }
    
    constructor(classLoader: ClassLoader, resourceName: String, vararg overrideResources: String) {
        this.classLoader = classLoader
        this.resourceName = resourceName
        if (overrideResources.isNotEmpty()) {
            this.overrideResources = listOf(*overrideResources)
        } else {
            this.overrideResources = null
        }
        this.overrideConstantMap = null
    }
    
    constructor(resourceLoader: ResourceLoader, resourceName: String, vararg overrideResources: String) {
        this.resourceLoader = resourceLoader
        this.resourceName = resourceName
        if (overrideResources.isNotEmpty()) {
            this.overrideResources = listOf(*overrideResources)
        } else {
            this.overrideResources = null
        }
        this.overrideConstantMap = null
    }
    
    @JvmOverloads 
    constructor(clz: Class<*>, resourceName: String, overrideResources: List<String>? = null) {
        this.clz = clz
        this.resourceName = resourceName
        if (overrideResources != null && overrideResources.isNotEmpty()) {
            this.overrideResources = overrideResources
        } else {
            this.overrideResources = null
        }
        this.overrideConstantMap = null
    }

    @JvmOverloads 
    constructor(classLoader: ClassLoader, resourceName: String, overrideResources: List<String>? = null) {
        this.classLoader = classLoader
        this.resourceName = resourceName
        if (overrideResources != null && overrideResources.isNotEmpty()) {
            this.overrideResources = overrideResources
        } else {
            this.overrideResources = null
        }
        this.overrideConstantMap = null
    }
    
    @JvmOverloads
    constructor(resourceLoader: ResourceLoader, resourceName: String, overrideResources: List<String>? = null) {
        this.resourceLoader = resourceLoader
        this.resourceName = resourceName
        if (overrideResources != null && overrideResources.isNotEmpty()) {
            this.overrideResources = overrideResources
        } else {
            this.overrideResources
        }
        this.overrideConstantMap = null
    }

    // parentConstantHandler should be nullable, setting it to be null means clearing the parentConstantHandler
    fun setParentConstantHandler(parentConstantHandler: ConstantHandler?) {
        this.parentConstantHandler = parentConstantHandler
    }

    private fun safeGetResourceContent(resourceName: String): String {
        val url: URL
        if (clz != null) {
            url = clz!!.getResource(resourceName)
        } else if (classLoader != null){
            url = classLoader!!.getResource(resourceName)
        } else if (resourceLoader != null) {
            url = resourceLoader!!.getResource(resourceName)!!
        } else {
            // Not expected, if happened just return an empty string
            return ""
        }

        val resourceFile = File(url.file)

        val result: String

        if (!resourceFile.exists()) {
            if (clz != null) {
                val stream = clz!!.getResourceAsStream(resourceName)
                result = FileUtil.getFileContent(stream, "UTF-8")
                FileUtil.safeClose(stream)
            } else if (classLoader != null) {
                val stream = classLoader!!.getResourceAsStream(resourceName)
                result = FileUtil.getFileContent(stream, "UTF-8")
                FileUtil.safeClose(stream)
            } else if (resourceLoader != null) {
                val stream = resourceLoader!!.getResourceAsStream(resourceName)
                result = FileUtil.getFileContent(stream, "UTF-8")
                FileUtil.safeClose(stream)
            } else {
                // Not expected, set result to an empty string
                result = ""
            }
        } else {
            result = FileUtil.getFileContent(resourceFile)
        }

        return result
    }

    private fun ensureResources() {
        val url: URL?
        
        if (clz != null) {
            url = clz!!.getResource(resourceName)
        } else if (classLoader != null) {
            url = classLoader!!.getResource(resourceName)
        } else if (resourceLoader != null) {
            url = resourceLoader!!.getResource(resourceName!!)!!
        } else {
            url = null
        }

        if (url != null) {
            resourceFile = File(url.file)
            
            // resourceFile.exists seems unnecessary and may raise access denied exception
            // resourceFile != null maybe enough, since the map will be load through getResourceAsStream
            // set resource editable to false to prevent any unintended resource file access
            if (resourceFile != null) {
                resourceEditable = false
                
                if (clz != null) {
                    val stream = clz!!.getResourceAsStream(resourceName)
                    constantMap = JsonParser.getInstance().parseMap<Any>(FileUtil.getFileContent(stream, "UTF-8"))
                    FileUtil.safeClose(stream)

                    processOverrideConstantMap()
                } else if (classLoader != null) {
                    val stream = classLoader!!.getResourceAsStream(resourceName)
                    constantMap = JsonParser.getInstance().parseMap<Any>(FileUtil.getFileContent(stream, "UTF-8"))
                    FileUtil.safeClose(stream)

                    processOverrideConstantMap()
                } else if (resourceLoader != null) {
                    val stream = resourceLoader!!.getResourceAsStream(resourceName!!)
                    constantMap = JsonParser.getInstance().parseMap<Any>(FileUtil.getFileContent(stream, "UTF-8"))
                    FileUtil.safeClose(stream)
                    
                    processOverrideConstantMap()
                }
                mapPreprocess(constantMap as MutableMap<String, Any?>)
            } else {
                loadResource()
            }
        }
    }

    private fun loadResource() {
        if (resourceFile != null && resourceFile!!.exists()) {
            resourceVersion = resourceFile!!.lastModified()
            constantMap = JsonParser.getInstance().parseMap<Any>(FileUtil.getFileContent(resourceFile))
            processOverrideConstantMap()
            mapPreprocess(constantMap as MutableMap<String, Any?>)
            
            // loaded from resource file, the resource file may be editable
            resourceEditable = true
        }
    }

    private fun copyOverridedContent(map: MutableMap<String, Any?>, overrideMap: Map<String, Any>) {
        for (k in overrideMap.keys) {
            val oo = overrideMap[k]

            if (!map.containsKey(k)) {
                map.put(k, oo)
            } else {
                val o = map[k]

                if (oo != null && o != null && o is Map<*, *> && oo is Map<*, *>) {
                    copyOverridedContent(o as MutableMap<String, Any?>, oo as Map<String, Any>)
                } else {
                    map.put(k, oo)
                }
            }
        }
    }

    private fun processOverrideConstantMap() {
        if (this.constantMap != null && overrideConstantMap == null && overrideResources != null) {
            this.overrideConstantMap = HashMap<String, Map<String, Any>>()

            for (overrideResource in overrideResources!!) {
                val content = safeGetResourceContent(overrideResource)

                if (!StringUtil.isEmptyString(content)) {
                    val overrideMap = JsonParser.getInstance().parseMap<Any>(content)
                    if (overrideMap != null) {
                        overrideConstantMap!!.put(overrideResource, overrideMap)
                        copyOverridedContent(constantMap!!, overrideMap)
                    }
                }
            }
        }
    }

    private fun ensureConstantMap() {
        if (constantMap == null) {
            ensureResources()
        } else {
            // Only check for modification version if resource file is editable
            if (resourceFile != null && resourceEditable && resourceFile!!.lastModified() != resourceVersion) {
                loadResource()
            }
        }
    }

    private fun _get(key: String): Any? {
        var v: Any? = getConstantValue<Any>(key, false)

        if (v == null) {
            v = DynamicConstant.get(key)
        }

        if (v == null) {
            v = ""
        }

        return v
    }

    private fun stringPreprocess(sv: String): Any? {
        if (sv.startsWith("@=") && sv.endsWith("=@") && sv.indexOf("@=", 2) == -1) {
            val key = sv.substring(2, sv.length - 2)
            return _get(key)
        } else {
            val hdl = CharacterStreamHandler(sv)

            var builder = StringBuilder()

            try {
                val startTag = "@="
                val endTag = "=@"

                while (hdl.hasNext()) {
                    val p = hdl.readUntil(startTag)

                    builder.append(p)

                    if (hdl.hasNext()) {
                        hdl.skipPattern(startTag, false)
                        val k = hdl.readUntil(endTag)
                        val v = _get(k)

                        builder.append(if (v != null) v.toString() else "")

                        hdl.skipPattern(endTag, false)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
                builder = StringBuilder(sv)
            }

            hdl.close()

            return builder.toString()
        }
    }

    private fun mapPreprocess(map: MutableMap<String, Any?>) {
        for (k in map.keys) {
            val v = map[k]

            if (v is String) {
                map.put(k, stringPreprocess(v))
            } else if (v is MutableMap<*, *>) {
                mapPreprocess(v as MutableMap<String, Any?>)
            } else if (v is MutableList<*>) {
                listPreprocess(v as MutableList<Any?>)
            }
        }
    }

    private fun listPreprocess(list: MutableList<Any?>) {
        val iter = list.listIterator()

        while (iter.hasNext()) {
            val v = iter.next()

            if (v is String) {
                iter.set(stringPreprocess(v))
            } else if (v is MutableMap<*, *>) {
                mapPreprocess(v as MutableMap<String, Any?>)
            } else if (v is MutableList<*>) {
                listPreprocess(v as MutableList<Any?>)
            }
        }
    }
    
    fun <V> getConstantValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
        ensureConstantMap()
        
        var cmap: MutableMap<String, Any?>? = constantMap

        var k: String

        var v: Any? = null

        for (i in keys.indices) {
            k = keys[i].trim { it <= ' ' }
            v = cmap?.get(k)
            if (i == keys.size - 1) {
                break
            } else if (v is MutableMap<*, *>) {
                cmap = v as MutableMap<String, Any?>?
            } else {
                break
            }
        }

        if (v != null) {
            if (v is String) {
                if (substitutes != null && substitutes.size > 0) {
                    for (i in substitutes.indices) {
                        v = (v as String).replace("\\{$i\\}".toRegex(), substitutes[i])
                    }
                }

                return v as V
            } else {
                return v as V
            }
        } else {
            if (parentConstantHandler != null) {
                return parentConstantHandler!!.getConstantValue<V>(keys, nullSafe, *substitutes)
            } else {
                return (if (nullSafe) "" else null) as V
            }
        }
    }
    
    fun <V> getConstantValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>?): V? {
        ensureConstantMap()

        var cmap: MutableMap<String, Any?>? = constantMap

        var k: String

        var v: Any? = null

        for (i in keys.indices) {
            k = keys[i].trim { it <= ' ' }
            v = cmap?.get(k)
            if (i == keys.size - 1) {
                break
            } else if (v is MutableMap<*, *>) {
                cmap = v as MutableMap<String, Any?>?
            } else {
                break
            }
        }

        if (v != null) {
            if (v is String && substituteMap != null && substituteMap.size > 0) {

                for (key in substituteMap.keys) {
                    v = (v as String).replace("\\{$key\\}".toRegex(), substituteMap[key].toString())
                }

                return v as V
            } else {
                return v as V
            }
        } else {
            if (parentConstantHandler != null) {
                return parentConstantHandler!!.getConstantValue<V>(keys, nullSafe, substituteMap)
            } else {
                return (if (nullSafe) "" else null) as V
            }
        }
    }

    fun <V> getConstantValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
        return getConstantValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, *substitutes)
    }

    fun <V> getConstantValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return getConstantValue(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, substituteMap)
    }

    fun getConstantStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
        val v = getConstantValue<Any>(keys, nullSafe, *substitutes)
        
        if (v != null) {
            return v.toString()
        } else {
            return if (nullSafe) "" else null
        }
    }

    fun getConstantStringValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        val v = getConstantValue<Any>(keys, nullSafe, substituteMap)
        
        if (v != null) {
            return v.toString()
        } else {
            return if (nullSafe) "" else null
        }
    }

    fun getConstantStringValue(keys: Array<String>): String? {
        val v = getConstantStringValue(keys, false)
        
        return if (v != null) v.toString() else v
    }

    fun getConstantStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
        val v = getConstantValue<Any>(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, *substitutes)
        if (v != null) {
            return v.toString()
        } else {
            return if (nullSafe) "" else null
        }
    }

    fun getConstantStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        val v = getConstantValue<Any>(key.split(".").dropLastWhile { it.isEmpty() }.toTypedArray(), nullSafe, substituteMap)
        if (v != null) {
            return v as String
        } else {
            return if (nullSafe) "" else null
        }
    }

    fun getConstantStringValue(key: String): String {
        // Since nullSafe, result will not be null
        return getConstantStringValue(key, true)!!
    }

    fun keys(): List<String> {
        ensureConstantMap()
        return keys("", constantMap)
    }

    private fun keys(keyPrefix: String, constantMap: MutableMap<String, Any?>?): List<String> {
        val results = ArrayList<String>()

        if (constantMap != null) {
            for (key in constantMap.keys) {
                val value = constantMap[key]

                if (value != null && value is Map<*, *>) {
                    results.addAll(keys(keyPrefix + key + ".", value as MutableMap<String, Any?>))
                } else {
                    results.add(keyPrefix + key)
                }
            }
        }

        return results
    }

    override fun <V> getVariableValue(keys: Array<String>, vararg substitutes: String): V? {
        return getConstantValue<V>(keys, false, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, substituteMap: Map<String, Any>): V? {
        return getConstantValue(keys, false, substituteMap)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): V? {
        return getConstantValue<V>(keys, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(keys: Array<String>, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return getConstantValue(keys, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, vararg substitutes: String): V? {
        return getConstantValue<V>(key, nullSafe, *substitutes)
    }

    override fun <V> getVariableValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): V? {
        return getConstantValue(key, nullSafe, substituteMap)
    }

    override fun <V> getVariableValue(key: String, vararg substitutes: String): V? {
        return getConstantValue<V>(key, false, *substitutes)
    }

    override fun <V> getVariableValue(key: String, substituteMap: Map<String, Any>): V? {
        return getVariableValue(key, substituteMap)
    }

    override fun getVariableIntegerValue(keys: Array<String>): Int {
        return getVariableIntegerValue(keys, -1)
    }

    override fun getVariableLongValue(keys: Array<String>): Long {
        return getVariableLongValue(keys, -1L)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>): BigDecimal {
        return getVariableBigDecimalValue(keys, BigDecimal.ZERO)!!
    }

    override fun getVariableIntegerValue(keys: Array<String>, defaultValue: Int): Int {
        val v = getConstantValue<Any>(keys, false)

        return MathUtil.parseInt(v.toString(), 10, defaultValue)
    }

    override fun getVariableLongValue(keys: Array<String>, defaultValue: Long): Long {
        val v = getConstantValue<Any>(keys, false)

        return MathUtil.parseLong(v.toString(), 10, defaultValue)
    }

    override fun getVariableBigDecimalValue(keys: Array<String>, defaultValue: BigDecimal?): BigDecimal? {
        val v = getConstantValue<Any>(keys, false)

        return MathUtil.parseBigDecimal(v.toString(), defaultValue)
    }

    override fun getVariableIntegerValue(key: String): Int {
        return getVariableIntegerValue(key, -1)
    }

    override fun getVariableLongValue(key: String): Long {
        return getVariableLongValue(key, -1L)
    }

    override fun getVariableBigDecimalValue(key: String): BigDecimal {
        return getVariableBigDecimalValue(key, BigDecimal.ZERO)!!
    }

    override fun getVariableIntegerValue(key: String, defaultValue: Int): Int {
        val v = getConstantValue<Any>(key, false)

        return MathUtil.parseInt(v.toString(), 10, defaultValue)
    }

    override fun getVariableLongValue(key: String, defaultValue: Long): Long {
        val v = getConstantValue<Any>(key, false)

        return MathUtil.parseLong(v.toString(), 10, defaultValue)
    }

    override fun getVariableBigDecimalValue(key: String, defaultValue: BigDecimal?): BigDecimal? {
        val v = getConstantValue<Any>(key, false)

        return MathUtil.parseBigDecimal(v.toString(), defaultValue)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, vararg substitutes: String): String? {
        return getConstantStringValue(keys, nullSafe, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, nullSafe: Boolean, substitutes: Map<String, Any>): String? {
        return getConstantStringValue(keys, nullSafe, substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, vararg substitutes: String): String? {
        return getConstantStringValue(keys, false, *substitutes)
    }

    override fun getVariableStringValue(keys: Array<String>, substituteMap: Map<String, Any>): String? {
        return getVariableStringValue(keys, substituteMap)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, vararg substitutes: String): String? {
        return getConstantStringValue(key, nullSafe, *substitutes)
    }

    override fun getVariableStringValue(key: String, nullSafe: Boolean, substituteMap: Map<String, Any>): String? {
        return getConstantStringValue(key, nullSafe, substituteMap)
    }

    override fun getVariableStringValue(key: String, vararg substitutes: String): String? {
        return getConstantStringValue(key, false, *substitutes)
    }

    override fun getVariableStringValue(key: String, substitutionMap: Map<String, Any>): String? {
        return getConstantStringValue(key, false, substitutionMap)
    }

    override fun variable(vararg keys: String): String? {
        if (keys.size == 0) {
            return ""
        } else if (keys.size == 1) {
            return getVariableStringValue(keys[0])
        } else {
            val substitutes = Array<String>(keys.size-1, { keys[it+1] })

            return getVariableStringValue(keys[0], *substitutes)
        }
    }
}
