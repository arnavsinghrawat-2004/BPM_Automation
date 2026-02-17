import { useQuery } from "@tanstack/react-query";

export interface OperationDescriptor {
  id: string;
  description: string;
  inputs: string[];
  outputs: string[];
  delegateClass: string;
  category: string;
  delegationType: "SERVICE" | "SCRIPT" | "USER_TASK";
}

interface DelegationResponse {
  success: boolean;
  message: string;
  data: OperationDescriptor[];
  count: number;
}

const API_BASE_URL = "http://localhost:8080/api/delegations";

export const useFetchDelegations = (delegationType: "SERVICE" | "SCRIPT" | "USER_TASK") => {
  return useQuery<OperationDescriptor[], Error>({
    queryKey: ["delegations", delegationType],
    queryFn: async () => {
      const response = await fetch(`${API_BASE_URL}/type/${delegationType}`);
      if (!response.ok) {
        throw new Error("Failed to fetch delegations");
      }
      const data: DelegationResponse = await response.json();
      return data.data || [];
    },
    enabled: !!delegationType,
  });
};

export const useFetchAllDelegations = () => {
  return useQuery<OperationDescriptor[], Error>({
    queryKey: ["delegations", "all"],
    queryFn: async () => {
      const response = await fetch(`${API_BASE_URL}/all`);
      if (!response.ok) {
        throw new Error("Failed to fetch delegations");
      }
      const data: DelegationResponse = await response.json();
      return data.data || [];
    },
  });
};
