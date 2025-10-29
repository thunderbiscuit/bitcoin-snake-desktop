package me.tb.bitcoinsnake.data

import org.bitcoindevkit.Address
import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.LocalOutput
import org.bitcoindevkit.Network
import org.bitcoindevkit.Persister
import org.bitcoindevkit.SyncRequest
import org.bitcoindevkit.Update
import org.bitcoindevkit.Wallet

class BitcoinWallet(
    val descriptor: Descriptor,
) {
    private val wallet: Wallet = Wallet.createSingle(
        descriptor = descriptor,
        network = Network.REGTEST,
        persister = Persister.newInMemory(),
    )

    fun getNewAddress(): String {
        return wallet.revealNextAddress(KeychainKind.EXTERNAL).address.toString()
    }

    fun getSyncRequest(): SyncRequest {
        return wallet.startSyncWithRevealedSpks().build()
    }

    fun updateWallet(update: Update) {
        wallet.applyUpdate(update)
    }

    fun checkTransaction(address: String): Boolean {
        val scriptPubKey = Address(address, Network.REGTEST).scriptPubkey().toString()
        val utxos: List<LocalOutput> = wallet.listUnspent()
        if (utxos.isEmpty()) return false
        utxos.forEach { utxo ->
            if (utxo.txout.scriptPubkey.toString() == scriptPubKey) return true
        }
        return false
    }
}
