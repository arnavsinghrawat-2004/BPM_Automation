import { memo } from "react";
import { Handle, Position, NodeProps } from "reactflow";
import { LucideIcon } from "lucide-react";

export type BaseNodeData = {
  label: string;
  description?: string;
  delegationId?: string;
  delegationName?: string;
};

interface BaseNodeProps extends NodeProps<BaseNodeData> {
  Icon: LucideIcon;
  color: string;
}

const BaseNode = ({ data, selected, Icon, color, id }: BaseNodeProps) => {
  return (
    <div
      className={`w-[160px] h-[80px] rounded-lg border-2 bg-card shadow-md flex items-center gap-3 px-4 transition-colors cursor-pointer hover:border-primary/50 ${
        selected ? "border-primary ring-2 ring-primary/20" : "border-border"
      }`}
    >
      <Handle id="t-top" type="target" position={Position.Top} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />
      <Handle id="t-right" type="target" position={Position.Right} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />
      <Handle id="t-left" type="target" position={Position.Left} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />
      <Handle id="t-bottom" type="target" position={Position.Bottom} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />

      <Handle id="s-top" type="source" position={Position.Top} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />
      <Handle id="s-right" type="source" position={Position.Right} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />
      <Handle id="s-left" type="source" position={Position.Left} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />
      <Handle id="s-bottom" type="source" position={Position.Bottom} className="!w-3 !h-3 !bg-muted-foreground !border-2 !border-background" />

      <div className={`shrink-0 ${color}`}>
        <Icon size={20} />
      </div>
      <div className="flex-1 min-w-0">
        <span className="text-xs font-medium text-muted-foreground block">
          {data.label}
        </span>
        {data.delegationName && (
          <span className="text-xs text-foreground font-semibold truncate block">
            {data.delegationName}
          </span>
        )}
      </div>
    </div>
  );
};

export default memo(BaseNode);
