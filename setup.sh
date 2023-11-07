#!/bin/bash

echo "---x---Adding Java Repository---x---"
sudo apt update && sudo apt upgrade -y

echo "---x---Installing Java 17---x---"
sudo apt install openjdk-17-jdk -y

#sudo apt install unzip
export CSV_PATH="/opt/users.csv"

echo "---x---Setting Java Environment Variables---x---"
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64/" >> ~/.bashrc
echo "export PATH=$PATH:$JAVA_HOME/bin" >> ~/.bashrc

# Refresh the current shell to recognize the changes made in .bashrc
source ~/.bashrc

echo "---x---Installing Maven---x---"
sudo apt install maven -y

echo "---x---Downloading Cloudwatch agent---x---"
sudo wget https://amazoncloudwatch-agent.s3.amazonaws.com/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb
echo "---x---Installing Cloudwatch agent---x---"
sudo dpkg -i -E ./amazon-cloudwatch-agent.deb






