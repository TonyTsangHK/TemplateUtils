package utils.template.constant

import utils.date.DateTimeParser
import java.util.Date

/**
 * Created with IntelliJ IDEA.
 * User: Tony Tsang
 * Date: 2014-01-09
 * Time: 10:25
 */
object DynamicConstant {
    @JvmStatic
    operator fun get(key: String): Any? {
        when (key) {
            "currentDate" -> return DateTimeParser.format(
                    Date(), DateTimeParser.NORMAL_DATE_FORMAT)
            "currentDateTime" -> return DateTimeParser.format(
                    Date(), DateTimeParser.NORMAL_DATETIME_FORMAT)
            "currentYear" -> return DateTimeParser.format(Date(), "yyyy")
            "currentMonth" -> return DateTimeParser.format(Date(), "MM")
            "currentDay" -> return DateTimeParser.format(Date(), "dd")
            "currentHour" -> return DateTimeParser.format(Date(), "HH")
            "currentMinute" -> return DateTimeParser.format(Date(), "mm")
            "currentSecond" -> return DateTimeParser.format(Date(), "ss")
            "currentTime" -> return DateTimeParser.format(Date(), DateTimeParser.NORMAL_TIME_FORMAT)
            else -> return null
        }
    }
}
