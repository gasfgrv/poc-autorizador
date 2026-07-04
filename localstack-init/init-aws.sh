#!/bin/bash
echo "=== Starting AWS LocalStack Resource Initialization ==="

# 1. Tabelas do DynamoDB
echo "Creating DynamoDB table: tb-autorizador-controle"
awslocal dynamodb create-table \
    --table-name tb-autorizador-controle \
    --attribute-definitions AttributeName=id_exec,AttributeType=S \
    --key-schema AttributeName=id_exec,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

echo "Creating DynamoDB table: tb-autorizador-resposta"
awslocal dynamodb create-table \
    --table-name tb-autorizador-resposta \
    --attribute-definitions AttributeName=id_transacao,AttributeType=S \
    --key-schema AttributeName=id_transacao,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST

# 2. Fila SQS
echo "Creating SQS queue: sqs-autorizador-eventos"
awslocal sqs create-queue --queue-name sqs-autorizador-eventos

# 3. Criação de IAM Role (necessário por consistência de parâmetros do AWS CLI)
echo "Creating IAM Role for services..."
awslocal iam create-role \
    --role-name execution-role \
    --assume-role-policy-document '{
        "Version": "2012-10-17",
        "Statement": [
            {
                "Effect": "Allow",
                "Principal": {
                    "Service": ["lambda.amazonaws.com", "states.amazonaws.com"]
                },
                "Action": "sts:AssumeRole"
            }
        ]
    }'

# 4. Criação da Lambda Function
echo "Packaging lambda-response using Python..."
python3 -c "
import zipfile, os
zipf = zipfile.ZipFile('/tmp/lambda.zip', 'w', zipfile.ZIP_DEFLATED)
for r, d, fs in os.walk('/opt/code/lambda'):
    for f in fs:
        path = os.path.join(r, f)
        arcname = os.path.relpath(path, '/opt/code/lambda')
        zipf.write(path, arcname)
zipf.close()
"

echo "Creating Lambda: lambda-response"
awslocal lambda create-function \
    --function-name lambda-response \
    --runtime python3.14 \
    --role arn:aws:iam::000000000000:role/execution-role \
    --handler index.handler \
    --zip-file fileb:///tmp/lambda.zip

# 5. Criação do Step Functions State Machine
echo "Creating StepFunction: autorizador-workflow"
awslocal stepfunctions create-state-machine \
    --name autorizador-workflow \
    --definition file:///opt/code/stepfunction/definition.json \
    --role-arn arn:aws:iam::000000000000:role/execution-role

echo "=== AWS LocalStack Resource Initialization Completed ==="
