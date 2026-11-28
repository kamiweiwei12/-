package edu.sysu.snowfield.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import edu.sysu.snowfield.booking.application.BookingApplicationService;
import edu.sysu.snowfield.booking.application.CreateLodgingOrderCommand;
import edu.sysu.snowfield.booking.application.CreatedLodgingOrder;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证启动类的调度配置会真实触发过期事务，不以直接调用服务代替调度证据。 */
@SpringBootTest(properties = {
        "snowfield.order-expiry-initial-delay-ms=1000",
        "snowfield.order-expiry-scan-ms=3600000"
})
@ActiveProfiles("test")
class OrderExpirySchedulingIT {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private BookingApplicationService bookingService;

    private long userId;
    private long resourceUnitId;

    @BeforeEach
    void setUp() throws SQLException {
        execute("""
                TRUNCATE TABLE user_role, app_user, campground, season, resource_type, holiday, price_rule,
                               package_offer, lodging_reservation
                RESTART IDENTITY CASCADE
                """);
        execute("""
                INSERT INTO app_user (email, password_hash, display_name)
                VALUES ('scheduled-expiry@example.com', 'x', '调度过期用户')
                """);
        userId = scalar("SELECT user_id FROM app_user WHERE email = 'scheduled-expiry@example.com'");
        execute("INSERT INTO campground (code, name, timezone, currency) VALUES ('main', '雪野', 'Asia/Shanghai', 'CNY')");
        long campgroundId = scalar("SELECT campground_id FROM campground WHERE code = 'main'");
        execute("""
                INSERT INTO resource_type (campground_id, code, name, kind, base_capacity, base_price)
                VALUES (%d, 'CABIN', '木屋', 'CABIN', 4, 1280.00)
                """.formatted(campgroundId));
        long typeId = scalar("SELECT resource_type_id FROM resource_type WHERE code = 'CABIN'");
        execute("INSERT INTO resource_type_stay_mode (resource_type_id, stay_mode) VALUES (%d, 'OVERNIGHT')"
                .formatted(typeId));
        execute("INSERT INTO resource_unit (resource_type_id, code, name, capacity) VALUES (%d, 'SCHEDULE-A', '调度测试屋', 4)"
                .formatted(typeId));
        resourceUnitId = scalar("SELECT resource_unit_id FROM resource_unit WHERE code = 'SCHEDULE-A'");
    }

    @Test
    void schedulerExpiresDueOrderAndReleasesLodging() throws Exception {
        CreatedLodgingOrder order = createOrder();
        execute("UPDATE customer_order SET expires_at = now() - interval '1 second' WHERE order_id = "
                + order.orderId());

        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5);
        while (System.nanoTime() < deadline && scalar("SELECT count(*) FROM customer_order WHERE order_id = "
                + order.orderId() + " AND status = 'EXPIRED'") == 0) {
            Thread.sleep(50);
        }

        assertThat(scalar("SELECT count(*) FROM customer_order WHERE order_id = " + order.orderId()
                + " AND status = 'EXPIRED' AND version = 1")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM order_item WHERE order_id = " + order.orderId()
                + " AND status = 'EXPIRED'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM lodging_reservation WHERE occupancy_status = 'RELEASED'"))
                .isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM order_status_history WHERE order_id = " + order.orderId()
                + " AND to_status = 'EXPIRED'")).isEqualTo(1);
        assertThat(scalar("SELECT count(*) FROM lodging_reservation_status_history WHERE to_status = 'RELEASED'"))
                .isEqualTo(1);
    }

    private CreatedLodgingOrder createOrder() {
        var line = new CreateLodgingOrderCommand.LodgingLine(
                resourceUnitId, "OVERNIGHT",
                OffsetDateTime.parse("2027-12-20T15:00+08"),
                OffsetDateTime.parse("2027-12-21T12:00+08"), 2);
        return bookingService.createLodgingOrder(
                new CreateLodgingOrderCommand(userId, "scheduled-expiry-order", List.of(line), null));
    }

    private void execute(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
    }

    private long scalar(String sql) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {
            rs.next();
            return rs.getLong(1);
        }
    }
}
