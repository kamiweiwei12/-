package edu.sysu.snowfield;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * S1 最小构建门禁：只验证 Spring 上下文可加载。
 *
 * <p>它不能替代任何业务、数据库、架构或并发测试；架构边界由
 * {@code architecture.ArchitectureTest} 保证，业务/数据库测试在真实 PostgreSQL 下产出。</p>
 */
@SpringBootTest
class SnowfieldApplicationTests {

    @Test
    void contextLoads() {
        // 上下文加载失败时本测试会失败，作为 S1 的最小构建证据
    }
}
