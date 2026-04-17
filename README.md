# DukaCred

DukaCred is a Compose Multiplatform fintech project for `AI-assisted inventory invoice financing` for small retailers.

The product goal is simple:
- a merchant captures a supplier invoice
- AI extracts the structured invoice data
- a risk engine evaluates the financing request
- admin staff review and approve or reject
- the supplier is "paid" through a mocked rail
- the merchant repays within a short window

This repo is currently in the `architecture-first` stage. The source of truth for the initial build is the blueprint below:

- [Architecture Blueprint](./docs/architecture-blueprint.md)

## Product Pitch

`DukaCred helps small retailers finance supplier invoices using AI-assisted document extraction and explainable short-term credit decisions.`

## V1 Scope

Merchant app:
- sign in
- merchant dashboard
- capture or upload invoice
- review extracted invoice fields
- submit financing request
- view offer, active financing, and repayment status

Admin dashboard:
- sign in
- requests queue
- request review with invoice image, extraction confidence, and risk summary
- approve, reject, or flag for manual review
- portfolio overview and overdue list

## Tech Direction

- Kotlin Multiplatform
- Compose Multiplatform
- shared domain and business logic across Android, iOS, and Desktop
- mobile-first merchant experience
- desktop-oriented admin workflow
- feature-based clean architecture with screen-level MVI

## Current Status

The repository still contains the default Compose template implementation. The next build step is to scaffold the modules, packages, and starter flows defined in the blueprint.

## Existing Commands

Build Android debug app:

```bash
./gradlew :composeApp:assembleDebug
```

Run Desktop app:

```bash
./gradlew :composeApp:run
```
# CMP_DukaCred
