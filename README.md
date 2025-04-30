# SimBank (Android 🏦)

SimBank is an **in-progress mobile-banking demo** built with **Kotlin, Jetpack Compose, and Firebase**.  
The goal is to showcase modern Android app architecture while recreating everyday fintech flows—sign-up/login, phone-OTP verification, balance dashboard, and deposit/withdrawal interactions.

* * *

## ✨ Features

- **On-boarding** – Welcome screen, e-mail sign-up/login.  
- **Authentication** – E-mail + password login/register, password-reset.
- **Home Dashboard** – Real-time balance, latest transactions, and quick actions (Deposit / Pay).  
- **Transactions** – Deposit & withdrawal with Firestore balance updates and error handling (insufficient funds, bad input).  
- **Account Page** – Profile details pulled from Firestore and a one-tap logout.
- **Design System** – Centralised colour palette, custom typography, reusable buttons & pop-ups.  
- **Architecture** – MVVM, Repository pattern, Kotlin Coroutines/Flow, Hilt DI, Navigation-Compose.

* * *

## 🔧 How It Works

1. **Launch & On-board** – Users land on *WelcomeScreen* and choose to register or log in.  
2. **Secure Auth** – `FirebaseAuth` handles e-mail/password and phone-OTP verification (implementation not complete); ViewModels expose auth state via `StateFlow`.  
3. **Data Layer** – `UserRepository` streams user & account documents from `Firebase Firestore`.  
4. **UI Update** – Compose screens collect the state and render the latest balance, transactions, and any error pop-ups.  
5. **Transactions** – `TransactionViewModel` validates inputs, writes a new transaction doc, and atomically updates the balance.  
6. **Result Feedback** – A `TransactionResultPopup` displays success or failure with an animated check / cross.  

* * *

## 🏅 Project Highlights

- **Modern Android Stack**  
  Built entirely with **Jetpack Compose** and **Hilt**, avoiding XML layouts and manual DI boilerplate.

- **Reactive Firestore Integration**  
  Replaces callback hell with **Kotlin Coroutines & Flow**, emitting live updates directly to the UI.

- **Composable Design System**  
  Custom `Color.kt`, `Type.kt`, and a unified `Theme.kt` deliver consistent styling across light/dark modes.

- **Learning by Building**  
  The project serves as a sandbox to experiment with architectural patterns, Compose animations, and Firebase best practices.

* * *

## 🛣️ Roadmap

- [ ] **Finish phone-OTP flow**
  - Auto-read SMS via SMS Retriever API  
  - Resend-code timer & error handling  

- [ ] **Budgeting & analytics widgets**
  - Monthly spending pie chart  
  - Category filters & CSV export
     
- [ ] **Peer-to-peer transfers**
  - Allow sending funds from one user account to another
  - Realtime balance sync + transactional Firestore write (sender – → receiver)
  - Add “Send” quick-action on Home and a searchable contacts list
     
- [ ] **Real-time push notifications**
  - FCM alerts for deposits, withdrawals, and low-balance thresholds  
  - In-app notifications tray with read/unread state  
  - Settings screen to toggle notification types

- [ ] **Multi-account support (Chequing, Savings, etc.)**
  - Create, rename, and archive multiple sub-accounts per user  
  - Separate balances, transactions, and budgets for each account  
  - Account selector on Home with swipeable cards and aggregated “Total Net Balance”

- [ ] **Dark theme & accessibility polish**
  - Material You dynamic color  
  - TalkBack labels, font scaling, contrast audit  

- [ ] **Testing & CI pipeline**
  - Unit tests (JUnit 5, Turbine)  
  - UI tests (Compose, Espresso)  
  - GitHub Actions workflow: build, lint, test  

- [ ] **Stretch goals**
  - Advanced personalization controls  
  - Additional financial products (loans, investments)  
