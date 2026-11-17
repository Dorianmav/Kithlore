package com.Kithlore.contracts;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

/** Emits JSON Schema documents from the Java records in {@link StompContracts}. */
public final class ContractSchemaGenerator {

  private static final String GENERATED_MARKER = "GENERATED - DO NOT EDIT";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private ContractSchemaGenerator() {}

  public static void main(String[] args) throws IOException {
    String outputPath = args.length == 1 ? args[0] : System.getProperty("contracts.output");
    if (outputPath == null || outputPath.isBlank()) {
      throw new IllegalArgumentException("Expected the contracts.output system property");
    }
    Path output = Path.of(outputPath);
    Files.createDirectories(output);
    for (Class<?> contract : StompContracts.class.getDeclaredClasses()) {
      if (contract.isRecord()) {
        writeSchema(contract, output);
      }
    }
  }

  private static void writeSchema(Class<?> contract, Path output) throws IOException {
    ObjectNode schema = MAPPER.createObjectNode();
    schema.put("$comment", GENERATED_MARKER);
    schema.put("$schema", "https://json-schema.org/draft/2020-12/schema");
    schema.put("$id", contract.getSimpleName() + ".schema.json");
    schema.put("title", contract.getSimpleName());
    schema.put("type", "object");
    schema.put("additionalProperties", false);
    ObjectNode properties = schema.putObject("properties");
    ArrayNode required = schema.putArray("required");
    Set<Class<?>> visiting = new LinkedHashSet<>();
    visiting.add(contract);
    for (RecordComponent component : contract.getRecordComponents()) {
      properties.set(component.getName(), schemaType(component.getGenericType(), visiting));
      if (!isOptional(component.getGenericType())) {
        required.add(component.getName());
      }
    }
    Files.writeString(
        output.resolve(contract.getSimpleName() + ".schema.json"),
        MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(schema) + "\n");
  }

  private static JsonNode schemaType(Type type, Set<Class<?>> visiting) {
    if (type instanceof ParameterizedType parameterized) {
      Class<?> raw = (Class<?>) parameterized.getRawType();
      if (Iterable.class.isAssignableFrom(raw)) {
        ObjectNode array = MAPPER.createObjectNode();
        array.put("type", "array");
        array.set("items", schemaType(parameterized.getActualTypeArguments()[0], visiting));
        return array;
      }
      if (raw == java.util.Optional.class) {
        return schemaType(parameterized.getActualTypeArguments()[0], visiting);
      }
      return schemaType(raw, visiting);
    }
    if (!(type instanceof Class<?> javaType)) {
      return MAPPER.createObjectNode();
    }
    if (javaType.isRecord()) {
      ObjectNode object = MAPPER.createObjectNode();
      object.put("type", "object");
      object.put("additionalProperties", false);
      ObjectNode properties = object.putObject("properties");
      ArrayNode required = object.putArray("required");
      if (!visiting.contains(javaType)) {
        Set<Class<?>> nested = new LinkedHashSet<>(visiting);
        nested.add(javaType);
        for (RecordComponent component : javaType.getRecordComponents()) {
          properties.set(component.getName(), schemaType(component.getGenericType(), nested));
          if (!isOptional(component.getGenericType())) {
            required.add(component.getName());
          }
        }
      }
      return object;
    }
    ObjectNode schema = MAPPER.createObjectNode();
    if (javaType == String.class || javaType == char.class || javaType == Character.class) {
      schema.put("type", "string");
    } else if (javaType == boolean.class || javaType == Boolean.class) {
      schema.put("type", "boolean");
    } else if (Number.class.isAssignableFrom(javaType)
        || javaType.isPrimitive() && javaType != boolean.class && javaType != char.class) {
      schema.put(
          "type", javaType == float.class || javaType == double.class ? "number" : "integer");
    } else if (javaType.isEnum()) {
      schema.put("type", "string");
      ArrayNode values = schema.putArray("enum");
      for (Object value : javaType.getEnumConstants()) {
        values.add(value.toString());
      }
    } else if (JsonNode.class.isAssignableFrom(javaType) || javaType == Object.class) {
      return schema;
    } else if (javaType == java.time.Instant.class) {
      schema.put("type", "string");
      schema.put("format", "date-time");
    }
    return schema;
  }

  private static boolean isOptional(Type type) {
    return type instanceof ParameterizedType parameterized
        && parameterized.getRawType() == java.util.Optional.class;
  }
}
