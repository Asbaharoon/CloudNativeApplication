#!/bin/bash
echo "Creating CircleCI Policy Stack"
stack_name=$1

if [ -z "$stack_name" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

s3BucketName=$(aws s3api list-buckets --query "Buckets[*].Name" --output text)

account_id=$(aws sts get-caller-identity --query "Account" --output text)

region="us-east-1"

applicationName="csye6225-webapp"

stackId=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf.circleci.json --parameters ParameterKey=accountID,ParameterValue=$account_id ParameterKey=awsRegion,ParameterValue=$region ParameterKey=s3BucketName,ParameterValue=$s3BucketName ParameterKey=applicationName,ParameterValue=$applicationName  --query [StackId] --capabilities CAPABILITY_NAMED_IAM --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stack_name
    echo "STACK CREATION COMPLETE."
fi
