# Midas
Project repo for the JPMC Advanced Software Engineering Forage program


Debugging Challenge Pack (5 Bugs) — Clarifications & Solutions
Author: Sai Mahendra


These are my notes for 5 debugging challenges I solved. I’m documenting them like real production issues: what failed, why it failed, and what fix I applied.
——————

Challenge 1 Solution — JIRA-101 (IndexOutOfBoundsException)

The error is setting that Index 3 out of bounds for length 3
When I navigated to the method calculateDailyCreditTotal.There is a loop used an incorrect boundary. If a list has size = 3, valid indexes are 0,1,2. But the loop attempted to read index 3.

Fix:  
Changed the loop condition from `i <= list.size()` to `i < list.size()`.  
Alternative safer fix: use an enhanced for-loop to avoid index math completely.

Takeaway:  
This bug is small but extremely common. Loop boundaries are one of the first places I check when I see IndexOutOfBounds.

———

Challenge 2 Solution — JIRA-102 (Side-effect bug / mutated inputs)

The tests are failing due to some assertion the validation method findInsufficientFundAccounts
 that should replicate transfers which was modifying the original Account objects passed into it. But the test expecting original balances to remain unchanged failed.

Important clarification   
Cascading/Issue is correct inside the simulation.  
If Transfer 1 changes the balance, Transfer 2 must use the updated balance. That’s how real banking works.

Here the root cause isJava passes object references. The method was finding Account objects from the original list and calling setBalance() on them, so the caller’s objects were permanently changed.

I have fixed it by adding the copies of accounts
1. Created working copies of accounts (new Account objects).
2. Run simulation on the copies.
3. Return results without altering/modifying the original inputs.

Finally the validation/simulation code should usually be side-effect free. If I need to simulate state changes, I simulate on copies.

---

Challenge #3 Solution — JIRA-103 (Integer division / BigDecimal precision)

These tests are also failing due to assertion.  Interest was coming back as 0.00 for rates like 3% or 6%.

The issue is with integer division part.
In Java,  we know that 6 / 12 using ints becomes 0, not 0.5. That makes the monthly rate zero, and all interest becomes zero.


The fix that I applied is I used entire rate math in BigDecimal and converted the annual percentage from 6 -> 0.06 and divided it by 12 using BigDecimal.divide(scale, roundingMode) and the multiplied with (balance * monthlyRate * months) and rounded final result to 2 decimals

I avoided calculations using integer division and floating point. I believe many of the financial companies using BigDecimal which is used for proper division

---

 Challenge #4 Solution — JIRA-104 (NullPointerException from null list)

This is the most common error that we faced always which is NullPointer.

 java.lang.NullPointerException: Cannot invoke "java.util.List.iterator()" because the return value of "com.jpmc.midascore.domain.UserProfileService$User.getAddresses()" is null

There is method called getPrimaryAddress which they have written in enhanced for loop and returning the address if found and returns null if not found.
Enhanced for-loop tries to create an iterator. If the list is null, it throws NullPointerException immediately.

I have applied the fix by adding a guard clause:
- If user == null return null (optional but safe)
- If addresses == null return null
- Else loop will work and return primary if found

Null handling is not just about adding if-checks. It’s about making the method safe for real-world data states (new users, partial loads, optional fields).

---

Challenge #5 Solution — JIRA-105 (Race condition / lost updates)
  This one is also an assertion error.  org.opentest4j.AssertionFailedError: The service should ignore duplicate transaction IDs. ==> 
It is not ignoring the duplicate id’s. Which we need to safeguard by show-casing the wrong money.

The calculateTotalVolume()is  simply adding every record’s amount even if it is duplicated.

To solve this, I have used a Set based approach because a Set naturally stores only unique values.
I created a Set called processedIds to track what we have already processed.

processedIds.add(transactionId) returns:
true → if it is first time seeing this ID (unique), so we should include it
false → we have already seen this ID (duplicate), so we can skip it
So this condition means:
If this transactionId is already processed, don’t add the amount again.
