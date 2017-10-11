import kotlin.js.Promise

external fun require(module: String): dynamic
external val exports: dynamic

fun main(args: Array<String>) {
    val functions = require("firebase-functions")

    exports.helloWorld = functions.https.onRequest { _, res ->
        res.status(200).send("Hello World!")
    }

    exports.ObjectJson = functions.https.onRequest { _, res ->
        val p = Package("package1", 20, listOf(Item("A", ItemType.CAR), Item("B", ItemType.FURNITURE)))
        res.status(200).send(p)
    }

    exports.promise = functions.https.onRequest { req, res ->
        Promise<Double>({ resolve, _ ->
            val piValue = (0..req.query.number).sumByDouble {
                val num = if(it % 2L == 0L) -1.0 else 1.0
                num / (2 * it + 1)
            }
            resolve(piValue)
        }).then({
            console.log("value $it")
            res.status(200).send(it.toString())
        }).catch {
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