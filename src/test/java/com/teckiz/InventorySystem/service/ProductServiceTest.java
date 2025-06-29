package com.teckiz.InventorySystem.service;

import com.teckiz.InventorySystem.dto.ProductDTO;
import com.teckiz.InventorySystem.dto.Response;
import com.teckiz.InventorySystem.entity.Category;
import com.teckiz.InventorySystem.entity.Product;
import com.teckiz.InventorySystem.exceptions.NotFoundException;
import com.teckiz.InventorySystem.repository.CategoryRepository;
import com.teckiz.InventorySystem.repository.ProductRepository;
import com.teckiz.InventorySystem.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product testProduct;
    private ProductDTO productDTO;
    private Category testCategory;
    private MockMultipartFile imageFile;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .name("Electronics")
                .build();

        testProduct = Product.builder()
                .name("iPhone 15")
                .sku("IPHONE-15-001")
                .price(new BigDecimal("999.99"))
                .stockQuantity(50)
                .description("Latest iPhone model")
                .category(testCategory)
                .imageUrl("products/test-image.jpg")
                .build();

        productDTO = new ProductDTO();
        productDTO.setName("iPhone 15");
        productDTO.setSku("IPHONE-15-001");
        productDTO.setPrice(new BigDecimal("999.99"));
        productDTO.setStockQuantity(50);
        productDTO.setDescription("Latest iPhone model");
        productDTO.setCategoryId(1L);

        imageFile = new MockMultipartFile(
                "image",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @Test
    void saveProduct_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Response response = productService.saveProduct(productDTO, null); // No image file to avoid file system issues

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Product successfully saved", response.getMessage());
        verify(categoryRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void saveProduct_WithoutImage_Success() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // Act
        Response response = productService.saveProduct(productDTO, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Product successfully saved", response.getMessage());
        verify(productRepository).save(argThat(product -> product.getImageUrl() == null));
    }

    @Test
    void saveProduct_CategoryNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.saveProduct(productDTO, imageFile));
        verify(categoryRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productDTO.setProductId(1L);

        // Act
        Response response = productService.updateProduct(productDTO, null); // No image file to avoid file system issues

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Product successfully Updated", response.getMessage());
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        productDTO.setProductId(1L);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productDTO, imageFile));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_WithNewCategory_Success() {
        // Arrange
        Category newCategory = Category.builder().name("Smartphones").build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productDTO.setProductId(1L);
        productDTO.setCategoryId(2L);

        // Act
        Response response = productService.updateProduct(productDTO, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(categoryRepository).findById(2L);
        verify(productRepository).save(argThat(product -> product.getCategory().equals(newCategory)));
    }

    @Test
    void updateProduct_WithNewCategory_CategoryNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        productDTO.setProductId(1L);
        productDTO.setCategoryId(2L);

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(productDTO, null));
        verify(categoryRepository).findById(2L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void getAllProducts_Success() {
        // Arrange
        List<Product> products = Arrays.asList(testProduct);
        List<ProductDTO> productDTOs = Arrays.asList(productDTO);

        when(productRepository.findAll(any(Sort.class))).thenReturn(products);
        when(modelMapper.map(any(List.class), any(java.lang.reflect.Type.class))).thenReturn(productDTOs);

        // Act
        Response response = productService.getAllProducts();

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getProducts());
        assertEquals(1, response.getProducts().size());
        verify(productRepository).findAll(any(Sort.class));
    }

    @Test
    void getProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(modelMapper.map(testProduct, ProductDTO.class)).thenReturn(productDTO);

        // Act
        Response response = productService.getProductById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("success", response.getMessage());
        assertNotNull(response.getProduct());
        verify(productRepository).findById(1L);
        verify(modelMapper).map(testProduct, ProductDTO.class);
    }

    @Test
    void getProductById_ProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
        verify(productRepository).findById(1L);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void deleteProduct_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Response response = productService.deleteProduct(1L);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        assertEquals("Product successfully deleted", response.getMessage());
        verify(productRepository).findById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProduct_ProductNotFound_ShouldThrowNotFoundException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.deleteProduct(1L));
        verify(productRepository).findById(1L);
        verify(productRepository, never()).deleteById(any());
    }

    @Test
    void updateProduct_WithPartialUpdates_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productDTO.setProductId(1L);
        productDTO.setName("iPhone 15 Pro");
        productDTO.setPrice(new BigDecimal("1199.99"));
        productDTO.setStockQuantity(25);

        // Act
        Response response = productService.updateProduct(productDTO, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(productRepository).save(argThat(product -> 
            "iPhone 15 Pro".equals(product.getName()) &&
            new BigDecimal("1199.99").equals(product.getPrice()) &&
            product.getStockQuantity() == 25
        ));
    }

    @Test
    void updateProduct_WithEmptyFields_ShouldNotUpdate() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        productDTO.setProductId(1L);
        productDTO.setName("");
        productDTO.setDescription("");

        // Act
        Response response = productService.updateProduct(productDTO, null);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatus());
        verify(productRepository).save(argThat(product -> 
            "iPhone 15".equals(product.getName()) &&
            "Latest iPhone model".equals(product.getDescription())
        ));
    }
} 