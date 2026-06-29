# Newman Run Instructions

The Postman collection at `kiwisaver-fee-calc.postman_collection.json` can be run headlessly with [Newman](https://github.com/postmanlabs/newman), Postman's CLI runner.

## Prerequisites

Install Newman:

```bash
npm install -g newman
```

## Running the collection

1. Start the service locally:

```bash
./mvnw quarkus:dev
```

2. Run the collection against it:

```bash
newman run postman/kiwisaver-fee-calc.postman_collection.json \
   --environment '{"variables": [{"key": "baseUrl", "value": "http://localhost:8081"}]}' \
   --reporters cli,json \
   --reporter-json-export=newman-results.json
```

## Viewing results

- Terminal: Newman prints a summary table with passed/failed assertions per request.
- JSON report: `newman-results.json` can be imported back into Postman for a visual overview.
