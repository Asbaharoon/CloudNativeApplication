#!/bin/bash
echo "Starting waf creation"
stack_name=$1

if [ -z "$stack_name" ]; then
  echo "ERROR: Stackname expected....."
  exit 1
fi

LOADBALANCER=$(aws elbv2 describe-load-balancers --query LoadBalancers[0].LoadBalancerArn --output text)

if [ -z "$LOADBALANCER" ]; then
  echo "ERROR: LoadBalancer is not available."
  exit 1
fi

echo "ELBResourceARN: $LOADBALANCER"

aws cloudformation create-stack --stack-name ${stack_name}-waf --template-body file://./../security-waf/owasp_10_base.yml --parameters ParameterKey=LOADBALANCER,ParameterValue=$LOADBALANCER
aws cloudformation wait stack-create-complete --stack-name ${stack_name}-waf
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name ${stack_name}-waf --query Stacks[0].StackId --output text)
echo $STACKDETAILS

if [ -z $STACKDETAILS ]; then
    echo 'Error occurred.Dont proceed. TERMINATED'
    exit 1
else
    aws cloudformation wait stack-create-complete --stack-name ${stack_name}-waf
    echo "STACK CREATION COMPLETE."
fi
exit 0