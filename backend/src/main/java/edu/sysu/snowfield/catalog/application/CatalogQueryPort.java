package edu.sysu.snowfield.catalog.application;

import java.util.Optional;

/**
 * catalog 模块对外的查询端口（由 infrastructure 实现）。
 */
public interface CatalogQueryPort {

    /** 按资源单元取快照；资源不存在或已停用时返回 empty。 */
    Optional<ResourceSnapshot> findResourceUnit(long resourceUnitId);

    /** 校验资源类型是否支持请求的住宿模式。 */
    boolean supportsStayMode(long resourceTypeId, String stayMode);
}
