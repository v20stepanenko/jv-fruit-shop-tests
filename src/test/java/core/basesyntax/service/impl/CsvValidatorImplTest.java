package core.basesyntax.service.impl;

import core.basesyntax.dao.FruitMapDao;
import core.basesyntax.dao.FruitStorageDao;
import core.basesyntax.model.OperationType;
import core.basesyntax.service.Validator;
import core.basesyntax.strategy.AddOperation;
import core.basesyntax.strategy.OperationHandler;
import core.basesyntax.strategy.RemoveOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CsvValidatorImplTest {
    private static Validator validator;
    private static FruitStorageDao fruitDao;
    private static Map<String, OperationHandler> operationHandlerMap;
    private static List<String> fileData;

    @BeforeClass
    public static void beforeClass() {
        validator = new CsvValidatorImpl();
        fileData = new ArrayList<>();
        fruitDao = new FruitMapDao();
        operationHandlerMap = new HashMap<>();
        operationHandlerMap.put(OperationType.BALANCE.getOperation(),
            new AddOperation(fruitDao));
        operationHandlerMap.put(OperationType.PURCHASE.getOperation(),
            new RemoveOperation(fruitDao));
        operationHandlerMap.put(OperationType.SUPPLY.getOperation(),
            new AddOperation(fruitDao));
        operationHandlerMap.put(OperationType.RETURN.getOperation(),
            new AddOperation(fruitDao));
    }

    @Before
    public void setUp() {
        fileData.clear();
        fileData.addAll(List.of("type,fruit,quantity",
            "b,banana,20",
            "b,apple,100",
            "s,banana,100",
            "s,apple,13",
            "r,banana,10",
            "r,apple,20",
            "p,banana,5",
            "p,apple,50"));
    }

    @Test
    public void validate_validData_Ok() {
        Assert.assertTrue(validator.isValid(fileData));
    }

    @Test(expected = RuntimeException.class)
    public void validate_null_notOk() {
        validator.isValid(null);
    }

    @Test(expected = RuntimeException.class)
    public void validate_emptyData_notOk() {
        validator.isValid(new ArrayList<>());
    }

    @Test(expected = RuntimeException.class)
    public void validate_notExistingHeadData_notOk() {
        fileData.remove(0);
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_invalidHeadData_notOk() {
        fileData.set(0, "type,fruit");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_notExistingOperationType_notOk() {
        fileData.set(1, "a,banana,20");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_upperCaseOperationType_notOk() {
        fileData.set(2, "B,banana,20");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_twoCharactersOperationType_notOk() {
        fileData.set(3, "bb,banana,20");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_quantityContainsLetters_notOk() {
        fileData.set(1, "b,banana,20");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_negativeQuantity_notOk() {
        fileData.set(1, "b,banana,-1");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_notWholeNumberQuantity_notOk() {
        fileData.set(1, "b,banana,12.4");
        validator.isValid(fileData);
    }

    @Test(expected = RuntimeException.class)
    public void validate_notWholeNumberQuantityCommaSeparated_notOk() {
        fileData.set(1, "b,banana,12,4");
        validator.isValid(fileData);
    }
}