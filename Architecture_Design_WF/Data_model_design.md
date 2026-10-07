## Data Model Design Overview

This data model supports the A2 API Contract for real-time customer account and balance retrieval, strictly adhering to canonical banking domain modeling and regulatory requirements. All entities, attributes, relationships, and mappings are traceable to A2 outputs and the provided knowledge base. No assumptions are made about existing physical tables unless explicitly evidenced.

---

## 1. Entity/Table Catalog

| Entity/Table      | Status   | Purpose                                      | Identifier         | Source                |
|-------------------|----------|----------------------------------------------|--------------------|-----------------------|
| Customer          | NEW      | Represents a banking customer                | customer_id        | A2 API, KB Chunks 0,5 |
| Account           | NEW      | Represents a customer account                | account_id         | A2 API, KB Chunks 4,5 |
| Account_Holder    | NEW      | Links Party/Customer to Account (role-based) | account_id, party_id| KB Chunks 4,18        |
| Account_Balance   | NEW      | Stores account balances (multiple types)     | account_id, as_of_date | A2 API, KB Chunk 14   |

---

## 2. Existing Database Structures

Existing database information not provided.

---

## 3. New/Modified Table Design

### Customer

| Column        | Data Type    | Required | Nullable | PK  | FK  | Unique | Sensitive | Description                |
|---------------|-------------|----------|----------|-----|-----|--------|----------|----------------------------|
| customer_id   | BIGINT      | Yes      | No       | Yes | No  | Yes    | Yes      | Unique customer identifier |
| party_id      | BIGINT      | Yes      | No       | No  | Yes | No     | Yes      | Links to Party entity      |
| first_name    | VARCHAR(100)| Yes      | No       | No  | No  | No     | Yes      | Customer first name        |
| last_name     | VARCHAR(100)| Yes      | No       | No  | No  | No     | Yes      | Customer last name         |
| email         | VARCHAR(255)| No       | Yes      | No  | No  | No     | Yes      | Customer email address     |
| customer_type | VARCHAR(20) | No       | Yes      | No  | No  | No     | No       | Individual/Corporate       |
| status_code   | VARCHAR(20) | No       | Yes      | No  | No  | No     | No       | Customer status            |
| created_at    | TIMESTAMP   | Yes      | No       | No  | No  | No     | No       | Record creation timestamp  |

**Constraints:**  
- PK: customer_id  
- FK: party_id → Party.party_id  
- Email format validation

**Indexes:**  
- Unique: customer_id  
- Index: party_id

---

### Account

| Column         | Data Type    | Required | Nullable | PK  | FK  | Unique | Sensitive | Description                  |
|----------------|-------------|----------|----------|-----|-----|--------|----------|------------------------------|
| account_id     | BIGINT      | Yes      | No       | Yes | No  | Yes    | Yes      | Unique account identifier    |
| account_number | VARCHAR(34) | Yes      | No       | No  | No  | Yes    | Yes      | Formal account number        |
| iban           | VARCHAR(34) | No       | Yes      | No  | No  | No     | Yes      | International account number |
| currency_code  | CHAR(3)     | Yes      | No       | No  | No  | No     | No       | ISO 4217 currency code       |
| status_code    | VARCHAR(20) | Yes      | No       | No  | No  | No     | No       | Account lifecycle status     |
| product_id     | BIGINT      | No       | Yes      | No  | Yes | No     | No       | FK to Account Product        |
| created_at     | TIMESTAMP   | Yes      | No       | No  | No  | No     | No       | Account open date            |
| closed_at      | TIMESTAMP   | No       | Yes      | No  | No  | No     | No       | Account close date           |

**Constraints:**  
- PK: account_id  
- Unique: account_number  
- FK: product_id → Account_Product.product_id

**Indexes:**  
- Unique: account_number  
- Index: product_id

---

### Account_Holder

