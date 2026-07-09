terraform {
  required_version = ">=1.14.0"

  backend "s3" {
    bucket = "gasfgrv-terraform-remote-state"
    key    = "autorizador/terraform.tfstate"
    region = "us-east-1"
  }

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "6.54.0"
    }
  }
}

provider "aws" {
  shared_config_files      = ["~/.aws/config"]
  shared_credentials_files = ["~/.aws/credentials"]
  profile                  = "default"

  default_tags {
    tags = {
      owner      = "gustavo"
      managed-by = "terraform"
    }
  }
}
