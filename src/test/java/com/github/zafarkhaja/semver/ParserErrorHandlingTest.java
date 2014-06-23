/*
 * The MIT License
 *
 * Copyright 2014 Zafar Khaja <zafarkhaja@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.zafarkhaja.semver;

import com.github.zafarkhaja.semver.VersionParser.CharType;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import static com.github.zafarkhaja.semver.VersionParser.CharType.*;
import static org.junit.Assert.*;

/**
 *
 * @author Zafar Khaja <zafarkhaja@gmail.com>
 */
@RunWith(Parameterized.class)
public class ParserErrorHandlingTest {

    private final String invalidVersion;
    private final Character unexpected;
    private final CharType[] expected;

    public ParserErrorHandlingTest(
        String invalidVersion,
        Character unexpected,
        CharType[] expected
    ) {
        this.invalidVersion = invalidVersion;
        this.unexpected = unexpected;
        this.expected = expected;
    }

    @Test
    public void shouldCorrectlyHandleParseErrors() {
        try {
            VersionParser.parseValidSemVer(invalidVersion);
        } catch (UnexpectedCharacterException e) {
            assertEquals(unexpected, e.getUnexpectedCharacter());
            assertArrayEquals(expected, e.getExpectedCharTypes());
            return;
        } catch (ParseException e) {
            if (e.getCause() != null) {
                UnexpectedCharacterException cause = (UnexpectedCharacterException) e.getCause();
                assertEquals(unexpected, cause.getUnexpectedCharacter());
                assertArrayEquals(expected, cause.getExpectedCharTypes());
            }
            return;
        }
        fail("Uncaught exception");
    }

    @Parameters(name = "{0}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
            { "1",                null, new CharType[] { DOT } },
            { "1 ",               ' ',  new CharType[] { DOT } },
            { "1.",               null, new CharType[] { DIGIT } },
            { "1.2",              null, new CharType[] { DOT } },
            { "1.2.",             null, new CharType[] { DIGIT } },
            { "a.b.c",            'a',  new CharType[] { DIGIT } },
            { "1.b.c",            'b',  new CharType[] { DIGIT } },
            { "1.2.c",            'c',  new CharType[] { DIGIT } },
            { "!.2.3",            '!',  new CharType[] { DIGIT } },
            { "1.!.3",            '!',  new CharType[] { DIGIT } },
            { "1.2.!",            '!',  new CharType[] { DIGIT } },
            { "v1.2.3",           'v',  new CharType[] { DIGIT } },
            { "1.2.3-",           null, new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2. 3",           ' ',  new CharType[] { DIGIT } },
            { "1.2.3=alpha",      '=',  new CharType[] { HYPHEN, PLUS, EOL } },
            { "1.2.3~beta",       '~',  new CharType[] { HYPHEN, PLUS, EOL } },
            { "1.2.3-be$ta",      '$',  new CharType[] { PLUS, EOL } },
            { "1.2.3+b1+b2",      '+',  new CharType[] { EOL } },
            { "1.2.3-rc!",        '!',  new CharType[] { PLUS, EOL } },
            { "1.2.3-+",          '+',  new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3-@",          '@',  new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3+@",          '@',  new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3-rc1.",       null, new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3+20140620.",  null, new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3-b.+b",       '+',  new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3-rc..",       '.',  new CharType[] { DIGIT, LETTER, HYPHEN } },
            { "1.2.3-rc+bld..",   '.',  new CharType[] { DIGIT, LETTER, HYPHEN } },
        });
    }
}