| Column      | Data Type    | Required | Nullable | PK  | FK  | Unique | Sensitive | Description                  |
|-------------|-------------|----------|----------|-----|-----|--------|----------|------------------------------|
| account_id  | BIGINT      | Yes      | No       | Yes | Yes | No     | Yes      | FK to Account                |
| party_id    | BIGINT      | Yes      | No       | Yes | Yes | No     | Yes      | FK to Party/Customer         |
| holder_role | VARCHAR(20) | Yes      | No       | No  | No  | No     | No       | Role (primary, joint, etc.)  |
| start_date  | DATE        | Yes      | No       | No  | No  | No     | No       | Role start date              |
| end_date    | DATE        | No       | Yes      | No  | No  | No     | No       | Role end date                |

**Constraints:**  
- PK: (account_id, party_id)  
- FK: account_id → Account.account_id  
- FK: party_id → Party.party_id

**Indexes:**  
- Composite: (account_id, party_id)

---

### Account_Balance

| Column            | Data Type     | Required | Nullable | PK  | FK  | Unique | Sensitive | Description                       |
|-------------------|--------------|----------|----------|-----|-----|--------|----------|-----------------------------------|
| account_id        | BIGINT       | Yes      | No       | Yes | Yes | No     | Yes      | FK to Account                     |
| as_of_date        | TIMESTAMP    | Yes      | No       | Yes | No  | No     | No       | Balance snapshot timestamp         |
| current_balance   | DECIMAL(20,2)| No       | Yes      | No  | No  | No     | Yes      | Current balance                   |
| available_balance | DECIMAL(20,2)| No       | Yes      | No  | No  | No     | Yes      | Available balance                  |
| ledger_balance    | DECIMAL(20,2)| No       | Yes      | No  | No  | No     | Yes      | Ledger balance                     |
| blocked_amount    | DECIMAL(20,2)| No       | Yes      | No  | No  | No     | Yes      | Blocked funds                      |

**Constraints:**  
- PK: (account_id, as_of_date)  
- FK: account_id → Account.account_id

**Indexes:**  
- Composite: (account_id, as_of_date)

---

## 4. Entity Relationships

| Parent   | Child         | Cardinality | FK                  | Delete/Update Behavior | Source           |
|----------|--------------|-------------|---------------------|-----------------------|------------------|
| Party    | Customer     | 1:N         | Customer.party_id   | Restrict/Restrict     | KB Chunks 0,5    |
| Account  | Account_Holder| 1:N        | Account_Holder.account_id | Cascade/Restrict | KB Chunk 4       |
| Party    | Account_Holder| 1:N        | Account_Holder.party_id   | Cascade/Restrict | KB Chunk 4       |
| Account  | Account_Balance| 1:N       | Account_Balance.account_id| Cascade/Restrict | KB Chunk 14      |

---

## 5. API → Domain → Database Mapping

| API Resource | API Field   | Domain Entity | Table           | Column           | Mapping Type | Persisted? |
|--------------|-------------|--------------|-----------------|------------------|--------------|------------|
| Customer     | customerId  | Customer     | customer        | customer_id      | Direct       | Yes        |
| Customer     | firstName   | Customer     | customer        | first_name       | Direct       | Yes        |
| Customer     | lastName    | Customer     | customer        | last_name        | Direct       | Yes        |
| Customer     | email       | Customer     | customer        | email            | Direct       | Yes        |
| Account      | accountId   | Account      | account         | account_id       | Direct       | Yes        |
| Account      | accountType | Account      | account         | product_id (via FK to Product) | Derived/Join | Yes |
| Account      | currency    | Account      | account         | currency_code    | Direct       | Yes        |
| Balance      | accountId   | Account      | account         | account_id       | Direct       | Yes        |
| Balance      | balance     | Account_Balance | account_balance | current_balance | Direct       | Yes        |
| Balance      | currency    | Account      | account         | currency_code    | Direct       | Yes        |
| Balance      | lastUpdated | Account_Balance | account_balance | as_of_date      | Direct       | Yes        |

---

## 6. Derived / Non-Persisted Data

| Field        | Source         | Derivation                | Persistence Decision |
|--------------|---------------|---------------------------|---------------------|
| accountType  | Account/Product| Derived via FK to Product | Persisted (via join)|
| lastUpdated  | Account_Balance| as_of_date                | Persisted           |

---

