# Loan Application - Demo Application for ING Hubs
This is a backend Loan API for a bank so that employees of the bank can create, list and pay loans for their customers. 

## Endpoints
Swagger-UI is used to document the endpoints. All endpoints are reachable via the link (http://localhost:8080/swagger-ui/index.html). Backend service have following endpoints (All endpoints are authorized with a user and password):

### Create Loan
Allows to create a new loan for a given customer, with following parameters: amount, interest rate and number of installments. Backend service chekcs if the customer has enough limit to get this new loan. Also Number of installments can only be 6, 9, 12, 24. Interest rate must be between 0.1 – 0.5. Upon creation of the loan installments are calculated and persisted to the relevant database table. All installments have same amount. Total amount for loan is calculated as amount * (1 + interest rate).
### List Loans
Allows list loans for a given customer. Filters can be applied to query using parameters customerId, isPaid and numberOfInstallments.
### List Installment
Allows to list installments for a given loan.
### Pay Loan
Allows to pay installment for a given loan with the specified amount.
### List Customer
## Database
## Security
