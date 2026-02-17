        import { useState, useEffect } from "react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "@/components/ui/dialog";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { useFetchDelegations } from "@/hooks/useFetchDelegations";
import { OperationDescriptor } from "@/hooks/useFetchDelegations";
import { AlertCircle, Loader2 } from "lucide-react";
import { Alert, AlertDescription } from "@/components/ui/alert";

interface DelegationModalProps {
  open: boolean;
  nodeName: string;
  nodeType: "user" | "service" | "script" | "parallel" | "start" | "end";
  selectedDelegation?: OperationDescriptor;
  onClose: () => void;
  onSelect: (delegation: OperationDescriptor) => void;
}

const nodeTypeToDelegationType: Record<string, "USER_TASK" | "SERVICE" | "SCRIPT"> = {
  user: "USER_TASK",
  service: "SERVICE",
  script: "SCRIPT",
};

export default function DelegationModal({
  open,
  nodeName,
  nodeType,
  selectedDelegation,
  onClose,
  onSelect,
}: DelegationModalProps) {
  const delegationType = nodeTypeToDelegationType[nodeType];
  const { data: delegations, isLoading, error } = useFetchDelegations(delegationType);
  const [selectedId, setSelectedId] = useState<string>("");

  useEffect(() => {
    if (selectedDelegation) {
      setSelectedId(selectedDelegation.id);
    }
  }, [selectedDelegation, open]);

  const handleSelect = () => {
    const selected = delegations?.find((d) => d.id === selectedId);
    if (selected) {
      onSelect(selected);
      onClose();
    }
  };

  const handleOpenChange = (isOpen: boolean) => {
    if (!isOpen) {
      onClose();
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChange}>
      <DialogContent className="max-w-md">
        <DialogHeader>
          <DialogTitle>Select Delegation</DialogTitle>
          <DialogDescription>
            Choose a delegation function for <span className="font-semibold">{nodeName}</span>
          </DialogDescription>
        </DialogHeader>

        <div className="space-y-4">
          <div className="space-y-2">
            <Label className="text-sm font-medium">Node Type</Label>
            <div className="flex items-center gap-2 px-3 py-2 rounded border border-border bg-muted/50">
              <span className="text-xs font-medium text-muted-foreground uppercase">
                {nodeType}
              </span>
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="delegation-select" className="text-sm font-medium">
              Delegation Function
            </Label>

            {error && (
              <Alert variant="destructive">
                <AlertCircle className="h-4 w-4" />
                <AlertDescription>
                  Failed to load delegations. Please ensure the backend is running on
                  http://localhost:8080
                </AlertDescription>
              </Alert>
            )}

            {!error && (
              <>
                {isLoading ? (
                  <div className="flex items-center justify-center py-6">
                    <Loader2 className="h-5 w-5 animate-spin text-muted-foreground" />
                    <span className="ml-2 text-sm text-muted-foreground">
                      Loading delegations...
                    </span>
                  </div>
                ) : delegations && delegations.length > 0 ? (
                  <Select value={selectedId} onValueChange={setSelectedId}>
                    <SelectTrigger id="delegation-select">
                      <SelectValue placeholder="Select a delegation..." />
                    </SelectTrigger>
                    <SelectContent>
                      {delegations.map((delegation) => (
                        <SelectItem key={delegation.id} value={delegation.id}>
                          <div className="flex flex-col">
                            <span>{delegation.id}</span>
                            <span className="text-xs text-muted-foreground">
                              {delegation.description}
                            </span>
                          </div>
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                ) : (
                  <div className="py-6 text-center text-sm text-muted-foreground">
                    No delegations available for this type
                  </div>
                )}
              </>
            )}
          </div>

          {selectedId && delegations?.find((d) => d.id === selectedId) && (
            <div className="rounded-lg border border-border bg-muted/50 p-3 space-y-2">
              <div>
                <p className="text-xs font-medium text-muted-foreground">Description</p>
                <p className="text-sm">
                  {delegations?.find((d) => d.id === selectedId)?.description}
                </p>
              </div>
              <div className="grid grid-cols-2 gap-2">
                <div>
                  <p className="text-xs font-medium text-muted-foreground">Inputs</p>
                  <div className="flex flex-wrap gap-1 mt-1">
                    {delegations?.find((d) => d.id === selectedId)?.inputs.map((inp) => (
                      <span
                        key={inp}
                        className="inline-block bg-primary/10 text-primary text-xs px-2 py-1 rounded"
                      >
                        {inp}
                      </span>
                    ))}
                  </div>
                </div>
                <div>
                  <p className="text-xs font-medium text-muted-foreground">Outputs</p>
                  <div className="flex flex-wrap gap-1 mt-1">
                    {delegations?.find((d) => d.id === selectedId)?.outputs.map((out) => (
                      <span
                        key={out}
                        className="inline-block bg-emerald-500/10 text-emerald-600 text-xs px-2 py-1 rounded"
                      >
                        {out}
                      </span>
                    ))}
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>
            Cancel
          </Button>
          <Button onClick={handleSelect} disabled={!selectedId || isLoading}>
            {isLoading ? (
              <>
                <Loader2 className="h-4 w-4 mr-2 animate-spin" />
                Loading...
              </>
            ) : (
              "Select"
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
