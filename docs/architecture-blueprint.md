# DukaCred Architecture Blueprint

## 1. Product Definition

### Product statement
`DukaCred` is an AI-assisted inventory financing platform for small retailers and dukas.

It helps merchants turn supplier invoices into short-term working capital requests, using:
- invoice image capture
- AI-assisted field extraction
- deterministic credit policy evaluation
- admin approval workflow
- mocked supplier disbursement
- short repayment tracking

### User roles
- `Merchant`: submits financing requests and tracks obligations
- `Admin`: reviews requests, approves or rejects them, and monitors the portfolio

### Country and product constraints for v1
- `Country`: Kenya
- `Financing type`: supplier invoice / inventory financing
- `Repayment window`: 7 days
- `Disbursement`: mocked direct supplier payment
- `Merchant segment`: informal and small retail shops / dukas

## 2. First Build Target

The first working demo should prove one end-to-end financing lifecycle:

1. Merchant signs in
2. Merchant uploads or captures invoice
3. App extracts invoice data
4. Merchant reviews and corrects extracted fields
5. Merchant submits financing request
6. Risk engine scores request
7. Admin reviews request
8. Admin approves or rejects
9. If approved, supplier payment is simulated
10. Merchant sees active repayment schedule

## 3. Module Strategy

Start with a clean multi-module shape, but keep it lean enough to ship.

### Recommended v1 modules
- `:composeApp`
- `:core:common`
- `:core:ui`
- `:core:designsystem`
- `:core:domain`
- `:core:data`
- `:core:testing`
- `:feature:auth`
- `:feature:merchant-home`
- `:feature:invoice-capture`
- `:feature:financing`
- `:feature:repayment`
- `:feature:admin-review`
- `:feature:portfolio`

### Responsibility per module
` :composeApp `
- app entry points
- navigation shell
- dependency injection composition root
- platform bootstrapping

` :core:common `
- result and error contracts
- base MVI abstractions
- dispatchers
- logging hooks
- common utilities

` :core:ui `
- reusable UI primitives
- screen scaffolds
- feedback components
- loading, empty, and error states

` :core:designsystem `
- colors
- typography
- spacing
- icon wrappers
- shared app theme

` :core:domain `
- cross-feature domain models
- workflow state machine contracts
- value objects

` :core:data `
- fake services
- repositories used across features
- local seed providers
- mock API adapters

` :core:testing `
- test fixtures
- fake builders
- shared assertions

Feature modules:
- own `data`, `domain`, and `presentation` slices
- depend on `core:*`
- expose route entry composables and feature APIs

## 4. Package Naming

Use one root package across shared code:

`com.samduka.dukacred`

### Package layout
```text
com.samduka.dukacred
├── app
│   ├── di
│   ├── navigation
│   └── shell
├── core
│   ├── common
│   │   ├── error
│   │   ├── result
│   │   ├── mvi
│   │   └── util
│   ├── data
│   │   ├── fake
│   │   ├── mapper
│   │   └── seed
│   ├── designsystem
│   ├── domain
│   │   ├── model
│   │   ├── policy
│   │   └── workflow
│   └── ui
│       ├── component
│       ├── feedback
│       └── screen
└── feature
    ├── adminreview
    ├── auth
    ├── financing
    ├── invoicecapture
    ├── merchanthome
    ├── portfolio
    └── repayment
```

## 5. Feature Internal Structure

Every feature should keep the same layout.

```text
feature/<name>/
├── data
│   ├── local
│   ├── remote
│   ├── repository
│   └── mapper
├── domain
│   ├── model
│   ├── repository
│   └── usecase
└── presentation
    ├── action
    ├── state
    ├── effect
    ├── viewmodel
    └── ui
```

## 6. Core Domain Model

### Core entities
- `Merchant`
- `Supplier`
- `Invoice`
- `InvoiceLineItem`
- `FinancingRequest`
- `RiskAssessment`
- `FinancingOffer`
- `FinancingContract`
- `RepaymentSchedule`
- `RepaymentInstallment`
- `Disbursement`
- `SalesSnapshot`
- `AuditEvent`
- `UserSession`

### Key value objects
- `Money`
- `MerchantId`
- `SupplierId`
- `InvoiceId`
- `RequestId`
- `ContractId`
- `UserId`

