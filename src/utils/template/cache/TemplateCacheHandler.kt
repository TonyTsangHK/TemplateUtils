package utils.template.cache

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-11-11
 * Time: 14:35
 */
interface TemplateCacheHandler {
    fun getCache(key: String): String?
    fun setCache(key: String, value: String)
}
