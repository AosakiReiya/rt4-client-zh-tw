package KondoKit.util

/**
 * Shared loader for GE price JSON from remote or bundled sources.
 */
object GEPriceService {
    private const val REMOTE_GE_URL = "https://cdn.2009scape.org/gedata/latest.json"

    private data class RemoteGEItem(
        val item_id: String?,
        val value: String?
    )

    private data class LocalGEItem(
        val id: String?,
        val grand_exchange_price: String?
    )

    private fun <T> mapToPriceMap(
        items: Array<T>,
        idSelector: (T) -> String?,
        priceSelector: (T) -> String?
    ): Map<String, String> {
        return items.mapNotNull { item ->
            val id = idSelector(item)
            val price = priceSelector(item)
            if (id != null && price != null) id to price else null
        }.toMap()
    }

    fun loadRemotePrices(): Map<String, String> {
        return try {
            val payload = HttpFetcher.fetchString(REMOTE_GE_URL)
            val items = JsonParser.fromJson<Array<RemoteGEItem>>(payload)
            mapToPriceMap(items, { it.item_id }, { it.value })
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun loadLocalPrices(jsonProvider: () -> String?): Map<String, String> {
        return try {
            val json = jsonProvider() ?: return emptyMap()
            val items = JsonParser.fromJson<Array<LocalGEItem>>(json)
            mapToPriceMap(items, { it.id }, { it.grand_exchange_price })
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
