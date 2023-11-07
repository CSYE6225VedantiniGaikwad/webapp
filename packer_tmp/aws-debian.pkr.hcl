packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9" # Debian-12
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "subnet_id" {
  type    = string
  default = "subnet-0e6388fd210c2e29c"
}

source "amazon-ebs" "my-ami" {
  region          = "${var.aws_region}"
  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  profile         = "default"
  instance_type   = "t2.micro"
  source_ami      = "${var.source_ami}"
  ssh_username    = "${var.ssh_username}"
  subnet_id       = "${var.subnet_id}"
  ami_users       = ["458264565990"]

  launch_block_device_mappings {
    delete_on_termination = true
    device_name           = "/dev/xvda"
    volume_size           = 25
    volume_type           = "gp2"
  }
}

build {
  sources = ["source.amazon-ebs.my-ami"]

  provisioner "file" {
    source      = "../demo/target/demo-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/demo-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    source      = "../opt/users.csv"
    destination = "/tmp/users.csv"
  }

  provisioner "file" {
    source      = "../systemd/autorunApplication.service"
    destination = "/tmp/autorunApp.service"
  }

  provisioner "file" {
    source      = "../cloudwatch/cloudwatch.json"
    destination = "/tmp/cloudwatch.json"
  }

  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    script = "../setup.sh"
  }

  provisioner "shell" {
    inline = [
      "sudo mv /tmp/users.csv /opt/users.csv",
      "sudo mv /tmp/demo-0.0.1-SNAPSHOT.jar /opt/demo-0.0.1-SNAPSHOT.jar",
      "sudo mv /tmp/cloudwatch.json /opt/cloudwatch.json",
      "sudo mv /tmp/autorunApp.service /etc/systemd/system/autorunApp.service"
    ]
  }

  provisioner "shell" {
    inline = [
      "sudo useradd WebappUser",
      "sudo chown WebappUser:WebappUser /opt/demo-0.0.1-SNAPSHOT.jar",
      "sudo chmod 500 /opt/demo-0.0.1-SNAPSHOT.jar",
      "sudo wget https://amazoncloudwatch-agent.s3.amazonaws.com/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb",
      "sudo dpkg -i -E ./amazon-cloudwatch-agent.deb",
      "sudo systemctl daemon-reload",
      "sudo systemctl enable autorunApp",
      "sudo systemctl start autorunApp"
    ]
  }

}
