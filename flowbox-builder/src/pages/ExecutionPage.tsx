import { useCallback, useEffect, useState } from "react";
import { useParams, useLocation } from "react-router-dom";
import ReactFlow, {
  Background,
  Controls,
  MiniMap,
  Node,
  Edge,
} from "reactflow";
import "reactflow/dist/style.css";

import FlowNode, { FlowNodeData } from "@/components/flow/FlowNode";
import { GraphData } from "@/lib/graphExport";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const nodeTypes = { flowNode: FlowNode };

type NodeStatus = "pending" | "active" | "completed";

interface ProcessStatus {
  activeTasks: Array<{
    id: string;
    name: string;
    nodeId: string;
  }>;
  completedActivities: string[];
  currentActivity?: string;
}

const ExecutionPage = () => {
  const { processInstanceId } = useParams<{ processInstanceId: string }>();
  const location = useLocation();
  const graphData = location.state as GraphData;

  const [nodes, setNodes] = useState<Node<FlowNodeData>[]>([]);
  const [edges, setEdges] = useState<Edge[]>([]);
  const [status, setStatus] = useState<ProcessStatus | null>(null);
  const [selectedNode, setSelectedNode] = useState<Node<FlowNodeData> | null>(null);

  useEffect(() => {
    if (graphData) {
      setNodes(graphData.nodes);
      setEdges(graphData.edges);
    }
  }, [graphData]);

  const fetchStatus = useCallback(async () => {
    if (!processInstanceId) return;
    try {
      const response = await fetch(`http://localhost:8080/process/status/${processInstanceId}`);
      if (response.ok) {
        const data: ProcessStatus = await response.json();
        setStatus(data);
      }
    } catch (error) {
      console.error("Failed to fetch status:", error);
    }
  }, [processInstanceId]);

  useEffect(() => {
    fetchStatus();
    const interval = setInterval(fetchStatus, 2000); // Poll every 2 seconds
    return () => clearInterval(interval);
  }, [fetchStatus]);

  const getNodeStatus = (nodeId: string): NodeStatus => {
    if (!status) return "pending";
    if (status.completedActivities.includes(nodeId)) return "completed";
    if (status.currentActivity === nodeId) return "active";
    return "pending";
  };

  const handleNodeClick = useCallback(
    async (_: React.MouseEvent, node: Node<FlowNodeData>) => {
      if (node.data.nodeType === "user" && getNodeStatus(node.id) === "active") {
        const task = status?.activeTasks.find((t) => t.nodeId === node.id);
        if (task) {
          try {
            const response = await fetch(`http://localhost:8080/process/complete-task/${task.id}`, {
              method: "POST",
            });
            if (response.ok) {
              fetchStatus(); // Refresh status
            }
          } catch (error) {
            console.error("Failed to complete task:", error);
          }
        }
      } else {
        setSelectedNode(node);
      }
    },
    [status, fetchStatus]
  );

  const updatedNodes = nodes.map((node) => ({
    ...node,
    data: {
      ...node.data,
      status: getNodeStatus(node.id),
    },
  }));

  return (
    <div className="flex h-screen w-full flex-col bg-background">
      <div className="flex items-center justify-between border-b border-border bg-card px-4 py-3">
        <h1 className="text-lg font-semibold text-foreground">
          Process Execution - {processInstanceId}
        </h1>
        <Button variant="outline" onClick={() => window.history.back()}>
          Back to Builder
        </Button>
      </div>

      <div className="flex flex-1 overflow-hidden">
        <div className="flex-1">
          <ReactFlow
            nodes={updatedNodes}
            edges={edges}
            onNodeClick={handleNodeClick}
            nodeTypes={nodeTypes}
            fitView
            className="bg-background"
            nodesDraggable={false}
            nodesConnectable={false}
            elementsSelectable={false}
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

        {selectedNode && (
          <Card className="w-80 m-4">
            <CardHeader>
              <CardTitle>{selectedNode.data.label}</CardTitle>
            </CardHeader>
            <CardContent>
              <p><strong>Type:</strong> {selectedNode.data.nodeType}</p>
              {selectedNode.data.description && (
                <p><strong>Description:</strong> {selectedNode.data.description}</p>
              )}
              {selectedNode.data.selectedFields && selectedNode.data.selectedFields.length > 0 && (
                <div>
                  <strong>Selected Fields:</strong>
                  <ul className="list-disc list-inside mt-1">
                    {selectedNode.data.selectedFields.map((field) => (
                      <li key={field}>{field}</li>
                    ))}
                  </ul>
                </div>
              )}
              {selectedNode.data.customFields && Object.keys(selectedNode.data.customFields).length > 0 && (
                <div>
                  <strong>Custom Fields:</strong>
                  <pre className="text-xs bg-muted p-2 rounded mt-1">
                    {JSON.stringify(selectedNode.data.customFields, null, 2)}
                  </pre>
                </div>
              )}
              <p><strong>Status:</strong> {getNodeStatus(selectedNode.id)}</p>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
};

export default ExecutionPage;