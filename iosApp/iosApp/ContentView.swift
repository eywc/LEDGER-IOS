import SwiftUI

struct ContentView: View {
    @State private var assets: [CryptoAsset] = [
        CryptoAsset(
            name: "Bitcoin",
            symbol: "BTC",
            balance: 1.4829,
            priceUsd: 68450.00,
            change24h: 2.34,
            receiveAddress: "bc1qxy2kgdygjrsqtzq2n0yrf2493p83kkfjhx0wlh",
            networkFee: 0.00012,
            priceHistory: [66500, 67200, 66900, 67800, 68100, 68450]
        ),
        CryptoAsset(
            name: "Ethereum",
            symbol: "ETH",
            balance: 12.3500,
            priceUsd: 3520.00,
            change24h: -0.85,
            receiveAddress: "0x71C80e8125835c249a5b39976451634B03d7c570",
            networkFee: 0.0025,
            priceHistory: [3590, 3560, 3540, 3500, 3515, 3520]
        ),
        CryptoAsset(
            name: "Solana",
            symbol: "SOL",
            balance: 145.2000,
            priceUsd: 178.50,
            change24h: 5.12,
            receiveAddress: "7xKXtg2CW87d97TXJSDpbD5jBkheTqA83TZRuJosgAsU",
            networkFee: 0.00005,
            priceHistory: [168, 171, 170, 174, 176, 178.5]
        ),
        CryptoAsset(
            name: "Ripple",
            symbol: "XRP",
            balance: 5400.0,
            priceUsd: 0.584,
            change24h: 1.15,
            receiveAddress: "rEb8TK3gBgk5auZyyb6328Gy6eaYTCQV8o",
            networkFee: 0.02,
            priceHistory: [0.57, 0.575, 0.572, 0.58, 0.582, 0.584]
        ),
        CryptoAsset(
            name: "Cardano",
            symbol: "ADA",
            balance: 8900.0,
            priceUsd: 0.462,
            change24h: -1.40,
            receiveAddress: "addr1q9d80dgy334p8syrzwhsz37s923k8p736scuwr673s",
            networkFee: 0.17,
            priceHistory: [0.47, 0.468, 0.465, 0.460, 0.461, 0.462]
        )
    ]

    @State private var transactions: [TransactionItem] = [
        TransactionItem(id: "tx-1", assetSymbol: "BTC", type: "Received", amount: 0.25, amountUsd: 17112.5, toAddress: "bc1qxy2kgdygjrsq...", timestamp: Date().addingTimeInterval(-3600 * 2), fee: 0.00012, status: "Confirmed"),
        TransactionItem(id: "tx-2", assetSymbol: "ETH", type: "Sent", amount: 1.5, amountUsd: 5280.0, toAddress: "0x89205A3A3b2A69De6...", timestamp: Date().addingTimeInterval(-3600 * 18), fee: 0.0025, status: "Confirmed")
    ]

    @State private var showingBackstage = false
    @State private var showingSendSheet = false
    @State private var showingReceiveSheet = false
    @State private var showingHistorySheet = false
    @State private var selectedAssetForSend: CryptoAsset?
    @State private var selectedAssetForReceive: CryptoAsset?

    // Send Form State
    @State private var sendAddress = ""
    @State private var sendAmount = ""

    var totalBalanceUsd: Double {
        assets.reduce(0) { $0 + $1.balanceUsd }
    }

    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea()

