.PHONY: all clean server client shared

# Root directory is assumed to be chess/
all: server client

# start commmands in main chess directory

# Build and run server
server:
	cd server && mvn compile exec:java -Dexec.mainClass=server.Server

# Build and run client
client:
	cd client && mvn compile exec:java -Dexec.mainClass=client.Repl

# Reinstall shared module
shared:
	cd shared && mvn clean install

# Clean all builds
clean:
	cd shared && mvn clean
	cd server && mvn clean
	cd client && mvn clean
