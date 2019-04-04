until [ -f /opt/tomcat/samplefile ]; do 
	echo "waiting for ec2 instance ..."
	sleep 5
done
echo "ec2 instance is running deploying code"
