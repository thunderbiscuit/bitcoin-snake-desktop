package me.tb.bitcoinsnake.data

import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Network
import org.bitcoindevkit.Persister
import org.bitcoindevkit.Wallet

class BitcoinWallet(
    val descriptor: Descriptor,
) {
    val wallet: Wallet = Wallet.Companion.createSingle(
        descriptor = descriptor,
        network = Network.REGTEST,
        persister = Persister.Companion.newInMemory(),
    )

    fun getNewAddress(): String {
        return wallet.revealNextAddress(KeychainKind.EXTERNAL).address.toString()
    }
}
