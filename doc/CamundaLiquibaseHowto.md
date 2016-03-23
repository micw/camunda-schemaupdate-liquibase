# What is it?

This package provides automatic schema updates for camunda based on liquibase. It consists of an initial schema for camunda 7.1 and incremental updates for newer versions.

# How to use?

## Starting with an empty database

The easiest way is to start with an empty database (or at least with a database that does not contain any camunda tables).

TODO!

## Starting with an existing database

Liquibase keeps track of all database updates (called "change sets") that are already applied to the database. This state is tracked in the table DATABASECHANGELOG. If the database already contains a camunda schema that was created manually (or by camunda itself), the state must be populated to the DATABASECHANGELOG manually:

1. Run the camunda-liquibase scripts against an empty database
2. Copy the contents of DATABASECHANGELOG to the target database
3. Ensure that all changesets are manually applied to the target database
  
Original changesets can be found at https://docs.camunda.org/manual/7.4/update/patch-level/#database-patches
 