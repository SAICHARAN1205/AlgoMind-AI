package com.algomind.factory;

import com.algomind.exception.AlgorithmNotSupportedException;
import com.algomind.model.ParsedAlgorithm;
import com.algomind.parser.AlgorithmParser;
import com.algomind.parser.BinarySearchParser;
import com.algomind.parser.BubbleSortParser;
import com.algomind.parser.ReverseArrayParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserFactoryTest {

    private ParserFactory factory;

    @BeforeEach
    void setUp() {
        List<AlgorithmParser> parsers = List.of(
            new ReverseArrayParser(),
            new BubbleSortParser(),
            new BinarySearchParser()
        );
        factory = new ParserFactory(parsers);
    }

    @Test
    void testParseValidBubbleSort() {
        ParsedAlgorithm parsed = factory.parseCode("bubbleSort(myArray);");
        assertEquals("bubbleSort", parsed.getAlgorithmName());
    }

    @Test
    void testParseValidBinarySearch() {
        ParsedAlgorithm parsed = factory.parseCode("binarySearch(arr, 42)");
        assertEquals("binarySearch", parsed.getAlgorithmName());
        assertEquals(42, parsed.getTargetValue());
    }

    @Test
    void testParseUnsupportedCode() {
        ParsedAlgorithm parsed = factory.parseCode("quickSort(arr)");
        assertEquals("Dynamic Execution", parsed.getAlgorithmName());
    }
}
