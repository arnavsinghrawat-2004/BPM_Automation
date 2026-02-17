# Application Workflows

## Loan Workflow (existing delegates)

The loan flow is implemented by the delegates in `src/main/java/com/iongroup/library/adapter/flowable` and services under `src/main/java/com/iongroup/library/service`.

- **Start Application** — Outputs: `applicationId`, `customerId`. Node: `StartLoanApplication` implemented by `src/main/java/com/iongroup/library/adapter/flowable/StartLoanApplicationTask.java`.
- **Get Customer Profile** — Inputs: `customerId`, Outputs: `customerProfile`. Node: `GetCustomerProfile` implemented by `src/main/java/com/iongroup/library/adapter/flowable/GetCustomerProfileTask.java`.
- **Check Eligibility** — Inputs: `customerProfile`, Outputs: `eligibilityStatus`. Node: `CheckEligibility` implemented by `src/main/java/com/iongroup/library/adapter/flowable/CheckEligibilityTask.java`.
- **Get Loan Policy** — Inputs: `customerProfile`, Outputs: `loanPricingPolicy`. Node: `GetLoanPolicy` implemented by `src/main/java/com/iongroup/library/adapter/flowable/GetLoanPolicyTask.java`.
- **Create Loan Offer** — Inputs: `customerProfile`, `loanPricingPolicy`, Outputs: `loanOffer`. Node: `CreateLoanOffer` implemented by `src/main/java/com/iongroup/library/adapter/flowable/CreateLoanOfferTask.java` and service `src/main/java/com/iongroup/library/service/impl/LoanOfferServiceImpl.java`.
- **Customer Approval** — Node: `CustomerApproval` implemented by `src/main/java/com/iongroup/library/adapter/flowable/CustomerApprovalTask.java`.
- **Notify Customer** — Node: `NotifyCustomer` implemented by `src/main/java/com/iongroup/library/adapter/flowable/NotifyCustomerTask.java`.
- **End Application** — Node: `EndLoanApplication` implemented by `src/main/java/com/iongroup/library/adapter/flowable/EndLoanApplicationTask.java`.

---

## Credit Card Workflow (new delegates & services)

The credit-card workflow reuses existing pieces (customer profile, eligibility, notification) and adds card-specific delegates and services.

1. **Start Application**
	- Inputs: `customerId`
	- Outputs: `applicationId`
	- Node: `StartApplication` implemented by `src/main/java/com/iongroup/library/adapter/flowable/StartCardApplicationTask.java`.

2. **Get Customer Profile**
	- Reuses `GetCustomerProfile` (`src/main/java/com/iongroup/library/adapter/flowable/GetCustomerProfileTask.java`).

3. **Check Eligibility**
	- Reuses `CheckEligibility` (`src/main/java/com/iongroup/library/adapter/flowable/CheckEligibilityTask.java`).

4. **Get Available Credit Cards**
	- Inputs: `customerProfile`, `eligibilityStatus`
	- Outputs: `availableCardOffers` (List of `CreditCardOffer`)
	- Node: `GetAvailableCreditCards` implemented by `src/main/java/com/iongroup/library/adapter/flowable/GetAvailableCreditCardsTask.java`.

5. **Select Credit Card**
	- Frontend step where user selects one of the `availableCardOffers`.

6. **Create Card Offer**
	- Inputs: `customerProfile`, `cardPricingPolicy`
	- Outputs: `creditCardOffer` (`src/main/java/com/iongroup/library/domain/CreditCardOffer.java`)
	- Node: `CreateCardOffer` implemented by `src/main/java/com/iongroup/library/adapter/flowable/CreateCardOfferTask.java` and service `src/main/java/com/iongroup/library/service/impl/CardOfferServiceImpl.java`.

7. **Customer Approval / eSign (Optional)**
	- Node: `CustomerApproval` (reusable `CustomerApprovalTask.java`).

8. **Issue Card**
	- Inputs: `customerProfile`, `eligibilityStatus`
	- Outputs: `cardIssued` (`src/main/java/com/iongroup/library/domain/Card.java`)
	- Node: `IssueCreditCard` implemented by `src/main/java/com/iongroup/library/adapter/flowable/IssueCreditCardTask.java` using `src/main/java/com/iongroup/library/service/impl/CardIssuanceServiceImpl.java`.

9. **Notify Customer**
	- Reuses `NotifyCustomerTask.java` to send status and card details.

---

## New/Updated domain & service classes

- `src/main/java/com/iongroup/library/domain/CreditCardOffer.java` — model for card offers.
- `src/main/java/com/iongroup/library/domain/CardPricingPolicy.java` — simple pricing policy for card creation.
- `src/main/java/com/iongroup/library/service/CardOfferService.java` — service interface.
- `src/main/java/com/iongroup/library/service/impl/CardOfferServiceImpl.java` — basic implementation used by delegates.

These integrate with existing `CustomerProfile`, `EligibilityResult`, and `CardIssuanceServiceImpl`.

---

## Notes

- Delegates are lightweight and intended to be replaced by Spring-injected beans in real applications.
- Frontend nodes (e.g., `SelectCreditCard`) are not implemented as Java delegates; they represent user tasks.
- To compile and verify, run `mvn -q -DskipTests package` from the project root.
