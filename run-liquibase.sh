#!/bin/bash

# Load environment variables from .env file
if [ -f .env ]; then
  export $(grep -v '^#' .env | xargs)
else
  echo ".env file not found"
  exit 1
fi

# Run Maven command with environment variables
mvn liquibase:generateChangeLog \
  -Ddatasource.driver=$DATASOURCE_DRIVERCLASSNAME \
  -Ddatasource.url=$DATASOURCE_URL \
  -Ddatasource.username=$DATASOURCE_USERNAME \
  -Ddatasource.password=$DATASOURCE_PASSWORD \
  -Dliquibase.changeLogFile=src/main/resources/migration/db.changelog-dev.xml \
  -Dliquibase.outputChangeLogFile=src/main/resources/migration/ddl/liquibase-initialChangeLog.xml
