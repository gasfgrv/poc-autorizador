{
  "Comment": "Workflow de Autorização com SQS e Lambda no LocalStack",
  "StartAt": "SQS SendMessage",
  "States": {
    "SQS SendMessage": {
      "Type": "Task",
      "Resource": "arn:aws:states:::sqs:sendMessage.waitForTaskToken",
      "Arguments": {
        "QueueUrl": "${sqs_queue_url}",
        "MessageBody": "{% $string($merge([$states.input, {'taskToken': $states.context.Task.Token, 'executionArn': $states.context.Execution.Id}])) %}"
      },
      "Catch": [
        {
          "ErrorEquals": [
            "States.ALL"
          ],
          "Comment": "erro ao publicar no SQS",
          "Next": "Falha"
        }
      ],
      "Next": "Lambda Invoke"
    },
    "Lambda Invoke": {
      "Type": "Task",
      "Resource": "arn:aws:states:::lambda:invoke",
      "Output": "{% $states.result.Payload %}",
      "Arguments": {
        "Payload": "{% $states.input %}",
        "FunctionName": "${lambda_function_arn}"
      },
      "Retry": [
        {
          "ErrorEquals": [
            "Lambda.ServiceException",
            "Lambda.AWSLambdaException",
            "Lambda.SdkClientException",
            "Lambda.TooManyRequestsException"
          ],
          "IntervalSeconds": 1,
          "MaxAttempts": 3,
          "BackoffRate": 2,
          "JitterStrategy": "FULL"
        }
      ],
      "End": true,
      "Catch": [
        {
          "ErrorEquals": [
            "States.ALL"
          ],
          "Next": "Falha",
          "Comment": "Erro na execução da Lambda"
        }
      ]
    },
    "Falha": {
      "Type": "Fail"
    }
  },
  "QueryLanguage": "JSONata"
}
