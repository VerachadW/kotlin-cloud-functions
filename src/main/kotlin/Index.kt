import kotlin.js.Promise

external fun require(module: String): dynamic
external val exports: dynamic

fun main(args: Array<String>) {
    val functions = require("firebase-functions")
    val geoHash = require("latlon-geohash")

    exports.helloWorld = functions.https.onRequest { _, res ->
        res.status(200).send("Hello World!")
    }

    exports.ObjectJson = functions.https.onRequest { _, res ->
        val p = Package("package1", 20, listOf(Item("A", ItemType.CAR), Item("B", ItemType.FURNITURE)))
        res.status(200).send(p)
    }

    exports.geoHash = functions.https.onRequest { req, res ->
        Promise<String>({ resolve, reject ->
            if (req.query?.lat == null || req.query?.lng == null) {
                reject(Throwable("Need lat and lng query parameters"))
            }
            val precision = req.query.precision ?: 5
            val hash = geoHash.encode(req.query.lat, req.query.lng, precision)
            resolve(hash)
        }).then({
            console.log("value $it")
            res.status(200).send(it)
        }).catch {
            res.status(500).send(it.message)
            console.log("error: $it")
        }
    }
}

enum class ItemType {
    CAR,
    FURNITURE;
}

data class Item(val name: String, val type: ItemType)

data class Package(val name: String, val quantity: Int, val items: List<Item>)