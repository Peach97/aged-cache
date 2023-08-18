package io.collective

import java.time.Clock

class SimpleAgedKache(private val clock: Clock = Clock.system(Clock.systemDefaultZone().zone)) {

    private val records: MutableMap<Any, ExpirableEntry> = HashMap()

    fun put(key: Any?, value: Any?, retentionInMillis: Int) {
        if (key != null && retentionInMillis !=0){
            val timeExpires = retentionInMillis + clock.millis()
            records[key] = ExpirableEntry(timeExpires, value)
        }
    }
    fun isEmpty(): Boolean {
        return records.isEmpty()
    }
    fun removeExpired(key: Any) {
        var currentTime = clock.millis()
        records.entries.removeIf{it.value.timeExpires <= currentTime}
    }
    fun size(): Int {
        return records.size
    }
    operator fun get(key: Any): Any? {
        removeExpired(key)
        val currentEntry = records[key]
        return currentEntry?.entry
    }
    private class ExpirableEntry(val timeExpires: Long, val entry: Any?)
}