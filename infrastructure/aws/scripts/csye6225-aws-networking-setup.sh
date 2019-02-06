#!/bin/bash
PATH="/usr/local/bin:$PATH"

stack_name=$1
port_1=$2
port_2=$3
csye_const=-csye6225-
vpc_const=vpc
ig_const=InternetGateway
route_table_const=route-table

echo 'Checking Parameters'
if [ -z $stack_name ]; then
	echo 'InValid Stack Name'
	exit 1
fi

if [ -z $port_1 ]; then
	echo 'InValid Port1 Number'
	exit 1
fi

if [ -z $port_2 ]; then
	echo 'InValid port2 Name'
	exit 1
fi

echo 'Creating VPC....'

vpc_id=$(aws ec2 create-vpc --cidr-block 10.0.0.0/16 --region us-east-1 --query [Vpc.VpcId] --output text)

if [ -z $vpc_id.item[0] ]; then
    echo 'Error creating VPC. Terminating' $vpc_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'VPC CREATED SUCCESSFULLY' $vpc_id
fi 

aws ec2 create-tags --resources $vpc_id --tags "Key=Name,Value=$stack_name$csye_const$vpc_const"

echo 'Tags created for VPC'

echo 'Creating Subnets....'
subnet1_id=$(aws ec2 create-subnet --availability-zone us-east-1a --vpc-id $vpc_id --cidr-block 10.0.1.0/24 --query [Subnet.SubnetId] --output text)
subnet2_id=$(aws ec2 create-subnet --availability-zone us-east-1b --vpc-id $vpc_id --cidr-block 10.0.2.0/24 --query [Subnet.SubnetId] --output text)
subnet3_id=$(aws ec2 create-subnet --availability-zone us-east-1c --vpc-id $vpc_id --cidr-block 10.0.3.0/24 --query [Subnet.SubnetId] --output text)

if [ -z $subnet1_id ]; then
    echo 'Error creating SUBNET 1. Terminating' $subnet1_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'SUBNET 1 CREATED SUCCESSFULLY' $subnet1_id
fi

if [ -z $subnet2_id ]; then
    echo 'Error creating SUBNET 2. Terminating' $subnet2_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'SUBNET 2 CREATED SUCCESSFULLY' $subnet2_id
fi

if [ -z $subnet3_id ]; then
    echo 'Error creating SUBNET 3. Terminating' $subnet3_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'SUBNET 3 CREATED SUCCESSFULLY' $subnet3_id
fi

aws ec2 create-tags --resources $subnet1_id --tags "Key=Name,Value=$stack_name$csye_const$vpc_const"
aws ec2 create-tags --resources $subnet2_id --tags "Key=Name,Value=$stack_name$csye_const$vpc_const"
aws ec2 create-tags --resources $subnet3_id --tags "Key=Name,Value=$stack_name$csye_const$vpc_const"
echo 'Tags Created for subnets'

echo 'Creating Internet Gateway....'

ig_id=$(aws ec2 create-internet-gateway --query [InternetGateway.InternetGatewayId] --output text)

if [ -z $ig_id ]; then
    echo 'Error creating INTERNET GATEWAY. Terminating' $ig_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'INTERNET GATEWAY CREATED SUCCESSFULLY' $ig_id
fi 

aws ec2 create-tags --resources $ig_id --tags "Key=Name,Value=$stack_name$csye_const$ig_const"
echo 'Tag created for Internet Gateway'

echo 'Ataching IG to VPC....'
aws ec2 attach-internet-gateway --internet-gateway-id $ig_id --vpc-id $vpc_id

echo 'Creating Route Table....'
route_table_id=$(aws ec2 create-route-table --vpc-id $vpc_id --query [RouteTable.RouteTableId] --output text)

if [ -z $route_table_id ]; then
    echo 'Error creating ROUTE TABLE. Terminating' $route_table_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'ROUTE TABLE CREATED SUCCESSFULLY' $route_table_id
    aws ec2 associate-route-table --route-table-id $route_table_id --subnet-id $subnet1_id
    echo 'SUBNET 1 ATTACHED TO ROUTE TABLE'
    aws ec2 associate-route-table --route-table-id $route_table_id --subnet-id $subnet2_id
    echo 'SUBNET 2 ATTACHED TO ROUTE TABLE'
    aws ec2 associate-route-table --route-table-id $route_table_id --subnet-id $subnet3_id
    echo 'SUBNET 3 ATTACHED TO ROUTE TABLE'
fi

aws ec2 create-tags --resources $route_table_id --tags "Key=Name,Value=$stack_name$csye_const$route_table_const"
echo 'Tag created for Route Table'

echo 'Creating a public route 0.0.0.0/0 ....'
aws ec2 create-route --route-table-id $route_table_id --destination-cidr-block 0.0.0.0/0 --gateway-id $ig_id
echo 'ROUTE CREATED SUCCESSFULLY'

sg_name=$(aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$vpc_id" --query "SecurityGroups[*].GroupName" --output text)

sg_id=$(aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$vpc_id" --query "SecurityGroups[*].GroupId" --output text)

if [ -z $sg_id ]; then
    echo 'No Default Security GroupId' $sg_id
    bash ./csye6225-aws-networking-teardown.sh
else
    echo 'Default Security GroupId' $sg_id
fi


sg_info=$(aws ec2 describe-security-groups --filters "Name=vpc-id,Values=$vpc_id" --query "SecurityGroups")


echo 'Revoking default Inbound & Outbound Rules....'
revoke_in=$(aws ec2 revoke-security-group-ingress --group-id $sg_id --source-group $sg_id --protocol all --port all)  
revoke_out=$(aws ec2 revoke-security-group-egress --group-id $sg_id --ip-permissions '{ "IpProtocol": "-1", "PrefixListIds": [], "IpRanges": [ { "CidrIp": "0.0.0.0/0" } ], "UserIdGroupPairs": [], "Ipv6Ranges": [] }')



echo 'Creating Inbound Rules....'
create_in22=$(aws ec2 authorize-security-group-ingress --group-id $sg_id --protocol tcp --port $port_1 --cidr 0.0.0.0/0 --output text)
create_in80=$(aws ec2 authorize-security-group-ingress --group-id $sg_id --protocol tcp --port $port_2 --cidr 0.0.0.0/0 --output text)


echo 'Creating Outbound Rules....'
create_out22=$(aws ec2 authorize-security-group-egress --group-id $sg_id --protocol tcp --port $port_1 --cidr 0.0.0.0/0 --output text)
create_out80=$(aws ec2 authorize-security-group-egress --group-id $sg_id --protocol tcp --port $port_2 --cidr 0.0.0.0/0 --output text)

echo 'FINISHEDCOMPLETED STACK CREATION'

exit 0