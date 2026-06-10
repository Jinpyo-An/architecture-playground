package com.example.exercise.search.infrastructure.event;

import com.example.exercise.search.application.SearchUsecase;
import com.example.exercise.search.infrastructure.event.dto.ProductDelete;
import com.example.exercise.search.infrastructure.event.dto.ProductSave;
import com.example.exercise.search.infrastructure.event.dto.ProductToSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafKaListener {
    private final SearchUsecase searchUsecase;
    @KafkaListener(
            topics = "search-service",
            groupId = "product-search",
            containerFactory = "searchKafkaContainerFactory"
//            , concurrency = "3"//partition 갯수에 맞춰서 하는게 좋음.
    )
    public void handle(ProductToSearch event) {
        if(event.eventType().equals("create")){
            ProductSave productSave = (ProductSave) event.payload();
            //TODO: 생성시 받아서 처리하는 부분.
        }else if(event.eventType().equals("update")){
            ProductSave productSave = (ProductSave) event.payload();
            //TODO: 갱신시 받아서 처리하는 부분.
        } else if(event.eventType().equals("delete")){
            ProductDelete productDelete = (ProductDelete) event.payload();
            //TODO: 삭제시 받아서 처리하는 부분.
        }
    }
}