                ScrollView {
                    VStack(alignment: .leading, spacing: 24) {
                        // Header Bar
                        HStack {
                            Text("LEDGER")
                                .font(.system(size: 20, weight: .black, design: .monospaced))
                                .tracking(3)
                                .foregroundColor(.white)

                            Spacer()

                            Button(action: { showingHistorySheet = true }) {
                                Image(systemName: "clock.arrow.circlepath")
                                    .font(.system(size: 18, weight: .semibold))
                                    .foregroundColor(.white)
                                    .frame(width: 40, height: 40)
                                    .background(Color.white.opacity(0.1))
                                    .clipShape(Circle())
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.top, 12)

                        // Balance Card
                        VStack(alignment: .leading, spacing: 8) {
                            Text("TOTAL PORTFOLIO")
                                .font(.system(size: 12, weight: .semibold))
                                .foregroundColor(.gray)
                                .tracking(1.5)

                            Text("$\(String(format: "%.2f", totalBalanceUsd))")
                                .font(.system(size: 38, weight: .bold))
                                .foregroundColor(.white)

                            HStack(spacing: 6) {
                                Image(systemName: "arrow.up.right")
                                    .font(.system(size: 12, weight: .bold))
                                Text("+2.48% (24h)")
                                    .font(.system(size: 13, weight: .medium))
                            }
                            .foregroundColor(Color.green)
                            .padding(.horizontal, 10)
                            .padding(.vertical, 4)
                            .background(Color.green.opacity(0.15))
                            .cornerRadius(12)
                        }
                        .padding(.horizontal, 20)

                        // Quick Actions
                        HStack(spacing: 12) {
                            actionButton(title: "Send", icon: "arrow.up.right") {
                                selectedAssetForSend = assets.first
                                showingSendSheet = true
                            }
                            actionButton(title: "Receive", icon: "arrow.down.left") {
                                selectedAssetForReceive = assets.first
                                showingReceiveSheet = true
                            }
                            actionButton(title: "History", icon: "list.bullet") {
                                showingHistorySheet = true
                            }
                        }
                        .padding(.horizontal, 20)

                        // Asset List Header & Hint
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text("YOUR ASSETS")
                                    .font(.system(size: 13, weight: .bold))
                                    .foregroundColor(.gray)
                                    .tracking(1.2)
                                Spacer()
                                Text("Hold asset to edit balance")
                                    .font(.system(size: 11))
                                    .foregroundColor(.gray.opacity(0.8))
                            }
                        }
                        .padding(.horizontal, 20)

                        // Assets
                        VStack(spacing: 12) {
                            ForEach($assets) { $asset in
                                assetRow(asset: asset)
                                    .contextMenu {
                                        Button(action: {
                                            showingBackstage = true
                                        }) {
                                            Label("Edit Balance in LEDGER Backstage", systemImage: "pencil")
                                        }
                                        Button(action: {
                                            selectedAssetForSend = asset
                                            showingSendSheet = true
                                        }) {
                                            Label("Send \(asset.symbol)", systemImage: "arrow.up.right")
                                        }
                                        Button(action: {
                                            selectedAssetForReceive = asset
                                            showingReceiveSheet = true
                                        }) {
                                            Label("Receive \(asset.symbol)", systemImage: "arrow.down.left")
                                        }
                                    }
                                    .onLongPressGesture {
                                        let impact = UIImpactFeedbackGenerator(style: .medium)
                                        impact.impactOccurred()
                                        showingBackstage = true
                                    }
                            }
                        }
                        .padding(.horizontal, 20)
                        .padding(.bottom, 40)
                    }
                }
                .refreshable {
                    await refreshPrices()
                }
            }
            .navigationBarHidden(true)
        }
        .preferredColorScheme(.dark)
        // Backstage Sheet
        .sheet(isPresented: $showingBackstage) {
            BackstageView(assets: $assets)
        }
        // Send Sheet
        .sheet(isPresented: $showingSendSheet) {
            if let asset = selectedAssetForSend {
                SendSheetView(
                    asset: asset,
                    allAssets: assets,
                    onSend: { sentAmount, toAddress in
                        executeSend(asset: asset, amount: sentAmount, toAddress: toAddress)
                    }
                )
            }
        }
        // Receive Sheet
        .sheet(isPresented: $showingReceiveSheet) {
            if let asset = selectedAssetForReceive {
                ReceiveSheetView(asset: asset)
            }
        }
        // History Sheet
        .sheet(isPresented: $showingHistorySheet) {
            HistorySheetView(transactions: transactions)
        }
    }

    private func actionButton(title: String, icon: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            VStack(spacing: 8) {
                Image(systemName: icon)
                    .font(.system(size: 18, weight: .bold))
                Text(title)
                    .font(.system(size: 13, weight: .medium))
            }
            .foregroundColor(.white)
            .frame(maxWidth: .infinity)
            .padding(.vertical, 14)
            .background(Color(red: 0.12, green: 0.12, blue: 0.14))
            .cornerRadius(16)
        }
    }

    private func assetRow(asset: CryptoAsset) -> some View {
        HStack(spacing: 16) {
            // Icon
            ZStack {
                Circle()
                    .fill(Color(red: 0.16, green: 0.16, blue: 0.18))
                    .frame(width: 44, height: 44)
                Text(asset.symbol.prefix(1))
                    .font(.system(size: 18, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
            }

            VStack(alignment: .leading, spacing: 4) {
                Text(asset.name)
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.white)
                Text("$\(String(format: "%.2f", asset.priceUsd))")
                    .font(.system(size: 13))
                    .foregroundColor(.gray)
            }

            Spacer()

            VStack(alignment: .trailing, spacing: 4) {
                Text("$\(String(format: "%.2f", asset.balanceUsd))")
                    .font(.system(size: 16, weight: .semibold))
                    .foregroundColor(.white)
                Text("\(String(format: "%.4f", asset.balance)) \(asset.symbol)")
                    .font(.system(size: 13))
                    .foregroundColor(.gray)
            }
        }
        .padding(16)
        .background(Color(red: 0.08, green: 0.08, blue: 0.09))
        .cornerRadius(18)
    }

    private func refreshPrices() async {
        let generator = UIImpactFeedbackGenerator(style: .light)
        generator.impactOccurred()
        try? await Task.sleep(nanoseconds: 800_000_000)

        // Realistic live market tick simulation
        for i in assets.indices {
            let delta = Double.random(in: -0.015...0.02)
            assets[i].priceUsd = max(0.001, assets[i].priceUsd * (1.0 + delta))
        }
    }

    private func executeSend(asset: CryptoAsset, amount: Double, toAddress: String) {
        guard let index = assets.firstIndex(where: { $0.symbol == asset.symbol }) else { return }
        if assets[index].balance >= amount {
            assets[index].balance -= amount
            let tx = TransactionItem(
                id: "tx-\(UUID().uuidString.prefix(6))",
                assetSymbol: asset.symbol,
                type: "Sent",
                amount: amount,
                amountUsd: amount * asset.priceUsd,
                toAddress: toAddress,
                timestamp: Date(),
                fee: asset.networkFee,
                status: "Confirmed"
            )
            transactions.insert(tx, at: 0)
        }
    }
}

