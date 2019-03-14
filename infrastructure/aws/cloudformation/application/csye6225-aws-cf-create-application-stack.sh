#!/bin/bash
echo "Creating Stack"
stack_name=$1
ec2InstanceTagVal=$2
DBUSER="csye6225master"
DBPWD="csye6225password"

if [ -z "$stack_name" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

if [ -z "$ec2InstanceTagVal" ]; then
  echo "ERROR: ec2 Instance tag value expected....."
  exit 1
fi

keyTagValue=$(aws ec2 describe-key-pairs --query "KeyPairs[*].[KeyName]" --output text)

amiId=$(aws ec2 describe-images --filters "Name=tag:Name,Values=centos_assignment4" --query Images[*].ImageId --output text)

echo $amiId

account_id=$(aws sts get-caller-identity --query "Account" --output text)

region="us-east-1"

s3codedeploy=$(aws s3api list-buckets --query "Buckets[*].[Name][0]" --output text)

s3attachments=$(aws s3api list-buckets --query "Buckets[*].[Name][1]" --output text)

applicationName="csye6225-webapp"

webSecurityGroupTagValue=csye6225-webapp
dbSecurityGroupTagValue=csye6225-rds

stackId=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-application.json --parameters ParameterKey=webSecurityGroupTag,ParameterValue=$webSecurityGroupTagValue ParameterKey=dbSecurityGroupTag,ParameterValue=$dbSecurityGroupTagValue ParameterKey=keyTag,ParameterValue=$keyTagValue ParameterKey=amiId,ParameterValue=$amiId ParameterKey=ec2InstanceTag,ParameterValue=$ec2InstanceTagVal ParameterKey=DBUSER,ParameterValue=$DBUSER ParameterKey=DBPWD,ParameterValue=$DBPWD ParameterKey=accountID,ParameterValue=$account_id ParameterKey=awsRegion,ParameterValue=$region ParameterKey=s3CodeDeploy,ParameterValue=$s3codedeploy ParameterKey=s3Attachment,ParameterValue=$s3attachments ParameterKey=applicationName,ParameterValue=$applicationName  --query [StackId] --capabilities CAPABILITY_NAMED_IAM --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stack_name
    echo "STACK CREATION COMPLETE."
fi
