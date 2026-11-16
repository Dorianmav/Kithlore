.PHONY: lint test build

lint:
	cd apps/api && ./mvnw spotless:check checkstyle:check
	npm run lint

test:
	cd apps/api && ./mvnw test
	npm test --workspace apps/web -- --watch=false

build:
	cd apps/api && ./mvnw clean verify
	npm run build
