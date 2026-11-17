import type { PatchOperation } from './stomp/PatchOperation';
import type { StatePatch } from './stomp/StatePatch';

// Keep a representative front-end consumer in Angular's compilation graph.
// Removing a field from the Java records then fails the production build.
export function getPatchQuantity(patch: StatePatch, operationIndex: number): number | undefined {
  const operation: PatchOperation = patch.ops[operationIndex];
  return operation.qty;
}
