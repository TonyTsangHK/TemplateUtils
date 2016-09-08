package utils.template.cache

import net.sf.ehcache.Cache
import net.sf.ehcache.CacheManager
import net.sf.ehcache.Element

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-11-11
 * Time: 14:36
 */
object EhcacheTemplateHandler: TemplateCacheHandler {
    private val CACHE_NAME = "templateCache"

    private fun _getTemplateCache(): Cache? {
        val cacheManager = CacheManager.create()

        return cacheManager.getCache(CACHE_NAME)
    }

    override fun getCache(key: String): String? {
        val cache = _getTemplateCache()

        if (cache != null) {
            if (cache.isElementInMemory(key)) {
                val element = cache.get(key)
                if (element != null && element.objectValue != null && !element.isExpired) {
                    return element.objectValue.toString()
                }
            }
        }

        // Return null if something goes wrong
        return null
    }

    override fun setCache(key: String, value: String) {
        val cache = _getTemplateCache()

        cache?.put(Element(key, value))
    }
}
