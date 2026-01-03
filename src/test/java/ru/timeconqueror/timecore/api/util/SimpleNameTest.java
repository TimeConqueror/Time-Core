package ru.timeconqueror.timecore.api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.timeconqueror.timecore.api.util.SimpleName.convertClassNameToSnake;

class SimpleNameTest {
    @Test
    public void testSimpleNameWithClassHierarchy() {
        String s = SimpleName.simpleNameWithClassHierarchy(SimpleNameTest.class);
        assertEquals("SimpleNameTest", s);
    }

    @Test
    public void testSimpleNameWithClassHierarchyInnerClass() {
        String s = SimpleName.simpleNameWithClassHierarchy(SimpleNameTest.Inner.class);
        assertEquals("SimpleNameTest$Inner", s);
    }

    @Test
    public void testSimpleNameWithClassHierarchy2InnerClass() {
        String s = SimpleName.simpleNameWithClassHierarchy(SimpleNameTest.Inner.TwiceInner.class);
        assertEquals("SimpleNameTest$Inner$TwiceInner", s);
    }

    @Test
    void testConvertClassNameToSnakeSingleWord() {
        assertEquals("outer", convertClassNameToSnake("Outer"));
        assertEquals("simple_name", convertClassNameToSnake("SimpleName"));
        assertEquals("xml_http_request", convertClassNameToSnake("XMLHttpRequest"));
        assertEquals("simple_xml_http_request", convertClassNameToSnake("SimpleXMLHttpRequest"));
    }

    @Test
    void testConvertClassNameToSnakeNestedClasses() {
        assertEquals("outer/inner", convertClassNameToSnake("Outer$Inner"));
        assertEquals("outer/simple_name", convertClassNameToSnake("Outer$SimpleName"));
        assertEquals("xml_http_request/inner_class", convertClassNameToSnake("XMLHttpRequest$InnerClass"));
        assertEquals("simple_xml_http_request/inner_class", convertClassNameToSnake("SimpleXMLHttpRequest$InnerClass"));
    }

    @Test
    void testConvertClassNameToSnakeMultipleNestedClasses() {
        assertEquals("outer/inner/inner_most", convertClassNameToSnake("Outer$Inner$InnerMost"));
        assertEquals("simple/inner/most_inner", convertClassNameToSnake("Simple$Inner$MostInner"));
    }

    @Test
    void testConvertClassNameToSnakeAlreadySnakeCaseOrLower() {
        assertEquals("lowercase", convertClassNameToSnake("lowercase"));
        assertEquals("snake_case", convertClassNameToSnake("snake_case"));
        assertEquals("outer/inner_class", convertClassNameToSnake("Outer$inner_class"));
    }

    public static class Inner {
        public static class TwiceInner {

        }
    }
}