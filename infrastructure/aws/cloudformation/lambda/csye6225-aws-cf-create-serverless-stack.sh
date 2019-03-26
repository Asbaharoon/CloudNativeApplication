
#!/bin/bash
echo "Creating Stack"
stack_name=$1
topicName=$2
domain=$(aws route53 list-hosted-zones --query "HostedZones[*].[Name]" --output text)

echo $stack_name

export account_id=$(aws sts get-caller-identity --query "Account" --output text)
export region=us-east-1
export lambaexecRole="arn:aws:iam::"$account_id":role/LambdaExecutionRole"

s3lamba=$(aws s3api list-buckets --query "Buckets[*].[Name][2]" --output text)


stackId=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-serverless.json --parameters ParameterKey=s3lamba,ParameterValue=$s3lamba ParameterKey=topicName,ParameterValue=$topicName ParameterKey=domain,ParameterValue=$domain ParameterKey=lambaexecRole,ParameterValue=$lambaexecRole --query [StackId] --capabilities CAPABILITY_NAMED_IAM --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stack_name
    echo "STACK CREATION COMPLETE."
fi