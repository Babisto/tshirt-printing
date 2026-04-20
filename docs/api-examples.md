# API Examples

## 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@tshirt.local",
    "password": "Admin@123"
  }'
```

## 2. Create Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "PLAIN-WHT-001",
    "name": "Plain T-Shirt",
    "category": "PLAIN_T_SHIRTS",
    "description": "High quality cotton plain tee",
    "active": true,
    "variants": [
      {
        "size": "M",
        "color": "White",
        "quantityInStock": 120,
        "unitCost": 4.50,
        "retailPrice": 8.00,
        "barcode": "123456789012",
        "lowStockThreshold": 20
      },
      {
        "size": "L",
        "color": "Black",
        "quantityInStock": 80,
        "unitCost": 5.00,
        "retailPrice": 9.00,
        "barcode": "123456789013",
        "lowStockThreshold": 15
      }
    ]
  }'
```

## 3. Add Stock

```bash
curl -X POST http://localhost:8080/api/stock/add \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "variantId": 1,
    "quantity": 50,
    "unitCost": 4.60,
    "reference": "PO-1001",
    "note": "Supplier replenishment"
  }'
```

## 4. Create Paint

```bash
curl -X POST http://localhost:8080/api/paints \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Premium Red Ink",
    "paintType": "PLASTISOL",
    "color": "Red",
    "quantityAvailable": 25.00,
    "unit": "LITERS",
    "costPerUnit": 12.50,
    "lowStockThreshold": 5.00,
    "active": true
  }'
```

## 5. Create Print Job

```bash
curl -X POST http://localhost:8080/api/print-jobs \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "variantId": 1,
    "quantityPrinted": 40,
    "status": "COMPLETED",
    "notes": "School event order",
    "paintUsages": [
      {
        "paintId": 1,
        "quantityUsed": 2.50
      }
    ]
  }'
```

## 6. Dashboard Summary

```bash
curl -X GET http://localhost:8080/api/products/dashboard/summary \
  -H "Authorization: Bearer <TOKEN>"
```

## 7. Stock Report JSON

```bash
curl -X GET "http://localhost:8080/api/reports/stock?from=2026-03-01&to=2026-03-31" \
  -H "Authorization: Bearer <TOKEN>"
```

## 8. Export Stock Report PDF

```bash
curl -X GET "http://localhost:8080/api/reports/stock/export/pdf?from=2026-03-01&to=2026-03-31" \
  -H "Authorization: Bearer <TOKEN>" \
  --output stock-report.pdf
```

## 9. Export Print Jobs CSV

```bash
curl -X GET "http://localhost:8080/api/reports/print-jobs/export/csv?from=2026-03-01&to=2026-03-31" \
  -H "Authorization: Bearer <TOKEN>" \
  --output print-jobs-report.csv
```

## 10. Export Paint Usage PDF

```bash
curl -X GET "http://localhost:8080/api/reports/paint-usage/export/pdf?from=2026-03-01&to=2026-03-31" \
  -H "Authorization: Bearer <TOKEN>" \
  --output paint-usage-report.pdf
```
