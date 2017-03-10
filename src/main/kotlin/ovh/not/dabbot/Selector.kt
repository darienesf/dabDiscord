package ovh.not.dabbot

class Selector(val items: List<SelectorItem>, val callback: (Boolean, SelectorItem?) -> Unit, val limit: Int) {
    fun display(): String {
        var r = String()
        var i = 1
        for (item in items) {
            r += "`$i` ${item.format()}\n"
            if (i == limit) break
            i++
        }
        r += "\n**To choose**, use `!!!choose <number>`\nExample: `!!!choose 2` would pick the second option." +
                "\n**To cancel**, use `!!!cancel`."
        return r
    }

    fun choose(item: Int) {
        callback(true, items[item - 1])
    }

    fun cancel() {
        callback(false, null)
    }
}