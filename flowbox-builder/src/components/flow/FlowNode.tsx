import { memo } from "react";
import { NodeProps } from "reactflow";
import UserNode from "./nodes/UserNode";
import ServiceNode from "./nodes/ServiceNode";
import ScriptNode from "./nodes/ScriptNode";
import ParallelNode from "./nodes/ParallelNode";
import StartNode from "./nodes/StartNode";
import EndNode from "./nodes/EndNode";

export type FlowNodeData = {
  label: string;
  nodeType: "user" | "service" | "script" | "parallel" | "start" | "end";
  description?: string;
  delegationId?: string;
  delegationName?: string;
  delegationType?: "USER_TASK" | "SERVICE" | "SCRIPT";
  customFields?: Record<string, string>;
  selectedFields?: string[];
};

const nodeComponentMap = {
  user: UserNode,
  service: ServiceNode,
  script: ScriptNode,
  parallel: ParallelNode,
  start: StartNode,
  end: EndNode,
};

const FlowNode = (props: NodeProps<FlowNodeData>) => {
  const NodeComponent = nodeComponentMap[props.data.nodeType];
  return <NodeComponent {...props} />;
};

export default memo(FlowNode);
