import { Play } from "lucide-react";
import CircleNode from "./CircleNode";

export default function StartNode(props: any) {
  return <CircleNode {...props} Icon={Play} color="text-green-500" />;
}
