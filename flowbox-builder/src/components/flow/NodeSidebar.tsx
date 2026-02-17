import { User, Server, Code, GitBranch, Play, Square } from "lucide-react";
import { DragEvent } from "react";

const nodeTypes = [
  { type: "user", label: "User Box", icon: User, color: "text-blue-500" },
  { type: "service", label: "Service Box", icon: Server, color: "text-emerald-500" },
  { type: "script", label: "Script Box", icon: Code, color: "text-amber-500" },
  { type: "parallel", label: "Parallel Box", icon: GitBranch, color: "text-violet-500" },
  { type: "start", label: "Start Node", icon: Play, color: "text-green-500" },
  { type: "end", label: "End Node", icon: Square, color: "text-red-500" },
] as const;

const NodeSidebar = () => {
  const onDragStart = (event: DragEvent, nodeType: string) => {
    event.dataTransfer.setData("application/reactflow", nodeType);
    event.dataTransfer.effectAllowed = "move";
  };

  return (
    <aside className="w-56 shrink-0 border-r border-border bg-card p-4 flex flex-col gap-3">
      <h2 className="text-sm font-semibold text-foreground tracking-wide uppercase mb-1">
        Node Types
      </h2>
      {nodeTypes.map(({ type, label, icon: Icon, color }) => (
        <div
          key={type}
          className="w-[160px] h-[80px] rounded-lg border-2 border-border bg-background shadow-sm flex items-center gap-3 px-4 cursor-grab active:cursor-grabbing hover:border-primary/50 transition-colors"
          draggable
          onDragStart={(e) => onDragStart(e, type)}
        >
          <div className={`shrink-0 ${color}`}>
            <Icon size={20} />
          </div>
          <span className="text-sm font-medium text-foreground">{label}</span>
        </div>
      ))}
    </aside>
  );
};

export default NodeSidebar;
