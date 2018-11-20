package utils.template.constant

import utils.string.StringUtil
import utils.template.ResourceLoader
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
    
    @JvmOverloads
    fun getConstantHandler(resourceName: String, constantPreprocessor: ConstantPreprocessor? = null): ConstantHandler? {
        val k = "_$resourceName"
        
        var handler: ConstantHandler? = constantHandlerMap[k]

        if (handler == null) {
            if (Thread.currentThread().contextClassLoader.getResource(resourceName) != null) {
                handler = ConstantHandler(Thread.currentThread().contextClassLoader, resourceName, constantPreprocessor)
                constantHandlerMap.put(k, handler)
            }
        }

        return handler
    }
    
    @JvmOverloads
    fun getConstantHandler(parentConstantHandler: ConstantHandler?, resourceName: String, constantPreprocessor: ConstantPreprocessor? = null): ConstantHandler? {
        val hdl = getConstantHandler(resourceName, constantPreprocessor)

        hdl?.setParentConstantHandler(parentConstantHandler)
        
        return hdl
    }

    @JvmOverloads
    fun getConstantHandler(clz: Class<*>, resourceName: String, constantPreprocessor: ConstantPreprocessor? = null): ConstantHandler? {
        val k = "${clz.name}_$resourceName"

        var handler: ConstantHandler? = constantHandlerMap[k]

        if (handler == null) {
            if (clz.getResource(resourceName) != null) {
                handler = ConstantHandler(clz, resourceName, constantPreprocessor)
                constantHandlerMap[k] = handler
            }
        }

        return handler
    }
    
    @JvmOverloads
    fun getConstantHandler(resourceLoader: ResourceLoader, resourceName: String, constantPreprocessor: ConstantPreprocessor? = null): ConstantHandler? {
        // use resource loader's parent resource path as part of key
        val k = "${resourceLoader.parentResourcePath}_$resourceName"
        
        var handler = constantHandlerMap[k]
        
        if (handler == null) {
            if (resourceLoader.getResource(resourceName) != null) {
                handler = ConstantHandler(resourceLoader, resourceName, constantPreprocessor)
                constantHandlerMap[k] = handler
            }
        }
        
        return handler
    }
    
    fun getConstantHandler(
        clz: Class<*>, resourceName: String, vararg overrideResources: String
    ): ConstantHandler? = getConstantHandler(clz, resourceName, null, *overrideResources)
    
    fun getConstantHandler(
        clz: Class<*>, resourceName: String, constantPreprocessor: ConstantPreprocessor?, vararg overrideResources: String
    ): ConstantHandler? {
        val k = "${clz.name}_$resourceName+${StringUtil.join(",", overrideResources)}"
        
        var handler = constantHandlerMap[k]
        
        if (handler == null) {
            if (clz.getResource(resourceName) != null) {
                handler = ConstantHandler(clz, resourceName, constantPreprocessor, *overrideResources)
                constantHandlerMap[k] = handler
            }
        }
        
        return handler
    }
    
    fun getConstantHandler(
        resourceLoader: ResourceLoader, resourceName: String, vararg overrideResources: String
    ): ConstantHandler? = getConstantHandler(resourceLoader, resourceName, null, *overrideResources)
    
    fun getConstantHandler(
        resourceLoader: ResourceLoader, resourceName: String, constantPreprocessor: ConstantPreprocessor?,
        vararg overrideResources: String
    ): ConstantHandler? {
        // use resource loader's parent resource path as part of key
        val k = "${resourceLoader.parentResourcePath}_$resourceName+${StringUtil.join(",", overrideResources)}"
        
        var handler = constantHandlerMap[k]
        
        if (handler == null) {
            if (resourceLoader.getResource(resourceName) != null) {
                handler = ConstantHandler(resourceLoader, resourceName, constantPreprocessor, *overrideResources)
                constantHandlerMap[k] = handler
            }
        }
        
        return handler
    }

    fun getConstantHandler(
        clz: Class<*>, resourceName: String, overrideResources: List<String>
    ): ConstantHandler? = getConstantHandler(clz, resourceName, null, overrideResources)
    
    fun getConstantHandler(
        clz: Class<*>, resourceName: String, constantPreprocessor: ConstantPreprocessor?,
        overrideResources: List<String>
    ): ConstantHandler? {
        val k = "${clz.name}_$resourceName+${StringUtil.join(",", overrideResources)}"

        var handler = constantHandlerMap[k]

        if (handler == null) {
            if (clz.getResource(resourceName) != null) {
                handler = ConstantHandler(clz, resourceName, constantPreprocessor, overrideResources)
                constantHandlerMap[k] = handler
            }
        }

        return handler
    }
    
    fun getConstantHandler(
        resourceLoader: ResourceLoader, resourceName: String, overrideResources: List<String>
    ): ConstantHandler? = getConstantHandler(resourceLoader, resourceName, null, overrideResources)
    
    fun getConstantHandler(
        resourceLoader: ResourceLoader, resourceName: String, constantPreprocessor: ConstantPreprocessor?, 
        overrideResources: List<String>
    ): ConstantHandler? {
        // use resource loader's parent resource path as part of key
        val k = "${resourceLoader.parentResourcePath}_$resourceName"
        
        var handler = constantHandlerMap[k]
        
        if (handler == null) {
            if (resourceLoader.getResource(resourceName) != null) {
                handler = ConstantHandler(resourceLoader, resourceName, constantPreprocessor, overrideResources)
                constantHandlerMap[k] = handler
            }
        }
        
        return handler
    }
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, clz: Class<*>, resourceName: String
    ): ConstantHandler? = getConstantHandler(parentConstantHandler, null, clz, resourceName)
    
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, constantPreprocessor: ConstantPreprocessor?, clz: Class<*>, resourceName: String
    ): ConstantHandler? {
        val hdl = getConstantHandler(clz, resourceName, constantPreprocessor)
        
        hdl?.setParentConstantHandler(parentConstantHandler)
        
        return hdl
    }
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, resourceLoader: ResourceLoader, resourceName: String
    ): ConstantHandler? = getConstantHandler(parentConstantHandler, resourceLoader, resourceName)
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, resourceLoader: ResourceLoader, resourceName: String,
        constantPreprocessor: ConstantPreprocessor?
    ): ConstantHandler? {
        val hdl = getConstantHandler(resourceLoader, resourceName, constantPreprocessor)

        hdl?.setParentConstantHandler(parentConstantHandler)

        return hdl
    }
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler, clz: Class<*>, resourceName: String,
        vararg overrideResources: String
    ): ConstantHandler? = getConstantHandler(parentConstantHandler, clz, resourceName, null, *overrideResources)
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler, clz: Class<*>, resourceName: String, constantPreprocessor: ConstantPreprocessor?,
        vararg overrideResources: String
    ): ConstantHandler? {
        val hdl = getConstantHandler(clz, resourceName, constantPreprocessor, *overrideResources)

        hdl?.setParentConstantHandler(parentConstantHandler)

        return hdl
    }
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler, resourceLoader: ResourceLoader, resourceName: String, vararg overrideResources: String
    ): ConstantHandler? = getConstantHandler(parentConstantHandler, resourceLoader, resourceName, null, *overrideResources)
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler, resourceLoader: ResourceLoader, resourceName: String,
        constantPreprocessor: ConstantPreprocessor?, vararg overrideResources: String
    ): ConstantHandler? {
        val hdl = getConstantHandler(resourceLoader, resourceName, constantPreprocessor, *overrideResources)

        hdl?.setParentConstantHandler(parentConstantHandler)

        return hdl
    }

    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, clz: Class<*>, resourceName: String,
        overrideResources: List<String>
    ): ConstantHandler? = getConstantHandler(parentConstantHandler, clz, resourceName, null, overrideResources)
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, clz: Class<*>, resourceName: String,
        constantPreprocessor: ConstantPreprocessor?, overrideResources: List<String>
    ): ConstantHandler? {
        val hdl = getConstantHandler(clz, resourceName, constantPreprocessor, overrideResources)
        
        hdl?.setParentConstantHandler(parentConstantHandler)
        
        return hdl
    }
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, resourceLoader: ResourceLoader, resourceName: String,
        overrideResources: List<String>
    ): ConstantHandler? = getConstantHandler(parentConstantHandler, resourceLoader, resourceName, null, overrideResources)
    
    fun getConstantHandler(
        parentConstantHandler: ConstantHandler?, resourceLoader: ResourceLoader, resourceName: String,
        constantPreprocessor: ConstantPreprocessor?, overrideResources: List<String>
    ): ConstantHandler? {
        val hdl = getConstantHandler(resourceLoader, resourceName, constantPreprocessor, overrideResources)

        hdl?.setParentConstantHandler(parentConstantHandler)

        return hdl
    }

    @JvmOverloads
    fun getConstantHandler(clz: Class<out ValueSubstitutor>, constantPreprocessor: ConstantPreprocessor? = null): ConstantHandler? {
        return getConstantHandler(clz, "def.json", constantPreprocessor)
    }

    @JvmOverloads
    fun getConstantHandler(parentConstantHandler: ConstantHandler?, clz: Class<out ValueSubstitutor>, constantPreprocessor: ConstantPreprocessor? = null): ConstantHandler? {
        val hdl = getConstantHandler(clz, constantPreprocessor)

        if (hdl != null) {
            hdl.setParentConstantHandler(parentConstantHandler)
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
