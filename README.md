# Mongo Demo - Spring Boot + MongoDB

This project exposes REST APIs for product CRUD operations, pagination, and MongoDB aggregations.

## Features

- Create, update, get, delete Product APIs
- Paginated and sortable listing API
- MongoDB persistence via Spring Data MongoDB
- Swagger UI / OpenAPI integration
- Aggregation API over `unitsSold` (sum, avg, min, max, count, and by-category totals)
- Additional operation API to adjust `unitsSold` by delta

## Tech Stack

- Java 17
- Spring Boot 3.3.x
- Spring Data MongoDB
- Spring Web + Validation
- springdoc-openapi

## Run

1. Start MongoDB locally (default URI in `application.properties`):
   - `mongodb://localhost:27017/mongo_demo`
2. Build and run the app.

## Quick MongoDB Docker + Seed Script

Use the helper script to create the MongoDB Docker container and seed product data.

- Script path: `scripts/mongo-data.sh`
- Default seed amount: `1500` products
- Suggested range: `1000` to `2000` products

Examples:

```bash
./scripts/mongo-data.sh up
./scripts/mongo-data.sh seed 1200
./scripts/mongo-data.sh up-seed 1800
./scripts/mongo-data.sh reset
./scripts/mongo-data.sh status
./scripts/mongo-data.sh down
```

You can override defaults with env vars, for example:

```bash
MONGO_PORT=27018 MONGO_DB_NAME=mongo_demo ./scripts/mongo-data.sh up-seed 1500
```

## API Endpoints

Base path: `/api/products`

- `POST /api/products` - create product
- `GET /api/products/{id}` - get product by id
- `PUT /api/products/{id}` - update product
- `DELETE /api/products/{id}` - delete product
- `GET /api/products?page=0&size=10&sortBy=name&direction=ASC` - paginated list
- `POST /api/products/{id}/units-sold/adjust` - adjust unitsSold by delta
- `GET /api/products/analytics/units-sold` - aggregated analytics

Swagger:

- UI: `/swagger-ui.html`
- OpenAPI JSON: `/api-docs`

## Sample Payloads

Create / Update product:

```json
{
  "name": "Laptop Pro",
  "category": "Electronics",
  "availableStock": 120,
  "unitsSold": 35
}
```

Adjust units sold:

```json
{
  "delta": 10
}
```

