package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestRunnerServiceTest {

    @Mock
    private IOService ioService;
    @Mock
    private QuestionDao questionDao;

    private Student student;

    private TestService testService;

    @BeforeEach
    void setUp() {
        student = new Student("Test", "Tester");
        Answer answerFirst = new Answer(
                "ClassLoader#geResourceAsStream or ClassPathResource#getInputStream",
                true);
        Answer answerSecond = new Answer("ClassLoader#geResource#getFile + FileReader", false);
        Answer answerThird = new Answer("Wingardium Leviosa", false);
        Question question = new Question(
                "How should resources be loaded form jar in Java?",
                List.of(answerFirst, answerSecond, answerThird));

        when(questionDao.findAll()).thenReturn(List.of(question));
    }

    @DisplayName("Проверка на успешное прохождение тестирования.")
    @Test
    void executeTestPassedTestService() {
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(1);
        testService = new TestServiceImpl(ioService, questionDao);
        TestResult testResult = testService.executeTestFor(student);
        assertEquals(1, testResult.getRightAnswersCount());
        assertEquals(1, testResult.getAnsweredQuestions().size());
    }

    @DisplayName("Проверка на не успешное прохождение тестирования.")
    @Test
    void executeTestFailedTestService() {
        when(ioService.readIntForRange(anyInt(), anyInt(), anyString())).thenReturn(2);
        testService = new TestServiceImpl(ioService, questionDao);
        TestResult testResult = testService.executeTestFor(student);
        assertEquals(0, testResult.getRightAnswersCount());
        assertEquals(1, testResult.getAnsweredQuestions().size());
    }

}
