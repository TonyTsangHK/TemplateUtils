package utils.template.constant

import utils.string.StringUtil
import utils.template.ValueSubstitutor

import java.util.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2014-01-09
 * Time: 10:30
 */
object ConstantFactory {
    private val constantHandlerMap = HashMap<String, ConstantHandler>()

    fun getConstantHandler(resourceName: String): ConstantHandler? {
        val k = "_" + resourceName

        var handler: ConstantHandler? = constantHandlerMap[k]

        if (handler == null) {
            if (Thread.currentThread().contextClassLoader.getResource(resourceName) != null) {
                handler = ConstantHandler(Thread.currentThread().contextClassLoader, resourceName)
                constantHandlerMap.put(k, handler)
            }
        }

        return handler
    }

    fun getConstantHandler(parentConstantHandler: ConstantHandler, resourceName: String): ConstantHandler? {
        val hdl = getConstantHandler(resourceName)

        if (hdl != null) {
            hdl.setParentConstantHalder(parentConstantHandler)
            return hdl
        } else {
            return null
        }
    }

    fun getConstantHandler(clz: Class<*>, resourceName: String): ConstantHandler? {
        val k = clz.name + "_" + resourceName

        var handler: ConstantHandler? = constantHandlerMap[k]

        if (handler == null) {
            if (clz.getResource(resourceName) != null) {
                handler = ConstantHandler(clz, resourceName)
                constantHandlerMap.put(k, handler)
            }
        }

        return handler
    }

    fun getConstantHandler(
            clz: Class<*>, resourceName: String, overrideResourceNames: List<String>
    ): ConstantHandler? {
        val k = clz.name + "_" + resourceName + "+" + StringUtil.join(",", overrideResourceNames)

        var handler: ConstantHandler? = constantHandlerMap[k]

        if (handler == null) {
            if (clz.getResource(resourceName) != null) {
                handler = ConstantHandler(clz, resourceName, overrideResourceNames)
                constantHandlerMap.put(k, handler)
            }
        }

        return handler
    }

    fun getConstantHandler(
            parentConstantHandler: ConstantHandler, clz: Class<*>, resourceName: String
    ): ConstantHandler? {
        val hdl = getConstantHandler(clz, resourceName)

        if (hdl != null) {
            hdl.setParentConstantHalder(parentConstantHandler)
            return hdl
        } else {
            return null
        }
    }

    fun getConstantHandler(
            parentConstantHandler: ConstantHandler, clz: Class<*>, resourceName: String,
            overrideResources: List<String>
    ): ConstantHandler? {
        val hdl = getConstantHandler(clz, resourceName, overrideResources)

        if (hdl != null) {
            hdl.setParentConstantHalder(parentConstantHandler)
            return hdl
        } else {
            return null
        }
    }

    fun getConstantHandler(
            clz: Class<out ValueSubstitutor>): ConstantHandler? {
        return getConstantHandler(clz, "def.json")
    }

    fun getConstantHandler(
            parentConstantHandler: ConstantHandler, clz: Class<out ValueSubstitutor>): ConstantHandler? {
        val hdl = getConstantHandler(clz)

        if (hdl != null) {
            hdl.setParentConstantHalder(parentConstantHandler)
            return hdl
        } else {
            return null
        }
    }

    @JvmStatic
    fun getInstance(): ConstantFactory {
        return this
    }
}
