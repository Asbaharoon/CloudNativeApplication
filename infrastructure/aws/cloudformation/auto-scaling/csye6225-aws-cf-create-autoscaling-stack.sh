#!/bin/bash
echo "Creating Stack"
stack_name=$1
autoScaleGroupName="csye-6225-asg"
DBUSER="csye6225master"
DBPWD="csye6225password"
topicName="password_reset"
elb_security_group="elb-sg"
region="us-east-1"
applicationName="csye6225-webapp"
webSecurityGroupTagValue=csye6225-webapp
dbSecurityGroupTagValue=csye6225-rds


if [ -z "$stack_name" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

if [ -z "$autoScaleGroupName" ]; then
  echo "ERROR: ec2 Instance tag value expected....."
  exit 1
fi

CERTIFICATE_ARN1=$(aws acm list-certificates --query "CertificateSummaryList[0].CertificateArn" --output text)

if [ -z "$CERTIFICATE_ARN1" ]; then
  echo "ERROR: Certificate1 is not available....."
  exit 1
fi

echo $CERTIFICATE_ARN1

CERTIFICATE_ARN2=$(aws acm list-certificates --query "CertificateSummaryList[1].CertificateArn" --output text)

if [ -z "$CERTIFICATE_ARN2" ]; then
  echo "ERROR: Certificate2 is not available....."
  exit 1
fi

echo $CERTIFICATE_ARN2

keyTagValue=$(aws ec2 describe-key-pairs --query "KeyPairs[*].[KeyName]" --output text)

if [ -z "$keyTagValue" ]; then
  echo "ERROR: EC2 keyValue pair is not available....."
  exit 1
fi

echo $keyTagValue

amiId=$(aws ec2 describe-images --filters "Name=tag:Name,Values=centos_assignment4" --query Images[0].ImageId --output text)

if [ -z "$amiId" ]; then
  echo "ERROR: AMI is not available....."
  exit 1
fi

account_id=$(aws sts get-caller-identity --query "Account" --output text)

echo $account_id

s3codedeploy=$(aws s3api list-buckets --query "Buckets[*].[Name][0]" --output text)

echo $s3codedeploy

s3attachments=$(aws s3api list-buckets --query "Buckets[*].[Name][1]" --output text)

echo $s3attachments


# export lambdaexecRole="arn:aws:iam::"$account_id":role/LambdaExecutionRole"

domainName=$(aws route53 list-hosted-zones --query "HostedZones[*].[Name]" --output text)

echo $domainName

newdomainName=nowaf.${domainName}

echo $newdomainName

stackId=$(aws cloudformation create-stack --stack-name $stack_name --template-body file://csye6225-cf-autoscaling.json --parameters ParameterKey=webSecurityGroupTag,ParameterValue=$webSecurityGroupTagValue ParameterKey=dbSecurityGroupTag,ParameterValue=$dbSecurityGroupTagValue ParameterKey=keyTag,ParameterValue=$keyTagValue ParameterKey=amiId,ParameterValue=$amiId ParameterKey=elbSecurityGroupNameTag,ParameterValue=$elbSecurityGroupNameTag ParameterKey=DBUSER,ParameterValue=$DBUSER ParameterKey=DBPWD,ParameterValue=$DBPWD ParameterKey=accountID,ParameterValue=$account_id ParameterKey=awsRegion,ParameterValue=$region ParameterKey=s3CodeDeploy,ParameterValue=$s3codedeploy ParameterKey=s3Attachment,ParameterValue=$s3attachments ParameterKey=applicationName,ParameterValue=$applicationName ParameterKey=domainName,ParameterValue=$domainName ParameterKey=CertificateArn1,ParameterValue=$CERTIFICATE_ARN1 ParameterKey=topicName,ParameterValue=$topicName ParameterKey=hostedZoneName,ParameterValue=$domainName ParameterKey=autoScaleGroupName,ParameterValue=$autoScaleGroupName ParameterKey=elbSecurityGroupNameTag,ParameterValue=$elb_security_group ParameterKey=newdomainName,ParameterValue=$newdomainName ParameterKey=CertificateArn2,ParameterValue=$CERTIFICATE_ARN2 --query [StackId] --capabilities CAPABILITY_NAMED_IAM --output text)

echo $stackId

if [ -z $stackId ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
    exit 1
else
    aws cloudformation wait stack-create-complete --stack-name $stack_name
    echo "STACK CREATION COMPLETE."
fi
exit 0
