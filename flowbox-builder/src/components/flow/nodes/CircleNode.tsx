import { memo } from "react";
import { Handle, Position, NodeProps } from "reactflow";
import { LucideIcon } from "lucide-react";

export type CircleNodeData = {
  label: string;
  description?: string;
};

interface CircleNodeProps extends NodeProps<CircleNodeData> {
  Icon: LucideIcon;
  color: string;
}

const CircleNode = ({ data, selected, Icon, color }: CircleNodeProps) => {
  return (
    <div className="relative w-[100px] h-[100px] flex items-center justify-center">
      {/* CIRCLE */}
      <div
        className={`w-[80px] h-[80px] rounded-full bg-card border-2 shadow-md flex items-center justify-center transition-colors ${
          selected ? "border-primary ring-2 ring-primary/20" : "border-border"
        }`}
      >
        <div className={`${color}`}>
          <Icon size={24} />
        </div>
      </div>

      {/* HANDLES — 2 CONNECTOR DOTS (Left and Right) */}

      {/* LEFT */}
      <Handle
        id="t-left"
        type="target"
        position={Position.Left}
        style={{ top: "50%", left: 0, transform: "translate(-50%, -50%)" }}
        className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background"
      />
      <Handle
        id="s-left"
        type="source"
        position={Position.Left}
        style={{ top: "50%", left: 0, transform: "translate(-50%, -50%)" }}
        className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background"
      />

      {/* RIGHT */}
      <Handle
        id="t-right"
        type="target"
        position={Position.Right}
        style={{ top: "50%", right: 0, transform: "translate(50%, -50%)" }}
        className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background"
      />
      <Handle
        id="s-right"
        type="source"
        position={Position.Right}
        style={{ top: "50%", right: 0, transform: "translate(50%, -50%)" }}
        className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background"
      />
    </div>
  );
};

export default memo(CircleNode);
