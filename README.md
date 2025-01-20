# Loan Application - Demo Application for ING Hubs
This is a backend Loan API for a bank so that employees of the bank can create, list and pay loans for their customers. Spring boot and spring security is used to implement application.

## Configuration parameters and application start up.
On application startup, an sql file(`data.sql`) is executed in order to insert 3 customers and also some configuration parameters to the database. It is preferred to fetch configuration parameters from the database rather than getting them from application.properties file in order to allow configuration parameter changes without the need to restart the application.

## Endpoints
Swagger-UI is used to document the endpoints. All endpoints are reachable on localhost on port 8080 via the link (http://localhost:8080/swagger-ui/index.html). Backend service have following endpoints (**All endpoints are authorized with a user and password**):

### Create Loan
Allows to create a new loan for a given customer, with following parameters: amount, interest rate and number of installments. Backend service chekcs if the customer has enough limit to get this new loan. Also Number of installments can only be 6, 9, 12, 24. Interest rate must be between 0.1 – 0.5. Upon creation of the loan installments are calculated and persisted to the relevant database table. All installments have same amount. Total amount for loan is calculated as amount * (1 + interest rate).
### List Loans
Allows list loans for a given customer. Filters can be applied to query using parameters customerId, isPaid and numberOfInstallments.
### List Installment
Allows to list installments for a given loan.
### Pay Loan
Allows to pay installment for a given loan with the specified amount. This endpoint can pay multiple installments with respect to amount sent withsome restrictions, such as; installments are paid wholly or not at all. So if installments amount is 10 and you send 20, 2 installments are paid. If you send 15, only 1 is paid. If you send 5, no installments are paid. Earliest installment is paid first and if there are more money then you are allowed to continue to next installment. Installments have due date that still more than 3 calendar months can not be paid. So if we are in January, you are allowed to pay only for January, February and March installments. A result is returned to inform how many installments paid, total amount spent and if loan is paid completely or not(responses with remaining number of installments). Behind the scenes, in the Backend Layer necessary updates are done in customer, loan and installment tables.

**Note: If an installment is paid before due date, a discount equal to installmentAmount*0.001*(number of days before due date) is made. So in this case paidAmount of installment will be lower than amount. Vice versa, If an installment is paid after due date, a penalty equal to installmentAmount *0.001*(number of days after due date) is added. So in this case paidAmount of installment will be higher than amount.**

### List Customer
Allows to list customers in database. <ins>**By default, 3 customers are inserted to the database on start up.**</ins>

## Database
Database of the application consists of 4 tables, namely Config, Customer, Installment and Loan.

+ Customer: id, name, surname, creditLimit. (Really sorry, I forgot to include usedCreditLimit field, I calculate the remaining credit lmit by manipulating the creditLimit field)
+ Loan: id, customerId, loanAmount, numberOfInstallment, createDate, isPaid, interestRate (I also forgot to include crateDate field)
+ Installment: id, loanId, amount, paidAmount, dueDate, paymentDate, isPaid, installmentNumber
+ Config: configurationId, configurationKey, configurationValue, explanation

## Security
All endpoints are authorized with a user and password. **There are 2 types of users (roles): admin and customer. Admin role can reach all endpoints via username: admin and passeord: admin. Customer role is allowed to reach only pay Loan and List installments for a given loan endpoints with username: customer and password: customer.**
