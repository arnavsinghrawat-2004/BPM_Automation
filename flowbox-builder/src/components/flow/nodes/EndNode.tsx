import { Square } from "lucide-react";
import CircleNode from "./CircleNode";

export default function EndNode(props: any) {
  return <CircleNode {...props} Icon={Square} color="text-red-500" />;
}