// Backstage View for Editing Balances
struct BackstageView: View {
    @Binding var assets: [CryptoAsset]
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea()

                List {
                    Section(header: Text("EDIT BALANCES").foregroundColor(.gray)) {
                        ForEach($assets) { $asset in
                            HStack {
                                VStack(alignment: .leading) {
                                    Text(asset.name)
                                        .font(.system(size: 16, weight: .bold))
                                        .foregroundColor(.white)
                                    Text(asset.symbol)
                                        .font(.system(size: 12))
                                        .foregroundColor(.gray)
                                }

                                Spacer()

                                TextField("Balance", value: $asset.balance, format: .number)
                                    .keyboardType(.decimalPad)
                                    .multilineTextAlignment(.trailing)
                                    .font(.system(size: 16, weight: .semibold))
                                    .foregroundColor(.white)
                                    .frame(width: 120)
                            }
                            .listRowBackground(Color(red: 0.1, green: 0.1, blue: 0.12))
                        }
                    }
                }
                .listStyle(InsetGroupedListStyle())
            }
            .navigationTitle("LEDGER")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.white)
                }
            }
        }
        .preferredColorScheme(.dark)
    }
}

// Send Sheet View
struct SendSheetView: View {
    let asset: CryptoAsset
    let allAssets: [CryptoAsset]
    let onSend: (Double, String) -> Void

    @Environment(\.presentationMode) var presentationMode
    @State private var recipientAddress = ""
    @State private var amountString = ""
    @State private var errorMessage: String?

    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea()

                VStack(spacing: 20) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Recipient Address")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundColor(.gray)

