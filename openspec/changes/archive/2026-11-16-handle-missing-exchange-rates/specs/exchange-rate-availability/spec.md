# Spec Delta

## Purpose

Defines how the app behaves when an exchange rate needed to convert an amount to USD is not available, so that a missing rate never crashes the app or corrupts stored balances.

## ADDED Requirements

### Requirement: Saving requires an available exchange rate
The system SHALL NOT save an expense or payment in a non-USD currency unless an exchange rate for that currency is stored. A USD item SHALL always be saveable.

#### Scenario: Save non-USD item before first sync
- **WHEN** the user saves an expense in EUR and no rate for EUR is stored
- **THEN** nothing is written (no item, cross-refs or balance changes), the app does not crash, and a message says exchange rates are not available yet

#### Scenario: Save stays on the form after refusal
- **WHEN** a save is refused because of a missing rate
- **THEN** the user remains on the Add screen with their entered data intact and can retry

#### Scenario: Save USD item without any rates
- **WHEN** the user saves an expense in USD and the rate table is empty
- **THEN** the expense is saved and balances are updated

### Requirement: Deleting requires an available exchange rate
The system SHALL NOT delete an expense or payment whose currency has no stored rate, because its balance effect cannot be reversed without one.

#### Scenario: Delete non-USD item with rate missing
- **WHEN** the user deletes a payment in a currency that has no stored rate
- **THEN** the payment and the balances are left unchanged, the app does not crash, and a message says exchange rates are not available

#### Scenario: Delete USD item without any rates
- **WHEN** the user deletes a USD expense and the rate table is empty
- **THEN** the expense is deleted and its balance effect is reversed

### Requirement: Balances display tolerates a missing home-currency rate
The Balances tab SHALL NOT crash when the home currency is not USD and its rate is not stored. It SHALL show the "exchange rates not available" placeholder instead of balances, and SHALL show balances as soon as the rate becomes available, without the user reopening the screen.

#### Scenario: Open Balances before first sync
- **WHEN** the home currency is UAH, no rates are stored, and the user opens the Balances tab
- **THEN** the placeholder is shown and the app does not crash

#### Scenario: Rates arrive while Balances is open
- **WHEN** the placeholder is shown and the sync stores the home-currency rate
- **THEN** balances appear converted to the home currency

#### Scenario: Home currency is USD
- **WHEN** the home currency is USD and no rates are stored
- **THEN** balances are shown in USD
