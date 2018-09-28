package utils.template

import org.testng.Assert
import org.testng.annotations.BeforeMethod
import org.testng.annotations.Test
import utils.file.FileUtil
import utils.template.constant.ConstantFactory
import java.util.HashMap

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2015-01-09
 * Time: 15:13
 */
class TestTemplateProcessor {
    private lateinit var substituteInstance: SubstituteInstance
    private lateinit var templateProcessor: TemplateProcessor
    private lateinit var resourceLoader: ResourceLoader

    @BeforeMethod
    fun setup() {
        resourceLoader = ResourceLoader(SubstituteInstance::class.java)
        substituteInstance = SubstituteInstance()
        templateProcessor = TemplateProcessor(
            resourceLoader, substituteInstance.getConstantHandler()
        )
    }

    @Test
    fun testSubtitute() {
        templateProcessor.appendResourceContent("template.substitute.test.htmlt")

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.substitute.test.htmlt"))
        )

        val variableMap = mapOf(
            "labels.val3" to "val3", "labels.master" to "mmm"
        )

        templateProcessor.clean()
        templateProcessor.appendResourceContent("template.substitute.test.htmlt", variableMap)

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.substitute.test.var.htmlt"))
        )
    }

    @Test
    fun testFunctionSubstitute() {
        templateProcessor.appendResourceContent("template.function.substitute.test.htmlt", substituteInstance, null)

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.function.substitute.test.htmlt"))
        )
    }

    @Test
    fun testArithmetic() {
        val variableMap = mapOf("a" to 10, "b" to 11, "c" to 5)

        templateProcessor.appendResourceContent(
            "template.arithmetic.test.htmlt", substituteInstance, variableMap
        )

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.arithmetic.test.htmlt"))
        )
    }

    @Test
    fun testRepeat() {
        val a = listOf(1, 2, 3, 4, 5)
        val b = listOf(10, 20, 30, 40, 50)

        val variableMap = HashMap<String, Any>()

        variableMap.put("a", a)
        variableMap.put("b", b)

        variableMap.put("testStart", 2)
        variableMap.put("testLength", 10)

        templateProcessor.appendResourceContent("template.repeat.test.htmlt", substituteInstance, variableMap)

        assertTemplateResultEquals(
            templateProcessor.toString().replace("\n\r", "\n"),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.repeat.test.htmlt")).replace("\n\r", "\n")
        )
    }

    @Test
    fun testInclude() {
        val variableMap = HashMap<String, Any>()

        variableMap.put("abc", "inc.abc")
        variableMap.put("bac", "inc.test.bac")

        templateProcessor.appendResourceContent("template.include.test.htmlt", substituteInstance, variableMap)

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.include.test.htmlt"))
        )
    }

    @Test
    fun testTemplateTagVariable() {
        templateProcessor.appendResourceContent("template.var.test.htmlt")

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.var.test.htmlt"))
        )
    }

    @Test
    fun testLangTemplate() {
        val langs = arrayOf("en_US", "zh_TW", "zh_CN")

        val formatConstantHandler = ConstantFactory.getInstance().getConstantHandler(SubstituteInstance::class.java, "formatVariable.json")

        for (lang in langs) {
            val enUSConstantHandler = ConstantFactory.getInstance().getConstantHandler("utils/template/lang/lang.$lang.json")

            val templateProcessor = TemplateProcessor(ResourceLoader(Package.getPackage("utils.template")), enUSConstantHandler)

            templateProcessor.appendResourceContent("template.lang.test.htmlt", formatConstantHandler)

            assertTemplateResultEquals(
                templateProcessor.toString(),
                FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.lang.$lang.test.htmlt"))
            )
        }
    }

    @Test
    fun testNewlineBrReplace() {
        val variableMap = HashMap<String, Any>()

        variableMap.put("newlinedContent", "This is line1\nThis is line2\nThis is line3\nThis is final line without new line!")

        variableMap.put("bredContent", "This is line1<br>This is line2<br/>This is line 3<br />This is line4<br     />This is final line without br!")

        templateProcessor.appendResourceContent("template.newLine.replace.test.htmlt", variableMap)

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.newline.replace.test.htmlt"))
        )
    }

    @Test
    fun testSubVariable() {
        val variableMap = HashMap<String, Any>()

        variableMap.put("testValue", "This is test value from variableMap")

        templateProcessor.appendResourceContent("template.subvar.test.htmlt", substituteInstance, variableMap)

        assertTemplateResultEquals(
            templateProcessor.toString(),
            FileUtil.getFileContent(resourceLoader.getResourceAsStream("result.subvar.test.htmlt"))
        )
    }

    private fun assertTemplateResultEquals(actual: String, expected: String, message: String? = null) {
        val actualModified = actual.replace("\r\n", "\n")
        val expectedModified = expected.replace("\r\n", "\n")

        if (message == null) {
            Assert.assertEquals(actualModified, expectedModified)
        } else {
            Assert.assertEquals(actualModified, expectedModified, message)
        }
    }
}
