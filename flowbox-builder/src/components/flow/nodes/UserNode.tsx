import { User } from "lucide-react";
import BaseNode from "./BaseNode";

export default function UserNode(props: any) {
  return <BaseNode {...props} Icon={User} color="text-blue-500" />;
}
