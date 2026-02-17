import { useCallback, useRef, useState, DragEvent } from "react";
import ReactFlow, {
  addEdge,
  Background,
  Controls,
  MiniMap,
  useNodesState,
  useEdgesState,
  Connection,
  ReactFlowProvider,
  ReactFlowInstance,
  Node,
  ConnectionMode,
} from "reactflow";
import "reactflow/dist/style.css";

import FlowNode, { FlowNodeData } from "@/components/flow/FlowNode";
import NodeSidebar from "@/components/flow/NodeSidebar";
import PropertiesPanel from "@/components/flow/PropertiesPanel";
import { Button } from "@/components/ui/button";
import { downloadGraphAsJSON, getGraphData } from "@/lib/graphExport";
import { Download, Copy, Check, Play } from "lucide-react";

const nodeTypes = { flowNode: FlowNode };

let id = 0;
const getId = () => `node_${id++}`;

const labelMap: Record<string, string> = {
  user: "User",
  service: "Service",
  script: "Script",
  parallel: "Parallel",
};

const FlowCanvas = () => {
  const reactFlowWrapper = useRef<HTMLDivElement>(null);
  const [nodes, setNodes, onNodesChange] = useNodesState([]);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [rfInstance, setRfInstance] = useState<ReactFlowInstance | null>(null);
  const [selectedNode, setSelectedNode] = useState<Node<FlowNodeData> | null>(null);
  const [copied, setCopied] = useState(false);
  const [isConverting, setIsConverting] = useState(false);
  const [conversionResult, setConversionResult] = useState<any>(null);
  const [conversionError, setConversionError] = useState<string | null>(null);

  const onConnect = useCallback(
    (params: Connection) => setEdges((eds) => addEdge({ ...params, animated: true, style: { stroke: "hsl(var(--muted-foreground))" } }, eds)),
    [setEdges]
  );

  const onDragOver = useCallback((event: DragEvent) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
  }, []);

  const onDrop = useCallback(
    (event: DragEvent) => {
      event.preventDefault();
      const type = event.dataTransfer.getData("application/reactflow");
      if (!type || !rfInstance || !reactFlowWrapper.current) return;

      const bounds = reactFlowWrapper.current.getBoundingClientRect();
      const position = rfInstance.screenToFlowPosition({
        x: event.clientX - bounds.left,
        y: event.clientY - bounds.top,
      });

      const newNode: Node<FlowNodeData> = {
        id: getId(),
        type: "flowNode",
        position,
        data: { label: labelMap[type] || type, nodeType: type as FlowNodeData["nodeType"], description: "" },
      };

      setNodes((nds) => nds.concat(newNode));
    },
    [rfInstance, setNodes]
  );

  const onNodeDoubleClick = useCallback(
    (_: React.MouseEvent, node: Node<FlowNodeData>) => {
      setSelectedNode(node);
    },
    []
  );

  const onPaneClick = useCallback(() => {
    setSelectedNode(null);
  }, []);

  const onUpdateNode = useCallback(
    (updates: Partial<FlowNodeData>) => {
      if (!selectedNode) return;
      setNodes((nds) =>
        nds.map((n) =>
          n.id === selectedNode.id ? { ...n, data: { ...n.data, ...updates } } : n
        )
      );
      setSelectedNode((prev) => (prev ? { ...prev, data: { ...prev.data, ...updates } } : null));
    },
    [selectedNode, setNodes]
  );

  const handleExportJSON = useCallback(() => {
    const timestamp = new Date().toISOString().slice(0, 10);
    downloadGraphAsJSON(nodes, edges, `graph-${timestamp}.json`, {
      x: rfInstance?.getViewport().x || 0,
      y: rfInstance?.getViewport().y || 0,
      zoom: rfInstance?.getZoom() || 1,
    });
  }, [nodes, edges, rfInstance]);

  const handleCopyJSON = useCallback(async () => {
    const graphData = getGraphData(nodes, edges, {
      x: rfInstance?.getViewport().x || 0,
      y: rfInstance?.getViewport().y || 0,
      zoom: rfInstance?.getZoom() || 1,
    });
    const jsonString = JSON.stringify(graphData, null, 2);

    try {
      await navigator.clipboard.writeText(jsonString);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    } catch (error) {
      console.error("Failed to copy JSON:", error);
    }
  }, [nodes, edges, rfInstance]);

  const handleConvertAndRun = useCallback(async () => {
    setIsConverting(true);
    setConversionError(null);
    setConversionResult(null);

    const graphData = getGraphData(nodes, edges, {
      x: rfInstance?.getViewport().x || 0,
      y: rfInstance?.getViewport().y || 0,
      zoom: rfInstance?.getZoom() || 1,
    });

    try {
      const response = await fetch("http://localhost:8080/api/flowable/convert-and-execute", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(graphData),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to convert and execute");
      }

      const data = await response.json();
      setConversionResult(data);
      console.log("Conversion and execution successful:", data);
    } catch (error) {
      const message = error instanceof Error ? error.message : "Unknown error occurred";
      setConversionError(message);
      console.error("Error converting and executing:", error);
    } finally {
      setIsConverting(false);
    }
  }, [nodes, edges, rfInstance]);

  return (
    <div className="flex h-screen w-full flex-col bg-background">
      <div className="flex items-center justify-between border-b border-border bg-card px-4 py-3">
        <h1 className="text-lg font-semibold text-foreground">Flow Builder</h1>
        <div className="flex gap-2">
          <Button
            variant="outline"
            size="sm"
            onClick={handleCopyJSON}
            className="gap-2"
          >
            {copied ? (
              <>
                <Check size={16} />
                Copied!
              </>
            ) : (
              <>
                <Copy size={16} />
                Copy JSON
              </>
            )}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={handleExportJSON}
            className="gap-2"
          >
            <Download size={16} />
            Export JSON
          </Button>
          <Button
            variant="default"
            size="sm"
            onClick={handleConvertAndRun}
            disabled={nodes.length === 0 || isConverting}
            className="gap-2"
          >
            <Play size={16} />
            {isConverting ? "Converting..." : "Convert & Run"}
          </Button>
        </div>
      </div>

      {conversionError && (
        <div className="border-b border-red-300 bg-red-50 px-4 py-3 text-red-700">
          <p className="font-semibold">Error: {conversionError}</p>
        </div>
      )}

      {conversionResult && (
        <div className="border-b border-green-300 bg-green-50 px-4 py-3 text-green-700">
          <p className="font-semibold">✓ Conversion and Execution Successful!</p>
          <p className="text-sm mt-1">
            Process Instance ID: <code className="bg-white px-2 py-1 rounded">{conversionResult.executionResult?.processInstanceId}</code>
          </p>
          {conversionResult.executionResult?.processVariables && Object.keys(conversionResult.executionResult.processVariables).length > 0 && (
            <div className="mt-2 text-sm">
              <p className="font-semibold">Process Variables:</p>
              <pre className="bg-white p-2 rounded mt-1 text-xs overflow-auto max-h-32">
                {JSON.stringify(conversionResult.executionResult.processVariables, null, 2)}
              </pre>
            </div>
          )}
        </div>
      )}
      <div className="flex flex-1 overflow-hidden">
        <NodeSidebar />
        <div className="flex-1" ref={reactFlowWrapper}>
          <ReactFlow
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            onInit={setRfInstance}
            onDrop={onDrop}
            onDragOver={onDragOver}
            onNodeDoubleClick={onNodeDoubleClick}
            onPaneClick={onPaneClick}
            nodeTypes={nodeTypes}
            fitView
            deleteKeyCode={["Backspace", "Delete"]}
            className="bg-background"
            connectionMode={ConnectionMode.Loose}
          >
            <Background gap={16} size={1} color="hsl(var(--border))" />
            <Controls className="[&>button]:bg-card [&>button]:border-border [&>button]:text-foreground" />
            <MiniMap
              nodeColor="hsl(var(--primary))"
              maskColor="hsl(var(--background) / 0.7)"
              className="!bg-card !border-border"
            />
          </ReactFlow>
        </div>
        <PropertiesPanel
          open={!!selectedNode}
          onClose={() => setSelectedNode(null)}
          data={selectedNode?.data ?? null}
          onUpdate={onUpdateNode}
        />
      </div>
    </div>
  );
};

const Index = () => (
  <ReactFlowProvider>
    <FlowCanvas />
  </ReactFlowProvider>
);

export default Index;
