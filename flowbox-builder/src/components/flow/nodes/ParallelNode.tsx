import { memo } from "react";
import { Handle, Position, NodeProps } from "reactflow";
import { GitBranch } from "lucide-react";

const ParallelNode = ({ selected }: NodeProps<any>) => {
  return (
    <div className="relative w-[80px] h-[80px] flex items-center justify-center">

      {/* DIAMOND (rotated only visually) */}
      <div
        className={`w-[56px] h-[56px] rotate-45 bg-card border-2 shadow-md flex items-center justify-center
        ${selected ? "border-primary ring-2 ring-primary/20" : "border-border"}`}
      >
        <div className="-rotate-45">
          <GitBranch size={18} className="text-violet-500" />
        </div>
      </div>

      {/* HANDLES — OUTSIDE ROTATION */}

      {/* TOP TIP */}
      <Handle
        id="t-top"
        type="target"
        position={Position.Top}
        style={{ top: 0, left: "50%", transform: "translate(-50%, -50%)" }}
      />
      <Handle
        id="s-top"
        type="source"
        position={Position.Top}
        style={{ top: 0, left: "50%", transform: "translate(-50%, -50%)" }}
      />

      {/* RIGHT TIP */}
      <Handle
        id="t-right"
        type="target"
        position={Position.Right}
        style={{ top: "50%", right: 0, transform: "translate(50%, -50%)" }}
      />
      <Handle
        id="s-right"
        type="source"
        position={Position.Right}
        style={{ top: "50%", right: 0, transform: "translate(50%, -50%)" }}
      />

      {/* LEFT TIP */}
      <Handle
        id="t-left"
        type="target"
        position={Position.Left}
        style={{ top: "50%", left: 0, transform: "translate(-50%, -50%)" }}
      />
      <Handle
        id="s-left"
        type="source"
        position={Position.Left}
        style={{ top: "50%", left: 0, transform: "translate(-50%, -50%)" }}
      />

      {/* BOTTOM TIP */}
      <Handle
        id="t-bottom"
        type="target"
        position={Position.Bottom}
        style={{ bottom: 0, left: "50%", transform: "translate(-50%, 50%)" }}
      />
      <Handle
        id="s-bottom"
        type="source"
        position={Position.Bottom}
        style={{ bottom: 0, left: "50%", transform: "translate(-50%, 50%)" }}
      />
    </div>
  );
};

export default memo(ParallelNode);
