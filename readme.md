This reproducer is based on https://quarkus.io/guides/aws-lambda

Checkout this repo and build native using:
`quarkus build --native --no-tests -Dquarkus.native.container-build=true`

Create an execution role like described in https://quarkus.io/guides/aws-lambda#create-an-execution-role and use it.

`export LAMBDA_ROLE_ARN="your created role arn"`

Add AppConfig layer and `DISABLE_SIGNAL_HANDLERS` env var to `target/manage.sh`.
```
    --environment 'Variables={DISABLE_SIGNAL_HANDLERS=true}' \
    --layers "arn:aws:lambda:eu-central-1:066940009817:layer:AWS-AppConfig-Extension-Arm64:132" \
```

For x86-64 platform use:
`arn:aws:lambda:eu-central-1:066940009817:layer:AWS-AppConfig-Extension:189`

Complete example of `cmd_create()` in  `target/manage.sh`:
```
function cmd_create() {
  echo Creating function
  set -x
  aws lambda create-function \
    --function-name ${FUNCTION_NAME} \
    --zip-file ${ZIP_FILE} \
    --handler ${HANDLER} \
    --runtime ${RUNTIME} \
    --role ${LAMBDA_ROLE_ARN} \
    --timeout 15 \
    --memory-size 1024 \
    --architectures "${ARCHITECTURE}" \
    --environment 'Variables={DISABLE_SIGNAL_HANDLERS=true}' \
    --layers "arn:aws:lambda:eu-central-1:066940009817:layer:AWS-AppConfig-Extension-Arm64:132" \
    ${LAMBDA_META}
# Enable and move this param above ${LAMBDA_META}, if using AWS X-Ray
#    --tracing-config Mode=Active \
}
```

The memory size is important! Only using 256M did not trigger the error. So memory is set with `--memory-size 1024`.
Assumption: Shutdown duration was too long.

Now run the lambda with the example payload:
```
{
  "name": "Bill",
  "greeting": "hello"
}
```

Check cloudwatch logs of the lambda execution. The execution logs an error because the requested AppConfig resource is not available. This error can be ignored.

Now wait for the lambda to be terminated and check logs again. The logs should now show the following error:
```
(Lambda Thread (NORMAL)) Error running lambda (NORMAL) [Error Occurred After Shutdown]: java.net.SocketException: Socket closed
```