                        HStack {
                            TextField("Paste any \(asset.symbol) address", text: $recipientAddress)
                                .foregroundColor(.white)
                                .font(.system(size: 14))

                            Button(action: {
                                if let string = UIPasteboard.general.string {
                                    recipientAddress = string
                                }
                            }) {
                                Text("PASTE")
                                    .font(.system(size: 12, weight: .bold))
                                    .foregroundColor(.white)
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(Color.white.opacity(0.15))
                                    .cornerRadius(8)
                            }
                        }
                        .padding()
                        .background(Color(red: 0.1, green: 0.1, blue: 0.12))
                        .cornerRadius(12)
                    }

                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            Text("Amount (\(asset.symbol))")
                                .font(.system(size: 13, weight: .semibold))
                                .foregroundColor(.gray)
                            Spacer()
                            Text("Balance: \(String(format: "%.4f", asset.balance))")
                                .font(.system(size: 12))
                                .foregroundColor(.gray)
                        }

                        TextField("0.0", text: $amountString)
                            .keyboardType(.decimalPad)
                            .foregroundColor(.white)
                            .font(.system(size: 24, weight: .bold))
                            .padding()
                            .background(Color(red: 0.1, green: 0.1, blue: 0.12))
                            .cornerRadius(12)
                    }

                    // Network Fee
                    HStack {
                        Text("Network Fee (Real)")
                            .foregroundColor(.gray)
                            .font(.system(size: 13))
                        Spacer()
                        Text("\(String(format: "%.6f", asset.networkFee)) \(asset.symbol)")
                            .foregroundColor(.white)
                            .font(.system(size: 13, weight: .medium))
                    }
                    .padding(.horizontal, 4)

                    if let err = errorMessage {
                        Text(err)
                            .foregroundColor(.red)
                            .font(.system(size: 13))
                    }

                    Spacer()

                    Button(action: {
                        guard !recipientAddress.trimmingCharacters(in: .whitespaces).isEmpty else {
                            errorMessage = "Please enter or paste an address"
                            return
                        }
                        guard let amt = Double(amountString), amt > 0 else {
                            errorMessage = "Please enter a valid amount"
                            return
                        }
                        if amt > asset.balance {
                            errorMessage = "Insufficient \(asset.symbol) balance"
                            return
                        }

                        onSend(amt, recipientAddress)
                        presentationMode.wrappedValue.dismiss()
                    }) {
                        Text("Confirm and Send")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(.black)
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.white)
                            .cornerRadius(16)
                    }
                }
                .padding(20)
            }
            .navigationTitle("Send \(asset.symbol)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Cancel") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.white)
                }
            }
        }
        .preferredColorScheme(.dark)
    }
}

// Receive Sheet View
struct ReceiveSheetView: View {
    let asset: CryptoAsset
    @Environment(\.presentationMode) var presentationMode
    @State private var copied = false

    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea()

                VStack(spacing: 24) {
                    Text("Only send \(asset.name) (\(asset.symbol)) to this address")
                        .font(.system(size: 13))
                        .foregroundColor(.gray)
                        .multilineTextAlignment(.center)

                    VStack(spacing: 12) {
                        Text(asset.receiveAddress)
                            .font(.system(size: 13, design: .monospaced))
                            .foregroundColor(.white)
                            .multilineTextAlignment(.center)
                            .padding()
                            .background(Color(red: 0.1, green: 0.1, blue: 0.12))
                            .cornerRadius(12)

                        Button(action: {
                            UIPasteboard.general.string = asset.receiveAddress
                            copied = true
                            DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
                                copied = false
                            }
                        }) {
                            HStack {
                                Image(systemName: copied ? "checkmark" : "doc.on.doc")
                                Text(copied ? "Copied!" : "Copy Address")
                            }
                            .font(.system(size: 14, weight: .bold))
                            .foregroundColor(.black)
                            .padding(.horizontal, 24)
                            .padding(.vertical, 12)
                            .background(Color.white)
                            .cornerRadius(20)
                        }
                    }

                    Spacer()
                }
                .padding(24)
            }
            .navigationTitle("Receive \(asset.symbol)")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.white)
                }
            }
        }
        .preferredColorScheme(.dark)
    }
}

// History Sheet View
struct HistorySheetView: View {
    let transactions: [TransactionItem]
    @Environment(\.presentationMode) var presentationMode

    var body: some View {
        NavigationView {
            ZStack {
                Color.black.ignoresSafeArea()

                if transactions.isEmpty {
                    Text("No transactions yet")
                        .foregroundColor(.gray)
                } else {
                    List(transactions) { tx in
                        HStack {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("\(tx.type) \(tx.assetSymbol)")
                                    .font(.system(size: 15, weight: .semibold))
                                    .foregroundColor(.white)
                                Text(tx.toAddress)
                                    .font(.system(size: 11, design: .monospaced))
                                    .foregroundColor(.gray)
                            }
                            Spacer()
                            VStack(alignment: .trailing, spacing: 4) {
                                Text("\(tx.type == "Sent" ? "-" : "+")\(String(format: "%.4f", tx.amount)) \(tx.assetSymbol)")
                                    .font(.system(size: 15, weight: .bold))
                                    .foregroundColor(tx.type == "Sent" ? .white : .green)
                                Text("$\(String(format: "%.2f", tx.amountUsd))")
                                    .font(.system(size: 12))
                                    .foregroundColor(.gray)
                            }
                        }
                        .padding(.vertical, 4)
                        .listRowBackground(Color(red: 0.1, green: 0.1, blue: 0.12))
                    }
                    .listStyle(InsetGroupedListStyle())
                }
            }
            .navigationTitle("Transfers History")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        presentationMode.wrappedValue.dismiss()
                    }
                    .foregroundColor(.white)
                }
            }
        }
        .preferredColorScheme(.dark)
    }
}
