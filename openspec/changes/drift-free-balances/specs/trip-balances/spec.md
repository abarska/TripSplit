# Spec Delta

## Purpose

Defines how expenses and payments are recorded in a trip's currency and how participant balances are derived from them, so that balances always match the trip's history exactly and never drift.

## ADDED Requirements

### Requirement: Amounts are exact in minor units
The system SHALL store every amount as a whole number of minor units of its currency, using the currency's standard number of decimal places. It SHALL NOT use binary floating point for amounts or rates at any point.

#### Scenario: Currency with two decimals
- **WHEN** the user saves an expense of 12.34 EUR
- **THEN** it is stored as 1234 minor units of EUR and shown as 12.34 EUR

#### Scenario: Currency with no decimals
- **WHEN** the user saves an expense of 1500 JPY
- **THEN** it is stored as 1500 minor units of JPY and shown as 1500 JPY

#### Scenario: Too many decimals entered
- **WHEN** the user enters 12.345 for a EUR expense
- **THEN** the amount is not accepted, and the form says the currency allows at most 2 decimal places

### Requirement: Conversion to the trip currency is frozen at save time
When an expense or payment is saved, the system SHALL store its original amount and currency, the rate from that currency to the trip's default currency, and the converted total in the trip currency. It SHALL round the total half-to-even to whole minor units. Later exchange-rate syncs SHALL NOT change a saved item.

#### Scenario: Foreign-currency expense
- **WHEN** the trip currency is EUR, the stored rates give 1 USD = 0.9137 EUR, and the user saves an expense of 100.00 USD
- **THEN** the expense keeps 100.00 USD as its original amount and records 91.37 EUR as its trip-currency total

#### Scenario: Expense in the trip currency
- **WHEN** the trip currency is EUR and the user saves an expense of 30.00 EUR
- **THEN** its trip-currency total is exactly 30.00 EUR

#### Scenario: Rates change after saving
- **WHEN** an expense was saved with a total of 91.37 EUR and a later sync changes the USD rate
- **THEN** the expense's total and every balance stay the same

### Requirement: Expense split is snapshotted and exact
When an expense is saved, the system SHALL store each sharer's weight (their multiplicator at that moment) and their share in trip-currency minor units. Each share SHALL be the trip-currency total times the sharer's weight divided by the sum of weights, rounded down. The shares SHALL always add up exactly to the total.

#### Scenario: Weighted split
- **WHEN** a 91.37 EUR expense is shared by A (weight 1), B (weight 3) and C (weight 1)
- **THEN** the rounded-down shares are A 18.27, B 54.82 and C 18.27, and the 0.01 left over is allocated by the leftover rule, so the shares add up to 91.37

#### Scenario: Multiplicator changes later
- **WHEN** B's multiplicator changes from 3 to 2 after the expense above was saved
- **THEN** that expense's shares stay unchanged, and only expenses saved afterwards use weight 2 for B

### Requirement: Leftover minor units go to a defined participant
The minor units left over after rounding the shares down SHALL all be added to one sharer's share: the payer's, if the payer shares the expense. Otherwise they go to the sharer who was added to the trip earliest.

#### Scenario: Payer shares the expense
- **WHEN** 10.00 EUR paid by B is shared equally by A, B and C
- **THEN** the shares are A 3.33, B 3.34 and C 3.33

#### Scenario: Payer does not share the expense
- **WHEN** 10.00 EUR paid by A is shared equally by B, C and D, where B was added to the trip first
- **THEN** the shares are B 3.34, C 3.33 and D 3.33

### Requirement: Balances are derived from the trip's history
Each participant's balance SHALL be calculated from the trip's stored expenses, shares and payments every time it is needed, not kept as a separately updated total. It SHALL be the sum of: expense totals they paid, minus their expense shares, plus payments they made, minus payments they received. A trip's balances SHALL always sum to exactly zero.

#### Scenario: Balances sum to zero
- **WHEN** a trip has any mix of expenses and payments
- **THEN** the participants' balances in the trip currency sum to exactly 0

#### Scenario: Payment settles a debt
- **WHEN** B's balance is −20.00 EUR and A's is +20.00 EUR, and B records a 20.00 EUR payment to A
- **THEN** both balances become 0.00 EUR

### Requirement: Deleting an item removes exactly its effect
Deleting an expense or payment SHALL leave every balance exactly as it would be if that item had never been saved, regardless of rate changes or multiplicator changes since it was saved.

#### Scenario: Delete after rate and weight changes
- **WHEN** expense 1 (100.00 USD paid by A, frozen at 91.37 EUR, shares A 18.28, B 54.82, C 18.27) and expense 2 (30.00 EUR paid by C, shared by A and C with weight 1 each) exist, then the rate changes and B's multiplicator changes, and the user deletes expense 1
- **THEN** the balances are exactly those of expense 2 alone (A −15.00, B 0.00, C +15.00 EUR) and they sum to zero

### Requirement: Balances are shown in the trip currency with an approximate home-currency value
The Balances tab SHALL show each balance in the trip's default currency. When the user's home currency differs from it, each balance SHALL also show an approximate value in the home currency, converted at the current stored rate and marked as approximate.

#### Scenario: Home currency equals trip currency
- **WHEN** the trip and home currencies are both EUR
- **THEN** balances are shown in EUR only

#### Scenario: Home currency differs
- **WHEN** the trip currency is EUR, the home currency is UAH, and both rates are stored
- **THEN** each balance is shown in EUR with an approximate UAH value beside it

#### Scenario: Rate sync changes only the approximate value
- **WHEN** a rate sync changes the EUR/UAH rate while Balances is open
- **THEN** the EUR balances stay the same and only the approximate UAH values update

### Requirement: Removed participants keep their history
Removing a participant from a trip SHALL keep their past expenses, shares, payments and balance. A removed participant SHALL NOT be offered as a payer or sharer for new expenses. They SHALL still appear on the Balances tab, marked as removed, and SHALL still be selectable as the payer or payee of a new payment.

#### Scenario: Remove a participant who owes money
- **WHEN** C has a balance of −15.00 EUR and is removed from the trip
- **THEN** every balance is unchanged and C is still listed on the Balances tab, marked as removed, with −15.00 EUR

#### Scenario: New expense after removal
- **WHEN** the user adds an expense after C was removed
- **THEN** C is not offered as a payer or sharer

#### Scenario: Settle with a removed participant
- **WHEN** C was removed with a balance of −15.00 EUR and the user records a 15.00 EUR payment from C to A
- **THEN** C's balance becomes 0.00 EUR
