package edu.sysu.snowfield;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * S2 起 Spring 上下文包含数据源与 Flyway 迁移，因此本测试改为在 {@code db} profile
 * 下指向隔离的测试库运行；真实数据库证据由 {@code *IT} 用例产出。
 *
 * <p>它不能替代任何业务、数据库、架构或并发测试；架构边界由
 * {@code architecture.ArchitectureTest} 保证。</p>
 */
@SpringBootTest
@ActiveProfiles("test")
class SnowfieldApplicationTests {

    @Test
    void contextLoads() {
        // 上下文加载失败时本测试会失败，作为最小构建证据
    }
}
