import { Node, Edge } from "reactflow";
import { FlowNodeData } from "@/components/flow/FlowNode";

export interface GraphData {
  nodes: Node<FlowNodeData>[];
  edges: Edge[];
  viewport: {
    x: number;
    y: number;
    zoom: number;
  };
}

/**
 * Export graph data to JSON format
 */
export const exportGraphAsJSON = (
  nodes: Node<FlowNodeData>[],
  edges: Edge[],
  viewport = { x: 0, y: 0, zoom: 1 }
): string => {
  const graphData: GraphData = {
    nodes,
    edges,
    viewport,
  };
  return JSON.stringify(graphData, null, 2);
};

/**
 * Download graph data as a JSON file
 */
export const downloadGraphAsJSON = (
  nodes: Node<FlowNodeData>[],
  edges: Edge[],
  filename = "graph.json",
  viewport = { x: 0, y: 0, zoom: 1 }
): void => {
  const jsonData = exportGraphAsJSON(nodes, edges, viewport);
  const blob = new Blob([jsonData], { type: "application/json" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = filename;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
};

/**
 * Get graph data as a JSON object
 */
export const getGraphData = (
  nodes: Node<FlowNodeData>[],
  edges: Edge[],
  viewport = { x: 0, y: 0, zoom: 1 }
): GraphData => {
  return {
    nodes,
    edges,
    viewport,
  };
};

/**
 * Import graph data from JSON
 */
export const importGraphFromJSON = (jsonString: string): GraphData => {
  try {
    return JSON.parse(jsonString) as GraphData;
  } catch (error) {
    console.error("Failed to parse graph JSON:", error);
    throw new Error("Invalid graph JSON format");
  }
};
