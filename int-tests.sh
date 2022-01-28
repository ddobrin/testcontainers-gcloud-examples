# fetch the latest TCC agent version and make it executable
apt install curl
curl -L -o agent https://app.testcontainers.cloud/download/testcontainers-cloud-agent_linux_x86-64
chmod +x agent

# start agent as background process
./agent &

# make sure TCC is ready before proceeding
./agent wait

# run your Testcontainers powered tests as usual
apt update
apt install maven
mvn verify