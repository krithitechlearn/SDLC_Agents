### API Design Overview:
**Purpose:** Expose real-time account balance information for authenticated banking customers.  
**Base Path:** `/api/v1`  
**Resources:** Account Balance  

---

### API Resource Model:
| Resource       | Identifier   | Description                                   |
|----------------|--------------|-----------------------------------------------|
| AccountBalance | accountId    | Represents the real-time balance of an account |

---

### Endpoint Catalog:
| ID     | Method | Path                          | Purpose                                      |
|--------|--------|-------------------------------|----------------------------------------------|
| API-001| GET    | /accounts/{accountId}/balance | Retrieve the real-time balance of an account|

---

### Detailed API Contracts:
#### **API-001: Retrieve Account Balance**
- **Method:** GET  
- **Path:** `/accounts/{accountId}/balance`  
- **Purpose:** Retrieve the real-time balance of a specific account.  
- **Authentication:** Bearer OAuth2/JWT  
- **Authorization:** Required scope: `account.balance.read`  
- **Path Parameters:**  
  - `accountId`: string, required  
- **Request Body:** None  
- **Success Response:**  
  - **Status Code:** 200 OK  
  - **Response Schema:** `AccountBalanceResponse`  
- **Error Responses:**  
  - 400 Bad Request  
  - 401 Unauthorized  
  - 403 Forbidden  
  - 404 Not Found  

---

### Request/Response Schemas:
#### **Request Schema:**
None (GET request).

#### **Response Schema:**
```yaml
AccountBalanceResponse:
  type: object
  required:
    - accountId
    - currentBalance
    - currency
    - lastUpdated
  properties:
    accountId:
      type: string
      description: Unique identifier for the account.
    currentBalance:
      type: number
      format: float
      description: The real-time balance of the account.
    currency:
      type: string
      description: ISO 4217 currency code.
    lastUpdated:
      type: string
      format: date-time
      description: Timestamp of the last balance update.
```

---

### Error Model:
```yaml
ErrorResponse:
  type: object
  required:
    - code
    - message
    - traceId
  properties:
    code:
      type: string
      description: Error code identifier.
    message:
      type: string
      description: Human-readable error message.
    traceId:
      type: string
      description: Correlation ID for tracing the request.
```

---

### Validation Rules:
- `accountId` must be a valid, non-null string.
- `currentBalance` must be a numeric value.
- `currency` must be a valid ISO 4217 code.
- `lastUpdated` must be a valid date-time string.

---

### Authentication & Authorization:
- **Authentication:** OAuth2/JWT  
- **Authorization:** Scope `account.balance.read` required.  

---

### Pagination/Filtering/Sorting:
Not applicable for single resource retrieval.

---

### Versioning Strategy:
- **API Version:** v1  
- Breaking changes will require a new major version.

---

### API Dependencies:
- **Upstream Services:**  
  - Account Service (for account validation).  
  - Balance Service (for real-time balance retrieval).  

---

### LLD-to-API Traceability:
| API     | LLD Component          | Requirement                                   | Status      |
|---------|-------------------------|-----------------------------------------------|-------------|
| API-001 | BalanceViewService      | Retrieve real-time account balance            | TRACEABLE   |

---

### Assumptions:
- OAuth2/JWT is used for authentication.
- Real-time balance includes only posted transactions.
- The `accountId` is globally unique.

---

### Open Decisions:
- Confirm whether pending transactions should be included in the balance.

---

### API Contract Quality Checks:
- [x] All endpoints traceable to LLD.
- [x] No duplicate endpoints.
- [x] All schemas defined.
- [x] HTTP methods validated.
- [x] Security requirements applied.
- [x] OpenAPI structure validated.

---

### Data Model Handoff:
| LLD Entity | API Resource       | API Schema             | API Field       | LLD Source Field       | Type    | Required | Nullable | Access     | Mapping  |
|------------|---------------------|------------------------|-----------------|------------------------|---------|----------|----------|------------|----------|
| Account    | AccountBalance      | AccountBalanceResponse | accountId       | Account.accountId      | string  | Yes      | No       | Read-only  | Direct   |
| Balance    | AccountBalance      | AccountBalanceResponse | currentBalance  | Balance.balanceValue   | number  | Yes      | No       | Read-only  | Direct   |
| Balance    | AccountBalance      | AccountBalanceResponse | currency        | Balance.balanceCurrency| string  | Yes      | No       | Read-only  | Direct   |
| Balance    | AccountBalance      | AccountBalanceResponse | lastUpdated     | Balance.balanceTimestamp| string | Yes      | No       | Read-only  | Direct   |

---

### OpenAPI Specification:
```yaml
openapi: 3.0.3
info:
  title: Real-Time Account Balance API
  description: API for retrieving real-time account balance information.
  version: 1.0.0
servers:
  - url: https://api.bank.com/v1
paths:
  /accounts/{accountId}/balance:
    get:
      summary: Retrieve Account Balance
      operationId: getAccountBalance
      parameters:
        - name: accountId
          in: path
          required: true
          schema:
            type: string
            description: Unique identifier for the account.
      responses:
        '200':
          description: Account balance retrieved successfully.
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/AccountBalanceResponse'
        '400':
          description: Bad Request
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '401':
          description: Unauthorized
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '403':
          description: Forbidden
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
        '404':
          description: Not Found
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'
components:
  schemas:
    AccountBalanceResponse:
      type: object
      required:
        - accountId
        - currentBalance
        - currency
        - lastUpdated
      properties:
        accountId:
          type: string
          description: Unique identifier for the account.
        currentBalance:
          type: number
          format: float
          description: The real-time balance of the account.
        currency:
          type: string
          description: ISO 4217 currency code.
        lastUpdated:
          type: string
          format: date-time
          description: Timestamp of the last balance update.
    ErrorResponse:
      type: object
      required:
        - code
        - message
        - traceId
      properties:
        code:
          type: string
          description: Error code identifier.
        message:
          type: string
          description: Human-readable error message.
        traceId:
          type: string
          description: Correlation ID for tracing the request.
```
