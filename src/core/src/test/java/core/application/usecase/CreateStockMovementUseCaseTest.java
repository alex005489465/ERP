package core.application.usecase;

import core.application.usecase.CreateStockMovementUseCase;
import core.contract.usecase.dto.CreateStockMovementInput;
import core.contract.usecase.dto.CreateStockMovementOutput;
import core.library.base.BaseRepository;
import core.domain.entity.Stock;
import core.domain.entity.StockMovement;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * CreateStockMovementUseCase 測試類別
 */
public class CreateStockMovementUseCaseTest {

    private CreateStockMovementUseCase useCase;
    
    @Mock
    private BaseRepository<Stock, Long> stockRepository;
    
    @Mock
    private BaseRepository<StockMovement, Long> stockMovementRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new CreateStockMovementUseCase(stockRepository, stockMovementRepository);
    }

    @Test
    public void testValidateInput_ValidInput_ReturnsTrue() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            1L, 
            "A001", 
            1, 
            new BigDecimal("10.00")
        );

        // Act
        boolean result = useCase.validateInput(input);

        // Assert
        assertTrue(result, "Valid input should pass validation");
    }

    @Test
    public void testValidateInput_NullInput_ReturnsFalse() {
        // Act
        boolean result = useCase.validateInput(null);

        // Assert
        assertFalse(result, "Null input should fail validation");
    }

    @Test
    public void testValidateInput_InvalidItemId_ReturnsFalse() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            null, 
            "A001", 
            1, 
            new BigDecimal("10.00")
        );

        // Act
        boolean result = useCase.validateInput(input);

        // Assert
        assertFalse(result, "Null itemId should fail validation");
    }

    @Test
    public void testCalculateNewQuantity_ValidInputs_ReturnsCorrectSum() {
        // Arrange
        BigDecimal currentQuantity = new BigDecimal("50.00");
        BigDecimal quantityChange = new BigDecimal("10.00");

        // Act
        BigDecimal result = useCase.calculateNewQuantity(currentQuantity, quantityChange);

        // Assert
        assertEquals(new BigDecimal("60.00"), result, "Should correctly calculate new quantity");
    }

    @Test
    public void testCalculateNewQuantity_NullCurrentQuantity_UsesZero() {
        // Arrange
        BigDecimal quantityChange = new BigDecimal("10.00");

        // Act
        BigDecimal result = useCase.calculateNewQuantity(null, quantityChange);

        // Assert
        assertEquals(new BigDecimal("10.00"), result, "Should use zero for null current quantity");
    }

    @Test
    public void testValidateBusinessRules_NegativeNewQuantity_ReturnsFalse() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            1L, 
            "A001", 
            1, 
            new BigDecimal("-60.00")
        );
        BigDecimal currentQuantity = new BigDecimal("50.00");
        BigDecimal newQuantity = new BigDecimal("-10.00");

        // Act
        boolean result = useCase.validateBusinessRules(input, currentQuantity, newQuantity);

        // Assert
        assertFalse(result, "Negative new quantity should fail business rules validation");
    }

    @Test
    public void testValidateBusinessRules_ValidQuantities_ReturnsTrue() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            1L, 
            "A001", 
            1, 
            new BigDecimal("10.00")
        );
        BigDecimal currentQuantity = new BigDecimal("50.00");
        BigDecimal newQuantity = new BigDecimal("60.00");

        // Act
        boolean result = useCase.validateBusinessRules(input, currentQuantity, newQuantity);

        // Assert
        assertTrue(result, "Valid quantities should pass business rules validation");
    }

    @Test
    public void testMaintask_ValidInput_ReturnsSuccessResult() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            1L, 
            "A001", 
            1, 
            new BigDecimal("10.00")
        );
        
        // Mock repository behavior
        Stock existingStock = new Stock(1L, "A001", new BigDecimal("50.00"));
        when(stockRepository.findFirst(any())).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(new StockMovement());

        // Act
        CreateStockMovementOutput result = useCase.maintask(input);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isSuccess(), "Should return success for valid input");
        assertEquals("庫存異動記錄創建成功", result.getMessage(), "Should return success message");
        
        // Verify repository interactions
        verify(stockRepository, times(1)).findFirst(any());
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(stockMovementRepository, times(1)).save(any(StockMovement.class));
    }

    @Test
    public void testMaintask_InvalidInput_ReturnsFailureResult() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            null, 
            "A001", 
            1, 
            new BigDecimal("10.00")
        );

        // Act
        CreateStockMovementOutput result = useCase.maintask(input);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isSuccess(), "Should return failure for invalid input");
        assertEquals("輸入參數驗證失敗", result.getMessage(), "Should return validation failure message");
    }

    @Test
    public void testExecute_ValidInput_ReturnsSuccessResult() {
        // Arrange
        CreateStockMovementInput input = new CreateStockMovementInput(
            1L, 
            "A001", 
            1, 
            new BigDecimal("10.00")
        );
        
        // Mock repository behavior
        Stock existingStock = new Stock(1L, "A001", new BigDecimal("50.00"));
        when(stockRepository.findFirst(any())).thenReturn(Optional.of(existingStock));
        when(stockRepository.save(any(Stock.class))).thenReturn(existingStock);
        when(stockMovementRepository.save(any(StockMovement.class))).thenReturn(new StockMovement());

        // Act
        CreateStockMovementOutput result = useCase.execute(input);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isSuccess(), "Should return success for valid input");
    }
}