### Important enums
- `UserRole`: `MERCHANT`, `ADMIN`
- `FinancingRequestStatus`: `DRAFT`, `EXTRACTED`, `READY_FOR_SUBMISSION`, `SUBMITTED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `MANUAL_REVIEW`
- `ContractStatus`: `PENDING_DISBURSEMENT`, `ACTIVE`, `OVERDUE`, `REPAID`, `DEFAULTED`
- `DisbursementStatus`: `PENDING`, `SENT`, `FAILED`
- `RiskBand`: `LOW`, `MEDIUM`, `HIGH`
- `ConfidenceLevel`: `HIGH`, `MEDIUM`, `LOW`

## 7. Workflow State Machine

The financing lifecycle should be explicit and testable.

```text
DRAFT
  -> EXTRACTED
  -> READY_FOR_SUBMISSION
  -> SUBMITTED
  -> UNDER_REVIEW
  -> APPROVED | MANUAL_REVIEW | REJECTED
  -> PENDING_DISBURSEMENT
  -> ACTIVE
  -> REPAID | OVERDUE
```

### Rules
- every transition emits an `AuditEvent`
- rejected requests are immutable except for a new retry flow later
- low-confidence extraction can still proceed, but should mark `MANUAL_REVIEW`
- overdue merchants should block new approvals in v1 policy

## 8. MVI Contract

Use a small shared MVI base instead of ad hoc screen patterns.

### Base contracts
- `UiState`
- `UiAction`
- `UiEffect`
- `BaseViewModel<S : UiState, A : UiAction, E : UiEffect>`

### Rules
- state is immutable
- actions are the only input into screen logic
- one-off events use effects, not state booleans
- reducers handle state transitions in one place
- async work updates state through shared helpers

### Typical screen setup
- `State`: screen data and status
- `Action`: user or lifecycle events
- `Effect`: snackbar, navigation, external intents
- `ViewModel`: action dispatcher + reducer + use case bridge

## 9. Result and Error Strategy

### Shared result type
- `AppResult<T, AppError>`

### Error families
- `NetworkError`
- `ValidationError`
- `ExtractionError`
- `RiskPolicyError`
- `DisbursementError`
- `StorageError`
- `AuthError`

### Layering rules
- `data` maps infra failures into typed errors
- `domain` enforces policy and workflow rules
- `presentation` maps typed errors into user-facing copy

### UX expectations
- field-level validation on invoice review form
- retryable failures are explicit
- policy rejection messages must be explainable
- extraction confidence warnings should never look like generic crashes

## 10. Initial Repository Interfaces

These are the first shared interfaces worth defining before UI grows.

### Auth
```kotlin
interface AuthRepository {
    suspend fun signInAsMerchant(phoneNumber: String, pin: String): AppResult<UserSession, AuthError>
    suspend fun signInAsAdmin(email: String, password: String): AppResult<UserSession, AuthError>
    suspend fun getActiveSession(): AppResult<UserSession?, AuthError>
    suspend fun signOut(): AppResult<Unit, AuthError>
}
```

### Invoice extraction
```kotlin
interface InvoiceExtractionRepository {
    suspend fun extractInvoice(document: InvoiceDocumentInput): AppResult<ExtractedInvoiceDraft, ExtractionError>
}
```

### Financing requests
```kotlin
interface FinancingRepository {
    suspend fun submitRequest(draft: FinancingDraft): AppResult<FinancingRequest, ValidationError>
    suspend fun getMerchantRequests(merchantId: MerchantId): AppResult<List<FinancingRequest>, AppError>
    suspend fun getRequestDetails(requestId: RequestId): AppResult<FinancingRequestDetails, AppError>
    suspend fun approveRequest(requestId: RequestId): AppResult<FinancingContract, AppError>
    suspend fun rejectRequest(requestId: RequestId, reason: String): AppResult<Unit, AppError>
}
```

### Risk
```kotlin
interface RiskRepository {
    suspend fun assess(requestId: RequestId): AppResult<RiskAssessment, RiskPolicyError>
}
```

### Portfolio
```kotlin
interface PortfolioRepository {
    suspend fun getPortfolioSummary(): AppResult<PortfolioSummary, AppError>
    suspend fun getPendingRequests(): AppResult<List<FinancingRequestSummary>, AppError>
    suspend fun getOverdueContracts(): AppResult<List<FinancingContractSummary>, AppError>
}
```

## 11. Initial Use Cases

Start with these use cases before expanding feature logic:

- `SignInMerchantUseCase`
- `SignInAdminUseCase`
- `RestoreSessionUseCase`
- `ExtractInvoiceUseCase`
- `ConfirmExtractedInvoiceUseCase`
- `SubmitFinancingRequestUseCase`
- `AssessFinancingRequestUseCase`
- `ApproveFinancingRequestUseCase`
- `RejectFinancingRequestUseCase`
- `GetMerchantDashboardUseCase`
- `GetRepaymentScheduleUseCase`
- `GetPendingRequestsUseCase`
- `GetPortfolioSummaryUseCase`

## 12. First Screens To Scaffold

These should be the first real screens in the repo.

### Merchant flow
1. `RolePickerScreen`
2. `MerchantSignInScreen`
3. `MerchantHomeScreen`
4. `InvoiceCaptureScreen`
5. `InvoiceReviewScreen`
6. `FinancingDecisionScreen`
7. `RepaymentDetailScreen`

### Admin flow
1. `AdminSignInScreen`
2. `AdminQueueScreen`
3. `AdminReviewScreen`
4. `PortfolioOverviewScreen`

## 13. Screen Contracts

### MerchantHomeState
- merchant name
- active financing count
- next repayment amount
- next repayment due date
- recent requests
- loading state
- error state

### InvoiceReviewState
- invoice image reference
- supplier name
- invoice number
- invoice date
- total amount
- till or paybill number
- line items
- per-field confidence
- warnings
- form errors
- submission state

### AdminReviewState
- request summary
- merchant profile snapshot
- invoice image
- extracted fields
- confidence flags
- risk band
- risk reasons
- decision loading state

## 14. Navigation Blueprint

Split navigation by role.

### Root routes
- `Splash`
- `RolePicker`
- `MerchantGraph`
- `AdminGraph`

### Merchant graph
- `MerchantHome`
- `InvoiceCapture`
- `InvoiceReview`
- `FinancingDecision`
- `RepaymentDetail`

### Admin graph
- `AdminQueue`
- `AdminReview`
- `PortfolioOverview`

## 15. Fake Services and Seed Data

Use realistic seeded fake services from the start. This will make the app demo-ready before external integrations exist.

### Fake services
- `FakeAuthService`
- `FakeInvoiceExtractionService`
- `FakeRiskService`
- `FakeDisbursementService`
- `FakePortfolioService`

### Seeded merchants
- `Mama Njeri Shop`
  - stable daily sales
  - good repayment history
  - expected approval
- `Blue Kiosk Enterprises`
  - blurry invoice
  - incomplete supplier details
  - expected manual review
- `Baraka Retail`
  - overdue contract
  - expected rejection

### Seeded suppliers
- `Tawi Wholesalers`
- `Jitegemee Distributors`
- `EastHub FMCG Supplies`

### Demo request scenarios
- `approved_request`
- `manual_review_request`
- `rejected_request`

## 16. Risk Engine Rules For V1

The first implementation should be deterministic and explainable.

### Input signals
- average daily sales
- 7-day sales trend
- existing active contracts
- overdue history
- invoice amount
- supplier trust tier
- extraction confidence

### Example policy rules
- reject if merchant has overdue financing
- reject if invoice amount exceeds policy cap
- approve if amount fits projected 7-day sales and merchant is in good standing
- send to manual review if supplier is new or confidence is low
- reduce eligible amount when sales consistency is weak

### Output fields
- `riskBand`
- `recommendedDecision`
- `maxEligibleAmount`
- `reasons`

## 17. Suggested UI Tone

### Merchant experience
- clear
- trustworthy
- low cognitive load
- task-oriented

### Admin experience
- denser information layout
- document plus data side-by-side
- visible risk markers
- strong workflow status clarity

Avoid building a generic neon-bank aesthetic. This product should feel operational and credible.

## 18. Testing Priorities

Write tests early for the parts that make the app feel serious:
- workflow state transitions
- risk policy rules
- reducer behavior in viewmodels
- mapping from extraction output to editable form state
- approval and rejection flows

## 19. Build Order

### Milestone 1
- rename package from template
- establish modules
- add theme shell
- add root navigation
- add base MVI and result/error contracts

### Milestone 2
- scaffold auth and role picker
- scaffold merchant home
- scaffold admin queue

### Milestone 3
- implement fake extraction flow
- build invoice review screen
- build request submission flow

### Milestone 4
- implement risk engine
- implement admin review flow
- implement disbursement simulation

### Milestone 5
- implement repayment tracking
- build portfolio overview
- add tests
- polish README and demo assets

## 20. Immediate Next Coding Step

The next implementation pass should do exactly this:

1. rename `org.example.project` to `com.samduka.dukacred`
2. introduce root packages for `app`, `core`, and `feature`
3. replace the template `App()` with a role-based shell
4. add base `UiState`, `UiAction`, `UiEffect`, and `BaseViewModel`
5. add `AppResult` and typed `AppError`
6. scaffold the first three screens:
   - `RolePickerScreen`
   - `MerchantHomeScreen`
   - `AdminQueueScreen`

That is the smallest useful slice that moves this repo from template to real product foundation.
