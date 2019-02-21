#!/bin/bash
echo "Creating Stack"
stack_name=$1
keyTagValue=$2
ec2InstanceTagVal=$3
DBUSER="csye6225master"
DBPWD="csye6225password"

if [ -z "$stack_name" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

if [ -z "$keyTagValue" ]; then
  echo "ERROR: key pair name expected....."
  exit 1
fi

if [ -z "$ec2InstanceTagVal" ]; then
  echo "ERROR: ec2 Instance tag value expected....."
  exit 1
fi

amiId=$(aws ec2 describe-images --filters "Name=tag:Name,Values=centos_assignment4" --query Images[*].ImageId --output text)

echo $amiId

webSecurityGroupTagValue=csye6225-webapp
dbSecurityGroupTagValue=csye6225-rds

stackId=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-application.json --parameters ParameterKey=webSecurityGroupTag,ParameterValue=$webSecurityGroupTagValue ParameterKey=dbSecurityGroupTag,ParameterValue=$dbSecurityGroupTagValue ParameterKey=keyTag,ParameterValue=$keyTagValue ParameterKey=amiId,ParameterValue=$amiId ParameterKey=ec2InstanceTag,ParameterValue=$ec2InstanceTagVal ParameterKey=DBUSER,ParameterValue=$DBUSER ParameterKey=DBPWD,ParameterValue=$DBPWD  --query [StackId] --capabilities CAPABILITY_NAMED_IAM --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
else
    aws cloudformation wait stack-create-complete --stack-name $stack_name
    echo "STACK CREATION COMPLETE."
fi
