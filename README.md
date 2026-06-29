# KiwiSaver Fee Impact Calculator

A Quarkus microservice that calculates the long-term impact of fees on KiwiSaver fund balances.

## Features

- **GET /funds** — List all available KiwiSaver funds (conservative, balanced, growth types)
- **GET /funds/{id}** — Get a single fund by ID
- **POST /calculate** — Calculate projected balance with fee drag over N years

## How to run

```bash
./mvnw quarkus:dev
```

The service starts on port 8081.

## Example requests

### List funds

```bash
curl http://localhost:8081/funds
```

### Get a single fund

```bash
curl http://localhost:8081/funds/balanced-1
```

### Calculate fee impact

```bash
curl -X POST http://localhost:8081/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "fundId": "balanced-1",
    "balance": 50000,
    "annualContribution": 6000,
    "years": 30,
    "annualReturnPercent": 5.0
  }'
```

Example response:

```json
{
  "fund": "KiwiSaver Balanced Fund",
  "projectedBalance": 482741.32,
  "totalFeesPaid": 95618.47,
  "balanceWithoutFees": 578359.79
}
```

## Tests

Run all tests with:

```bash
./mvnw test
```

This includes:
- Integration tests (REST endpoints via REST-assured)
- Unit test for the fee calculator service with hand-checkable expected values

## API Testing

A Postman collection is provided at `postman/kiwisaver-fee-calc.postman_collection.json` covering all three endpoints with happy-path, edge-case, and validation scenarios. It uses a `{{baseUrl}}` variable (default: `http://localhost:8081`) so the same collection works locally and in CI.

### Run in Postman

1. Import the `.postman_collection.json` file via Postman's **Import** button.
2. Start the service (`./mvnw quarkus:dev`).
3. Run the **KiwiSaver Fee Impact Calculator** collection against the configured `baseUrl`.

### Run headlessly with Newman

```bash
npm install -g newman
newman run postman/kiwisaver-fee-calc.postman_collection.json \
     --env-var baseUrl=http://localhost:8081 \
     --reporters cli,json
```

See `postman/README.md` for full instructions.
