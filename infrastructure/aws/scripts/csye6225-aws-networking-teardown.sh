#!/bin/bash
stack_name=$1
csye_const=-csye6225-
vpc_const=vpc
ig_const=InternetGateway
route_table_const=route-table

echo 'Deleting Subnets'
subnet1_id=$(aws ec2 describe-subnets --filters Name="tag-value",Values=$stack_name$csye_const$vpc_const --query [Subnets[0].SubnetId] --output text)
subnet2_id=$(aws ec2 describe-subnets --filters Name="tag-value",Values=$stack_name$csye_const$vpc_const --query [Subnets[1].SubnetId] --output text)
subnet3_id=$(aws ec2 describe-subnets --filters Name="tag-value",Values=$stack_name$csye_const$vpc_const --query [Subnets[2].SubnetId] --output text)

if [ -z $subnet1_id ]; then
    echo 'Error fetching SUBNET 1 ID' $subnet1_id
    if [ -z $subnet2_id ]; then
        echo 'Error fetching SUBNET 2 ID' $subnet2_id
        if [ -z $subnet3_id ]; then
            echo 'Error fetching SUBNET 3 ID' $subnet3_id
        fi
    fi
else
    aws ec2 delete-subnet --subnet-id $subnet1_id
    echo 'DELETED SUBNET 1' $subnet1_id
    aws ec2 delete-subnet --subnet-id $subnet2_id
    echo 'DELETED SUBNET 2' $subnet1_id
    aws ec2 delete-subnet --subnet-id $subnet3_id
    echo 'DELETED SUBNET 3' $subnet1_id
    echo 'ALL 3 SUBNETS DELETED SUCCESSULLY'
    echo 'Deleting RouteTables'
    route_table_id=$(aws ec2 describe-route-tables --filters Name="tag-value",Values=$stack_name$csye_const$route_table_const --query [RouteTables[0].RouteTableId] --output text)
    if [ -z $route_table_id ]; then
        echo 'Error fetching ROUTE TABLE ID' $route_table_id    
    else
        aws ec2 delete-route --route-table-id $route_table_id --destination-cidr-block 0.0.0.0/0
        aws ec2 delete-route-table --route-table-id $route_table_id
            echo 'DELETED ROUTE TABLE' $route_table_id  
    fi              
    echo 'Deleting Internet Gateway'
    vpc_id=$(aws ec2 describe-vpcs --filters Name="tag-value",Values=$stack_name$csye_const$vpc_const --query [Vpcs[0].VpcId] --output text)
    if [ -z $vpc_id ]; then
        echo 'Error fetching VPC ID' $vpc_id
    else
        ig_id=$(aws ec2 describe-internet-gateways --filters Name="tag-value",Values=$stack_name$csye_const$ig_const --query [InternetGateways[0].InternetGatewayId] --output text)
        if [ -z $ig_id ]; then
                echo 'Error fetching Internet Gateway ID' $ig_id
        else
            aws ec2 detach-internet-gateway --internet-gateway-id $ig_id --vpc-id $vpc_id
            aws ec2 delete-internet-gateway --internet-gateway-id $ig_id
            echo 'DELETED INTERNET GATEWAY' $ig_id
        fi      
        echo 'Deleting vpc'
        aws ec2 delete-vpc --vpc-id $vpc_id
        echo 'DELETED VPC' $vpc_id
    fi
    
    echo 'COMPLETED STACK TERMINATION.'
fi  