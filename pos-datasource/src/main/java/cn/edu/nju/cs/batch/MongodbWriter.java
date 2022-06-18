package cn.edu.nju.cs.batch;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

public class MongodbWriter implements ItemWriter<Product> {
    private final ProductRepository repository;

    public MongodbWriter(ProductRepository repository) {
        this.repository = repository;
    }


    @Override
    public void write(List<? extends Product> list) throws Exception {

        repository.saveAll(list);
    }
}
