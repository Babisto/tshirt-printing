package com.tshirtprinting.stockmanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "app.bootstrap.admin-email=admin@test.local",
        "app.bootstrap.admin-password=Admin@123",
        "app.bootstrap.staff-email=staff@test.local",
        "app.bootstrap.staff-password=Staff@123",
        "app.jwt.secret=Y2hhbmdlLXRoaXMtc2VjcmV0LWtleS1jaGFuZ2UtdGhpcy1zZWNyZXQta2V5",
        "app.jwt.expiration-ms=86400000"
})
class StockManagementApplicationTests {

    @Test
    void contextLoads() {
    }
}
