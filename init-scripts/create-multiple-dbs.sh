#!/bin/bash

set -e
set -u

# Function to create databases
function create_database() {
  local database=$1
  local user=$2
  echo "  Creating database '$database' and granting privileges to user '$user'"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE $database;
    GRANT ALL PRIVILEGES ON DATABASE $database TO $user;
EOSQL
}

# Main database is created by POSTGRES_DB environment variable
# Create additional databases
if [ -n "$POSTGRES_USER" ] && [ -n "$POSTGRES_PASSWORD" ]; then
  # Create kc_db
  create_database "kc_db" "$POSTGRES_USER"
fi
