Test Me
========

This is a program for you to make some tests in your company or group.

Main features:
---
* YML file format for tests.
* Single answer and multiple answers questions.
* Open questions to be graded later with criterias of grading.
* Question categories, so you can divide large test into areas.
* Test UI with single question per time.
* Answer time countdown with sever-side recording.
* An admin UI to view results.
* Markdown (GFM) is supported in questions and answers (as well as test description).
* LDAP / Simple db authentication.

Test format:
---

Tests are like this (check tests/sample.yml):
```yml
title: Sample test

description: |
  This test shows how to use different features of this stuff.

  ~```sql
  SELECT * FROM your_knowledge
  ~```
time: About 2 minutes   # Text description of time estimate
defaultTime: 65         # Default time for question, in seconds, could be overriden with timeOverride
shuffleQuestions: true
shuffleAnswers: true
questions:
  -
    question: What is the meaning of life?
    timeOverride: 120
    weight: 2
    answers:
      - +42 # Correct answer is marked with + at the beginning.
      - 12
      - 256
```

Technologies used
----
* Spring Boot 1.3
* AngularJS (+ route)
* Bootstrap
* Marked
* HighlightJS
* PostgreSQL (should work with other SQL's as well)
* FreeMarker

Installation
----

```shell
git clone ...
cd testme
mvnw clean package
```

* Then grab `target/...jar` to your directory.
* Create test folder with tests.
* Create database.
* To be continued...
