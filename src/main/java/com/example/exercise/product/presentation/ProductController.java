package com.example.exercise.product.presentation;

import java.util.List;
import java.util.UUID;

import com.example.exercise.product.application.service.ProductApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exercise.product.application.usecase.ProductUseCase;
import com.example.exercise.product.domain.model.Product;
import com.example.exercise.product.presentation.dto.ProductCreateRequest;
import com.example.exercise.product.presentation.dto.ProductUpdateRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("${api.init}/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductUseCase productUseCase;
    private final ProductApplicationService productApplicationService;

    @PostMapping
    @Operation(summary = "상품 생성", description = "신규 상품을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "요청 값 오류")
    })
    public ResponseEntity<Product> create(@RequestBody ProductCreateRequest request) {
        Product response = productUseCase.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    @Operation(summary = "상품 단건 조회", description = "상품 ID로 상품 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    public Product getById(@Parameter(description = "상품 UUID") @PathVariable UUID productId) {
        return productUseCase.getById(productId);
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "전체 상품 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    public List<Product> getAll() {
        return productUseCase.getAll();
    }

    @PutMapping("/{productId}")
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    public Product update(@Parameter(description = "상품 UUID") @PathVariable UUID productId,
                          @RequestBody ProductUpdateRequest request) {
        return productUseCase.update(productId, request);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "상품 없음")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "상품 UUID") @PathVariable UUID productId) {
        productUseCase.delete(productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/embeddings/refresh")
    @Operation(summary = "상품 임베딩 재생성", description = "저장된 상품 전체에 대해 임베딩을 다시 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "재생성 완료")
    })
    public ResponseEntity<Void> refreshEmbeddings() {
        productApplicationService.refreshEmbeddings();
        return ResponseEntity.noContent().build();
    }
}
