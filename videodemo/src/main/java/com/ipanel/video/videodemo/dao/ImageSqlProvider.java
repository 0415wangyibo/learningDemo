package com.ipanel.video.videodemo.dao;

import com.ipanel.video.videodemo.model.Image;
import org.apache.ibatis.jdbc.SQL;

public class ImageSqlProvider {

    public String insertSelective(Image record) {
        SQL sql = new SQL();
        sql.INSERT_INTO("image");
        
        if (record.getId() != null) {
            sql.VALUES("id", "#{id,jdbcType=INTEGER}");
        }
        
        if (record.getVideoid() != null) {
            sql.VALUES("videoId", "#{videoid,jdbcType=INTEGER}");
        }
        
        if (record.getGroupstring() != null) {
            sql.VALUES("groupString", "#{groupstring,jdbcType=VARCHAR}");
        }
        
        if (record.getPath() != null) {
            sql.VALUES("path", "#{path,jdbcType=VARCHAR}");
        }
        
        if (record.getSize() != null) {
            sql.VALUES("size", "#{size,jdbcType=VARCHAR}");
        }
        
        return sql.toString();
    }

    public String updateByPrimaryKeySelective(Image record) {
        SQL sql = new SQL();
        sql.UPDATE("image");
        
        if (record.getVideoid() != null) {
            sql.SET("videoId = #{videoid,jdbcType=INTEGER}");
        }
        
        if (record.getGroupstring() != null) {
            sql.SET("groupString = #{groupstring,jdbcType=VARCHAR}");
        }
        
        if (record.getPath() != null) {
            sql.SET("path = #{path,jdbcType=VARCHAR}");
        }
        
        if (record.getSize() != null) {
            sql.SET("size = #{size,jdbcType=VARCHAR}");
        }
        
        sql.WHERE("id = #{id,jdbcType=INTEGER}");
        
        return sql.toString();
    }
}