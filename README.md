# CSYE 6225 - Spring 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Vishnu Prasad Maruthi|001200200 |maruthi.v@husky.neu.edu |
| Vinyas Kaushik Tumakunte Raghavendrarao|001216716|tumakunteraghaven.v@husky.neu.edu|
| Vikram Ramesh|001856230|ramesh.vik@husky.neu.edu|
| Megan Simone Theresa Dsouza|001837524|dsouza.me@husky.neu.edu |

## Technology Stack

* Spring Boot
* IntelliJ
* MySQL
* Apache Tomcat 9.0.16
* JAVA 8
* Gradle 5.0
* Postman
* Git
* CircleCI
* AWS

## Cloud Native Application 

### User Functionality

User Creation: Send the POST request http://localhost:8080/user/register, if user doesnot exists user gets created with message "Registered Succesfully", else we get message "User already exists" .

Send the Get request http://localhost:8080/ and select Basic Auth after succesfully authenticating the request we get the current time else error message will be displayed "Unauthorised".

### Note Functionality 

* As a user, only I should be able to perform CRUD operations on my note.
* As a user, I must be authentication to the application and authorized on the note (i.e. I must be the owner of the note).
* As a user, I should be able to create a new note.
* As a user, I should be able to update a note that I have created.
* As a user, I should be able to delete a note that I have created.
* As a user, I should be able to get all note that I have created.
* As a user, I should be able to get a note that I have created.

| Endpoint | Request Type | Functionality |
| --- | --- | --- | --- |
| /note | GET | Get all notes for a User |
| /note | POST | Create a note for the user |
| /note/{idNotes} | GET | Get note for the user |
| /note/{idNotes} | PUT | Update a note for the user |
| /note/{idNotes} | DELETE | Delete a note for the user |

### Attachment Functionality 

* As a user, I want to add, update or delete attachements to my notes. I should be authenticated & authorized to be able to perform these operations.
* As a user, I want to add a attachements to my note. You must support any docs for attachements.
* As a user, I want to update attachements attached to note. Updating attachements should replace existing attachements.
* As a user, I want to delete attachements attached to note.
* As a user, I expect attachments to be stored in Amazon S3 bucket when the application is running on cloud (when running in EC2 instance). When the application is running locally on developer’s machine you must store attachments in a local temporary directory.
* Metadata about attachements attached to my note should be stored in RDBMS such as MySQL.

| Endpoint | Request Type | Functionality |
| --- | --- | --- | --- |
| /note/{idNotes}/attachments | GET | Get list of files attached to the note identified by the 'id' |
| /note/{idNotes}/attachments | POST | Attach a file to the note identified by the 'id' |
| /note/{idNotes}/attachments/{idAttachments} | PUT | Update file identified by 'idAttachments' attached to the note identified by the 'id' |
| note/{idNotes}/attachments/{idAttachments} | DELETE | Delete file identified by 'idAttachments' attached to the transaction identified by the 'id'|

## Deploy Instructions

* Run the CircleCI build then the code will be deployed in EC2
* Hit the Public DNS of EC2 follwed by application name (here CloudApp)
* Perform CRUD operations on User, Notes and Attachments on Postman and check whether Application is working.


## Unit Tests
The CloudappApplicationTests Junit file which is present in Test folder.

## CI
CircleCI is Continuous Integration, a development practice which is being used by software teams allowing them to to build, test and deploy applications easier and quicker on multiple platforms.

## CD 
AWS CodeDeploy is a fully managed deployment service that automates software deployments to a variety of compute services such as Amazon EC2.You can use AWS CodeDeploy to automate software deployments, eliminating the need for error-prone manual operations. The service scales to match your deployment needs.

### Trigger CircleCI Build Using API

```
curl -u TOKEN \
     -d build_parameters[CIRCLE_JOB]=build \
     https://circleci.com/api/v1.1/project/github/tejasparikh/csye6225-spring2019-ami/tree/master
```






