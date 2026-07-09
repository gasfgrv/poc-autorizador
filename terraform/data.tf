# Package the Lambda function code
data "archive_file" "lambda_zip" {
  type        = "zip"
  source_dir  = "${path.module}/../lambda"
  output_path = "${path.module}/lambda/function.zip"

  excludes = [
    ".idea",
    ".pytest_cache",
    ".venv",
    "__pycache__",
    "tests",
  ]
}
