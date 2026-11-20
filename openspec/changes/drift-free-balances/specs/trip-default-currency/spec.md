# Spec Delta

## Purpose

Defines each trip's default currency, which all of the trip's balances are kept in, and what happens to existing expenses and payments when the user changes it.

## ADDED Requirements

### Requirement: Every trip has a default currency from its currencies
Every trip SHALL have exactly one default currency, and it SHALL be one of the trip's active currencies. For a new trip it SHALL initially be the user's home currency. The user SHALL be able to choose any other trip currency as the default.

#### Scenario: New trip
- **WHEN** the user creates a trip and the home currency is UAH
- **THEN** UAH is in the trip's currencies and is preselected as the default currency

#### Scenario: Choose another default
- **WHEN** the user adds EUR to a new trip, marks it as default and saves
- **THEN** the trip's default currency is EUR

#### Scenario: Default currency cannot be removed
- **WHEN** EUR is the trip's default currency and the user edits the trip
- **THEN** EUR cannot be removed from the trip's currencies until another currency is made the default

### Requirement: New items default to the trip currency
When the user adds an expense or payment, its currency SHALL be preselected to the trip's default currency. The user SHALL still be able to pick any other trip currency.

#### Scenario: Add expense
- **WHEN** the trip's default currency is EUR and the user opens Add Expense
- **THEN** EUR is preselected as the expense currency

### Requirement: Changing the default currency converts existing items at the switch-time rate
When the default currency changes from currency A to currency B, the system SHALL convert every existing expense and payment of the trip to B in one atomic step. An item originally entered in B SHALL get exactly its original amount. Any other item SHALL get its previous rate to A multiplied by the current A-to-B rate, applied to its original amount.

#### Scenario: Switch with mixed items
- **WHEN** the trip currency is EUR, the trip has a 100.00 USD expense frozen at 91.37 EUR, a 20.00 GBP expense and a 300.00 EUR expense, and the user changes the default to GBP when 1 EUR = 0.85 GBP
- **THEN** the totals become 77.66 GBP, exactly 20.00 GBP and 255.00 GBP

#### Scenario: Shares and balances follow the switch
- **WHEN** the default currency is changed
- **THEN** each expense's shares are recalculated in the new currency from the weights stored with the expense, using the leftover rule, and the balances still sum to exactly zero

#### Scenario: Switching back keeps items entered in the trip currency exact
- **WHEN** the default currency is changed from EUR to GBP and later back to EUR
- **THEN** every item originally entered in EUR has exactly its original EUR amount again

#### Scenario: Rates sync after a switch
- **WHEN** a rate sync happens after the default currency was changed
- **THEN** no item's total and no balance changes

### Requirement: Changing the default currency requires its rates
The system SHALL NOT change a trip's default currency while it has expenses or payments, unless rates for both the old and the new currency are stored, with USD always counting as available. When a change is refused, nothing SHALL change, and the user SHALL be told that exchange rates are not available yet.

#### Scenario: Switch without a rate
- **WHEN** the trip has expenses in EUR and the user changes the default to GBP, but no GBP rate is stored
- **THEN** the trip keeps EUR as its default, no item or balance changes, and a message says exchange rates are not available yet

#### Scenario: Switch on an empty trip
- **WHEN** the trip has no expenses or payments and no rates are stored
- **THEN** the default currency changes
