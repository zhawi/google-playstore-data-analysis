#!/bin/bash

#Variables to create Databases needed
rawDb='rawplaystoredb';
cleanDb='cleanplaystoredb';
enrichedDb='enrichedplaystoredb';
initDbFile='./postgres/init_db.sql';

#Strings to run Databases creation commands on psql on docker, populating a file with data from Create Database String
createDbs="CREATE DATABASE $rawDb; CREATE DATABASE $cleanDb; CREATE DATABASE $enrichedDb;"
echo "$createDbs" > $initDbFile

# Create Docker network
networkDocker='grafana-net'

echo -e "\n------------------------------------ // ------------------------------------\n"
echo -e "\n\nCreating network to run both dockers called $networkDocker\n\n"
echo -e "\n------------------------------------ // ------------------------------------\n"
docker network rm grafana-net
docker network create grafana-net
echo -e "\n------------------------------------ // ------------------------------------\n"
echo "\n\nNetwork created with name $networkDocker\n\n"
echo -e "\n------------------------------------ // ------------------------------------\n"

# Build Docker images

cd grafana/
echo -e "\n\nMoved to $(pwd) to build grafana container\n\n"
docker build -t grafanaserver .
cd ..

echo -e "\n\nMoved to $(pwd) to build postgres container\n\n"
cd postgres/
docker build -t postgresserver .
cd ..

# Run containers with db.env file on postgres folder
docker run --env-file ./postgres/db.env --name postgres-server-container --network grafana-net -p 5432:5432 -d postgresserver
docker run --name grafana-server-container --network grafana-net -p 3000:3000 -d grafanaserver
sleep 5


echo -e "\n------------------------------------ // ------------------------------------\n"
echo -e "\n\nGrafana and PostgreSQL containers are up and running.\n\n"
echo -e "\n------------------------------------ // ------------------------------------\n"

#Copying script to create databases on postgresSQL
echo -e "\nCreating databases $rawDb, $cleanDb and $enrichedDb.\n"
echo -e "\nstarting to copy init_db to postgres Server from $initDbFile\n\n"

docker exec postgres-server-container sh -c 'mkdir /tmp/sqlScripts'
docker cp $initDbFile postgres-server-container:/tmp/sqlScripts
sleep 2
docker exec postgres-server-container sh -c 'chmod +x /tmp/sqlScripts/init_db.sql'

#Running init_db to create the 3 databases on the server
echo -e "\n------------------------------------ // ------------------------------------\n"
echo -e "\nRunning init_db to create the 3 databases.\n"
echo -e "\n------------------------------------ // ------------------------------------\n"
sleep 5

docker exec postgres-server-container sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -f /tmp/sqlScripts/init_db.sql'


echo -e "\n------------------------------------ // ------------------------------------\n"
echo -e "\nDatabases created on PostgresSQL.\n"
echo -e "\n------------------------------------ // ------------------------------------\n"
