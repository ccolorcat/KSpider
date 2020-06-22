package cc.colorcat.sample.internal

import cc.colorcat.kspider.Connection
import cc.colorcat.kspider.Log
import cc.colorcat.kspider.Seed
import org.openqa.selenium.By
import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.WebDriver
import kotlin.random.Random


/**
 * Author: cxx
 * Date: 2020-06-16
 * GitHub: https://github.com/ccolorcat
 */
class BiliWebDriverConnection(
    driverPath: String,
    needSetProperty: Boolean = true,
    private val reachEnd: ((WebDriver) -> Boolean) = { _ -> false }
) : WebDriverConnection(driverPath, needSetProperty) {

    private companion object {
        const val TAG = "BiliBili"
        private val noMore = By.className("no-more")
    }

    override fun afterLoadUrl(driver: WebDriver, seed: Seed) {
        super.afterLoadUrl(driver, seed)
        when (seed.uri.host.toLowerCase()) {
            BILI_SPACE_HOST -> scrollWithSpacePage(driver, seed)
            BILI_SPACE_DETAIL_HOST -> scrollWithDetailPage(driver, seed)
        }
    }

    override fun clone(): Connection {
        return BiliWebDriverConnection(driverPath, false, reachEnd)
    }

    /**
     * <div data-v-75d7980c="" data-v-740eec4a="" class="div-load-more tc-slate"><!----><div data-v-75d7980c="" class="no-more"><img data-v-75d7980c="" src="//s1.hdslb.com/bfs/seed/bplus-common/dynamic-assets/end.png" class="end-img"><p data-v-75d7980c="" class="end-text">你已经到达了世界的尽头</p></div></div>
     *
     * <div data-v-75d7980c="" class="no-more"><img data-v-75d7980c="" src="//s1.hdslb.com/bfs/seed/bplus-common/dynamic-assets/end.png" class="end-img"><p data-v-75d7980c="" class="end-text">你已经到达了世界的尽头</p></div>
     */
    private fun scrollWithSpacePage(driver: WebDriver, seed: Seed) {
        driver as JavascriptExecutor
        var count = 0
        while (count < 3) {
            scrollByEndKey(driver)
            sleep(50L, 100L)
            if (reachEnd(driver)) {
                Log.d(TAG, "${seed.uri} reach end")
                ++count
                if (count >= 5) sleep(500L, 1000L)
            }
        }
    }

    private fun reachEnd(driver: WebDriver): Boolean {
        if (reachEnd.invoke(driver)) return true
        if (driver.findElements(noMore).isNotEmpty()) return true
        return false
    }

    private fun scrollWithDetailPage(driver: WebDriver, seed: Seed) {
        driver as JavascriptExecutor
        for (count in 0..5) {
            scrollByCoordinate(driver, 0, Random.nextInt(50, 400))
            sleep(500L, 1000L)
            if (driver.findElements(By.className("img-content")).isNotEmpty()) {
                break
            }
        }
    }
}