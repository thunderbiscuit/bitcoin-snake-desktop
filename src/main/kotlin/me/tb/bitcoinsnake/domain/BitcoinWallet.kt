package me.tb.bitcoinsnake.domain

import org.bitcoindevkit.Descriptor
import org.bitcoindevkit.KeychainKind
import org.bitcoindevkit.Network
import org.bitcoindevkit.Persister
import org.bitcoindevkit.Wallet as BdkWallet

class BitcoinWallet(
    val descriptor: Descriptor,
) {
    val wallet: BdkWallet = BdkWallet.createSingle(
        descriptor = descriptor,
        network = Network.REGTEST,
        persister = Persister.newInMemory(),
    )

    fun getNewAddress(): String {
        return wallet.revealNextAddress(KeychainKind.EXTERNAL).address.toString()
    }
}
