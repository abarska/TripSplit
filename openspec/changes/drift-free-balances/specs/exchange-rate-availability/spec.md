# Spec Delta

## MODIFIED Requirements

### Requirement: Saving requires an available exchange rate
The system SHALL NOT save an expense or payment in a currency other than the trip's default currency unless rates for both that currency and the trip currency are stored. USD always counts as available. An item in the trip's default currency SHALL always be saveable.

#### Scenario: Save non-USD item before first sync
- **WHEN** the trip currency is UAH, the user saves an expense in EUR, and no rates are stored
- **THEN** nothing is written (no item, no shares), the app does not crash, and a message says exchange rates are not available yet

#### Scenario: Save stays on the form after refusal
- **WHEN** a save is refused because of a missing rate
- **THEN** the user remains on the Add screen with their entered data intact and can retry

#### Scenario: Save trip-currency item without any rates
- **WHEN** the trip currency is UAH, the user saves an expense in UAH, and the rate table is empty
- **THEN** the expense is saved and balances are updated

#### Scenario: Save USD item without any rates
- **WHEN** the trip currency is USD, the user saves an expense in USD, and the rate table is empty
- **THEN** the expense is saved and balances are updated

### Requirement: Balances display tolerates a missing home-currency rate
The Balances tab SHALL NOT crash when a rate needed to convert balances to the home currency is not stored. It SHALL still show balances in the trip currency and SHALL hide only the approximate home-currency values. Those values SHALL appear as soon as the rates become available, without the user reopening the screen.

#### Scenario: Open Balances before first sync
- **WHEN** the trip currency is EUR, the home currency is UAH, no rates are stored, and the user opens the Balances tab
- **THEN** balances are shown in EUR without approximate UAH values, and the app does not crash

#### Scenario: Rates arrive while Balances is open
- **WHEN** balances are shown without approximate values and the sync stores the needed rates
- **THEN** the approximate home-currency values appear

#### Scenario: Home currency is USD
- **WHEN** the home currency is USD, the trip currency is EUR, and no rates are stored
- **THEN** balances are shown in EUR without approximate USD values, because the EUR rate is missing

#### Scenario: Home currency equals trip currency
- **WHEN** the home currency and trip currency are both UAH and no rates are stored
- **THEN** balances are shown in UAH

## REMOVED Requirements

### Requirement: Deleting requires an available exchange rate
**Reason**: Balances are now derived from stored, frozen trip-currency amounts. Deleting an item removes its stored rows and needs no conversion, so a missing rate can no longer block or corrupt a delete.
**Migration**: None. Deletes always succeed, and the "rates not available" message is no longer shown on delete.
