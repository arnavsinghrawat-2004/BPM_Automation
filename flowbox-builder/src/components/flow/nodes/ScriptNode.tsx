import { Code } from "lucide-react";
import BaseNode from "./BaseNode";

export default function ScriptNode(props: any) {
  return <BaseNode {...props} Icon={Code} color="text-amber-500" />;
}
