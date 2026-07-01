# @enonic-types/lib-xslt

[![npm version](https://img.shields.io/npm/v/@enonic-types/lib-xslt.svg)](https://www.npmjs.com/package/@enonic-types/lib-xslt)
[![License](https://img.shields.io/github/license/enonic/lib-xslt.svg)](https://www.apache.org/licenses/LICENSE-2.0.html)

TypeScript type definitions for [lib-xslt](https://github.com/enonic/lib-xslt), the XSLT rendering library for [Enonic XP](https://enonic.com).

## Installation

```bash
npm install --save-dev @enonic-types/lib-xslt
```

The library itself must be included in your XP application's `build.gradle`:

```groovy
dependencies {
    include 'com.enonic.lib:lib-xslt:3.+'
}
```

## Configuration

Add the package to the `types` array in your `tsconfig.json`:

```json
{
  "compilerOptions": {
    "types": [
      "@enonic-types/lib-xslt"
    ]
  }
}
```

## Usage

```ts
import { render } from '/lib/xslt';

const view = resolve('fruit.xslt');

const model = {
    fruits: [
        { name: 'Apple', color: 'Red' },
        { name: 'Pear', color: 'Green' }
    ]
};

const html: string = render(view, model);
```

## API

### `render(view, model)`

Renders a view using XSLT. The model is automatically transformed to XML.

| Parameter | Type                      | Description                                              |
|-----------|---------------------------|----------------------------------------------------------|
| `view`    | `ResourceKey`             | Location of the view. Use `resolve(...)` to resolve it.  |
| `model`   | `Record<string, unknown>` | Model that is passed to the view.                        |

Returns the rendered output as a `string`.

## Documentation

See the [library documentation](https://github.com/enonic/lib-xslt/blob/master/docs/index.adoc) for details.

## License

[Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0.html)
