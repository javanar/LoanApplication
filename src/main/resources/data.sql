INSERT INTO customer(name, surname, creditLimit) VALUES('CUSTOMER 1', 'CUSTOMER 1', 1000000);
INSERT INTO customer(name, surname, creditLimit) VALUES('CUSTOMER 2', 'CUSTOMER 2', 2000000);
INSERT INTO customer(name, surname, creditLimit) VALUES('CUSTOMER 3', 'CUSTOMER 3', 3000000);

INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('MIN_INTEREST_RATE', '0.1', 'Minimum Applicable Interest Rate');
INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('MAX_INTEREST_RATE', '0.5', 'Maximum Applicable Interest Rate');
INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('VALID_NUMBER_OF_INSTALLMENTS', '6', 'Valid Number Of Installments');
INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('VALID_NUMBER_OF_INSTALLMENTS', '9', 'Valid Number Of Installments');
INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('VALID_NUMBER_OF_INSTALLMENTS', '12', 'Valid Number Of Installments');
INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('VALID_NUMBER_OF_INSTALLMENTS', '24', 'Valid Number Of Installments');
INSERT INTO config(configurationKey, configurationValue, explanation) VALUES('MAX_NUMBER_OF_INSTALLMENTS_TO_BE_PAID', '3', 'Maximum Number Of Installments That Can be Paid In Once');