# Authorization

Catálogo de permissions definido por `PermissionCode`, sincronizado no bootstrap. Roles ADMIN, MANAGER e USER são criadas inicialmente; ADMIN recebe todas as permissions. RolePermission e UserRole usam IDs escalares e constraints de unicidade. As authorities incluem `ROLE_<nome>` e os códigos de permissions das roles ativas. A API administrativa usa `@PreAuthorize` por permission. As permissões `CATALOG_PRODUCT_WRITE`, `SALES_ORDER_READ_ALL` e `SALES_DELIVERY_UPDATE` protegem os endpoints demonstrativos existentes sem criar uma arquitetura futura para Products.
