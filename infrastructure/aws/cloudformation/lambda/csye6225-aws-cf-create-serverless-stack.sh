
#!/bin/bash
echo "Creating Stack"
stack_name=$1
topicName="password_reset"
domain=$(aws route53 list-hosted-zones --query "HostedZones[*].[Name]" --output text)

domain=${domain%?}

echo $domain

echo $stack_name

export account_id=$(aws sts get-caller-identity --query "Account" --output text)
export region=us-east-1
export lambdaexecRole="arn:aws:iam::"$account_id":role/LambdaExecutionRole"

s3lambda=$(aws s3api list-buckets --query "Buckets[*].[Name][2]" --output text)


stackId=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-serverless.json --parameters ParameterKey=s3lambda,ParameterValue=$s3lambda ParameterKey=topicName,ParameterValue=$topicName ParameterKey=domain,ParameterValue=$domain ParameterKey=lambdaexecRole,ParameterValue=$lambdaexecRole --query [StackId] --capabilities CAPABILITY_NAMED_IAM --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stack_name
    echo "STACK CREATION COMPLETE."
fi