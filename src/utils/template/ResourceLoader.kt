package utils.template

import java.io.InputStream
import java.net.URL

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2014-01-09
 * Time: 09:43
 */
class ResourceLoader(parentResourcePath: String?) {
    private var parentResourcePath: String? = null

    constructor(clazz: Class<*>) : this(clazz.`package`) {
    }

    constructor(pack: Package) : this(pack.name.replace(".", "/")) {
    }

    init {
        if (parentResourcePath != null) {
            if (parentResourcePath.endsWith("/")) {
                this.parentResourcePath = parentResourcePath
            } else {
                this.parentResourcePath = parentResourcePath + "/"
            }
        } else {
            this.parentResourcePath = ""
        }
    }

    fun getResource(resourceLocation: String): URL {
        val url: URL?

        val classLoader = Thread.currentThread().contextClassLoader
        if (classLoader != null) {
            url = classLoader.getResource(parentResourcePath!! + resourceLocation)
            if (url != null) {
                return url
            }
        }

        return ClassLoader.getSystemResource(parentResourcePath!! + resourceLocation)
    }

    fun getResourceAsStream(resourceLocation: String): InputStream {
        val stream: InputStream?

        val classLoader = Thread.currentThread().contextClassLoader
        if (classLoader != null) {
            stream = classLoader.getResourceAsStream(parentResourcePath!! + resourceLocation)
            if (stream != null) {
                return stream
            }
        }

        return ClassLoader.getSystemResourceAsStream(parentResourcePath!! + resourceLocation)
    }
}
