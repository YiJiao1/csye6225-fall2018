#!/bin/bash


display_usage()
{
echo "Usage:$0 [StackName] [SSH_Key] [DB_UserName] [DB_Password]"
}

if [ $# -lt 4 ];then
	display_usage
	exit 1
fi

stackID=$(aws cloudformation create-stack --template-body file://csye6225-cf-application.yml --stack-name $1 --parameters ParameterKey=KeyName,ParameterValue=$2 ParameterKey=DBuserName,ParameterValue=$3 ParameterKey=DBpassword,ParameterValue=$4| grep StackId)

if [ -z "$stackID" ];then 
	echo failed
	exit 1
fi


echo " 
Creating VPCSecurityGroup........
Creating EC2Instance.....
Creating DBSecurityGroup........
Creating DBSubnetGroup..........
Creating RDSInstance............
"

status=$(aws cloudformation describe-stacks --stack-name  $1| grep StackStatus| cut -d'"' -f4)


while [ "$status" != "CREATE_COMPLETE" ]
do

       echo "StackStatus: $status"

       if [ "$status" == "ROLLBACK_COMPLETE" ];then 
	       echo "$1 Stack_Create_Uncomplete !!"
	       exit 1
       fi

       sleep 3
       status=$(aws cloudformation describe-stacks --stack-name  $1 2>&1 | grep StackStatus| cut -d'"' -f4)

done

echo "$1 Stack_Create_Complete !!"

exit 0