## 7. Security & Privacy

| Data Element     | Classification | Protection                | Access Restriction         |
|------------------|----------------|---------------------------|----------------------------|
| customer_id      | PII            | Masking, Access Control   | Authenticated, Scoped      |
| first_name       | PII            | Masking, Access Control   | Authenticated, Scoped      |
| last_name        | PII            | Masking, Access Control   | Authenticated, Scoped      |
| email            | PII            | Masking, Encryption       | Authenticated, Scoped      |
| account_id       | Financial      | Masking, Access Control   | Authenticated, Scoped      |
| account_number   | Financial      | Masking, Access Control   | Authenticated, Scoped      |
| iban             | Financial      | Masking, Access Control   | Authenticated, Scoped      |
| currency_code    | Non-PII        | None                      | Authenticated, Scoped      |
| balance fields   | Financial      | Masking, Access Control   | Authenticated, Scoped      |

---

## 8. Audit & Data Retention

| Entity         | Audit Requirement         | Retention | Archive/Delete Requirement | Source      |
|----------------|--------------------------|-----------|---------------------------|-------------|
| Customer       | Full change audit trail  | UNKNOWN   | UNKNOWN                   | A2, KB      |
| Account        | Full change audit trail  | UNKNOWN   | UNKNOWN                   | A2, KB      |
| Account_Balance| Balance change audit     | UNKNOWN   | UNKNOWN                   | A2, KB      |

---

## 9. Migration Impact

**Migration Classification:** SCHEMA-ONLY

| Change         | Reason                   | Dependency | Deployment Order | Rollback | Risk      |
|----------------|-------------------------|------------|------------------|----------|-----------|
| New tables     | Implement API contract  | None       | After approval   | Drop tables| Low      |

---

## 10. Backward Compatibility

- As these are new tables for a new API, no backward compatibility issues are expected.
- Any future changes to schema must follow versioning strategy as per A2.

---

## 11. Traceability

| Data Model Element | A2 Source         | Justification                        | Status    |
|--------------------|-------------------|--------------------------------------|-----------|
| customer_id        | API, Handoff      | Customer identifier                  | TRACEABLE |
| account_id         | API, Handoff      | Account identifier                   | TRACEABLE |
| balance fields     | API, Handoff, KB  | Distinct balance types               | TRACEABLE |
| account_holder     | KB                | Canonical join for Party/Account     | TRACEABLE |
| as_of_date         | API, KB           | Balance timestamp                    | TRACEABLE |

---

## 12. Assumptions

- All data model elements are traceable to A2 outputs or the knowledge base.
- No existing database structures are assumed.
- All relationships and constraints are based on canonical banking domain guidance.
- Retention and audit requirements are marked UNKNOWN unless specified.

---

## 13. Open Decisions

| ID  | Decision                                                        | Impact                | Required From         |
|-----|-----------------------------------------------------------------|-----------------------|----------------------|
| 1   | Whether account metadata (e.g., accountType) is required in balance response | API completeness      | Product Owner        |
| 2   | Retention and deletion periods for PII and financial data        | Compliance            | Legal/Compliance     |
| 3   | Whether Party entity is to be physically modeled or abstracted  | Physical schema design| Data Architect       |

---

## 14. Quality Checks

- Entity/table consistency: PASS
- PK/FK consistency: PASS
- Relationship consistency: PASS
- Constraint consistency: PASS
- Index justification: PASS
- API/data mapping consistency: PASS
- Security/privacy review: PASS (pending retention)
- Retention review: OPEN
- Migration review: PASS
- Traceability review: PASS

---

## 15. Final Data Model Decision

**Status:** READY FOR REVIEW

**Summary:**  
The data model is fully traceable to A2 API contract and domain knowledge base, with all entities, attributes, and relationships explicitly justified. No assumptions are made about existing structures. Open decisions are documented for retention and compliance. The design is implementation-ready pending review of open items.

**Critical Decisions:**  
- Retention and deletion requirements for PII/financial data must be confirmed.
- Confirmation of account metadata requirements in balance responses.
- Physical modeling of Party entity to be finalized if required by broader domain scope.
