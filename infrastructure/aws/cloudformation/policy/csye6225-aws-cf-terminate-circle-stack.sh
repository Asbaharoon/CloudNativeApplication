#!/bin/bash
echo "Deleting Stack"
stack_name=$1
# keyTagValue=$2
# ec2InstanceTagValue=$3
echo $stack_name

if [ -z "$stack_name" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

aws cloudformation delete-stack --stack-name $stack_name

stackid=`aws cloudformation describe-stacks --stack-name $stack_name --query 'Stacks[*][StackId]' --output text`
stackstatus=""
if [ -z "$stackid" ]; then
  exit 1
fi

until [ "$stackstatus" = 'DELETE_COMPLETE' ]; do
  stackstatus=`aws cloudformation list-stacks --query 'StackSummaries[?StackId==\`'$stackid'\`][StackStatus]' --output text`


done
if [ "$stackstatus" = 'DELETE_COMPLETE' ]; then
  echo "$StackName terminated sucessfully!!"
fi