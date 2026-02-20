# BPM Workflow Designer – Frontend

A visual, drag-and-drop Business Process Modeling (BPM) application built with **React** and **React Flow**. The designer allows users to build, configure, execute, and monitor workflows through an intuitive canvas interface, while all BPMN conversion and execution logic are handled by backend services.

---

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Supported Node Types](#supported-node-types)
- [Delegation-Based Task Configuration](#delegation-based-task-configuration)
- [UIJson Structure](#uijson-structure)
- [Execution Flow](#execution-flow)
- [Execution Page](#execution-page)
- [Design Principles](#design-principles)
- [Tech Stack](#tech-stack)

---

## Overview

The BPM Workflow Designer enables users to:

- Design customizable workflows using atomic tasks
- Dynamically configure tasks using backend delegations
- Persist workflows as structured JSON (UIJson)
- Execute workflows via backend Flowable integration
- Monitor real-time process execution status

---

## Architecture

### Frontend Responsibilities

- Render interactive workflow canvas
- Provide draggable node palette
- Fetch delegation metadata dynamically
- Manage node configuration panel
- Generate structured `UIJson`
- Trigger workflow execution
- Display execution progress visually

### Backend Responsibilities

The backend uses the internal library:

- `com.iongroup.library.registry`
- `com.iongroup.library.flowable_service.UiJsonToBpmnService`
- `com.iongroup.library.flowable_service.FlowableRuntimeService`
- `com.iongroup.library.flowable_service.FlowableProcessService`

> The frontend does **not** directly interact with Flowable APIs or BPMN models.

---

## Supported Node Types

| Node Type     | Description                                              |
|---------------|----------------------------------------------------------|
| Start Node    | Entry point of the process                               |
| End Node      | Terminates the process                                   |
| User Task     | Requires human intervention (approvals, form inputs)     |
| Service Task  | Executes backend logic (no human interaction)            |
| Script Task   | Executes script-based logic                              |
| Parallel Node | Enables parallel execution paths                         |

---

## Delegation-Based Task Configuration

When a **User Task** or **Service Task** is selected, the frontend calls:
```
GET http://localhost:8080/api/delegations/type/USER_TASK
```

The backend returns delegation metadata including:

- `id`
- `description`
- `inputs` / `outputs`
- `delegateClass`
- `delegationType`
- `selectableFields`
- `customizableFields`

This response dynamically populates the node properties panel. The backend retrieves delegations via `com.iongroup.library.registry` using:

- `getAllOperations()`
- `getOperationsByType(DelegationType type)`
- `getOperationCount()`
- `getValidDelegationTypes()`

---

## UIJson Structure

The frontend generates and sends a structured JSON representation of the workflow called **UIJson**.

### Graph Data Generator
```ts
export const getGraphData = (
  nodes,
  edges,
  viewport = { x: 0, y: 0, zoom: 1 }
) => {
  return { nodes, edges, viewport };
};
```

### FlowNodeData Type
```ts
export type FlowNodeData = {
  label: string;
  nodeType: "user" | "service" | "script" | "parallel" | "start" | "end";
  description?: string;
  delegationId?: string;
  delegationName?: string;
  delegationType?: "USER_TASK" | "SERVICE" | "SCRIPT";
  customFields?: Record<string, string>;
  selectedFields?: string[];
  status?: "pending" | "active" | "completed";
};
```

### Required JSON Format
```json
{
  "nodes": [],
  "edges": [],
  "viewport": {}
}
```

Each node must include: `id`, `type` (`"flowNode"`), `position`, `data` (FlowNodeData), `width`, and `height`.

### User Task Example
```json
{
  "nodeType": "user",
  "delegationId": "EnterCustomerDetails",
  "delegationType": "USER_TASK",
  "selectedFields": ["CUSTOMER_NAME", "PAN"]
}
```

### Service Task Example
```json
{
  "nodeType": "service",
  "delegationId": "CheckEligibility",
  "delegationType": "SERVICE",
  "customFields": {
    "AMOUNT": "1000"
  }
}
```

---

## Execution Flow

| Step | Description | Handler |
|------|-------------|---------|
| 1 | UIJson sent to backend by frontend | Frontend |
| 2 | UIJson → BPMN conversion | `UiJsonToBpmnService` |
| 3 | Process deployment & execution | `FlowableRuntimeService` |
| 4 | Process status tracking | `FlowableProcessService` |

The frontend periodically polls process state and updates node UI accordingly.

---

## Execution Page

The execution page was built to demonstrate that User Tasks are real wait states and to visually show workflow progress to stakeholders.

### Node Status States

| Status    | Visual Representation  |
|-----------|------------------------|
| `pending`   | Default styling        |
| `active`    | Highlighted            |
| `completed` | Green border           |

---

## Design Principles

- **Visual-first** BPM modeling
- **Delegation-driven** configuration
- **Strict UIJson contract** between frontend and backend
- **Clear separation of concerns**
- **Backend abstraction** via `com.iongroup.library`
- **Real-time process visibility**

---

## Tech Stack

- React
- React Flow
- TypeScript
- REST APIs
- Flowable (Backend)
- `com.iongroup.library` (Internal)

---

## Summary

The BPM Workflow Designer frontend provides a flexible visual workflow builder with dynamic task configuration, seamless backend integration, automated BPMN generation, and real-time execution monitoring. It acts as a clean abstraction layer over Flowable while leveraging the internal `com.iongroup.library` for conversion, deployment, execution, and process tracking.