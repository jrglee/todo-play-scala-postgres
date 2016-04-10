docker run --name todo-postgres \
  -e POSTGRES_DB=todo \
  -e POSTGRES_PASSWORD=mysecretpassword \
  -p 5432:5432 -d postgres:9.4
