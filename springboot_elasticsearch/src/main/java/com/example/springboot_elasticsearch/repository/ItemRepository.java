package com.example.springboot_elasticsearch.repository;

import com.example.springboot_elasticsearch.entity.Item;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2019/3/22 11:02
 * Modified By:
 * Description:
 */
public interface ItemRepository extends ElasticsearchRepository<Item,Long>{

    List<Item> findByPriceBetween(Double price1, Double price2);

}
