package com.ipanel.video.videodemo.dao;

import com.ipanel.video.videodemo.model.Image;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;

@Mapper
public interface ImageMapper {
    @Delete({
        "delete from image",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into image (id, videoId, ",
        "groupString, path, ",
        "size)",
        "values (#{id,jdbcType=INTEGER}, #{videoid,jdbcType=INTEGER}, ",
        "#{groupstring,jdbcType=VARCHAR}, #{path,jdbcType=VARCHAR}, ",
        "#{size,jdbcType=VARCHAR})"
    })
    int insert(Image record);

    @InsertProvider(type=ImageSqlProvider.class, method="insertSelective")
    int insertSelective(Image record);

    @Select({
        "select",
        "id, videoId, groupString, path, size",
        "from image",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="videoId", property="videoid", jdbcType=JdbcType.INTEGER),
        @Result(column="groupString", property="groupstring", jdbcType=JdbcType.VARCHAR),
        @Result(column="path", property="path", jdbcType=JdbcType.VARCHAR),
        @Result(column="size", property="size", jdbcType=JdbcType.VARCHAR)
    })
    Image selectByPrimaryKey(Integer id);

    @UpdateProvider(type=ImageSqlProvider.class, method="updateByPrimaryKeySelective")
    int updateByPrimaryKeySelective(Image record);

    @Update({
        "update image",
        "set videoId = #{videoid,jdbcType=INTEGER},",
          "groupString = #{groupstring,jdbcType=VARCHAR},",
          "path = #{path,jdbcType=VARCHAR},",
          "size = #{size,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Image record);

    @Select("select * from image where videoId=#{videoId}")
    List<Image> selectByVideoId(@Param("videoId") Integer videoId);

}