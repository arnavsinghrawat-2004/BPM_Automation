package com.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Converts a BPMN 2.0 XML file to a Flowable Modeler JSON file.
 *
 * Usage:
 *   java -jar target/bpmn2json-1.0.0.jar path/to/process.bpmn20.xml [path/to/output.json]
 *
 * If the output path is omitted, it writes: <inputBaseName>.json
 */
public class Bpmn2JsonConverter {

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length > 2) {
            System.err.println("Usage: java -jar target/bpmn2json-1.0.0.jar <input.bpmn20.xml> [output.json]");
            System.exit(2);
        }

        Path in = Path.of(args[0]);
        if (!Files.exists(in)) {
            System.err.println("Input file does not exist: " + in);
            System.exit(2);
        }

        Path out = (args.length == 2)
                ? Path.of(args[1])
                : in.resolveSibling(stripExtension(in.getFileName().toString()) + ".json");

        // 1) Read BPMN XML -> BpmnModel
        BpmnModel bpmnModel = readBpmnModel(in);

        if (bpmnModel == null || bpmnModel.getProcesses() == null || bpmnModel.getProcesses().isEmpty()) {
            System.err.println("Parsed BPMN has no processes. Check that your XML is a valid BPMN 2.0 file.");
            System.exit(3);
        }

        // 2) Convert BpmnModel -> Flowable Modeler JSON
        BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
        ObjectNode modelJson = jsonConverter.convertToJson(bpmnModel);

        // 3) Pretty-print JSON
        ObjectMapper mapper = new ObjectMapper();
        byte[] jsonBytes = mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(modelJson);

        // 4) Write output
        if (out.getParent() != null) Files.createDirectories(out.getParent());
        Files.write(out, jsonBytes);

        System.out.println("Success!");
        System.out.println("Input BPMN: " + in.toAbsolutePath());
        System.out.println("Output JSON: " + out.toAbsolutePath());
    }

    private static BpmnModel readBpmnModel(Path xmlPath) throws Exception {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        try (InputStream is = Files.newInputStream(xmlPath)) {
            XMLStreamReader xtr = xif.createXMLStreamReader(is, StandardCharsets.UTF_8.name());
            BpmnXMLConverter xmlConverter = new BpmnXMLConverter();
            return xmlConverter.convertToBpmnModel(xtr);
        }
    }

    private static String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        return (dot >= 0 ? name.substring(0, dot) : name);
    }
}