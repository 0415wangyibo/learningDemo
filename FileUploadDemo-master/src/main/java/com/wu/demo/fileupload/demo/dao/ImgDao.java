package com.wu.demo.fileupload.demo.dao;

import com.wu.demo.fileupload.demo.entity.Img;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImgDao extends CrudRepository<Img,Long>{
    Img findByVideoId(Integer videoId);
    
    void deleteByVideoId(Integer videoId);
}
