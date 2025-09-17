package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.AiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI配置Mapper接口
 */
@Mapper
public interface AiConfigMapper extends BaseMapper<AiConfig> {

    /**
     * 根据配置键获取配置
     */
    AiConfig selectByConfigKey(@Param("configKey") String configKey);

    /**
     * 根据配置分组获取配置列表
     */
    List<AiConfig> selectByConfigGroup(@Param("configGroup") String configGroup);

    /**
     * 获取所有启用的配置
     */
    List<AiConfig> selectEnabledConfigs();

    /**
     * 批量更新配置
     */
    int updateBatch(@Param("configs") List<AiConfig> configs);
}