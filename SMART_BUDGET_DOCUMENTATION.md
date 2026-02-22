# Smart Budget - Project Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Features](#features)
3. [Application Views](#application-views)
4. [Technical Architecture](#technical-architecture)
5. [User Workflows](#user-workflows)
6. [Lessons Learned](#lessons-learned)

---

## Project Overview

**Smart Budget** is a personal finance management Android app for tracking income, expenses, and budgets with real-time alerts.

### Development Team
- Tervil Moywaywa SCT213-C002-0012/2023
- Michael Lumumba SCT213-C002-0097/2022
- Edwin Vaz Muiruri SCT213-C002-0137/2023


### Version Info
- **Version:** 1.0.0
- **Target SDK:** 36 (Android 14)
- **Minimum SDK:** 24 (Android 7.0)

### Goals
- Simplify personal finance tracking
- Real-time budget monitoring
- Easy income/expense categorization
- Actionable spending insights

---

## Features

### 1. User Authentication
- Email-based registration and login with validation
- Password confirmation and change functionality
- Persistent login sessions with SessionManager
- Profile management (name, email, avatar with initials)

### 2. Transaction Management
**Capabilities:**
- Add, edit, delete transactions
- Support for income and expenses
- Optional notes
- Swipe-to-delete with confirmation
- Real-time balance updates

**Categories:**
- **Expense (7):** Food, Transport, Shopping, Entertainment, Bills, Health, Other
- **Income (4):** Salary, Friends & Family, Business, Other

### 3. Budget Tracking
- Customizable monthly budget limits
- Circular progress bar showing utilization
- Real-time budget vs. spending comparison
- Alerts: 🟡 Warning at 90%, 🔴 Alert when exceeded
- Remaining budget calculations

### 4. Dashboard
**Components:**
- Total balance (income - expenses)
- Monthly budget amount
- Remaining budget
- Recent transactions (latest 5-10)
- Quick action buttons

### 5. Transaction List
- Complete transaction history
- Filter by: All, Income, or Expense
- Tap to edit, swipe to delete
- Chronological order (newest first)
- Empty state for new users

### 6. Settings
**Profile:** Edit name, change password, view member date, logout

**Preferences:** Currency selection (KES, USD, EUR), budget notifications toggle

**Data Management:** Clear all data with confirmation, export to CSV (planned)

**Help & Support:** Usage guide, email support, app version

---

## Application Views

### 1. Splash Screen (`SplashActivity.java`)
- 2-second branding display
- Session validation
- Auto-routes to Dashboard (logged in) or Login (not logged in)

### 2. Login Screen (`LoginActivity.java`)
- Email and password fields with validation
- Loading state during authentication
- Links to Sign Up and Forgot Password (planned)

### 3. Sign Up Screen (`SignupActivity.java`)
- Fields: Name, Email, Password, Confirm Password
- Validation: Password matching, email format, 6-char minimum
- Auto-login after registration

### 4. Dashboard (`DashboardActivity.java`)
**Layout:**
- Header with settings button
- Financial cards: Balance, Monthly Budget, Remaining Budget
- Recent transactions RecyclerView
- Bottom navigation with Add Transaction button

**Interactions:**
- Tap budget cards → Budget screen
- "View All" → Transaction List
- Uses ViewModel and LiveData for real-time updates

### 5. Add/Edit Transaction (`AddTransactionActivity.java`)
**Dual Mode:** Add new or edit existing

**Components:**
- Income/Expense toggle with visual indicators
- Amount input (numeric keyboard)
- Category spinner (dynamic based on type)
- Notes field (optional)

**Validation:** Positive amount required, category auto-selected

### 6. Monthly Budget (`MonthlyBudgetActivity.java`)
- Editable budget field (auto-saves)
- Circular progress (color-coded: green <70%, yellow 70-90%, red >90%)
- Metrics: Spent, Remaining, Percentage
- Automatic alerts at thresholds

### 7. Transaction List (`TransactionListActivity.java`)
- Complete history with filter chips
- Tap to edit, swipe to delete (with confirmation)
- Real-time updates via LiveData
- Empty state with helpful message

### 8. Settings (`SettingsActivity.java`)
- Profile header with avatar, name, email, member date
- Edit profile dialog
- Password change with validation
- Currency selection
- Notification toggle
- Clear data with double confirmation
- Help and email support links

---

## Technical Architecture

### Pattern: MVVM (Model-View-ViewModel)
**Benefits:** Separation of concerns, testable logic, reactive UI, clean structure

### Components

**Models** (`data/model/`):
- `User.java`, `Transaction.java`, `Category.java`

**Views** (Activities):
- UI layer with View Binding
- Observes ViewModel LiveData
- No business logic

**ViewModels** (`viewmodel/`):
- `AuthViewModel.java`, `DashboardViewModel.java`
- Exposes LiveData, handles transformations

**Repository** (`repository/`):
- `TransactionRepository.java`, `UserRepository.java`
- Abstracts data sources, single source of truth

**Data Layer** (`data/`):
- Room Database with DAOs: `UserDao`, `TransactionDao`, `CategoryDao`
- `AppDatabase.java` configuration

**Utilities** (`utils/`):
- `SessionManager.java` wraps SharedPreferences for session management

---

### Technology Stack

**Core:**
- Java 11, Gradle, SDK 24-36

**Architecture Components:**
- **Room Database 2.6.1:** Local persistence, type-safe queries, LiveData integration
- **LiveData 2.8.0:** Observable, lifecycle-aware data holder
- **ViewModel 2.8.0:** Survives configuration changes

**UI Libraries:**
- Material Design Components (buttons, cards, chips)
- View Binding (type-safe view access)
- RecyclerView 1.3.2 (efficient lists)
- ConstraintLayout 2.1.4 (flexible layouts)

**Navigation:**
- Navigation Component 2.7.7 (type-safe navigation, deep linking)

---

### Database Schema

**Tables:**

1. **users:** id (PK), email (UNIQUE), name, password, balance, currency, createdAt

2. **transactions:** id (PK), userId (FK), name, description, amount (negative for expenses), dateEpoch, categoryId (FK), notes

3. **categories:** id (PK), name, type (income/expense), icon, color

**Relationships:**
- User → Transactions (One-to-Many, CASCADE on delete)
- Category → Transactions (One-to-Many, RESTRICT on delete)

---

## User Workflows

### 1. First-Time User
1. Launch → Splash → Login
2. Click "Sign Up" → Fill form (name, email, password)
3. Submit → Auto-login → Dashboard (empty state)
4. Prompted to add first transaction

### 2. Daily Usage
1. Launch → Auto-login to Dashboard
2. View balance and recent transactions
3. Add expense: Tap "Add Transaction" → Select category → Enter amount → Add notes → Save
4. Dashboard updates: Balance decreases, transaction appears, budget updates
5. Check budget: Tap "Monthly Budget" → View progress and remaining amount

### 3. Budget Management
1. Navigate to Dashboard → Tap "Monthly Budget" card
2. Enter desired budget (e.g., 50,000) → Auto-saves
3. View circular progress, spent, and remaining amounts
4. Receive alerts: 90% warning, 100%+ exceeded notification
5. Adjust spending based on insights

### 4. Transaction Management
**View:** Dashboard → "View All" → Filter by All/Income/Expense

**Edit:** Tap transaction → Modify fields → "UPDATE"

**Delete:** Swipe transaction → Confirmation dialog → Confirm or Cancel

### 5. Settings & Profile
**Edit Profile:** Settings → "Edit Profile" → Enter new name → Save

**Change Password:** Enter current password → New password → Confirm

**Currency:** Select KES/USD/EUR → Restart app

**Logout:** Confirmation dialog → Session cleared → Return to Login

---

## Lessons Learned

### 1. Architecture & Design

**MVVM Benefits:**
- Clear separation: Models (data), ViewModels (logic), Views (UI)
- More maintainable, easier testing, reduced Activity complexity
- **Key Takeaway:** Architecture patterns provide real practical benefits as projects grow

**Repository Pattern:**
- Centralizes data logic, abstracts data sources
- Simplifies testing, clarifies data flow
- **Key Takeaway:** Clean abstraction makes data layer flexible and testable

### 2. Database & Data

**Room Database:**
- Type-safe queries, LiveData integration, compile-time verification
- Less boilerplate, automatic updates, easy maintenance
- **Key Takeaway:** Room dramatically reduces complexity while adding powerful features

**Foreign Keys:**
- CASCADE on User deletion, RESTRICT on Category deletion
- Maintains integrity, prevents orphaned records
- **Key Takeaway:** Proper relationships prevent inconsistencies and reduce maintenance

**LiveData for Reactive UI:**
```java
viewModel.getTransactions().observe(this, transactions -> {
    adapter.submitList(transactions);
});
```
- Automatic updates, lifecycle-aware, no manual refresh
- **Key Takeaway:** Eliminates manual refresh logic and prevents lifecycle bugs

### 3. UI/UX Design

**View Binding:**
- Null-safe, type-safe, better IDE support vs. findViewById()
- **Key Takeaway:** No-brainer improvement for all modern Android projects

**Material Design:**
- Consistent professional UI with MaterialButton, CardView, Chips
- Built-in animations and accessibility
- **Key Takeaway:** Provides professional look with minimal effort

**Empty States:**
- Helpful messages, icons, call-to-action prompts
- Guides new users, prevents confusion
- **Key Takeaway:** Crucial for onboarding and clarity when no data exists

**User Feedback:**
- Toast messages, loading states, confirmation dialogs, progress indicators
- Examples: "Transaction saved", "Budget exceeded by KES 5,000"
- **Key Takeaway:** Always provide clear, immediate feedback to build confidence

### 4. Validation & Input

**Multi-Layer Validation:**
- Client-side validation, database constraints, user-friendly errors
- Checks: Email format, password strength, positive amounts, required fields
- **Key Takeaway:** Validation at multiple layers prevents bad data and improves UX

**Input Types:**
- TYPE_NUMBER_DECIMAL for amounts, TYPE_TEXT_EMAIL_ADDRESS for emails
- Better UX, fewer errors, faster entry
- **Key Takeaway:** Correct input types dramatically improve UX

### 5. State & Session

**SessionManager:**
- SharedPreferences wrapper, centralized session logic
- Consistent handling, single source of truth
- **Key Takeaway:** Centralizing session management simplifies authentication

**Lifecycle-Aware Components:**
- ViewModel survives rotations, no manual state save/restore
- **Key Takeaway:** ViewModels and LiveData handle configuration changes gracefully

### 6. Performance

**RecyclerView:**
- ViewHolder pattern, efficient recycling, DiffUtil, animations
- Smooth scrolling, memory efficient
- **Key Takeaway:** Modern standard for lists with significant performance benefits

**Background Threads:**
- Room's automatic thread handling, LiveData on background, Executors for writes
- No UI freezes or ANR errors
- **Key Takeaway:** Always perform database operations off main thread

### 7. User Testing

**Real Users Find Real Issues:**
- Observed navigation, asked for feedback, watched struggles
- Discoveries: Expected swipe-to-delete, needed prominent alerts, wanted editing
- **Key Takeaway:** User testing reveals issues you'd never discover alone

**Confirmation Dialogs:**
- Added for: Delete transaction, clear data, logout, password change
- Prevents accidental actions, increases confidence
- **Key Takeaway:** Always confirm destructive actions

### 8. Feature Planning

**MVP First:**
- Core features: Authentication, transactions, balance, basic budget
- Then added: Editing, filtering, settings, enhanced budget
- Working app faster, iterative improvements, better focus
- **Key Takeaway:** Start with core features, then iterate

**Plan for Future:**
- "Coming Soon" placeholders: CSV export, advanced filters, forgot password
- Manages expectations, clear roadmap
- **Key Takeaway:** Okay to launch without everything—communicate what's next

### 9. Code Quality

**Naming Conventions:**
- Activities: `*Activity.java`, Layouts: `activity_*.xml`
- ViewModels: `*ViewModel.java`, Database: `*Dao.java`, `*Repository.java`
- **Key Takeaway:** Consistent naming makes navigation significantly easier

**Documentation:**
- Class-level JavaDoc, method descriptions, complex logic explanations
- Easier to return, helps others, documents decisions
- **Key Takeaway:** Good comments explain "why," not just "what"

**Version Control:**
- Small focused commits, descriptive messages, regular commits
- Easy tracking, simple reverts, clear history
- **Key Takeaway:** Good practices save time and prevent headaches

### 10. Security & Privacy

**Password Storage:**
- Current: Plain text (NOT production-ready)
- Production needs: BCrypt/Argon2 hashing, salt per user, encryption
- **Key Takeaway:** Security should be priority from start, not afterthought

**Data Privacy:**
- Local storage only (v1.0), no personal data tracking, user controls data
- Future: Cloud backup with encryption, biometric auth, data export
- **Key Takeaway:** Respect user privacy and give them data control

### 11. Project Management

**Team Communication:**
- Regular meetings, clear task division, informal code reviews, shared docs
- Fewer conflicts, better quality, successful collaboration
- **Key Takeaway:** Clear communication and defined roles essential for teams

**Time Management:**
- Realistic milestones, prioritize core features, regular reviews, flexible scope
- Delivered on time, managed scope creep
- **Key Takeaway:** Know when to say "not in this version"

### 12. Testing & QA

**Manual Testing:**
- Test features, edge cases, different devices, end-to-end flows
- Found: Empty list crashes, budget errors, UI overflow, session issues
- **Key Takeaway:** Thorough testing catches unexpected bugs

**Edge Cases:**
- Examples: Zero amounts, large numbers, empty notes, special characters, negative balance
- Solutions: Input validation, null checks, defaults, limits, error handling
- **Key Takeaway:** Always test edge cases—users will find ways to break your app

### 13. Learning & Growth

**Document While Building:**
- Created multiple .md files during development
- Easy handoff, future reference, portfolio, learning record
- **Key Takeaway:** Document as you build, not after

**Learning by Doing:**
- Implemented incrementally, consulted docs, learned from errors
- Gained: Room, MVVM, LiveData, Material Design, Git collaboration
- **Key Takeaway:** Building real projects is the best way to learn

**Code Reviews:**
- Team reviews, discussing approaches, sharing knowledge
- Better quality, shared knowledge, caught bugs, improved skills
- **Key Takeaway:** Code reviews are valuable learning opportunities

### 14. Future Roadmap

**Priority 1 (v1.1):**
- Password encryption/hashing
- Biometric authentication
- Forgot password functionality
- Advanced date filters

**Priority 2 (v1.2):**
- CSV export
- Charts and spending graphs
- Category customization
- Recurring transactions

**Priority 3 (v2.0):**
- Cloud sync with encryption
- Multiple accounts/budgets
- Budget goals and targets
- AI spending insights
- Home screen widget
- Dark theme

**Key Takeaway:** Successful apps are never "finished"—always room for improvement

---

## Summary

### Achievements
**Smart Budget** delivers:
- ✅ Secure authentication
- ✅ Complete transaction management (CRUD)
- ✅ Budget tracking with visual indicators
- ✅ Real-time financial overview
- ✅ Intuitive modern UI
- ✅ Settings and profile management
- ✅ Multi-user support
- ✅ Category-based organization

### Technical Success
- Clean MVVM architecture
- Room Database integration
- LiveData for reactive UI
- Material Design implementation
- View Binding throughout
- Proper lifecycle management
- Repository pattern
- Session management

### Team Success
- Effective collaboration and communication
- Clear task distribution and version control
- Delivered on time with complete feature set

### Learning Success
- Mastered Android fundamentals and MVVM architecture
- Database design and implementation
- UI/UX best practices
- Project management and team collaboration
- Real-world problem-solving skills

---

## Conclusion

Building **Smart Budget** provided comprehensive experience in Android development—from architecture and database design to UI/UX and team collaboration. The lessons extend beyond technical skills to include project management, user experience, and the software development process.

The app demonstrates that well-designed personal finance tools can help users manage their financial lives. The implementation showcases modern Android practices and provides a solid foundation for future enhancements.

Most importantly, this project proved that with good planning, clear architecture, and effective teamwork, a small team can build professional, functional applications that solve real problems for real users.

---

**Version:** 1.0.0  
**Last Updated:** January 28, 2026  
**Team:** Tervil Moywaywa, Michael Lumumba, Edwin Vaz Muiruri

---


