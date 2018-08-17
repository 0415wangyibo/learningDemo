package com.wu.demo.fileupload.demo.dao;

import com.wu.demo.fileupload.demo.entity.Image;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageDao extends CrudRepository<Image, Long> {
    List<Image> findByVideoId(Integer videoID);

    void deleteById(Integer id);
}
