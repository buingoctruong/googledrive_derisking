# Google Drive API Derisking

<!-- TOC -->

* [Google Drive API Derisking](#google-drive-api-derisking)
* [Prerequisite](#prerequisite)
* [Project structure](#project-structure)
* [Features](#features)
    * [Google Drive API](#google-drive-api)
    * [Lombok](#lombok)
    * [Swagger](#swagger)
* [Application Setup](#application-setup)
    * [Google Drive API Credentials Setup](#google-drive-api-credentials-setup)
    * [Google Drive Root Folder Setup](#google-drive-root-folder-setup)
    * [Application Startup](#application-startup)
* [API Testing](#api-testing)

<!-- TOC -->

# Prerequisite

* Java 17
* Spring Boot v3.3.3
* Google Drive API Credentials

# Project structure

* `src` - Source Code
    * `config` - Configuration for Google Drive API credentials
    * `controllers` - API controllers
    * `exceptions` - Application exceptions
    * `services` - Services that implement business logics
    * `utils` - Utilities classes
* `resources` - Resources Properties
    * 'upload-blt-files' - Testing file uploads
* `test` - Test

# Features

## Google Drive API

The Google Drive API lets you upload file data when you create or update a File.

References:

* https://developers.google.com/drive/api/guides/about-files

## Lombok

A Java library minimizes code generation for methods.

References:

* https://projectlombok.org/

## Swagger

Swagger is used for visualizing API documentation, and it's also can help automatically generate client libraries for
API in many languages and explore other possibilities like automated testing.

We can access it here: http://localhost:8080/swagger-ui/index.html#/

References:

* https://github.com/springdoc/springdoc-openapi

# Application Setup

## Google Drive API Credentials Setup

This step is for those who want to use the personal Google account. Otherwise, you can use the LocT team account
credentials.

These credentials are for a Google Cloud service account, separate from a personal Google account, with its own identity
and Google Drive.

After generating the credentials, update the corresponding properties in
the [application.yml](src/main/resources/application.yml) file.

```yaml
google:
  drive:
    type:
    project-id:
    private-key-id:
    private-key:
    client-email:
    client-id:
    auth-uri:
    token-uri:
    auth-provider-x509-cert-url:
    client-x509-cert-url:
    universe-domain: 
```

References:

* https://developers.google.com/drive/api/quickstart/java#set-up-environment

## Google Drive Root Folder Setup

This step creates a root folder in Google Drive for executing all operations.

After creating the root folder, share permissions with the account associated with the credentials from the previous
step. You can find the email address in the `client-email` property of the `application.yml` file and set it
to `Editor`.

## Application Startup

Run [GoogledriveDeriskingApplication.main()](src/main/java/indeed/googledrive_derisking/GoogledriveDeriskingApplication.java)
method,
it should run successfully on port 8080.

# API Testing

I use Swagger to document the APIs. You can access it: http://localhost:8080/swagger-ui/index.html#/

![swagger_ui.png](src%2Fmain%2Fresources%2Fswagger_ui.png)