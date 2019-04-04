LOADBALANCER=$(aws elbv2 describe-load-balancers --query LoadBalancers[0].LoadBalancerArn --output text)

echo "ELBResourceARN: $LOADBALANCER"

echo "Starting waf creation"
aws cloudformation create-stack --stack-name $1-waf --template-body file://./owasp_10_base.yml --parameters ParameterKey=LOADBALANCER,ParameterValue=$LOADBALANCER
aws cloudformation wait stack-create-complete --stack-name $1-waf
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name $1-waf --query Stacks[0].StackId --output text)