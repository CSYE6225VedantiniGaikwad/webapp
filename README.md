# webapp

Steps to run code:
1. Clone the Repository in your local using it clone.
2. Open the package in Intellij and do Maven Clean install using mvn clean install.
3. Run the code.

Database:
1. Run brew services start to start mysql.

Postman:

Get Healthz:
localhost:8080/healthz

Get all assignments:
localhost:8080/v1/assignments using GET

Create assignment:
localhost:8080/v1/assignments/id using POST and pass body in raw

Update assignment:
localhost:8080/v1/assignments/id using PUT and pass updated body in raw

Delete assignment:
localhost:8080/v1/assignments/id using DELETE

Body :
{
  "name": "Assignment 015",
  "points": 8,
  "num_of_attempts": 5,
  "deadline": "2016-08-29T09:12:33.001Z"
}
