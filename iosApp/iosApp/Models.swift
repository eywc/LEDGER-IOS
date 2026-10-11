import Foundation

struct CryptoAsset: Identifiable, Codable {
    var id: String { symbol }
    let name: String
    let symbol: String
    var balance: Double
    var priceUsd: Double
    var change24h: Double
    let receiveAddress: String
    let networkFee: Double
    var priceHistory: [Double]

    var balanceUsd: Double {
        balance * priceUsd
    }
}

struct TransactionItem: Identifiable, Codable {
    let id: String
    let assetSymbol: String
    let type: String // "Sent", "Received"
    let amount: Double
    let amountUsd: Double
    let toAddress: String
    let timestamp: Date
    let fee: Double
    let status: String // "Confirmed", "Pending"
}
