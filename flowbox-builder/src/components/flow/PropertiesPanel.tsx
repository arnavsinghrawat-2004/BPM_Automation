import { Sheet, SheetContent, SheetHeader, SheetTitle } from "@/components/ui/sheet";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Checkbox } from "@/components/ui/checkbox";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { User, Server, Code, GitBranch, Play, Square, Loader2, AlertCircle } from "lucide-react";
import { Alert, AlertDescription } from "@/components/ui/alert";
import { useEffect, useState } from "react";
import type { FlowNodeData } from "./FlowNode";
import { useFetchDelegations } from "@/hooks/useFetchDelegations";

const iconMap = {
  user: User,
  service: Server,
  script: Code,
  parallel: GitBranch,
  start: Play,
  end: Square,
};

const nodeTypeToDelegationType: Record<string, "USER_TASK" | "SERVICE" | "SCRIPT" | undefined> = {
  user: "USER_TASK",
  service: "SERVICE",
  script: "SCRIPT",
  start: undefined,
  end: undefined,
  parallel: undefined,
};

interface PropertiesPanelProps {
  open: boolean;
  onClose: () => void;
  data: FlowNodeData | null;
  onUpdate: (updates: Partial<FlowNodeData>) => void;
}

const PropertiesPanel = ({ open, onClose, data, onUpdate }: PropertiesPanelProps) => {
  if (!data) return null;

  const Icon = iconMap[data.nodeType];
  const delegationType = nodeTypeToDelegationType[data.nodeType];
  const supportsDelegations = delegationType !== undefined;

  const { data: delegations, isLoading, error } = useFetchDelegations(delegationType || "SERVICE");

  const [selectedDelegationId, setSelectedDelegationId] = useState<string>("");

  useEffect(() => {
    if (data.delegationId) {
      setSelectedDelegationId(data.delegationId);
    } else {
      setSelectedDelegationId("");
    }
  }, [data.delegationId]);

  const selectedDelegation = delegations?.find((d) => d.id === selectedDelegationId);

  const handleDelegationChange = (value: string) => {
    setSelectedDelegationId(value);
    const selected = delegations?.find((d) => d.id === value);
    if (selected) {
      onUpdate({
        delegationId: selected.id,
        delegationName: selected.id,
        delegationType: delegationType || undefined,
      });
    }
  };

  return (
    <Sheet open={open} onOpenChange={(v) => !v && onClose()}>
      <SheetContent className="w-80 overflow-y-auto">
        <SheetHeader>
          <SheetTitle className="flex items-center gap-2">
            <Icon size={18} />
            Node Properties
          </SheetTitle>
        </SheetHeader>

        <div className="mt-6 flex flex-col gap-5">
          {/* Node Type */}
          <div>
            <Label className="text-xs text-muted-foreground">Type</Label>
            <Badge variant="secondary" className="mt-1 capitalize">
              {data.nodeType}
            </Badge>
          </div>

          {/* Node Name */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="node-name">Name</Label>
            <Input
              id="node-name"
              value={data.label}
              onChange={(e) => onUpdate({ label: e.target.value })}
            />
          </div>

          {/* Node Description */}
          <div className="flex flex-col gap-1.5">
            <Label htmlFor="node-desc">Description</Label>
            <Textarea
              id="node-desc"
              value={data.description || ""}
              onChange={(e) => onUpdate({ description: e.target.value })}
              rows={4}
              placeholder="Add a description…"
            />
          </div>

          {/* Delegation Selector */}
          {supportsDelegations && (
            <div className="border-t pt-5">
              <Label htmlFor="delegation-select" className="text-sm font-medium">
                Delegation Function
              </Label>

              {error && (
                <Alert variant="destructive" className="mt-3">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription className="text-xs">
                    Failed to load delegations. Ensure backend is running on
                    http://localhost:8080
                  </AlertDescription>
                </Alert>
              )}

              {!error && (
                <>
                  {isLoading ? (
                    <div className="flex items-center justify-center py-6">
                      <Loader2 className="h-4 w-4 animate-spin text-muted-foreground" />
                      <span className="ml-2 text-xs text-muted-foreground">
                        Loading delegations...
                      </span>
                    </div>
                  ) : delegations && delegations.length > 0 ? (
                    <>
                      <Select value={selectedDelegationId} onValueChange={handleDelegationChange}>
                        <SelectTrigger id="delegation-select" className="mt-2">
                          <SelectValue placeholder="Select a delegation..." />
                        </SelectTrigger>
                        <SelectContent>
                          {delegations.map((delegation) => (
                            <SelectItem key={delegation.id} value={delegation.id}>
                              {delegation.id}
                            </SelectItem>
                          ))}
                        </SelectContent>
                      </Select>

                      {/* Delegation Details */}
                      {selectedDelegation && (
                        <div className="mt-4 rounded-lg border border-border bg-muted/50 p-3 space-y-3">
                          {/* Description */}
                          <div>
                            <p className="text-xs font-medium text-muted-foreground">
                              Description
                            </p>
                            <p className="text-xs mt-1">
                              {selectedDelegation.description}
                            </p>
                          </div>

                          {/* Inputs */}
                          {selectedDelegation.inputs.length > 0 && (
                            <div>
                              <p className="text-xs font-medium text-muted-foreground">
                                Inputs
                              </p>
                              <div className="flex flex-wrap gap-1 mt-1">
                                {selectedDelegation.inputs.map((inp) => (
                                  <span
                                    key={inp}
                                    className="inline-block bg-blue-500/10 text-blue-600 text-xs px-2 py-1 rounded"
                                  >
                                    {inp}
                                  </span>
                                ))}
                              </div>
                            </div>
                          )}

                          {/* Outputs */}
                          {selectedDelegation.outputs.length > 0 && (
                            <div>
                              <p className="text-xs font-medium text-muted-foreground">
                                Outputs
                              </p>
                              <div className="flex flex-wrap gap-1 mt-1">
                                {selectedDelegation.outputs.map((out) => (
                                  <span
                                    key={out}
                                    className="inline-block bg-emerald-500/10 text-emerald-600 text-xs px-2 py-1 rounded"
                                  >
                                    {out}
                                  </span>
                                ))}
                              </div>
                            </div>
                          )}

                          {/* Customizable Fields */}
                          {selectedDelegation.customizableFields.length > 0 && (
                            <div className="mt-3 space-y-3">
                              <p className="text-xs font-medium text-muted-foreground">
                                Customizable Fields
                              </p>
                              {selectedDelegation.customizableFields.map((field) => (
                                <div key={field} className="flex flex-col gap-1.5">
                                  <Label htmlFor={`custom-${field}`} className="text-xs">
                                    {field}
                                  </Label>
                                  <Input
                                    id={`custom-${field}`}
                                    placeholder={`Enter value for ${field}`}
                                    value={(data.customFields && data.customFields[field]) || ""}
                                    onChange={(e) =>
                                      onUpdate({
                                        customFields: {
                                          ...data.customFields,
                                          [field]: e.target.value,
                                        },
                                      })
                                    }
                                  />
                                </div>
                              ))}
                            </div>
                          )}

                          {/* Selectable Fields */}
                          {selectedDelegation.selectableFields.length > 0 && (
                            <div className="mt-3 space-y-3">
                              <p className="text-xs font-medium text-muted-foreground">
                                Required Fields
                              </p>
                              <div className="space-y-2">
                                {selectedDelegation.selectableFields.map((field) => (
                                  <div key={field} className="flex items-center gap-2">
                                    <Checkbox
                                      id={`select-${field}`}
                                      checked={(data.selectedFields || []).includes(field)}
                                      onCheckedChange={(checked) => {
                                        const current = data.selectedFields || [];
                                        const updated = checked
                                          ? [...current, field]
                                          : current.filter((f) => f !== field);
                                        onUpdate({ selectedFields: updated });
                                      }}
                                    />
                                    <Label
                                      htmlFor={`select-${field}`}
                                      className="text-xs cursor-pointer"
                                    >
                                      {field}
                                    </Label>
                                  </div>
                                ))}
                              </div>
                            </div>
                          )}
                        </div>
                      )}
                    </>
                  ) : (
                    <div className="py-6 text-center text-xs text-muted-foreground">
                      No delegations available for {data.nodeType} nodes
                    </div>
                  )}
                </>
              )}
            </div>
          )}
        </div>
      </SheetContent>
    </Sheet>
  );
};

export default PropertiesPanel;
