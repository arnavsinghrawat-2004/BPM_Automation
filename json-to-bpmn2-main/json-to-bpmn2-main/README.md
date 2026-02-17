Create a json file that is ready to be converted into bpmn20.xml

Next run the following commands: 
mvn -U clean package
java -jar target/json2bpmn2-1.0.0.jar <your-json-file.json>
mvn -q exec:java "-Dexec.args=<your_bpmn20_file_name.bpmn20.xml>"