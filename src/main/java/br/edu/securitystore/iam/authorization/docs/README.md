# Authorization

Catálogo de permissions definido por `PermissionCode`, sincronizado no bootstrap. Roles ADMIN, MANAGER e USER são criadas inicialmente; ADMIN recebe todas as permissions. RolePermission e UserRole usam IDs escalares e constraints de unicidade. As authorities incluem `ROLE_<nome>` e os códigos de permissions das roles ativas. As permissões `CATALOG_PRODUCT_WRITE`, `SALES_ORDER_READ_ALL`, `LOGISTICS_DELIVERY_READ` e `LOGISTICS_DELIVERY_UPDATE` protegem os endpoints dos módulos correspondentes.
