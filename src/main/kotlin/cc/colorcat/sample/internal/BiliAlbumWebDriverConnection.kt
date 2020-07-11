package cc.colorcat.sample.internal;

import cc.colorcat.kspider.Connection
import cc.colorcat.kspider.Seed
import cc.colorcat.kspider.WebSnapshot
import org.openqa.selenium.By

/**
 * https://space.bilibili.com/16021397/album
 */
class BiliAlbumWebDriverConnection(
    driverPath: String,
    needSetProperty: Boolean = true
) : WebDriverConnection(driverPath, needSetProperty) {
    private companion object {
        private val filter = "^(http)(s)?://space.bilibili.com/\\d+/(album)$".toRegex()
    }

    override fun get(seed: Seed): WebSnapshot? {
        if (snapshot?.uri == seed.uri) {
            return snapshot!!
        }
        driver.get(seed.uri.toString())
        val builder = StringBuilder(driver.pageSource)
        val nextPage = driver.findElement(By.cssSelector("li.panigation:last-child"))
        while (nextPage != null && nextPage.isDisplayed) {
            nextPage.click()
            sleep(500L, 800L)
            builder.append(driver.pageSource)
        }
        return WebSnapshot(seed.uri, builder.toString().toByteArray())
    }

    override fun clone(): Connection {
        return BiliAlbumWebDriverConnection(driverPath, false)
            .apply { _driver = this@BiliAlbumWebDriverConnection._driver }
    }

    override fun canConnect(seed: Seed): Boolean {
        return seed.uri.toString().matches(filter)
    }
}
