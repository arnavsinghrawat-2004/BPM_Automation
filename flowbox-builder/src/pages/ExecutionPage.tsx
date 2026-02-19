import { useCallback, useEffect, useState } from "react";
import { useParams, useLocation } from "react-router-dom";
import ReactFlow, {
  Background,
  Controls,
  MiniMap,
  Node,
  Edge,
  ReactFlowProvider,
  ConnectionMode,
} from "reactflow";
import "reactflow/dist/style.css";

import FlowNode, { FlowNodeData } from "@/components/flow/FlowNode";
import { GraphData } from "@/lib/graphExport";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const nodeTypes = { flowNode: FlowNode };

type NodeStatus = "pending" | "active" | "completed";

interface ProcessStatus {
  activeNodes: string[];
  pendingUserTasks: Array<{
    taskId: string;
    nodeId: string;
    taskName: string;
  }>;
  completedNodes: string[];
}

const ExecutionPage = () => {
  const { processInstanceId } = useParams<{ processInstanceId: string }>();
  const location = useLocation();
  const graphData = location.state as GraphData;

  const [nodes, setNodes] = useState<Node<FlowNodeData>[]>([]);
  const [edges, setEdges] = useState<Edge[]>([]);
  const [status, setStatus] = useState<ProcessStatus | null>(null);
  const [selectedNode, setSelectedNode] = useState<Node<FlowNodeData> | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState<Record<string, string>>({});

  // Load graph data
  useEffect(() => {
    if (graphData) {
      setNodes(graphData.nodes);
      setEdges(graphData.edges);
    } else if (processInstanceId) {
      const stored = localStorage.getItem(`graphData_${processInstanceId}`);
      if (stored) {
        try {
          const parsedGraphData = JSON.parse(stored) as GraphData;
          setNodes(parsedGraphData.nodes);
          setEdges(parsedGraphData.edges);
        } catch (error) {
          console.error("Failed to parse stored graph data:", error);
        }
      }
    }
  }, [graphData, processInstanceId]);

  // Fetch execution status
  const fetchStatus = useCallback(async () => {
    if (!processInstanceId) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/process/status/${processInstanceId}`
      );

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
    const interval = setInterval(fetchStatus, 5000); // Poll every 5 seconds
    return () => clearInterval(interval);
  }, [fetchStatus]);

  // Determine node status
  const getNodeStatus = (nodeId: string): NodeStatus => {
    if (!status) return "pending";

    if (status.completedNodes.includes(nodeId)) return "completed";

    if (status.activeNodes.includes(nodeId)) return "active";

    return "pending";
  };

  // Handle node click
  const handleNodeClick = useCallback(
    (_: React.MouseEvent, node: Node<FlowNodeData>) => {
      console.log("Clicked:", node.id);
      console.log("Type:", node.data.nodeType);
      console.log("Status:", getNodeStatus(node.id));

      if (
        node.data.nodeType === "user" &&
        getNodeStatus(node.id) === "active"
      ) {
        setSelectedNode(node);
        // Initialize formData with customFields values
        const initialData: Record<string, string> = {};
        if (node.data.customFields) {
          Object.entries(node.data.customFields).forEach(([key, value]) => {
            initialData[key] = value;
          });
        }
        setFormData(initialData);
        setIsModalOpen(true);
      } else {
        setSelectedNode(node);
      }
    },
    [status]
  );

  // Handle form submission for user task
  const handleFormSubmit = useCallback(async () => {
    if (!selectedNode || !processInstanceId) return;

    try {
      const response = await fetch(
        `http://localhost:8080/api/process/task/complete/${selectedNode.id}?processInstanceId=${processInstanceId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(formData),
        }
      );

      if (response.ok) {
        setIsModalOpen(false);
        setSelectedNode(null);
        setFormData({});
        fetchStatus(); // Refresh state
      }
    } catch (error) {
      console.error("Failed to complete task:", error);
    }
  }, [selectedNode, processInstanceId, formData, fetchStatus]);

  // Inject status into node data
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
          <ReactFlowProvider>
            <ReactFlow
              nodes={updatedNodes}
              edges={edges}
              onNodeClick={handleNodeClick}
              nodeTypes={nodeTypes}
              defaultViewport={graphData?.viewport}
              fitView
              className="bg-background"
              nodesDraggable={false}
              nodesConnectable={false}
              elementsSelectable={false}
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
          </ReactFlowProvider>
        </div>

        {selectedNode && (
          <Card className="w-80 m-4">
            <CardHeader>
              <CardTitle>{selectedNode.data.label}</CardTitle>
            </CardHeader>
            <CardContent>
              <p><strong>Type:</strong> {selectedNode.data.nodeType}</p>
              <p><strong>Status:</strong> {getNodeStatus(selectedNode.id)}</p>
            </CardContent>
          </Card>
        )}

        <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Complete Task: {selectedNode?.data.label}</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              {selectedNode?.data.selectedFields?.map((field) => (
                <div key={field}>
                  <Label htmlFor={field}>{field.replace('_', ' ')}</Label>
                  <Input
                    id={field}
                    value={formData[field] || ''}
                    onChange={(e) => setFormData({ ...formData, [field]: e.target.value })}
                  />
                </div>
              ))}
              {selectedNode?.data.customFields && Object.keys(selectedNode.data.customFields).length > 0 && (
                <div>
                  <h4 className="font-semibold">Custom Fields</h4>
                  {Object.entries(selectedNode.data.customFields).map(([key, value]) => (
                    <div key={key}>
                      <Label htmlFor={key}>{key}</Label>
                      <Input
                        id={key}
                        value={formData[key] || value}
                        onChange={(e) => setFormData({ ...formData, [key]: e.target.value })}
                      />
                    </div>
                  ))}
                </div>
              )}
              <Button onClick={handleFormSubmit}>Submit</Button>
            </div>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  );
};

export default ExecutionPage;
