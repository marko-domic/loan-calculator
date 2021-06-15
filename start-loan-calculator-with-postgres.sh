
echo "This could take some time..."

# Build docker image
./gradlew bootBuildImage --imageName=loan-calculator

# Go to a directory where docker compose yml file is
cd docker/postgres/

# Run docker compose file
docker-compose up -d

echo "Loan calculator service with Postgres is up and running."
