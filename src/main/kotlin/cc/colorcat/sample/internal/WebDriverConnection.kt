package cc.colorcat.sample.internal

import cc.colorcat.kspider.Connection
import cc.colorcat.kspider.Seed
import cc.colorcat.kspider.WebSnapshot
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import kotlin.random.Random

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
class WebDriverConnection(private val driverPath: String, needSetProperty: Boolean = true) : Connection {
    init {
        // IE浏览器    （webdriver.ie.driver）
        // 火狐浏览器    (webdriver.gecko.driver)
        // 谷歌浏览器    (webdriver.chrome.driver)
        if (needSetProperty) {
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverPath)
        }
    }

    private var _driver: WebDriver? = null
    private val driver: WebDriver
        get() {
            if (_driver != null) return _driver!!
//            //创建chrome参数对象
//            val options = ChromeOptions()
//            //浏览器后台运行
//            options.addArguments("headless")
//            _driver = ChromeDriver(options)
            _driver = ChromeDriver()
            return _driver!!
        }
    private var snapshot: WebSnapshot? = null

    override fun get(seed: Seed): WebSnapshot? {
        if (snapshot?.uri == seed.uri) {
            return snapshot!!
        }
        Thread.sleep(Random.nextLong(500L, 2000L))
        driver.get(seed.uri.toString())
        snapshot = driver.pageSource?.let { WebSnapshot(seed.uri, it.toByteArray()) }
        return snapshot
    }

    override fun clone(): Connection {
        val connection = WebDriverConnection(driverPath, false)
        connection._driver = driver
        return connection
    }

    override fun onSeedFinish(seed: Seed) {
        snapshot = null
    }

    override fun onAllFinish() {
        _driver?.run {
            close()
            quit()
        }
    }
}