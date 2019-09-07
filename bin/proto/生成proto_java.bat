
for %%i in (proto\*.proto) do (

	protoc --proto_path=proto --java_out=..\..\common\src\main\java\ %%i

)
pause