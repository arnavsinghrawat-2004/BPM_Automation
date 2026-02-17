import { Server } from "lucide-react";
import BaseNode from "./BaseNode";

export default function ServiceNode(props: any) {
  return <BaseNode {...props} Icon={Server} color="text-emerald-500" />;
}
