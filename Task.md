# Platform Lunar backend homework

## Functional Requirements

Implement a service that connects to GitHub API and retrieves a list of 10 most active (as defined by the  number of stars) Java frameworks.

For each framework identified the following attributes must be exposed by your REST API:

- General info:
  - Name
  - Description
  - Licence name
  - Link to the repo
  - Whether or not current user (see below) has put a star on the repo
- Key metrics:
  - Number of contributors (including anonymous)
  - Number of stars

The API should make it possible to return the entries sorted by either key metric so user could query something like: `list top 10 most active Java frameworks and those with the least number of contributors should be listed first`.

To give it some personal touch, the API should allow starring/un-starring a repo. Obviously, this requires authenticating against GitHub. The presence or absence of a personal star is included in the API response as described above.

To summarize, your REST API should expose endpoints to:

1. Query the most active frameworks, with or without one’s own GitHub credentials supplied (if they are supplied, the response includes whether or not the repo is starred by the user)
1. Star the repo
1. Un-star the repo

## Technical Requirements

1. The code is written in any JVM language (e.g. Java / Groovy / Scala / Clojure / Kotlin) or a reasonable mix of those
1. Your build scripts produce a Docker image which runs with minimal fuss:
  `$ docker run image-tag` or even `$ docker-compose up`

## Evaluation Criteria

- Convenience of building and running the application, making as little assumptions about OS, Maven/Gradle versions installed, etc. Ideally, JDK and Docker should suffice.
- Code is readable, tidy and is consistent in style and formatting
- Effort to minimize boilerplate code imposed by the language or framework of choice
- Code is well structured into packages
- Clear and concise README
- Reasonable use of version control and commit messages
- Functional requirements fulfilled and covered by tests
- Value of the test code and its focus on asserting functionality vs. implementation details
- [Soundness](https://en.oxforddictionaries.com/definition/soundness) of REST API design
- Effectiveness of retrieving information from GitHub API
- Simplicity of overall approach and lack of over-engineering

## Review

Use [Homework review template](homework_review_template.md) and export it to PDF.
