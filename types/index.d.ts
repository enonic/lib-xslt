import type { ResourceKey } from "@enonic-types/core";

declare module "/lib/xslt" {
    /**
     * Renders a view using XSLT. The model is automatically transformed to XML.
     *
     * @param view Location of the view. Use `resolve(...)` to resolve a view.
     * @param model Model that is passed to the view.
     * @returns The rendered output.
     */
    export function render(view: ResourceKey, model: Record<string, unknown>): string;
}

export {};
