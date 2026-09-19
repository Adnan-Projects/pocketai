# PocketAI 💰📱

PocketAI is a smart, privacy-first, local Android expense tracker that automatically categorizes your spending by reading banking SMS notifications. Built with modern Android architecture (Jetpack Compose, Room, Hilt, WorkManager), PocketAI keeps your financial data entirely on your device—no cloud servers required.

## Features ✨

*   **Automated SMS Tracking:** Silently listens for banking and UPI SMS notifications in the background and logs them as transactions.
*   **Smart Categorization ML:** A built-in classification engine automatically categorizes your expenses (e.g., Swiggy -> Food & Dining, Uber -> Transportation).
*   **Continuous Learning:** If you manually re-categorize a transaction, PocketAI remembers your choice and applies it to future texts from that merchant.
*   **Multi-Account Extraction:** Intelligently extracts the Bank Name and the Last 4 Digits of your account/card directly from the SMS.
*   **Budgeting & Alerts:** Set monthly limits per category and get visual warnings when you are approaching your budget.
*   **Interactive Dashboards:** View your expenses in a beautiful "Daily" timeline, "Monthly" view, or a detailed expandable "History" accordion.
*   **Data Visualization:** A dedicated "Reports" tab with animated progress bars showing your spending breakdown.
*   **CSV Import & Export:** Safely export your entire financial history to a neatly formatted `.csv` file, or import previous statements directly into the app (with built-in duplicate prevention).
*   **Dark Mode Support:** A sleek, edge-to-edge UI that seamlessly respects your system's light or dark mode preferences.

## Tech Stack 🛠️

*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** Clean Architecture + MVVM
*   **Local Database:** Room (SQLite)
*   **Dependency Injection:** Dagger Hilt
*   **Background Processing:** AndroidX WorkManager & Coroutines
*   **Navigation:** Jetpack Navigation Compose

## Installation 🚀

Since PocketAI requires sensitive permissions (`READ_SMS` and `RECEIVE_SMS`), it is meant to be compiled and run locally for complete privacy.

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/yourusername/PocketAI.git
    ```
2.  **Open in Android Studio:**
    Open the cloned directory in Android Studio (Koala Feature Drop or newer recommended).
3.  **Build and Run:**
    Connect your Android device or start an emulator and click **Run** (`Shift + F10`).
4.  **Grant Permissions:**
    Upon first launch, the app will prompt you for SMS permissions. You must grant these for the automated tracking and historical sync features to function.

## Usage Guide 📖

1.  **Sync History:** When you first install the app, tap the "Sync" button (the refresh icon) in the top right corner. This will securely scan your SMS inbox to instantly populate your dashboard with your past 30 days of expenses.
2.  **Train the Model:** If a transaction shows up as "UNCATEGORIZED", tap it, and type in a category. The app will remember this for next time.
3.  **Set Budgets:** Navigate to the "Reports" tab and tap "Set Budget" next to any category to monitor your spending limits.
4.  **Export Data:** Tap the Download icon in the top right to save your data as an Excel-ready `.csv` file to your Downloads folder.

## Privacy Promise 🔒

**PocketAI is completely offline.** It does not request `INTERNET` permissions. All SMS parsing, ML classification, and data storage happen strictly on-device using SQLite. Your financial data is yours alone.

## Contributing 🤝

Contributions, issues, and feature requests are welcome! Feel free to check the [issues page](../../issues).

## License 📝

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
