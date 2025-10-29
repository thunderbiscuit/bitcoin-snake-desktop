package me.tb.bitcoinsnake.data

import org.bitcoindevkit.FullScanRequest
import org.bitcoindevkit.SyncRequest
import org.bitcoindevkit.Update
import org.bitcoindevkit.ElectrumClient as BdkElectrumClient

class ElectrumClient(
    val url: String,
) {
    private val client: BdkElectrumClient = BdkElectrumClient(url)

    fun sync(syncRequest: SyncRequest): Update {
        return client.sync(syncRequest, 10uL, true)
    }

    fun fullScan(fullScanRequest: FullScanRequest): Update {
        return client.fullScan(fullScanRequest, 20uL, 10uL, true)
    }
}
