package com.kgpay.app.data.ussd

data class UssdHistoryLine(val amount: String, val receiver: String, val date: String, val time: String)

/**
 * Kotlin port of the character-scanning parser from the original `TransactionsHistory.java`
 * (`getTransactions` / `extractData`). The bank's *99*6*1# reply is one long string like
 * "1. Sent Rs.1.00 to 98765@upi on 27-Jun-21 2:42 AM  2. Sent Rs.2.00 to ..." — this walks it
 * looking for the "Rs" / "to" / "on" landmarks the bank always includes.
 */
object UssdHistoryParser {
    fun parse(status: String): List<UssdHistoryLine> {
        val results = mutableListOf<UssdHistoryLine>()
        var searchFrom = 0
        runCatching {
            while (true) {
                val idxRsRaw = status.indexOf("Rs", searchFrom)
                if (idxRsRaw == -1) break
                var idxRs = idxRsRaw
                while (status[idxRs] !in '0'..'9') idxRs++

                var idxUpiId = status.indexOf("to", idxRs) + 2
                while (!(status[idxUpiId] in '0'..'9' || status[idxUpiId] in 'A'..'z')) idxUpiId++

                var idxDate = status.indexOf("on", idxUpiId)
                while (status[idxDate] !in '0'..'9') idxDate++

                var idxTime = status.indexOf(" ", idxDate)
                while (status[idxTime] !in '0'..'9') idxTime++

                val amount = "₹" + extract(status, idxRs, spaces = 0)
                val receiver = extract(status, idxUpiId, spaces = 0)
                val date = extract(status, idxDate, spaces = 0).replace('-', ' ')
                val time = extract(status, idxTime, spaces = 1)

                results += UssdHistoryLine(amount, receiver, date, time)
                searchFrom = idxTime
            }
        }
        return results
    }

    private fun extract(status: String, startIdx: Int, spaces: Int): String {
        val lastIdx = status.indexOf(" ", startIdx)
        if (lastIdx == -1) return status.substring(startIdx)
        var result = status.substring(startIdx, lastIdx)
        if (spaces == 1 && lastIdx + 1 < status.length) {
            result += if (status[lastIdx + 1] == 'A') " AM" else " PM"
        }
        return result
    }
}
