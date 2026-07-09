# Tabelas do DynamoDB
resource "aws_dynamodb_table" "tb_autorizador_controle" {
  name         = "tb-autorizador-controle"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id_exec"

  attribute {
    name = "id_exec"
    type = "S"
  }
}

resource "aws_dynamodb_table" "tb_autorizador_resposta" {
  name         = "tb-autorizador-resposta"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id_transacao"

  attribute {
    name = "id_transacao"
    type = "S"
  }
}

# Filas do SQS
resource "aws_sqs_queue" "sqs_autorizador_eventos" {
  name = "sqs-autorizador-eventos"
}

# Role do Lambda
resource "aws_iam_role" "execution_role" {
  name = "execution-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = [
            "lambda.amazonaws.com",
            "states.amazonaws.com"
          ]
        }
      },
    ]
  })
}

# Lambda Function
resource "aws_lambda_function" "lambda_response" {
  function_name = "lambda-response"
  role          = aws_iam_role.execution_role.arn
  handler       = "index.handler"
  runtime       = "python3.14"

  filename         = data.archive_file.lambda_zip.output_path
  source_code_hash = data.archive_file.lambda_zip.output_base64sha256
}

# Step Function
resource "aws_sfn_state_machine" "autorizador_workflow" {
  name     = "autorizador-workflow"
  role_arn = aws_iam_role.execution_role.arn

  definition = templatefile("${path.module}/step_function_definition.json.tpl", {
    sqs_queue_url       = aws_sqs_queue.sqs_autorizador_eventos.url
    lambda_function_arn = aws_lambda_function.lambda_response.arn
  })

  depends_on = [
    aws_lambda_function.lambda_response,
    aws_sqs_queue.sqs_autorizador_eventos
  ]
}
