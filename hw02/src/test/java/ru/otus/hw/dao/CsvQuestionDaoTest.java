package ru.otus.hw.dao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvQuestionDaoTest {

    private QuestionDao questionDao;

    private String testFileName;
    @Mock
    private TestFileNameProvider testFileNameProvider;

    @DisplayName("Проверка на возникновения исключение чтения файла.")
    @Test
    void executeTestQuestionReadException() {
        testFileName = "test-questions.cs";
        when(testFileNameProvider.getTestFileName()).thenReturn(testFileName);
        questionDao = new CsvQuestionDao(testFileNameProvider);
        QuestionReadException exception = assertThrowsExactly(
                QuestionReadException.class,
                () -> questionDao.findAll());
        assertEquals("Failed to load file %s, it could not be found.".formatted(testFileName), exception.getMessage());
    }

    @DisplayName("Проверка чтения файла тестов, корректный парсинг и заполнение DTO классов.")
    @Test
    void executeTestSuccess() {
        testFileName = "test-questions.csv";
        when(testFileNameProvider.getTestFileName()).thenReturn(testFileName);
        questionDao = new CsvQuestionDao(testFileNameProvider);

        List<Question> questions = questionDao.findAll();

        assertEquals(questions.size(), 2);
        assertFalse(questions.get(0).text().isBlank());
        assertEquals(questions.get(0).answers().size(), 3);
        assertFalse(questions.get(0).answers().get(0).text().isBlank());
    }
}
