package cc.colorcat.sample.internal

import cc.colorcat.kspider.Connection
import cc.colorcat.kspider.Seed
import cc.colorcat.kspider.WebSnapshot
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.Keys
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeDriverService
import kotlin.random.Random

/**
 * Author: cxx
 * Date: 2020-05-28
 * GitHub: https://github.com/ccolorcat
 */
open class WebDriverConnection(protected val driverPath: String, needSetProperty: Boolean = true) : Connection {
    init {
        // IE浏览器    （webdriver.ie.driver）
        // 火狐浏览器    (webdriver.gecko.driver)
        // 谷歌浏览器    (webdriver.chrome.driver)
        if (needSetProperty) {
            System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverPath)
        }
    }

    protected var _driver: WebDriver? = null

    private val driver: WebDriver
        get() {
            if (_driver == null) {
                //创建chrome参数对象
//                val options = ChromeOptions()
                //浏览器后台运行
//            options.addArguments("headless")
//                _driver = ChromeDriver(options)
                _driver = ChromeDriver()
            }
            return _driver!!
        }
    private var snapshot: WebSnapshot? = null

    override fun get(seed: Seed): WebSnapshot? {
        if (snapshot?.uri == seed.uri) {
            return snapshot!!
        }
        beforeLoadUrl(driver, seed)
        driver.get(seed.uri.toString())
        afterLoadUrl(driver, seed)
        snapshot = driver.pageSource?.let { WebSnapshot(seed.uri, it.toByteArray()) }
        return snapshot
    }

    open fun beforeLoadUrl(driver: WebDriver, seed: Seed) {

    }

    open fun afterLoadUrl(driver: WebDriver, seed: Seed) {
        sleep(300L, 1000L)
    }

    override fun clone(): Connection {
        return WebDriverConnection(driverPath, false).apply {
            _driver = this@WebDriverConnection._driver
        }
    }

    override fun onSeedFinish(seed: Seed) {
        snapshot = null
        _driver?.apply {
            close()
        }
    }

    override fun onAllFinish() {
        _driver?.apply {
            quit()
        }
    }

    protected fun sleep(from: Long, until: Long) {
        Thread.sleep(Random.nextLong(from, until))
    }

    protected fun scrollByCoordinate(driver: WebDriver, fromY: Int = 0, toY: Int = 600) {
        driver as JavascriptExecutor
        driver.executeScript("window.scrollBy($fromY, $toY)")
    }

    protected fun scrollByBodyHeight(driver: WebDriver) {
        driver as JavascriptExecutor
        // 至页面底部
        driver.executeScript("window.scrollTo(0, document.body.scrollHeight)")
    }

    protected fun scrollToElement(driver: WebDriver, by: By) {
        driver as JavascriptExecutor
        val script = "return arguments[0].scrollIntoView();"
        val element = driver.findElement(by) // driver.findElement(By.id("#test"))
        driver.executeScript(script, element)
    }

    protected fun scrollByEndKey(driver: WebDriver) {
        driver as JavascriptExecutor
        val body = driver.findElement(By.cssSelector("body"))
//        body.click() // 有的时候必须点击一下，下拉才能生效（有的网站是这样，原因未找到）
//        body.sendKeys(Keys.PAGE_DOWN) // 小幅下滑
        body.sendKeys(Keys.END) // 一滑到底
    }
}