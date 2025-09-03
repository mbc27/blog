package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.SystemSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 系统设置Mapper接口
 */
@Mapper
public interface SystemSettingsMapper extends BaseMapper<SystemSettings> {
    
    /**
     * 获取所有设置
     */
    @Select("SELECT * FROM system_settings")
    List<SystemSettings> selectAllSettings();
    
    /**
     * 根据key获取设置
     */
    @Select("SELECT * FROM system_settings WHERE setting_key = #{key}")
    SystemSettings selectByKey(String key);
}