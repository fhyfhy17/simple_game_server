for i in `ls proto/*.proto` 
do
echo $i
protoc --proto_path=proto --java_out=../../common/src/main/java $i
done
