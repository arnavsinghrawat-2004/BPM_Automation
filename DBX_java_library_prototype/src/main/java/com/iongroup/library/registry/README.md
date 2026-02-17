# Operation Registry

The Operation Registry is a metadata system that describes all reusable workflow operations (delegates) in the library. It enables frontends (like React Flow) to dynamically discover and display available workflow nodes without hardcoding them.

## Architecture

### Core Classes

**OperationDescriptor**
- Metadata about a single operation
- Fields: `id`, `description`, `inputs`, `outputs`, `delegateClass`, `category`
- Serializable to JSON for REST endpoints

**OperationRegistry** (interface)
- Contract for registry implementations
- Methods: `register()`, `getAllOperations()`, `getOperation()`, `getOperationsByCategory()`, `describeAll()`, `getOperationCount()`

**DefaultOperationRegistry**
- Complete, thread-safe implementation
- Uses `LinkedHashMap` to maintain insertion order
- Converts to JSON via Jackson `ObjectMapper`

**OperationRegistryFactory**
- Static factory for the singleton registry
- **Key extension point**: Add new operations by calling `registerXxxOperation()` and adding it to `createRegistry()`
- Pre-registers all 12 existing operations (8 loan + 4 card)

**OperationMetadataService**
- High-level service for querying the registry
- Used by REST controllers to expose metadata to frontends
- Methods: `getAllOperations()`, `getOperationsByCategory()`, `getOperationsAsJson()`, `getStats()`

## Usage

### Get the Singleton Registry

```java
OperationRegistry registry = OperationRegistryFactory.getRegistry();
```

### Query All Operations

```java
List<OperationDescriptor> all = registry.getAllOperations();
all.forEach(op -> System.out.println(op.getId() + ": " + op.getDescription()));
```

### Filter by Category

```java
List<OperationDescriptor> cardOps = registry.getOperationsByCategory("card");
```

### Get Single Operation

```java
registry.getOperation("CreateLoanOffer").ifPresent(op -> {
    System.out.println("Inputs: " + op.getInputs());
    System.out.println("Outputs: " + op.getOutputs());
});
```

### Export as JSON (for frontends)

```java
JsonNode json = registry.describeAll();
// Return to REST endpoint
```

## Adding New Operations

### Step 1: Create the JavaDelegate

```java
package com.iongroup.library.adapter.flowable;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;

public class ValidateDocumentTask implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) {
        // Implementation
    }
}
```

### Step 2: Register in OperationRegistryFactory

Add a private static method:

```java
private static void registerValidateDocument(OperationRegistry registry) {
    registry.register(new OperationDescriptor(
            "ValidateDocument",
            "Validate customer documents (ID, income proof, etc.)",
            Arrays.asList("customerId", "documentType"),
            Arrays.asList("validationResult"),
            "com.iongroup.library.adapter.flowable.ValidateDocumentTask",
            "common"
    ));
}
```

Then call it in `createRegistry()`:

```java
public static OperationRegistry createRegistry() {
    OperationRegistry registry = new DefaultOperationRegistry();
    
    // ... existing registrations ...
    
    registerValidateDocument(registry);  // Add this
    
    return registry;
}
```

That's it! The operation is now discoverable by all frontends.

## REST Endpoints (Spring Boot)

If using Spring Boot, uncomment the `OperationRegistryController.java` class and define a Bean:

```java
@Configuration
public class RegistryConfig {
    @Bean
    public OperationRegistry operationRegistry() {
        return OperationRegistryFactory.getRegistry();
    }

    @Bean
    public OperationMetadataService operationMetadataService(OperationRegistry registry) {
        return new OperationMetadataService(registry);
    }
}
```

Then access the endpoints:

- `GET /api/operations` — All operations
- `GET /api/operations/by-category/{category}` — Filter by category
- `GET /api/operations/{operationId}` — Single operation
- `GET /api/operations/metadata` — As JSON
- `GET /api/operations/stats` — Registry statistics

## Sample Response (JSON)

```json
[
  {
    "id": "CreateLoanOffer",
    "description": "Generate loan terms including principal amount, interest rate, tenure, and EMI calculations.",
    "inputs": ["customerProfile", "loanPricingPolicy"],
    "outputs": ["loanOffer"],
    "delegateClass": "com.iongroup.library.adapter.flowable.CreateLoanOfferTask",
    "category": "loan"
  },
  {
    "id": "IssueCreditCard",
    "description": "Issue and provision the credit card to the customer after approval.",
    "inputs": ["customerProfile", "eligibilityStatus"],
    "outputs": ["cardIssued"],
    "delegateClass": "com.iongroup.library.adapter.flowable.IssueCreditCardTask",
    "category": "card"
  }
]
```

## Running the Sample

To see the registry in action:

```bash
cd /home/arnavsinghrawat/DBX_java_library_prototype
mvn clean compile
mvn exec:java -Dexec.mainClass="com.iongroup.library.registry.RegistrySample"
```

## Frontend Integration (React Flow Example)

Once you fetch the operations from the REST endpoint:

```javascript
const [operations, setOperations] = useState([]);

useEffect(() => {
  fetch('/api/operations')
    .then(res => res.json())
    .then(data => {
      // Convert to React Flow nodes
      const nodes = data.map(op => ({
        id: op.id,
        data: { label: op.id, description: op.description },
        position: { x: 0, y: 0 }
      }));
      setNodes(nodes);
    });
}, []);
```

## Key Design Principles

1. **Extensibility**: Add operations by adding a method and one line in `createRegistry()`.
2. **No Boilerplate**: The registry factory handles all the plumbing.
3. **Metadata-Driven**: Frontends guide the UI entirely from metadata, not hardcoded definitions.
4. **Serializable**: JSON output enables REST endpoints and frontend consumption.
5. **Thread-Safe**: Registry implementation uses synchronized methods for concurrent access.
6. **Categorized**: Operations grouped by workflow type (loan, card, common) for UI filtering.

## Extending Beyond the Factory

For complex scenarios, you can also:

1. **Create a custom registry implementation** by implementing `OperationRegistry`:

```java
public class CustomRegistry implements OperationRegistry {
    // Your implementation
}
```

2. **Dynamically register operations at runtime**:

```java
registry.register(new OperationDescriptor(...));
```

3. **Load operations from external config** (SQL, JSON files, etc.):

```java
List<OperationDescriptor> ops = loadFromDatabase();
ops.forEach(registry::register);
```

---

For questions or samples, see `RegistrySample.java`.
